package com.shiro.springbootshiro.mapper;

import com.shiro.springbootshiro.bean.Cart;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartMapper {
	Cart findCartById(Integer id);
	Integer addCart(Cart cart);
	Integer deleteCart(Integer cartId);
	Integer updateCart(Cart cart);
	List<Cart> findCartListByUserId(@Param("userId") Integer userId);
	Cart findCartByUserId(@Param("userId") Integer userId, @Param("goodsId") Integer goodsId);
}
