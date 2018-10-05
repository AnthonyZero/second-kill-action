package com.anthonyzero.seckill.controller;

import com.anthonyzero.seckill.common.core.Result;
import com.anthonyzero.seckill.domain.SeckillUser;
import com.anthonyzero.seckill.service.GoodsService;
import com.anthonyzero.seckill.vo.GoodsDetailVO;
import com.anthonyzero.seckill.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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

    /**
     * 商品列表
     * @param model
     * @param seckillUser
     * @return
     */
    @GetMapping("/list")
    public String goodsList(Model model, SeckillUser seckillUser) {
        model.addAttribute("user", seckillUser);

        List<GoodsVO> list = goodsService.listGoodsVO();
        model.addAttribute("goodsList", list);
        return "goods_list";
    }

    /**
     *
     * @param goodsId
     * @param user
     * @return
     */
    @GetMapping("/to_detail")
    public String goodsDetail(Model model, SeckillUser user, long goodsId) {
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
        return "goods_detail";
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
