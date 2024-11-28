package com.jools.rpc.serializer.impl;

import com.jools.rpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/10/30 10:08
 * @description: TODO
 */
@Slf4j
public class JdkSerializer implements Serializer {

    /**
     * 序列化 - 基于 JDK 动态代理
     *
     * @param object
     * @param <T>
     * @return
     * @throws IOException
     */
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        //创建一个字节数组输出流，用于存储序列化之后的数据
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        try {
            // 将对象写入输出流 (序列化)
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            // 返回序列化后的字节数组
            return outputStream.toByteArray();
        } finally {
            // 关闭输出流，释放资源
            objectOutputStream.close();
        }
    }

    /**
     * 反序列化 - 基于 JDK
     *
     * @param bytes
     * @param type
     * @param <T>
     * @return
     * @throws IOException
     */
    @Override
    @SuppressWarnings("all")
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        if (bytes == null || bytes.length == 0) {
            throw new IOException("字节数组为空或长度为 0，无法反序列化");
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            Object obj = objectInputStream.readObject();
            if (!type.isInstance(obj)) {
                throw new IOException("反序列化的对象类型不匹配");
            }
            return type.cast(obj);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("反序列化时无法找到类：" + e.getMessage(), e);
        }
    }
}
