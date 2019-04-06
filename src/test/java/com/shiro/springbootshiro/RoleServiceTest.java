package com.shiro.springbootshiro;

import com.github.pagehelper.PageInfo;
import com.shiro.springbootshiro.bean.Role;
import com.shiro.springbootshiro.mapper.RoleMapper;
import com.shiro.springbootshiro.service.RoleService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 作用：测试角色
 */
public class RoleServiceTest extends SpringbootShiroApplicationTests {

    @Autowired
    RoleService roleService;

    @Autowired
    RoleMapper roleMapper;
    @Test
    public void test(){
        Role role=new Role();
        role.setId(1);
        PageInfo<Role> rolePageInfo = roleService.selectByPage(role, 1, 10);
        System.out.println(rolePageInfo);
    }
}
