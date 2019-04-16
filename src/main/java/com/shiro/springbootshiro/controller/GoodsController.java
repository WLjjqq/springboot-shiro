package com.shiro.springbootshiro.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.shiro.springbootshiro.bean.Evaluate;
import com.shiro.springbootshiro.bean.Goods;
import com.shiro.springbootshiro.service.IEvaluateService;
import com.shiro.springbootshiro.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {
	@Autowired
	private IGoodsService goodsService;

	@Autowired
	private IEvaluateService evaluateService;
	

	
	@RequestMapping("{goodsId}")
	public String findAllGoods(@PathVariable("goodsId")Integer goodsId, Model model){
		Goods goods = goodsService.findById(goodsId);
		List<Evaluate> evaList = evaluateService.findEvaluateByGoodsId(goodsId);
		model.addAttribute("goods", goods);
		model.addAttribute("evaList", evaList);
		return "detail";
	}
	
	@RequestMapping("preUpdate")
	public String preUpdate(Integer goodsId, Model model){
		Goods goods = goodsService.findById(goodsId);
		model.addAttribute("goods", goods);
		return "update";
	}
	
	@RequestMapping("findBySplitPage")
	@ResponseBody
	public JSONObject findBySplitPage(Integer page, Integer limit, String keyword){
		PageInfo<Goods> info = goodsService.findBySplitPage(page, limit,keyword);
		JSONObject obj=new JSONObject();
		obj.put("msg", "");
		obj.put("code", 0);
		obj.put("count", info.getTotal());
		obj.put("data", info.getList());
		return obj;
	}



	@RequestMapping("findGoodsByType")
	@ResponseBody
	public List<Goods> findGoodsByType(Integer typeId){
		 List<Goods> list = goodsService.findGoodsByType(typeId);
		 return list;
	}


	@RequestMapping("searchPre")
	@ResponseBody
	public List<Goods> searchPreGoods(String keyword){
		List<Goods> list = goodsService.findGoodsLikeName(keyword);
		return list;
	}
	@RequestMapping("delete")
	@ResponseBody
	public String deleteGoods(Integer goodsId){
		Integer rs = goodsService.deleteGoods(goodsId);
		if(rs>0){
			return "success";
		}else{
			return "fail";
		}
	}
	@RequestMapping("updateGoods")
	@ResponseBody
	public String updateGoods(Goods goods){
		Integer rs = goodsService.update(goods);
		if(rs>0){
			return "success";
		}else{
			return "fail";
		}
	}

	@RequestMapping("addGoods")
	@ResponseBody
	public String addGoods(Goods goods){
		Integer rs = goodsService.addGoods(goods);
		if(rs>0){
			return "success";
		}else{
			return "fail";
		}
	}
	@RequestMapping("batchDelete")
	@ResponseBody
	public String batchDelete(String batchId){
		Integer rs=0;
		String[] id = batchId.split(",");
		for (String s : id) {
			Integer goodsId = Integer.valueOf(s);
			rs = goodsService.deleteGoods(goodsId);
		}
		if(rs>0){
			return "success";
		}else{
			return "fail";
		}
	}

}
