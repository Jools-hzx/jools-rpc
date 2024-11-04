package com.jools.rpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.jools.rpc.serializer.Serializer;
import com.jools.rpc.serializer.SerializerKeys;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/4 17:03
 * @description: TODO
 */
public class TestSpiLoader {

    @Before
    public void testLoaderLoadAll() {
        SpiLoader.loadAll();
    }

    @Test
    public void readFiles() throws IOException {

        List<URL> resources = ResourceUtil.getResources("META-INF/rpc/system/com.jools.rpc.serializer.Serializer");

        for (URL url : resources) {
            System.out.println(url.getPath());
        }

        resources = ResourceUtil.getResources("META-INF/rpc/custom/com.jools.rpc.serializer.Serializer");
        for (URL url : resources) {
            System.out.println(url.getPath());
        }
    }

    //测试加载一类
    @Test
    public void testLoaderLoad() {
        Map<String, Class<?>> load = SpiLoader.load(Serializer.class);

        System.out.println("Serializer.class 类型对应的实现类有:" + load.size());

        for (Map.Entry<String, Class<?>> classEntry : load.entrySet()) {
            System.out.printf("key:{%s} --> instance:{%s}%n",
                    classEntry.getKey(),
                    classEntry.getValue().getName());
        }
    }

    @Test
    public void testErrorKey() {
        Serializer serializer = SpiLoader.getInstance(Serializer.class, "jdk");
        System.out.println(serializer);
    }

    //测试基于 key 获取实例
    @Test
    public void testLoaderGetInstance() {

        //测试基于接口类型和 key 获取实例
        Serializer kryoInstance = SpiLoader.getInstance(Serializer.class, SerializerKeys.KRYO);
        Serializer jdkInstance = SpiLoader.getInstance(Serializer.class, SerializerKeys.JDK);
        Serializer hessianInstance = SpiLoader.getInstance(Serializer.class, SerializerKeys.HESSIAN);
        Serializer jsonInstance = SpiLoader.getInstance(Serializer.class, SerializerKeys.JSON);

        assert hessianInstance != null;
        assert jdkInstance != null;
        assert kryoInstance != null;
        assert jsonInstance != null;

        //测试单例
        Serializer newJsonInstance = SpiLoader.getInstance(Serializer.class, SerializerKeys.JSON);
        assert newJsonInstance == jsonInstance;
    }
}
