package com.zzx.entity.registry;

import com.zzx.entity.factory.ServiceFactory;
import com.zzx.entity.iface.TestInterface;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;

import java.util.Arrays;
import java.util.List;

/**
 * @Info:
 * @ClassName: ServiceBeanDefinitionRegistry
 * @Author: weiyang
 * @Data: 2020/3/16 4:39 PM
 * @Version: V1.0
 **/
public class ServiceBeanDefinitionRegistry implements BeanDefinitionRegistryPostProcessor {
    /**
     * BeanDefinitionRegistryPostProcessor
     * 主要是用来动态的往Spring中注册bean定义
     * 该方法在refresh中的invokeBeanFactoryPostProcessors方法中被调用，
     * dubbo和mybatis等第三方插件想在使用Spring的ioc容器，就需要在容器中注册bean定义
     * 注册主要需要以下信息，
     * 1.接口class文件
     * 2.获取实现类的工厂，实际上是FactoryBean的实现类
     * --通过FactoryBean的getObject()方法来获取bean
     * 3.对于mybatis等这些只有接口的第三方框架而言，就需要通过代理的形式生成接口实现类
     * 4.spring构建FactoryBean的时候回根据构造器传入对应接口的class，所以只需要重写FactoryBean的getObject()方法即可
     * 5.因为已经有了class文件，所以只需要通过(T)Proxy.newProxyInstance(interfaceType.getClassLoader(),new Class[]{interfaceType},handler)
     * 6.此时就成了在InvocationHandler.invoke()方法中试下业务逻辑即可。
     * 对于mybatis就是解析xml文件，执行sql语句
     * 对于dubbo，就是从zk上拉取服务提供方的注册信息，然后发起网络通信
     * @param registry
     * @throws BeansException
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        List<Class<?>> beanClazzs = Arrays.asList(TestInterface.class);
        //构建beanDefintion
        for (Class clazz : beanClazzs){
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
            GenericBeanDefinition beanDefinition = (GenericBeanDefinition)builder.getRawBeanDefinition();
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(clazz);

            beanDefinition.setBeanClass(ServiceFactory.class);
            beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            String simpleName = clazz.getSimpleName();
            registry.registerBeanDefinition(simpleName,beanDefinition);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
