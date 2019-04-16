package com.shiro.springbootshiro;

import com.shiro.springbootshiro.bean.Cart;
import com.shiro.springbootshiro.mapper.CartMapper;
import com.shiro.springbootshiro.service.ICartService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 作用：
 */
public class CartTest extends SpringbootShiroApplicationTests {

    @Autowired
    ICartService cartService;

    @Autowired
    CartMapper cartMapper;
    @Test
    public void test() {
        Cart cartById = cartMapper.findCartById(1);
        System.out.println(cartById);
    }

}
