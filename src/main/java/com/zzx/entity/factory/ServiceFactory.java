package com.zzx.entity.factory;

import com.zzx.entity.proxy.ServiceProxy;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @Info:
 * @ClassName: ServiceFactory
 * @Author: weiyang
 * @Data: 2020/3/17 9:24 AM
 * @Version: V1.0
 **/
public class ServiceFactory<T> implements FactoryBean<T> {

    private Class<T> interfaceType;

    public ServiceFactory(Class<T> interfaceType){
        this.interfaceType = interfaceType;
    }

    @Override
    public T getObject() throws Exception {
        InvocationHandler handler = new ServiceProxy<>(interfaceType);
        return (T)Proxy.newProxyInstance(interfaceType.getClassLoader(),new Class[]{interfaceType},handler);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
