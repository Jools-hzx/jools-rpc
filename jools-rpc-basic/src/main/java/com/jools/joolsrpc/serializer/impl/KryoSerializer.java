package com.jools.joolsrpc.serializer.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.jools.joolsrpc.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/4 12:32
 * @description: TODO
 */
public class KryoSerializer implements Serializer {

    /**
     * Kryo 实例应该是线程不安全的，所以每个线程应该有自己的 Kryo 实例
     */
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(Kryo::new);

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        Kryo kryo = KRYO_THREAD_LOCAL.get();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        kryo.writeObject(output, object);
        output.close();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        Kryo kryo = KRYO_THREAD_LOCAL.get();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        T object = kryo.readObject(input, type);
        input.close();
        return object;
    }
}
