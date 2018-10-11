package com.anthonyzero.seckill.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {
    int seconds(); //规定时间 秒为单位

    int maxCount(); //规定时间内最大访问次数

    boolean needLogin() default true; //是否需要登录
}
