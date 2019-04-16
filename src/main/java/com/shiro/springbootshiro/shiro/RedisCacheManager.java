package com.shiro.springbootshiro.shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 自定义cache管理
 * CacheManager用于创建和维护一些在其他的Shiro组件中用到的Cache实例，维护这些Cache实例的生命周期。缓存用于存储那些从后端获取到的用户验证与权限控制方面的数据以提高性能，缓存是一等公民，在获取数据时，总是先从缓存中查找，如果没有再调用后端接口从其他数据源获取。
 */
public class RedisCacheManager implements CacheManager {
    private long cacheLive;
    private String cacheKeyPrefix;
    private RedisTemplate redisTemplate;


    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>();

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        Cache cache = this.caches.get(name);
        if (cache == null) {
            //自定义shiroCache
            cache = new ShiroRedisCache<K, V>(redisTemplate, cacheLive, cacheKeyPrefix);
            this.caches.put(name, cache);
        }
        return cache;
    }


    public void setCacheLive(long cacheLive) {
        this.cacheLive = cacheLive;
    }

    public void setCacheKeyPrefix(String cacheKeyPrefix) {
        this.cacheKeyPrefix = cacheKeyPrefix;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
