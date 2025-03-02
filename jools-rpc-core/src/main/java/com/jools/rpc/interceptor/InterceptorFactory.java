package com.jools.rpc.interceptor;

import com.jools.rpc.serializer.Serializer;
import com.jools.rpc.spi.SpiLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jools He
 * @version 1.0
 * @description: TODO
 */
@Slf4j
public class InterceptorFactory {

    private InterceptorFactory() {
        log.info("Enter InterceptorFactory `private` Constructor");
    }

    // 静态内部类在使用时才会被加载，并且只会被加载一次
    private static class InterceptorFactoryHolder {
        private static final InterceptorFactory INTERCEPTOR_FACTORY = new InterceptorFactory();
    }

    public static InterceptorFactory getFactoryInstance() {
        return InterceptorFactoryHolder.INTERCEPTOR_FACTORY;
    }

    public static final RpcHandlerInterceptor DEFAULT_INTERCEPTOR =
            SpiLoader.getInstance(RpcHandlerInterceptor.class, InterceptorKeys.INVALID_SERVICE_NAME);

    /**
     * 获取实例 - 如果获取不到返回默认拦截器
     *
     * @param key
     * @return
     */
    public static RpcHandlerInterceptor getInstance(String key) {
        try {
            return SpiLoader.getInstance(RpcHandlerInterceptor.class, key);
        } catch (Exception e) {
            return DEFAULT_INTERCEPTOR;
        }
    }

}
