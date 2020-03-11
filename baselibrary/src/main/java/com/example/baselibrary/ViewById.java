package com.example.baselibrary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)  //什么时候生效 运行时 还是编译时
@Target(ElementType.FIELD)  //代表Annotation的位置 Filed属性 TYPE类上 CONSTRUCTOR 构造函数上
public @interface ViewById {

    int value();
}
