package com.anthonyzero.seckill.vo;

import com.anthonyzero.seckill.domain.SeckillUser;
import lombok.Data;

@Data
public class GoodsDetailVO {

    private int seckillStatus = 0;
    private int remainSeconds = 0;
    private GoodsVO goods;
    private SeckillUser seckillUser;

}
