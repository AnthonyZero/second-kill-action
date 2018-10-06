package com.anthonyzero.seckill.controller;

import com.anthonyzero.seckill.common.core.Result;
import com.anthonyzero.seckill.common.redis.RedisService;
import com.anthonyzero.seckill.common.redis.key.GoodsKey;
import com.anthonyzero.seckill.domain.SeckillUser;
import com.anthonyzero.seckill.service.GoodsService;
import com.anthonyzero.seckill.vo.GoodsDetailVO;
import com.anthonyzero.seckill.vo.GoodsVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品
 */
@RequestMapping("/goods")
@Controller
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RedisService redisService;

    /**
     * 商品列表 （页面缓存）
     * @param model
     * @param seckillUser
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/list", produces = "text/html")
    public String goodsList(HttpServletRequest request, HttpServletResponse response, Model model, SeckillUser seckillUser) {
        model.addAttribute("user", seckillUser);

        List<GoodsVO> list = goodsService.listGoodsVO();
        model.addAttribute("goodsList", list);
//        return "goods_list";
        //取缓存
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class); //默认60秒过期
        if (StringUtils.isNotEmpty(html)) {
            return html;
        }
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap(), applicationContext);
        //手动渲染goods_list 商品列表页面
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", springWebContext);
        if (StringUtils.isNotEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;
    }

    /**
     * 商品详情页 （页面缓存）
     * @param goodsId
     * @param user
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/to_detail", produces = "text/html")
    public String goodsDetail(HttpServletRequest request, HttpServletResponse response, Model model, SeckillUser user, long goodsId) {
        // 取缓存
        String html = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        GoodsVO goodsVO = goodsService.getGoodsVOByGoodsId(goodsId);

        long startAt = goodsVO.getStartTime().getTime(); //秒杀开始时间
        long endAt = goodsVO.getEndTime().getTime(); //秒杀结束时间

        Map<String, Integer> map = checkTime(startAt, endAt);

        GoodsDetailVO goodsDetailVO = new GoodsDetailVO();
        goodsDetailVO.setGoods(goodsVO);
        goodsDetailVO.setSeckillUser(user);
        goodsDetailVO.setRemainSeconds(map.get("remainSeconds"));
        goodsDetailVO.setSeckillStatus(map.get("seckillStatus"));

        model.addAttribute("goods", goodsDetailVO);

        SpringWebContext ctx = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(),
                model.asMap(), applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);

        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsDetail, "" + goodsId, html);
        }

        return html;
//        return "goods_detail";
    }

    private Map<String, Integer> checkTime(long startAt, long endAt) {
        Map<String, Integer> map = new HashMap<>();

        long now = System.currentTimeMillis();
        int seckillStatus = 0;
        int remainSeconds = 0;

        if (now < startAt) {
            // 秒杀还没开始，倒计时
            seckillStatus = 0;
            remainSeconds = (int) ((startAt - now) / 1000); //倒计时
        } else if (now > endAt) {
            // 秒杀已经结束
            seckillStatus = 2;
            remainSeconds = -1;
        } else {
            // 秒杀进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }
        map.put("seckillStatus", seckillStatus);
        map.put("remainSeconds", remainSeconds);
        return map;

    }
}
