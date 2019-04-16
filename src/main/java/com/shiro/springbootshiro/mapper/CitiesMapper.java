package com.shiro.springbootshiro.mapper;

import com.shiro.springbootshiro.bean.Cities;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitiesMapper {
	List<Cities> findCitiesByProvinceId(String provinceId);
	Cities findCityByCityName(String name, String provinceId);
	Cities findCityById(String id);
}
