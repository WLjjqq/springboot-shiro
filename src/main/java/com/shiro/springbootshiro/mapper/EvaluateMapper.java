package com.shiro.springbootshiro.mapper;

import com.shiro.springbootshiro.bean.Evaluate;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluateMapper {
	Evaluate findEvaById(Integer id);
	Integer addEvaluate(Evaluate eva);
	Integer deleteEvaluate(Integer id);
	Integer updateEvaluate(Evaluate eva);
	List<Evaluate> findEvaByGoodsId(@Param(value = "goodsId") Integer goodsId);
	List<Evaluate> findEvaByUserId(Integer id);
	List<Evaluate> findAllEvalute();
	List<Evaluate> findAllEvaluteLikeContent(String keyword);
}
