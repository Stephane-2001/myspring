package com.leeyen.myspring.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 注解生效时间
@Retention(RetentionPolicy.RUNTIME)
// 指定位置，只能写在类上面
@Target(ElementType.TYPE)
public @interface Component {
    // 给当前所用的bean取名
    String value() default "";
}
