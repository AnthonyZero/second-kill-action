package com.anthonyzero.seckill.common.redis.key;

public class AccessKey extends AbstractPrefix {
	private AccessKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}

	public static AccessKey access = new AccessKey(5, "access");

	public static AccessKey withExpire(int expireSeconds) {
		return new AccessKey(expireSeconds, "access");
	}
}
