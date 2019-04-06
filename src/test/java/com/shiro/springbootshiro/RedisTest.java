package com.shiro.springbootshiro;

import com.shiro.springbootshiro.bean.User;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.SerializeUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * 作用：测试redis是否连接上
 */
public class RedisTest extends SpringbootShiroApplicationTests{

    //@Autowired 使用这个注解出现错误：No qualifying bean of type 'org.springframework.data.redis.core.RedisTemplate<java.lang.Object, com.shiro.springbootshiro.bean.User>' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true)}
    @Resource
    RedisTemplate<Object,User> redisTemplate;
    @Test
    public void test01(){

        User user = new User();
        user.setId(2);
        user.setUsername("admin");
        user.setPassword("3esdfffdsdfergfwdfdsfsdfewer");
        user.setEnable(1);
        byte[] serialize = SerializeUtils.serialize(user);
        redisTemplate.opsForValue().set("u",user);
    }
}
