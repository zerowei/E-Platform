package com.mmall.common;

import com.mmall.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class RedisShardedPool {
    private static ShardedJedisPool pool;//ShardedJedis连接池
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperties("redis.max.total")); //最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperties("redis.max.idle"));//jedispool中最大的idle状态的jedis实例的个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperties("redis.min.idle"));//jedispool中最小的idle状态的jedis实例的个数

    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperties("redis.test.borrow"));//borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true。则得到的jedis实例可用。
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperties("redis.test.return"));//return一个jedis实例的时候，是否要进行验证操作，如果赋值true。则放回jedispool的jedis实例可用。

    private static String redis1Ip = PropertiesUtil.getProperties("redis1.ip");
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperties("redis1.port"));
    private static String redis2Ip = PropertiesUtil.getProperties("redis2.ip");
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperties("redis2.port"));


    private static void initPool(){

        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true);//连接耗尽的时候，是否阻塞，false会抛出异常，true阻塞直到超时。默认为true。

        JedisShardInfo info1 = new JedisShardInfo(redis1Ip, redis1Port, 1000 * 2);
        JedisShardInfo info2 = new JedisShardInfo(redis2Ip, redis2Port, 1000 * 2);
        List<JedisShardInfo> infoList = new ArrayList<>(Arrays.asList(info1, info2));

        pool = new ShardedJedisPool(config, infoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static{
        initPool();
    }

    public static ShardedJedis getJedis(){
        ShardedJedis jedis = null;
        if (pool == null) throw new RuntimeException("no Jedis Pool");
        try {
            jedis = pool.getResource();
        } catch (Exception e){
            log.error("get a jedis from pools failed", e);
        }
        return jedis;
    }


    public static void returnBrokenResource(ShardedJedis jedis){
        pool.returnBrokenResource(jedis);
    }

    public static void returnResource(ShardedJedis jedis){
        pool.returnResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis jedis = pool.getResource();
        for (int i = 0; i < 10; ++i) {
            jedis.set("key" + i, "" + i);
        }
        returnResource(jedis);
    }
}
