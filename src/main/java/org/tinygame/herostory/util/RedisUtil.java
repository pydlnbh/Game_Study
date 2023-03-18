package org.tinygame.herostory.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis连接工具类
 */
public final class RedisUtil {

    /**
     * 单例对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);

    /**
     * Redis 连接池
     */
    private static JedisPool _jedisPoll = null;

    /**
     * 私有化默认构造方法
     */
    private RedisUtil() {
    }

    /**
     * 初始化
     */
    public static void init() {
        try {
            _jedisPoll = new JedisPool("127.0.0.1", 6379);

            LOGGER.info("Redis 连接成功");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 获取 Redis 实例
     *
     * @return Redis 实例
     */
    public static Jedis getRedis() {
        if (_jedisPoll == null) {
            throw new RuntimeException("_jedisPool 尚未初始化");
        }

        Jedis redis = _jedisPoll.getResource();
        // redis.auth("root");

        return redis;
    }
}
