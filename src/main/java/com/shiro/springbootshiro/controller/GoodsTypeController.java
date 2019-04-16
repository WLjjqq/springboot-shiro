package com.shiro.springbootshiro.controller;

import java.util.List;

import com.shiro.springbootshiro.bean.GoodsType;
import com.shiro.springbootshiro.service.IGoodsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goodsType")
public class GoodsTypeController {
	@Autowired
	private IGoodsTypeService goodsTypeService;
	@RequestMapping("findAll")
	public List<GoodsType> finAllType(){
		List<GoodsType> list = goodsTypeService.findAllType();
		return list;
	}
	@RequestMapping("findTypeBySplitPage")
	public JSONObject findTypeBySplitPage(Integer page,Integer limit,String keyword){
		PageInfo<GoodsType> info = goodsTypeService.findTypeBySplitPage(page, limit, keyword);
		JSONObject obj=new JSONObject();
		obj.put("code", 0);
		obj.put("msg", "");
		obj.put("count", info.getTotal());
		obj.put("data", info.getList());
		return obj;
	}
	@RequestMapping("deleteGoodsType")
	public String deleteGoodsType(Integer typeId){
		Integer rs = goodsTypeService.deleteGoodsType(typeId);
		if(rs>0){
			return "success";
		}else{
			return "fail";
		}
	}
	@RequestMapping("updateGoodsType")
	public String updateGoodsType(GoodsType type){
		Integer rs = goodsTypeService.updateGoodsType(type);
		if(rs>0){
			return "success";
		}else{
			return "fail";
		}
	}
	@RequestMapping("changeTypeState")
	public String disableGoodsType(Integer typeId,Integer state){
		System.out.println(state+"state");
		System.out.println(typeId+"typeId");
		Integer rs = goodsTypeService.changeTypeState(state, typeId);
		if(rs>0){
			return "success";
		}else{
			return "fail";
		}
	}
	@RequestMapping("batchDelete")
	public String batchDelete(String batchId){
		String[] list = batchId.split(",");
		boolean flag=true;
		for (String s : list) {
			Integer typeId = Integer.valueOf(s);
			Integer rs = goodsTypeService.deleteGoodsType(typeId);
			if(rs<0){
				flag=false;
			}
		}
		if(flag){
			return "success";
		}else{
			return "fail";
		}
	}
	@RequestMapping("addGoodsType")
	public String addGoodsType(GoodsType goodsType){
		Integer rs = goodsTypeService.addGoodsType(goodsType);
		if(rs>0){
			return "success";
		}else{
			return "fail";
		}
	}
}
