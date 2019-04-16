package com.shiro.springbootshiro.controller;

import com.shiro.springbootshiro.bean.*;
import com.shiro.springbootshiro.service.IAddressService;
import com.shiro.springbootshiro.service.IProCityAreaService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/address")
public class AddressController {
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IProCityAreaService pService;

	@RequestMapping("findAddrByUserId")
	@ResponseBody
	public List<Address> findAddrByUserId() {
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		List<Address> list = addressService.findAddressByUserId(user.getId());
		return list;
	}

	@RequestMapping("addAddress")
	@ResponseBody
	public String addAddress(Address addr, HttpServletRequest request) {
		String provinceId = addr.getAddrProvince();
		String cityId = addr.getAddrCity();
		String areaId = addr.getAddrArea();
		Provinces province = pService.findProvinceById(provinceId);
		Cities city = pService.findCityById(cityId);
		Areas area = pService.findAreaById(areaId);
		addr.setAddrProvince(province.getProvinceName());
		addr.setAddrCity(city.getCityName());
		addr.setAddrArea(area.getAreaName());
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		addr.setAddrUser(user.getId());
		Integer rs = addressService.addAddress(addr);
		if(rs>0){
			return "success";
		}else{
			return "fail";
		}
	}
	@RequestMapping("findAddressById")
	@ResponseBody
	public Address findAddrByAddrId(Integer addrId){
		Address addr = addressService.findAddresById(addrId);
		Provinces province = pService.findProvinceByName(addr.getAddrProvince());
		Cities city = pService.findCityByName(addr.getAddrCity(),province.getProvinceId());
		Areas area = pService.findAreaByName(addr.getAddrArea(),city.getCityId());
		addr.setAddrProvince(province.getProvinceId());
		addr.setAddrCity(city.getCityId());
		addr.setAddrArea(area.getAreaId());
		return addr;
	}
	@RequestMapping("modifyAddress")
	@ResponseBody
	public String modifyAddress(Address addr){
		String provinceId = addr.getAddrProvince();
		String cityId = addr.getAddrCity();
		String areaId = addr.getAddrArea();
		Provinces province = pService.findProvinceById(provinceId);
		Cities city = pService.findCityById(cityId);
		Areas area = pService.findAreaById(areaId);
		addr.setAddrProvince(province.getProvinceName());
		addr.setAddrCity(city.getCityName());
		addr.setAddrArea(area.getAreaName());
		Integer rs = addressService.updateAddress(addr);
		if(rs>0){
			return "success";
		}else{
			return "fail";
		}
	}
	@RequestMapping("deleteAddress")
	@ResponseBody
	public String deleteAddress(Integer addrId){
		Integer rs = addressService.deleteAddress(addrId);
		if(rs>0){
			return "success";
		}else{
			return "fail";
		}
	}
}
