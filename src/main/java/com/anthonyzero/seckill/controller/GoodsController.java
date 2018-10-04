package com.anthonyzero.seckill.controller;

import com.anthonyzero.seckill.domain.SeckillUser;
import com.anthonyzero.seckill.service.GoodsService;
import com.anthonyzero.seckill.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
}
