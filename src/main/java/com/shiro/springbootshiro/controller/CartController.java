package com.shiro.springbootshiro.controller;

import com.shiro.springbootshiro.bean.Cart;
import com.shiro.springbootshiro.bean.Goods;
import com.shiro.springbootshiro.bean.User;
import com.shiro.springbootshiro.service.IAddressService;
import com.shiro.springbootshiro.service.ICartService;
import com.shiro.springbootshiro.service.IGoodsService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
	@Autowired
	private ICartService cartService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IAddressService addressService;
	
	@RequestMapping("addCart")
	public String addToCart(Integer goodsId, Integer num){
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		Cart cart = cartService.findCartByUserId(user.getId(), goodsId);
		if(cart!=null){
			cart.setCartNum(cart.getCartNum()+num);
			cartService.updateCart(cart);
		}else{
			Goods goods = goodsService.findById(goodsId);
			Cart c=new Cart(goods, num, goods.getGoodsPrice(), user);
			cartService.addGoodsToCart(c);
		}
		return "success";
	}
	@RequestMapping("findCartByUser")
	public List<Cart> findCartByUser(){
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		List<Cart> list = cartService.findCartByUserId(user.getId());
		return list;
	}
	
	@RequestMapping("deleteCart")
	public String deleteCart(Integer cartId){
		Integer rs = cartService.deleteCart(cartId);
		if(rs>0){
			return "success";
		}else{
			return "fail";
		}
	}
	
	@RequestMapping("reduceCartNum")
	public String reduceCartNum(Integer cartId){
		Cart cart = cartService.findCartById(cartId);
		cart.setCartNum(cart.getCartNum()-1);
		Integer rs = cartService.updateCart(cart);
		if(rs>0){
			return "success";
		}else{
			return "fail";
		}
	}
	@RequestMapping("addCartNum")
	public String addCartNum(Integer cartId){
		Cart cart = cartService.findCartById(cartId);
		cart.setCartNum(cart.getCartNum()+1);
		Integer rs = cartService.updateCart(cart);
		if(rs>0){
			return "success";
		}else{
			return "fail";
		}
	}

}
