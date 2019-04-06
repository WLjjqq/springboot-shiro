package com.shiro.springbootshiro.mapper;


import com.shiro.springbootshiro.bean.UserRole;
import com.shiro.springbootshiro.util.MyMapper;

import java.util.List;

public interface UserRoleMapper extends MyMapper<UserRole> {
    public List<Integer> findUserIdByRoleId(Integer roleId);
}