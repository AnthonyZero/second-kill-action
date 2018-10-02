package com.anthonyzero.seckill.common.redis.key;

public class OrderKey extends AbstractPrefix {

    private OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey seckillOrder = new OrderKey("order");
}
