package com.mmall.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
    private static Properties properties;

    static {
        properties = new Properties();
        try {
            InputStream input = PropertiesUtil.class.getClassLoader().getResourceAsStream("mmall.properties");
            properties.load(input);
        } catch (Exception e) {
            logger.error("读取配置文件出错", e);
        }
    }

    public static String getProperties(String key) {
        return properties.getProperty(key);
    }


}
