package com.leeyen.myspring.service;

import com.leeyen.myspring.spring.BeanNameAware;
import com.leeyen.myspring.spring.Component;
import com.leeyen.myspring.spring.InitializingBean;


@Component
public class OrderService implements BeanNameAware, InitializingBean {
    @Override
    public void setBeanName(String beanName) {

    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("这个是OrderService初始化方法");
    }
}
