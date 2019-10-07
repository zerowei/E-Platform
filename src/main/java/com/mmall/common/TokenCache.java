package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TokenCache {
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    private static LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(100000).expireAfterAccess(1, TimeUnit.DAYS)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });
    public static void setCache(String key, String value) {
        loadingCache.put(key, value);
    }

    public static String getCache(String key) {
        try {
            String value = loadingCache.get(key);
            if ("null".equals(value)) return null;
            return value;
        } catch(Exception e) {
            logger.error("Ops! Have problem finding the token..", e);
        }
        return null;
    }
}
