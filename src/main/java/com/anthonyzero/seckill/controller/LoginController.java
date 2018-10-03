package com.anthonyzero.seckill.controller;

import com.anthonyzero.seckill.common.core.Result;
import com.anthonyzero.seckill.service.SeckillUserService;
import com.anthonyzero.seckill.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 登陆
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private SeckillUserService seckillUserService;

    /**
     * 登陆页面
     * @return
     */
    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @PostMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVO loginVO){
        seckillUserService.login(response, loginVO);
        return Result.success();
    }
}
