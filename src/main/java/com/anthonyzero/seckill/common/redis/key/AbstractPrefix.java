package com.anthonyzero.seckill.common.redis.key;

/**
 * 统一redis前缀 所有业务模块需要继承AbstractPrefix
 */
public abstract class AbstractPrefix implements KeyPrefix{

    private int expireSeconds;

    private String prefix;

    /**
     * 不过期的键值
     * @param prefix
     */
    public AbstractPrefix(String prefix) {
        this.expireSeconds = 0;
        this.prefix = prefix;
    }

    /**
     * 设置过期时间的键值
     * @param expireSeconds
     * @param prefix
     */
    public AbstractPrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName(); //保证各业务模块key键独立不重复
        return className + ":" + prefix;
    }
}
