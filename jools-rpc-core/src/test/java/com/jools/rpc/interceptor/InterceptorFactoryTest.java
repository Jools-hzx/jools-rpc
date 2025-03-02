package com.jools.rpc.interceptor;

import com.jools.rpc.spi.SpiLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InterceptorFactoryTest {

    @Before
    public void setUp() throws Exception {
        SpiLoader.load(RpcHandlerInterceptor.class);
    }

    @Test
    public void getFactoryInstance() {
        InterceptorFactory interceptorFactory = InterceptorFactory.getFactoryInstance();
        Assert.assertNotNull(interceptorFactory);
    }

    @Test
    public void getInstance() {
        RpcHandlerInterceptor instance = InterceptorFactory.getInstance(InterceptorKeys.INVALID_SERVICE_NAME);
        Assert.assertNotNull(instance);
        RpcHandlerInterceptor interceptorInstance = InterceptorFactory.getInstance(InterceptorKeys.ILLEGAL_PARAMETER);
        Assert.assertNotNull(interceptorInstance);
    }
}