package com.shiro.springbootshiro;

import com.shiro.springbootshiro.bean.Resources;
import com.shiro.springbootshiro.mapper.ResourcesMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 作用：
 */
public class ResourcesTest extends SpringbootShiroApplicationTests {

    @Autowired
    ResourcesMapper resourcesMapper;

    @Test
    public void test(){
        List<Resources> resources = resourcesMapper.queryAll();
        for (Resources resources1 : resources) {
            System.out.println(resources1);
        }
    }
}
