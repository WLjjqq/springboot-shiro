package com.shiro.springbootshiro;

import com.shiro.springbootshiro.bean.User;
import org.apache.shiro.session.Session;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.SerializeUtils;
import org.junit.Test;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 作用：测试redis是否连接上
 */
public class RedisTest extends SpringbootShiroApplicationTests{

    //@Autowired 使用这个注解出现错误：No qualifying bean of type 'org.springframework.data.redis.core.RedisTemplate<java.lang.Object, com.shiro.springbootshiro.bean.User>' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true)}
    @Resource
    RedisTemplate redisTemplate;
    @Test
    public void test01(){

        Object o = redisTemplate.opsForValue().get("shiro_redis_session:a861d2de-c14e-48d3-945a-ba72a8f96ac6");
        System.out.println(o);
    }
}
