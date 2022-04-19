package com.leeyen.myspring.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 注解生效时间
@Retention(RetentionPolicy.RUNTIME)
// 指定位置，只能写在字段上面
@Target(ElementType.FIELD)
public @interface Autowired {
}
