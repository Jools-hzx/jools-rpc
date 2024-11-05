package com.jools.rpc.serializer;

import com.jools.rpc.spi.SpiLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/4 12:43
 * @description: 序列化器工厂:
 * 基于 工厂模式 + 单例模式 来简化创建和获取序列化器的操作
 */
@Slf4j
public class SerializerFactory {

    private SerializerFactory() {
        log.info("Enter SerializerFactory `private` Constructor");
    }

    private static class SerializerFactoryHolder {
        private static final SerializerFactory SERIALIZER_FACTORY = new SerializerFactory();
    }

    public static SerializerFactory getInstance() {
        return SerializerFactoryHolder.SERIALIZER_FACTORY;
    }

    /**
     * 序列化映射 - 使用 SPI 机制加载所有资源配置文件；
     * 加载所有关于 Serializer.class 配置的 key -> 实现类实例 映射
     */
    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 默认的序列化器 —— 基于 JDK
     */
    public static final Serializer DEFAULT_SERIALIZER = SpiLoader.getInstance(Serializer.class, SerializerKeys.JDK);


    /**
     * 获取实例 - 如果获取不到返回默认序列化
     */
    public static Serializer getInstance(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }
}
