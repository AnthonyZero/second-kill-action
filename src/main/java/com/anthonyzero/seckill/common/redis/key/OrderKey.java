package com.anthonyzero.seckill.common.redis.key;

public class OrderKey extends AbstractPrefix {

    private OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static OrderKey getSeckillOrderByUidGid = new OrderKey(60*60,"soug");
}
