package com.shiro.springbootshiro.controller;

import java.util.List;

import com.shiro.springbootshiro.bean.Memory;
import com.shiro.springbootshiro.service.IMemoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/memory")
public class MemoryController {
	@Autowired
	private IMemoryService memoryService;
	@RequestMapping("findAll")
	public List<Memory> findAllMemory(){
			List<Memory> list = memoryService.finAllMemory();
			return list;
	}
}
