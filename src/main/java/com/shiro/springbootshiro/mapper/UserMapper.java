package com.shiro.springbootshiro.mapper;

import com.shiro.springbootshiro.bean.User;
import com.shiro.springbootshiro.util.MyMapper;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends MyMapper<User> {
   User findByUserName(@Param("username")String username);
   int update(User user);
   User findUserById(Integer id);
}