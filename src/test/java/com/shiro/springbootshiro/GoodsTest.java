package com.shiro.springbootshiro;

import com.shiro.springbootshiro.bean.Evaluate;
import com.shiro.springbootshiro.bean.Goods;
import com.shiro.springbootshiro.mapper.GoodsMapper;
import com.shiro.springbootshiro.service.IEvaluateService;
import com.shiro.springbootshiro.service.IGoodsService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;

/**
 * 作用：
 */
public class GoodsTest extends SpringbootShiroApplicationTests {

    @Resource
    GoodsMapper goodsMapper;
    @Test
    public void test(){
        List<Goods> all = goodsMapper.findAll();
        for (Goods goods : all) {
            System.out.println(goods);
        }
    }

    @Autowired
    IEvaluateService evaluateService;
    @Test
    public void test1(){
        List<Evaluate> evaluateByGoodsId = evaluateService.findEvaluateByGoodsId(1);
        for (Evaluate evaluate : evaluateByGoodsId) {
            System.out.println(evaluate);
        }
    }

    @Autowired
    IGoodsService goodsService;

    @Test
    public void test2(){
        Goods byId = goodsService.findById(1);
        System.out.println(byId);
    }
}
