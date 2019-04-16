package com.shiro.springbootshiro.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * 作用：给spring aop中业务添加日志需求,使用动态代理
 */
public class ArithmeticCalculatorLoggingProxy {
    //要代理的对象
    private ArithmeticCalculator target;

    public ArithmeticCalculatorLoggingProxy(ArithmeticCalculator target) {
        this.target = target;
    }
    public ArithmeticCalculator getLoggingProxy(){
        ArithmeticCalculator proxy = null;
        //代理对象由哪一个类加载器负责加载。
        ClassLoader loader = target.getClass().getClassLoader();
        //代理对象的类型，即其中有哪些方法。
        Class[] interfaces = new Class[]{ArithmeticCalculator.class};
        //当调用代理对象中的方法的时候，要先执行什么代码。
        InvocationHandler h = new InvocationHandler() {

            /**
             *
             * @param proxy : 正在返回的那个代理对象，一般情况下，在invoke都不使用该对象。
             * @param method : 正在调用的那个方法
             * @param args :调用方法时，传入的参数
             * @return
             * @throws Throwable
             */

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String name = method.getName();
                //在执行方法前，先打印出方法的名称
                System.out.println("先打印出方法的名字："+name+"开始："+ Arrays.asList(args));
                //执行方法
                Object result = method.invoke(target, args);
                //执行方法后，打印日志，输出结果
                System.out.println("打印"+name+"后的结果："+result);
                return result;
            }
        };
        proxy= (ArithmeticCalculator) Proxy.newProxyInstance(loader,interfaces,h);
        return proxy;
    }
}
