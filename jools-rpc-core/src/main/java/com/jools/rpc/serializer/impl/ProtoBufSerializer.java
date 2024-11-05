package com.jools.rpc.serializer.impl;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import com.jools.rpc.serializer.Serializer;

import java.io.IOException;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/5 10:41
 * @description: TODO
 */
public class ProtoBufSerializer implements Serializer {

    /**
     * 序列化后，将对象转换为字节数组
     *
     * @param object 需要被序列化的对象
     * @param <T>    对象类型
     * @return 序列化后的字节数组
     * @throws IOException 如果序列化失败
     */
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        if (object instanceof Message) {
            return ((Message) object).toByteArray();
        } else {
            throw new IllegalArgumentException("Object must be an instance of com.google.protobuf.Message");
        }
    }

    /**
     * 反序列化，将字节数组转成对象实例
     *
     * @param bytes 字节数组
     * @param type  目标类的 Class 类型
     * @param <T>   目标类的类型
     * @return 反序列化后的对象
     * @throws IOException
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        try {
            // 检查类型是否是 Protobuf Message 的子类
            if (Message.class.isAssignableFrom(type)) {
                // 获取 Protobuf 的 parser 方法
                // Protobuf 生成的类通常会有静态的 `parser()` 方法
                Parser<?> parser = (Parser<?>) type.getMethod("parser").invoke(null);
                // 解析字节数组
                @SuppressWarnings("unchecked")
                T message = (T) parser.parseFrom(bytes);
                return message;
            } else {
                throw new IllegalArgumentException("Type must be a subclass of com.google.protobuf.Message");
            }
        } catch (Exception e) {
            throw new IOException("Failed to deserialize", e);
        }
    }
}

