package com.anthonyzero.seckill.vo;

import com.anthonyzero.seckill.domain.OrderInfo;
import lombok.Data;

@Data
public class OrderDetailVO {
    private GoodsVO goods;
    private OrderInfo order;
}
