package com.shiro.springbootshiro.mapper;

import com.shiro.springbootshiro.bean.Areas;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreasMapper {
	List<Areas> findAreasByCityId(String cityId);
	Areas findAreaByAreaName(String name, String cityId);
	Areas findAreaById(String id);
}
