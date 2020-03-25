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
