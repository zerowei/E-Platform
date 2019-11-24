package com.mmall.common;

import com.mmall.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class RedissonManager {

    private Redisson redisson = null;
    private Config config = new Config();

    private static String redis1Ip = PropertiesUtil.getProperties("redis1.ip");
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperties("redis1.port"));
    private static String redis2Ip = PropertiesUtil.getProperties("redis2.ip");
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperties("redis2.port"));

    public Redisson getRedisson() {
        return redisson;
    }

    @PostConstruct
    private void init() {
        String address = new StringBuilder().append(redis1Ip).append(":").append(redis1Port).toString();
        try {
            config.useSingleServer().setAddress(address);
            redisson = (Redisson) Redisson.create(config);
            log.info("Redisson初始化完成");
        } catch (Exception e) {
            log.error("Redisson初始化失败", e);
        }

    }
}
