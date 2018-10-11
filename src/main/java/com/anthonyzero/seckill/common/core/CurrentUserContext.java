package com.anthonyzero.seckill.common.core;

import com.anthonyzero.seckill.domain.SeckillUser;

/**
 * 用户上下文
 */
public class CurrentUserContext {

    // SeckillUser跟当前线程绑定
    private static ThreadLocal<SeckillUser> threadLocal = new ThreadLocal<>();

    public static void setUser(SeckillUser seckillUser) {
        threadLocal.set(seckillUser);
    }

    public static SeckillUser getUser() {
        return threadLocal.get();
    }
}
