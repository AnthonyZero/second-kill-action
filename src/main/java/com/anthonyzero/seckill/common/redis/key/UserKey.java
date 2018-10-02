package com.anthonyzero.seckill.common.redis.key;

/**
 * 用户模块
 */
public class UserKey extends AbstractPrefix{

    private UserKey(String prefix) {
        super(prefix);
    }

    public static UserKey userIdKey = new UserKey("id");
}
