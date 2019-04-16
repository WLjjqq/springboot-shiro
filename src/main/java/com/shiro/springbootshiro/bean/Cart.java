package com.shiro.springbootshiro.bean;

import java.io.Serializable;

public class Cart implements Serializable{
	private Integer cartId;
	private Goods cartGoods;
	private Integer cartNum;
	private Double cartPrice;
	private User cartUser;
	public Integer getCartId() {
		return cartId;
	}
	public void setCartId(Integer cartId) {
		this.cartId = cartId;
	}
	public Goods getCartGoods() {
		return cartGoods;
	}
	public void setCartGoods(Goods cartGoods) {
		this.cartGoods = cartGoods;
	}
	public Integer getCartNum() {
		return cartNum;
	}
	public void setCartNum(Integer cartNum) {
		this.cartNum = cartNum;
	}
	public Double getCartPrice() {
		return cartPrice;
	}
	public void setCartPrice(Double cartPrice) {
		this.cartPrice = cartPrice;
	}
	public User getCartUser() {
		return cartUser;
	}
	public void setCartUser(User cartUser) {
		this.cartUser = cartUser;
	}
	public Cart() {
		super();
	}
	public Cart(Integer cartId, Goods cartGoods, Integer cartNum, Double cartPrice, User cartUser) {
		super();
		this.cartId = cartId;
		this.cartGoods = cartGoods;
		this.cartNum = cartNum;
		this.cartPrice = cartPrice;
		this.cartUser = cartUser;
	}
	public Cart(Goods cartGoods, Integer cartNum, Double cartPrice, User cartUser) {
		super();
		this.cartGoods = cartGoods;
		this.cartNum = cartNum;
		this.cartPrice = cartPrice;
		this.cartUser = cartUser;
	}
}
