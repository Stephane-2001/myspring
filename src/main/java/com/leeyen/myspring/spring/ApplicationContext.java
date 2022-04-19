package com.leeyen.myspring.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {
    // 配置class类
    private Class configClass;
    // 存储beanDefinition对象
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    // 单例池
    private ConcurrentHashMap<String, Object> singletonObject = new ConcurrentHashMap<>();
    private ArrayList<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public ApplicationContext(Class configClass) {
        this.configClass = configClass;
        // 扫描
        // 1.判断通过配置类传入的扫描路径的类上是否有@ComponentScan注解
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            // 获取注解的的信息
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            // 扫描路径 com.leeyen 包名 真正要扫描的是编译后的class文件
            String path = componentScanAnnotation.value();
            // path com/leeyen/myspring
            path = path.replace(".", "/");
            // 通过classpath路径来获取.class文件的路径
            ClassLoader classLoader = ApplicationContext.class.getClassLoader();
            // 从类加载器中获取相对路径所对应的资源
            URL resource = classLoader.getResource(path);
            // 封装为file对象 file:/D:/JavaProject/leeyenSpring/target/classes/com/leeyen/myspring
            File file = new File(resource.getFile());
            if (file.isDirectory()){
                File[] files = file.listFiles();
                for (File f : files) {
                    String fileName = f.getAbsolutePath();
                    if (fileName.endsWith(".class")){
                        String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                        className = className.replace("\\", ".");
                        // 判断类是不是一个bean 就是判断有没有component注解
                        try {
                            // 1.拿到类的class对象
                            Class<?> aClass = classLoader.loadClass(className);
                            // 2.判断注解是否存在
                            if (aClass.isAnnotationPresent(Component.class)) {
                                if (BeanPostProcessor.class.isAssignableFrom(aClass)){
                                    BeanPostProcessor instance = (BeanPostProcessor) aClass.newInstance();
                                    beanPostProcessors.add(instance);
                                }

                                Component componentAnnotation = aClass.getAnnotation(Component.class);
                                String beanName = componentAnnotation.value();
                                if (beanName.equals("")){
                                    beanName = Introspector.decapitalize(aClass.getSimpleName());
                                }
                                // 那这个类就是bean类 判断单例多例 需要生成一个beandefinition对象
                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setType(aClass);
                                // 判断有无scope注解
                                if (aClass.isAnnotationPresent(Scope.class)){
                                    Scope scopeAnnotation = aClass.getAnnotation(Scope.class);
                                    beanDefinition.setScope(scopeAnnotation.value());
                                }else {
                                    beanDefinition.setScope("singleton");
                                }
                                beanDefinitionMap.put(beanName, beanDefinition);
                            }
                        }catch (ClassNotFoundException e){
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }

        // 创建单例bean对象
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")){
                Object bean = createBean(beanName, beanDefinition);
                singletonObject.put(beanName, bean);
            }
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition){
        Class aClass = beanDefinition.getType();
        try {
            Object instance = aClass.getConstructor().newInstance();
            // 依赖注入
            for (Field field : aClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)){
                    field.setAccessible(true);
                    field.set(instance, getBean(field.getName()));
                }
            }
            // 自动生成bean的名字 即Aware回调
            if (instance instanceof BeanNameAware){
                ((BeanNameAware)instance).setBeanName(beanName);
            }
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                instance = beanPostProcessor.postProcessBeforeInitializing(beanName, instance);
            }
            // 初始化步骤
            if (instance instanceof InitializingBean){
                ((InitializingBean)instance).afterPropertiesSet();
            }
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                instance = beanPostProcessor.postProcessAfterInitializing(beanName, instance);
            }
            // BeanPostProcessor 初始化后 aop

            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获得bean对象
    public Object getBean(String beanName){
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null){
            throw new NullPointerException();
        }else {
            String scope = beanDefinition.getScope();
            if (scope.equals("singleton")){
                // 单例
                Object bean = singletonObject.get(beanName);
                if (bean == null){
                   Object o = createBean(beanName, beanDefinition);
                   singletonObject.put(beanName, o);
                }
                return bean;
            }else {
                // 多例
                return createBean(beanName, beanDefinition);
            }
        }
    }
}
