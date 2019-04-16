package com.shiro.springbootshiro.aop;

import com.shiro.springbootshiro.Student;

/**
 * 作用：实现spring aop中的业务代码
 */
public class Main {
    public static void main(String[] args) {
        //目标方法
        ArithmeticCalculator target = new ArithmeticCalculatorImpl();
        //代理类
        ArithmeticCalculator proxy= new ArithmeticCalculatorLoggingProxy(target).getLoggingProxy();
        int i = proxy.add(3, 4);
        System.out.println(i);

        Student student=new Student("张三");
        Student student1=new Student("张三");
        System.out.println(student.equals(student1));
    }
}
