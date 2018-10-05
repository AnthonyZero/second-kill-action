package com.anthonyzero.seckill.common.enums;

import lombok.Getter;

/**
 * 订单状态
 */
@Getter
public enum OrderStatusEnum {
    NEW(0, "新建未支付"),
    PAY_DONE(1, "已支付"),
    DELIVER_DONE(2, "已发货"),
    TAKE_DONE(3, "已收货"),
    REFUND_DONE(4, "已退款"),
    FINISHED(5,"已完成");
    private int code;
    private String msg;

    OrderStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
