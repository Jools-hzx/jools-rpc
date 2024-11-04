package com.jools.rpc.spi;


import com.jools.rpc.serializer.Serializer;
import org.junit.Test;

import java.util.ServiceLoader;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/4 11:55
 * @description: TODO
 */
public class TestSPI {

    @Test
    public void testSysSpi() {
        //执行序列化器
        Serializer serializer = null;
        ServiceLoader<Serializer> serviceLoader = ServiceLoader.load(Serializer.class);
        for (Serializer service : serviceLoader) {
            serializer = service;
            System.out.println(serializer.getClass());
        }
    }
}
