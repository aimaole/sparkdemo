package com.maomao.examples;

import java.lang.reflect.Method;

public class ReflectionExample {
    public static void main(String[] args) {
        String className = "com.maomao.examples.MyClass";

        try {
            // 根据类名获取类的信息
            Class<?> clazz = Class.forName(className);

            // 创建类的实例
            Object obj = clazz.newInstance();

            // 获取方法对象
            Method method = clazz.getMethod("sayHello", String.class);

            // 调用方法
            method.invoke(obj, "World");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class MyClass {
    public void sayHello(String name) {
        System.out.println("Hello, " + name + "!");
    }
}
