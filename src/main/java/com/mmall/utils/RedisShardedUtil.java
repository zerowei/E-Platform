package com.mmall.utils;

import com.mmall.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;

@Slf4j
public class RedisShardedUtil {

    public static Long expire(String key, int expireTime){
        ShardedJedis jedis = null;
        Long res = null;
        try {
            jedis = RedisShardedPool.getJedis();
            res = jedis.expire(key, expireTime);
        } catch (Exception e) {
            log.error("expire key:{} failed", key, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return res;
        }
        RedisShardedPool.returnResource(jedis);
        return res;
    }

    public static String setEx(String key, String value, int expireTime){
        ShardedJedis jedis = null;
        String res;
        try {
            jedis = RedisShardedPool.getJedis();
            res = jedis.setex(key, expireTime, value);
        } catch (Exception e) {
            log.error("setex key-value: {}-{} error", key, value, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return null;
        }
        RedisShardedPool.returnResource(jedis);
        return res;
    }

    public static String set(String key, String value){
        ShardedJedis jedis = null;
        String res;
        try {
            jedis = RedisShardedPool.getJedis();
            res = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key-value: {}-{} failed", key, value, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return null;
        }
        RedisShardedPool.returnResource(jedis);
        return res;
    }

    public static String get(String key){
        ShardedJedis jedis = null;
        String res;
        try {
            jedis = RedisShardedPool.getJedis();
            res = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} failed", key, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return null;
        }
        RedisShardedPool.returnResource(jedis);
        return res;
    }

    public static Long del(String key){
        ShardedJedis jedis = null;
        Long res = null;
        try {
            jedis = RedisShardedPool.getJedis();
            res = jedis.del(key);
        } catch (Exception e) {
            log.error("delete key:{} failed", key, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return res;
        }
        RedisShardedPool.returnResource(jedis);
        return res;
    }

}
