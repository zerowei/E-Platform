package com.mmall.utils;

import com.mmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Slf4j
public class RedisUtil {
    
    public static Long expire(String key, int expireTime){
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = RedisPool.getJedis();
            res = jedis.expire(key, expireTime);
        } catch (Exception e) {
            log.error("expire key:{} failed", key, e);
            RedisPool.returnBrokenResource(jedis);
            return res;
        }
        RedisPool.returnResource(jedis);
        return res;
    }
    
    public static String setEx(String key, String value, int expireTime){
        Jedis jedis = null;
        String res;
        try {
            jedis = RedisPool.getJedis();
            res = jedis.setex(key, expireTime, value);
        } catch (Exception e) {
            log.error("setex key-value: {}-{} error", key, value, e);
            RedisPool.returnBrokenResource(jedis);
            return null;
        }
        RedisPool.returnResource(jedis);
        return res;
    }

    public static String set(String key, String value){
        Jedis jedis = null;
        String res;
        try {
            jedis = RedisPool.getJedis();
            res = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key-value: {}-{} failed", key, value, e);
            RedisPool.returnBrokenResource(jedis);
            return null;
        }
        RedisPool.returnResource(jedis);
        return res;
    }

    public static String get(String key){
        Jedis jedis = null;
        String res;
        try {
            jedis = RedisPool.getJedis();
            res = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} failed", key, e);
            RedisPool.returnBrokenResource(jedis);
            return null;
        }
        RedisPool.returnResource(jedis);
        return res;
    }

    public static Long del(String key){
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = RedisPool.getJedis();
            res = jedis.del(key);
        } catch (Exception e) {
            log.error("delete key:{} failed", key, e);
            RedisPool.returnBrokenResource(jedis);
            return res;
        }
        RedisPool.returnResource(jedis);
        return res;
    }

}
