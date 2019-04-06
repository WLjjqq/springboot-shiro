package com.shiro.springbootshiro.mapper;

import com.shiro.springbootshiro.bean.Role;
import com.shiro.springbootshiro.util.MyMapper;

import java.util.List;

public interface RoleMapper extends MyMapper<Role> {
    public List<Role> queryRoleListWithSelected(Integer id);
}