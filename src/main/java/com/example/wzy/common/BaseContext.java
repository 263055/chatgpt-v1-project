package com.example.wzy.common;

import lombok.Data;

/**
 * 基于 ThreadLocal 封装的工具类，用户保存和获取当前登录的id
 */
public class BaseContext {
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void setCurrent(String id){
        threadLocal.set(id);
    }

    public static String getCurrent(){
        return threadLocal.get();
    }
}
