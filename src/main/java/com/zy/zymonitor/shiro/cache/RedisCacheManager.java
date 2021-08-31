package com.zy.zymonitor.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

/**
 * @description 自定义Shiro缓存管理器
 * @author Ziyan_Jiang
 * @date 2021/7/4
 */
public class RedisCacheManager implements CacheManager {
    // s cacheName
    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) throws CacheException {
        System.out.println("=====New shiro cache=====" + cacheName);
        return new RedisCache<K, V>(cacheName);
    }
}
