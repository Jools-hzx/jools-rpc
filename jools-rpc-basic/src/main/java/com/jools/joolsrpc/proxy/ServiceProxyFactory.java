package com.jools.joolsrpc.proxy;

import java.lang.reflect.Proxy;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/10/30 11:59
 * @description: 服务代理工厂 (用于创建代理对象)
 */
public class ServiceProxyFactory {

    /**
     * 根据服务类接口返回代理对象
     *
     * @param serviceClass
     * @param <T>
     */
    @SuppressWarnings("all")
    public static <T> T getProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }
}
