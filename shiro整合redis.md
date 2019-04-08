# Shiro整合redis
# 一：需要重写cache、cacheManager、SessionDAO和初始化redis配置。
## 1)：重写cache
```Java
package com.shiro.springbootshiro.shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 自定义redisCache
 */
public class ShiroRedisCache<K, V> implements Cache<K, V> {
    private long cacheLive;
    private String cacheKeyPrefix;
    private RedisTemplate redisTemplate;

    @Override
    public V get(K k) throws CacheException {
        return (V) this.redisTemplate.opsForValue().get(this.getRedisCacheKey(k));
    }

    @Override
    public V put(K k, V v) throws CacheException {
        redisTemplate.opsForValue().set(this.getRedisCacheKey(k), v, cacheLive, TimeUnit.MINUTES);
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        V obj = (V) this.redisTemplate.opsForValue().get(this.getRedisCacheKey(k));
        redisTemplate.delete(this.getRedisCacheKey(k));
        return obj;
    }

    @Override
    public void clear() throws CacheException {
        Set keys = this.redisTemplate.keys(this.cacheKeyPrefix + "*");
        if (null != keys && keys.size() > 0) {
            Iterator itera = keys.iterator();
            this.redisTemplate.delete(itera.next());
        }
    }

    @Override
    public int size() {
        Set<K> keys = this.redisTemplate.keys(this.cacheKeyPrefix + "*");
        return keys.size();
    }

    @Override
    public Set<K> keys() {
        return this.redisTemplate.keys(this.cacheKeyPrefix + "*");
    }

    @Override
    public Collection<V> values() {
        Set<K> keys = this.redisTemplate.keys(this.cacheKeyPrefix + "*");
        Set<V> values = new HashSet<V>(keys.size());
        for (K key : keys) {
            values.add((V) this.redisTemplate.opsForValue().get(this.getRedisCacheKey(key)));
        }
        return values;
    }

    private String getRedisCacheKey(K key) {
        Object redisKey = this.getStringRedisKey(key);
        if (redisKey instanceof String) {
            return this.cacheKeyPrefix + redisKey;
        } else {
            return String.valueOf(redisKey);
        }
    }

    private Object getStringRedisKey(K key) {
        Object redisKey;
        if (key instanceof PrincipalCollection) {
            redisKey = this.getRedisKeyFromPrincipalCollection((PrincipalCollection) key);
        } else {
            redisKey = key.toString();
        }
        return redisKey;
    }

    private Object getRedisKeyFromPrincipalCollection(PrincipalCollection key) {
        List realmNames = this.getRealmNames(key);
        Collections.sort(realmNames);
        Object redisKey = this.joinRealmNames(realmNames);
        return redisKey;
    }

    private List<String> getRealmNames(PrincipalCollection key) {
        ArrayList realmArr = new ArrayList();
        Set realmNames = key.getRealmNames();
        Iterator i$ = realmNames.iterator();
        while (i$.hasNext()) {
            String realmName = (String) i$.next();
            realmArr.add(realmName);
        }
        return realmArr;
    }

    private Object joinRealmNames(List<String> realmArr) {
        StringBuilder redisKeyBuilder = new StringBuilder();
        for (int i = 0; i < realmArr.size(); ++i) {
            String s = realmArr.get(i);
            redisKeyBuilder.append(s);
        }
        String redisKey = redisKeyBuilder.toString();
        return redisKey;
    }


    public ShiroRedisCache(RedisTemplate redisTemplate, long cacheLive, String cachePrefix) {
        this.redisTemplate = redisTemplate;
        this.cacheLive = cacheLive;
        this.cacheKeyPrefix = cachePrefix;
    }
}

```
## 2): 重写cacheManager
```Java
package com.shiro.springbootshiro.shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 自定义cache管理
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

```
## 3): 重写SessionDAO
```Java
package com.shiro.springbootshiro.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 自定义sessionDAO
 */
public class RedisSessionDAO extends AbstractSessionDAO {
    private long sessionLive;
    private String sessionKeyPrefix;
    private RedisTemplate redisTemplate;


    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        redisTemplate.opsForValue().set(sessionKeyPrefix + sessionId, session, sessionLive, TimeUnit.MINUTES);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        return (Session) redisTemplate.opsForValue().get(sessionKeyPrefix + sessionId);
    }

    @Override
    public void update(Session session) {
        this.redisTemplate.opsForValue().set(sessionKeyPrefix + session.getId(), session, sessionLive, TimeUnit.MINUTES);
    }

    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            return;
        }
        this.redisTemplate.delete(sessionKeyPrefix + session.getId());
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<Session> sessions = new HashSet<Session>();
        Set<Serializable> keys = redisTemplate.keys(sessionKeyPrefix + "*");
        if (keys != null && keys.size() > 0) {
            for (Serializable key : keys) {
                Session s = (Session) redisTemplate.opsForValue().get(key);
                sessions.add(s);
            }
        }
        return sessions;
    }


    public void setSessionLive(long sessionLive) {
        this.sessionLive = sessionLive;
    }

    public void setSessionKeyPrefix(String sessionKeyPrefix) {
        this.sessionKeyPrefix = sessionKeyPrefix;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}

```
## 4): 重写SessionManager
```Java
package com.shiro.springbootshiro.shiro;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

/**
 * 作用：自定义MySessionManager重写DefaultWebSessionManager类
 */
public class MySessionManager extends DefaultWebSessionManager {
    //前端请求头传这个
    private static final String AUTHORIZATION = "Authorization";

    private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";

    public MySessionManager() {
        super();
    }

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        String id = WebUtils.toHttp(request).getHeader(AUTHORIZATION);
        //如果请求头中有 Authorization 则其值为sessionId
        if (!StringUtils.isEmpty(id)) {
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return id;
        } else {
            //否则按默认规则从cookie取sessionId
            return super.getSessionId(request, response);
        }
    }
}

```
## 5): 初始化redis配置
```Java
package com.shiro.springbootshiro.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.*;


@Configuration   //定义配置类
@EnableCaching  //开启注解
public class RedisConfig {

    @Bean
    public  RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        //解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        // 配置序列化（解决乱码的问题）
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();

        RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
        return redisCacheManager;
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(mapper);

        template.setValueSerializer(serializer);
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(factory);
        return stringRedisTemplate;
    }

    /**
     * session的缓存。使用JDK的序列化，序列化成字节码。如果转换成JSON的序列化的话，反序列化会出错。
     * @param redisConnectionFactory
     * @return
     */
    @Bean(name = "redisTemplateSession")
    RedisTemplate<String, Object> redisTemplateSession(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new StringRedisTemplate(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
        return redisTemplate;
    }

}

```
## 6): 配置ShiroConfig
```Java
/**
     * 自定义shiro cache管理
     *
     * @return
     */
    @Bean(name = "redisCacheManager")
    public RedisCacheManager redisCacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        //cache过期时间及前缀
        redisCacheManager.setCacheLive(cacheLive);
        redisCacheManager.setCacheKeyPrefix(cachePrefix);
        redisCacheManager.setRedisTemplate(redisTemplate);
        return redisCacheManager;
    }
    /**
     * Session ID生成管理器
     *
     * @return
     */
    @Bean(name = "sessionIdGenerator")
    public JavaUuidSessionIdGenerator sessionIdGenerator() {
        JavaUuidSessionIdGenerator sessionIdGenerator = new JavaUuidSessionIdGenerator();
        return sessionIdGenerator;
    }
    /**
     * 自定义shiro session
     *
     * @return
     */
    @Bean(name = "redisSessionDAO")
    public RedisSessionDAO redisSessionDAO(JavaUuidSessionIdGenerator sessionIdGenerator, RedisTemplate redisTemplateSession) {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setSessionIdGenerator(sessionIdGenerator);
        //session过期时间及前缀
        redisSessionDAO.setSessionLive(sessionLive);
        redisSessionDAO.setSessionKeyPrefix(sessionPrefix);
        redisSessionDAO.setRedisTemplate(redisTemplateSession);
        return redisSessionDAO;
    }
    /**
     * 自定义sessionManager
     *
     * @return
     */
    @Bean(name = "sessionManager")
    public SessionManager sessionManager(RedisSessionDAO redisSessionDAO) {
        MySessionManager mySessionManager = new MySessionManager();
        mySessionManager.setSessionDAO(redisSessionDAO);
        return mySessionManager;
    }
    /**
     * Shiro的安全管理，主要是身份认证的管理，缓存管理，cookie管理，
     * 所以在实际开发中我们主要是和SecurityManager进行打交道的。
     * 创建DefaultWebSecurityManager
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm,
                                                                  @Qualifier("redisCacheManager") RedisCacheManager redisCacheManager,
                                                                  @Qualifier("sessionManager") SessionManager sessionManager
    ){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 关联realm
        securityManager.setRealm(userRealm);
        //配置redis缓存
        securityManager.setCacheManager(redisCacheManager);
        //配置自定义session管理，使用redis
        securityManager.setSessionManager(sessionManager);
        return securityManager;
    }
    ```
