package com.shiro.springbootshiro.mapper;

import com.shiro.springbootshiro.bean.Provinces;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProvincesMapper {
	List<Provinces> findAllProvince();
	Provinces findProByProName(String name);
	Provinces findProByProId(String id);
}
