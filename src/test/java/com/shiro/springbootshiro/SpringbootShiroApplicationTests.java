package com.shiro.springbootshiro;

import com.shiro.springbootshiro.bean.User;
import com.shiro.springbootshiro.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest

public class SpringbootShiroApplicationTests {

    @Autowired
    UserMapper userMapper;
    @Test
    public void contextLoads() {
    }

    @Test
    public void test01(){
        User user = new User();
        user.setEnable(0);
        user.setId(21);
        userMapper.update(user);
    }

}
