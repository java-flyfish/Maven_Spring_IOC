package com.zzx.entity.proxy;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Info:
 * @ClassName: ServiceProxy
 * @Author: weiyang
 * @Data: 2020/3/17 9:26 AM
 * @Version: V1.0
 **/
public class ServiceProxy<T> implements InvocationHandler {

    private Class<T> interfaceType;

    public ServiceProxy(Class<T> interfaceType){
        this.interfaceType = interfaceType;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())){
            return method.invoke(this,args);
        }
        System.out.println("调用前，参数：{}" + args);
        return null;
    }
}
