package com.shiro.springbootshiro.service;

import com.shiro.springbootshiro.bean.Address;
import com.shiro.springbootshiro.bean.Areas;
import com.shiro.springbootshiro.bean.Cities;
import com.shiro.springbootshiro.bean.Provinces;

import java.util.List;

public interface IAddressService {
	List<Address> findAddressByUserId(Integer userId);
	Address findAddresById(Integer id);
	Provinces findProByProByName(String name);
	Cities findCityByCityName(String name, String provinceId);
	Areas findAreaByAreaName(String name, String cityId);
	Integer addAddress(Address addr);
	Integer updateAddress(Address addr);
	Integer deleteAddress(Integer addrId);
}
