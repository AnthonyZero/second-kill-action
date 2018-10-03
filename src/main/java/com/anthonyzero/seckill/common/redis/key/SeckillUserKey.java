package com.anthonyzero.seckill.common.redis.key;

public class SeckillUserKey extends AbstractPrefix {

	public static final int TOKEN_EXPIRE = 2 * 24 * 60 * 60;

	public SeckillUserKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}

	public static SeckillUserKey token = new SeckillUserKey(TOKEN_EXPIRE, "tk");
	public static SeckillUserKey getById = new SeckillUserKey(0, "id");

}
