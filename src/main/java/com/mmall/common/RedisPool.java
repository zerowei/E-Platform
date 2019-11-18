package com.mmall.common;

import com.mmall.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Slf4j
public class RedisPool {

    private static JedisPool pool;//jedis连接池
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperties("redis.max.total")); //最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperties("redis.max.idle"));//jedispool中最大的idle状态的jedis实例的个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperties("redis.min.idle"));//jedispool中最小的idle状态的jedis实例的个数

    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperties("redis.test.borrow"));//borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true。则得到的jedis实例可用。
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperties("redis.test.return"));//return一个jedis实例的时候，是否要进行验证操作，如果赋值true。则放回jedispool的jedis实例可用。

    private static String redisIp = PropertiesUtil.getProperties("redis.ip");
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperties("redis.port"));


    private static void initPool(){

        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true);//连接耗尽的时候，是否阻塞，false会抛出异常，true阻塞直到超时。默认为true。

        pool = new JedisPool(config, redisIp, redisPort,1000*2);
    }

    static{
        initPool();
    }

    public static Jedis getJedis(){
        Jedis jedis = null;
        if (pool == null) throw new RuntimeException("no Jedis Pool");
        try {
            jedis = pool.getResource();
        } catch (Exception e){
            log.error("get a jedis from pools failed", e);
        }
        return jedis;
    }


    public static void returnBrokenResource(Jedis jedis){
        pool.returnBrokenResource(jedis);
    }

    public static void returnResource(Jedis jedis){
        pool.returnResource(jedis);
    }
}
