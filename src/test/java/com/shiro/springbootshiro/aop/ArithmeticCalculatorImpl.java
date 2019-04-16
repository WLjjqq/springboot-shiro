package com.shiro.springbootshiro.aop;

/**
 * 作用：实现这个业务类
 */
public class ArithmeticCalculatorImpl implements ArithmeticCalculator {
    @Override
    public int add(int i, int j) {
        return i+j;
    }
}
