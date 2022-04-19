package com.leeyen.myspring.service;

import com.leeyen.myspring.spring.*;

@Component
@Scope("prototype")
public class UserService implements UserInterface{
    @Autowired
    private OrderService orderService;

    @Override
    public void test() {

    }
}
/*public class UserService implements BeanNameAware, InitializingBean {

    @Autowired
    private OrderService orderService;

    private String beanName;

    public void test(){
        System.out.println(orderService);
    }


    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() {
        // ...........初始化语句
        System.out.println("这个是UserService初始化方法");
    }
}*/
