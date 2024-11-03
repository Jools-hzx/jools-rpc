package com.jools.joolsrpc.proxy;

import com.jools.rpc.core.RpcApplication;

import java.lang.reflect.Proxy;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/10/30 11:59
 * @description: 服务代理工厂 (用于创建代理对象)
 */
public class ServiceProxyFactory {

    /**
     * 可以通过读取已经定义的全局配置 mock 来区分创建哪种代理对象
     *
     * @param mockClass
     * @param <T>
     * @return
     */
    @SuppressWarnings("all")
    public static <T> T getMockProxy(Class<T> mockClass) {
        return (T) Proxy.newProxyInstance(
                mockClass.getClassLoader(),
                new Class[]{mockClass},
                new MockServiceProxy()
        );
    }

    /**
     * 根据服务类接口返回代理对象
     *
     * @param serviceClass
     * @param <T>
     */
    @SuppressWarnings("all")
    public static <T> T getProxy(Class<T> serviceClass) {
        //如果开启 mock 配置，返回 Mock 代理对象
        if (RpcApplication.getRpcConfig().isMock()) {
            return getMockProxy(serviceClass);
        }
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }
}
