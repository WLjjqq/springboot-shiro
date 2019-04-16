package com.shiro.springbootshiro;

import java.util.ArrayList;
import java.util.List;

/**
 * 作用：
 */
public class B extends A implements C {
    @Override
    public String voidStr() {
        return null;
    }

    @Override
    public String a() {
        return null;
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList();
        list.add("aa");
        list.add("bb");
        list.remove("bb");
        for (String s : list) {
            System.out.println(s);
        }
    }
}
