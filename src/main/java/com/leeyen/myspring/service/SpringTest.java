package com.leeyen.myspring.service;

import com.leeyen.myspring.spring.ApplicationContext;

public class SpringTest {
    public static void main(String[] args) {
        // 创建一个spring容器
        ApplicationContext applicationContext = new ApplicationContext(AppConfig.class);
        UserInterface userService = (UserInterface) applicationContext.getBean("userService");
        userService.test();
    }
}
