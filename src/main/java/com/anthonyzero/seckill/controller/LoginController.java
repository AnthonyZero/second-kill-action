package com.anthonyzero.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登陆
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    /**
     * 登陆页面
     * @return
     */
    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }
}
