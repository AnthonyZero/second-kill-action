package com.anthonyzero.seckill.common.enums;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum CodeMsgEnum {
    //通用异常 5001xx
    SERVER_ERROR(500100, "服务端异常！"),
    BIND_ERROR(500101, "参数校验异常：%s!"),
    REQUEST_ILLEGAL(500102, "请求非法！"),
    ACCESS_LIMIT_REACHED(500103, "访问太频繁！"),

    //登录模块 5002XX
    SESSION_ERROR(500210, "Session不存在或者已经失效！"),
    PASSWORD_EMPTY(500211, "登录密码不能为空！"),
    MOBILE_EMPTY(500212, "手机号码不能为空！"),
    MOBILE_ERROR(500213, "手机号码格式错误！"),
    MOBILE_NOT_EXIST(500214, "用户不存在！"),
    PASSWORD_ERROR(500215, "密码错误！"),

    // 商品模块 5003XX

    // 订单模块 5004XX
    ORDER_NOT_EXIST(500400, "订单不存在！"),

    // 秒杀模块 5005XX
    SECKILL_OVER(500500, "商品已经秒杀完毕！"),
    REPEATE_SECKILL(500501, "不能重复秒杀！"),
    SECKILL_FAIL(500502, "秒杀失败！"),

    ;
    private int code;
    private String msg;

    CodeMsgEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
