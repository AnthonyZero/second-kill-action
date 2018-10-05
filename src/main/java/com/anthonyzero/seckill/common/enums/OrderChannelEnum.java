package com.anthonyzero.seckill.common.enums;

import lombok.Getter;

/**
 * 订单渠道
 */
@Getter
public enum OrderChannelEnum {
    PC(1, "pc"),
    ANDROID(2, "android"),
    IOS(3, "ios");
    private int code;
    private String channel;

    OrderChannelEnum(int code, String channel) {
        this.code = code;
        this.channel = channel;
    }
}
