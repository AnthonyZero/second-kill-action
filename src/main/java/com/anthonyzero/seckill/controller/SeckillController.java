package com.anthonyzero.seckill.controller;

import com.anthonyzero.seckill.common.annotation.AccessLimit;
import com.anthonyzero.seckill.common.core.CurrentUserContext;
import com.anthonyzero.seckill.common.core.Result;
import com.anthonyzero.seckill.common.enums.CodeMsgEnum;
import com.anthonyzero.seckill.common.rabbitmq.MQSender;
import com.anthonyzero.seckill.common.rabbitmq.SeckillMessage;
import com.anthonyzero.seckill.common.redis.RedisService;
import com.anthonyzero.seckill.common.redis.key.GoodsKey;
import com.anthonyzero.seckill.common.redis.key.OrderKey;
import com.anthonyzero.seckill.common.redis.key.SeckillKey;
import com.anthonyzero.seckill.domain.OrderInfo;
import com.anthonyzero.seckill.domain.SeckillOrder;
import com.anthonyzero.seckill.domain.SeckillUser;
import com.anthonyzero.seckill.service.GoodsService;
import com.anthonyzero.seckill.service.OrderService;
import com.anthonyzero.seckill.service.SeckillService;
import com.anthonyzero.seckill.vo.GoodsVO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 秒杀
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SeckillService seckillService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MQSender mqSender;

    private Map<Long, Boolean> localOverMap = new HashMap<>();


    /**
     * 系统初始化时商品库存信息放入缓存
     */
    @Override
    public void afterPropertiesSet() throws Exception{
        List<GoodsVO> goodsList = goodsService.listGoodsVO();
        if (goodsList == null || goodsList.size() == 0) {
            return;
        }
        for (GoodsVO goodsVO : goodsList) {
            redisService.set(GoodsKey.getSeckillGoodsStock, "" + goodsVO.getId(), goodsVO.getStockCount());
            localOverMap.put(goodsVO.getId(), false);
        }
    }
    /**
     * 秒杀
     * @param model
     * @param seckillUser
     * @param goodsId
     * @return
     */
    @PostMapping("/do_seckill")
    public String seckill(Model model, SeckillUser seckillUser, long goodsId) {
        model.addAttribute("user", seckillUser);
        if (seckillUser == null) {
            return "login";
        }

        //判断库存
        GoodsVO goodsVO = goodsService.getGoodsVOByGoodsId(goodsId);
        if (goodsVO == null) {
            model.addAttribute("errmsg", CodeMsgEnum.GOODS_NOT_EXIST.getMsg());
            return "seckill_fail";
        }
        if (goodsVO.getStockCount() <= 0) {
            model.addAttribute("errmsg", CodeMsgEnum.SECKILL_OVER.getMsg());
            return "seckill_fail";
        }
        //重复秒杀判断
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(seckillUser.getId(), goodsId);
        if (seckillOrder != null) {
            model.addAttribute("errmsg", CodeMsgEnum.REPEATE_SECKILL.getMsg());
            return "seckill_fail";
        }
        // 减库存失败 因此创建订单返回null
        OrderInfo orderInfo = seckillService.seckill(seckillUser, goodsVO);
        if (orderInfo == null) {
            model.addAttribute("errmsg", CodeMsgEnum.SECKILL_OVER.getMsg());
            return "seckill_fail";
        }
        model.addAttribute("goods", goodsVO);
        model.addAttribute("orderInfo", orderInfo);
        return "order_detail";
    }


    /**
     * 秒杀ajax请求
     * @param seckillUser
     * @param goodsId
     * @return
     */
    @ResponseBody
    @PostMapping("/seckill")
    public Result doSeckill(SeckillUser seckillUser, long goodsId) {
        if (seckillUser == null) {
            return Result.error(CodeMsgEnum.SESSION_ERROR);
        }

        // 内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsgEnum.SECKILL_OVER);
        }

        // redis预减库存  这里针对已经秒杀过的人再来秒杀 会导致缓存中的库存和数据库中的库存不一致（商品有剩余） TODO...
        long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsgEnum.SECKILL_OVER);
        }

        //重复秒杀判断
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(seckillUser.getId(), goodsId);
        if (seckillOrder != null) {
            return Result.error(CodeMsgEnum.REPEATE_SECKILL);
        }
        // 异步下单
        SeckillMessage seckillMessage = new SeckillMessage();
        seckillMessage.setGoodsId(goodsId);
        seckillMessage.setSeckillUser(seckillUser);
        mqSender.sendSeckillMessage(seckillMessage);

        // 排队中
        return Result.success();
    }


    /**
     * 秒杀地址
     * @param goodsId
     * @param path
     * @return
     */
    @ResponseBody
    @PostMapping("/{path}/seckill")
    @AccessLimit(seconds = 5, maxCount = 5)
    public Result doPathSeckill(long goodsId, @PathVariable("path") String path) {
        SeckillUser seckillUser = CurrentUserContext.getUser();
        if (seckillUser == null) {
            return Result.error(CodeMsgEnum.SESSION_ERROR);
        }

        // 验证path
        boolean check = seckillService.checkPath(seckillUser, goodsId, path);
        if (!check) {
            return Result.error(CodeMsgEnum.REQUEST_ILLEGAL);
        }

        // 内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsgEnum.SECKILL_OVER);
        }

        // redis预减库存  这里针对已经秒杀过的人再来秒杀 会导致缓存中的库存和数据库中的库存不一致（商品有剩余） TODO...
        long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsgEnum.SECKILL_OVER);
        }

        //重复秒杀判断
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(seckillUser.getId(), goodsId);
        if (seckillOrder != null) {
            return Result.error(CodeMsgEnum.REPEATE_SECKILL);
        }
        // 异步下单
        SeckillMessage seckillMessage = new SeckillMessage();
        seckillMessage.setGoodsId(goodsId);
        seckillMessage.setSeckillUser(seckillUser);
        mqSender.sendSeckillMessage(seckillMessage);

        // 排队中
        return Result.success();
    }

    /**
     * 获取秒杀结果
     * @param goodsId
     * @return  orderId:成功返回订单号 -1:秒杀失败 已经秒杀完了 0:排队中 正在异步下单中
     */
    @ResponseBody
    @GetMapping("/result")
    public Result<Long> getSeckillResult(long goodsId) {
        long result = seckillService.getSeckillResult(CurrentUserContext.getUser().getId(), goodsId);
        return Result.success(result);
    }

    /**
     * 获取验证码图片 数学公式
     * @param response
     * @param goodsId
     * @return
     */
    @ResponseBody
    @GetMapping("/verifyCode")
    public Result<String> getVerifyCode(HttpServletResponse response, long goodsId) {
        response.setContentType("application/json;charset=UTF-8");
        BufferedImage image = seckillService.createVerifyCode(CurrentUserContext.getUser(), goodsId);
        try {
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "JPEG", outputStream);
            outputStream.flush();
            outputStream.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsgEnum.SECKILL_FAIL);
        }
    }

    /**
     * 获取秒杀随机字符串 用于秒杀地址随机变化
     * @param request
     * @param goodsId
     * @param verifyCode
     * @return
     */
    @GetMapping("/path")
    @ResponseBody
    @AccessLimit(seconds = 5, maxCount = 2) //5S秒最多点击2次
    public Result<String> getSeckillPath(HttpServletRequest request,
                                         @RequestParam("goodsId") long goodsId,
                                         @RequestParam(value = "verifyCode", defaultValue = "0") int verifyCode) {

        boolean check = seckillService.checkVerifyCode(CurrentUserContext.getUser(), goodsId, verifyCode);
        if (!check) {
            return Result.error(CodeMsgEnum.VERIFYCODE_ERROR);
        }
        String path = seckillService.createSeckillPath(CurrentUserContext.getUser(), goodsId);
        return Result.success(path);
    }


    /**
     * 重置Redis缓存数据
     * @return
     */
    @ResponseBody
    @GetMapping("/reset")
    public Result<Boolean> redisReset() {
        List<GoodsVO> goodsVOList = goodsService.listGoodsVO();
        for (GoodsVO goodsVO : goodsVOList) {
            goodsVO.setStockCount(10);
            redisService.set(GoodsKey.getSeckillGoodsStock, "" + goodsVO.getId(), 10);
            localOverMap.put(goodsVO.getId(), false);
        }
        redisService.delete(OrderKey.getSeckillOrderByUidGid);
        redisService.delete(SeckillKey.isGoodsOver);
        // 数据库 手动删除订单相关数据 恢复商品库存数据
        return Result.success(true);
    }
}
