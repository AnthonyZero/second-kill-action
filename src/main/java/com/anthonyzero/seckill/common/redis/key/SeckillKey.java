package com.anthonyzero.seckill.common.redis.key;

public class SeckillKey extends AbstractPrefix {
	
	private SeckillKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}

	public static SeckillKey isGoodsOver = new SeckillKey(0, "go");
	public static SeckillKey getSeckillPath = new SeckillKey(60, "sp");
	public static SeckillKey getSeckillVerifyCode = new SeckillKey(300, "verifyCode");

}
