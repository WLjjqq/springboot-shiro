package com.shiro.springbootshiro.mapper;

import com.shiro.springbootshiro.bean.Goods;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodsMapper {
	List<Goods> findAllBaseData();
	List<Goods> findAll();
	Integer saveGoods(Goods goods);
	Integer deleteGoods(Integer goods);
	Integer updateGoods(Goods goods);
	Goods findGoodsById(Integer id);
	List<Goods> findGoodsByType(Integer typeId);
	List<Goods> findHotGoods(Integer num);
	List<Goods> findGoodsLikeName(String name);
	List<Goods> findGoodsByVolume(Integer limit);
}
