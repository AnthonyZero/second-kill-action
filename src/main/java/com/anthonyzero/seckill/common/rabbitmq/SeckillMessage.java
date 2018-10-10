package com.anthonyzero.seckill.common.rabbitmq;


import com.anthonyzero.seckill.domain.SeckillUser;
import lombok.Data;

/**
 * 秒杀队列信息
 */
@Data
public class SeckillMessage {
	private SeckillUser seckillUser;
	private long goodsId;

}
