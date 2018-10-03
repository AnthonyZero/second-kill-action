package com.anthonyzero.seckill.controller;

import com.anthonyzero.seckill.domain.SeckillUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 商品
 */
@RequestMapping("/goods")
@Controller
public class GoodsController {


    /**
     * 商品列表
     * @param model
     * @param seckillUser
     * @return
     */
    @GetMapping("/list")
    public String goodsList(Model model, SeckillUser seckillUser) {
        model.addAttribute("user", seckillUser);
        return "goods_list";
    }
}
