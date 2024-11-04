package com.jools.joolsrpc.serializer;

import com.jools.joolsrpc.serializer.impl.HessianSerializer;
import com.jools.joolsrpc.serializer.impl.JdkSerializer;
import com.jools.joolsrpc.serializer.impl.JsonSerializer;
import com.jools.joolsrpc.serializer.impl.KryoSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/4 12:43
 * @description:
 * 序列化器工厂:
 * 基于 工厂模式 + 单例模式 来简化创建和获取序列化器的操作
 */
public class SerializerFactory {

    /**
     * 序列化映射 - 基于 SerializerKeys
     */
    private static final Map<String, Serializer> KEY_SERIAILZER_MAP = new HashMap<>() {{
        put(SerializerKeys.JDK, new JdkSerializer());
        put(SerializerKeys.HESSIAN, new HessianSerializer());
        put(SerializerKeys.KRYO, new KryoSerializer());
        put(SerializerKeys.JSON, new JsonSerializer());
    }};


    /**
     * 默认的序列化器 —— 基于 JDK
     */
    public static final Serializer DEFAULT_SERIALIZER = KEY_SERIAILZER_MAP.get(SerializerKeys.JDK);


    /**
     * 获取实例 - 如果获取不到返回默认序列化
     */
    public static Serializer getInstance(String key) {
        return KEY_SERIAILZER_MAP.getOrDefault(key, DEFAULT_SERIALIZER);
    }
}
