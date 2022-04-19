package com.leeyen.myspring.service;

import com.leeyen.myspring.spring.BeanPostProcessor;
import com.leeyen.myspring.spring.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class BeanPostProcessorImpl implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitializing(String beanName, Object bean) {
        if (beanName.equals("userService")){
            System.out.println("这个是userService初始化前方法");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitializing(String beanName, Object bean) {
        if (beanName.equals("userService")){
            System.out.println("这个是userService初始化后方法");
            Object proxyInstance = Proxy.newProxyInstance(BeanPostProcessorImpl.class.getClassLoader(),
                    bean.getClass().getInterfaces(),
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            System.out.println("这个是切面逻辑");
                            return method.invoke(bean, args);
                        }
                    });
            return proxyInstance;
        }
        return bean;
    }
}
