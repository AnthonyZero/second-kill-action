package com.anthonyzero.seckill.controller;

import com.anthonyzero.seckill.common.core.Result;
import com.anthonyzero.seckill.common.enums.CodeMsgEnum;
import com.anthonyzero.seckill.domain.OrderInfo;
import com.anthonyzero.seckill.domain.SeckillOrder;
import com.anthonyzero.seckill.domain.SeckillUser;
import com.anthonyzero.seckill.service.GoodsService;
import com.anthonyzero.seckill.service.OrderService;
import com.anthonyzero.seckill.service.SeckillService;
import com.anthonyzero.seckill.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 秒杀
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SeckillService seckillService;
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
    public Result<OrderInfo> doSeckill(SeckillUser seckillUser, long goodsId) {
        if (seckillUser == null) {
            return Result.error(CodeMsgEnum.SESSION_ERROR);
        }

        //判断库存
        GoodsVO goodsVO = goodsService.getGoodsVOByGoodsId(goodsId);
        if (goodsVO == null) {
            return Result.error(CodeMsgEnum.GOODS_NOT_EXIST);
        }
        if (goodsVO.getStockCount() <= 0) {
            return Result.error(CodeMsgEnum.SECKILL_OVER);
        }
        //重复秒杀判断
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(seckillUser.getId(), goodsId);
        if (seckillOrder != null) {
            return Result.error(CodeMsgEnum.REPEATE_SECKILL);
        }
        // 减库存失败 因此创建订单返回null
        OrderInfo orderInfo = seckillService.seckill(seckillUser, goodsVO);
        if (orderInfo == null) {
            return Result.error(CodeMsgEnum.SECKILL_OVER);
        }
        return Result.success(orderInfo);
    }
}
