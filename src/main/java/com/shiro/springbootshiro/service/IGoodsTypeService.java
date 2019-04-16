package com.shiro.springbootshiro.service;

import com.github.pagehelper.PageInfo;
import com.shiro.springbootshiro.bean.GoodsType;

import java.util.List;

public interface IGoodsTypeService {
	List<GoodsType> findAllType();
	
	PageInfo<GoodsType> findTypeBySplitPage(Integer page, Integer limit, String keyword);
	Integer updateGoodsType(GoodsType type);
	Integer addGoodsType(GoodsType type);
	Integer changeTypeState(Integer state, Integer typeId);
	Integer deleteGoodsType(Integer typeId);
}
