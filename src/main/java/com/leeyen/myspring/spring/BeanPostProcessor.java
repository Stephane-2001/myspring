package com.leeyen.myspring.spring;

public interface BeanPostProcessor {
    Object postProcessBeforeInitializing(String beanName, Object bean);
    Object postProcessAfterInitializing(String beanName, Object bean);
}
