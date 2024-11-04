package com.jools.joolsrpc.serializer.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jools.joolsrpc.model.RpcRequest;
import com.jools.joolsrpc.model.RpcResponse;
import com.jools.joolsrpc.serializer.Serializer;

import java.io.IOException;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/4 12:11
 * @description: TODO
 */
public class JsonSerializer implements Serializer {

    // 创建一个 ObjectMapper 实例，用于序列化和反序列化
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        // 将对象序列化为 JSON 字符串，并将其转换为字节数组
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        // 将字节数组反序列化为指定类型的对象
        return OBJECT_MAPPER.readValue(bytes, type);
    }

    /**
     * 由于 Object 的原始对象会被擦除，导致反序列化时会被作为 LinkedHashMap 无法转换成原始对象，因此这里做了特殊处理
     *
     * @param rpcRequest
     * @param type
     * @param <T>
     * @return
     * @throws IOException
     */
    private <T> T handleRequest(RpcRequest rpcRequest, Class<T> type) throws IOException {
        Class<?>[] paramTypes = rpcRequest.getParamTypes();
        Object[] params = rpcRequest.getParams();

        //处理每个参数类型
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> cls = paramTypes[i];
            //如果类型不同需要重新处理
            if (!cls.isAssignableFrom(params[i].getClass())) {
                byte[] paramBytes = OBJECT_MAPPER.writeValueAsBytes(params[i]);
                params[i] = OBJECT_MAPPER.readValue(paramBytes, cls);
            }
        }
        return type.cast(rpcRequest);
    }

    private <T> T handleResponse(RpcResponse response, Class<T> type) throws IOException {
        //处理 RpcResponse 内 Object 类型的数据
        byte[] dataBytes = OBJECT_MAPPER.writeValueAsBytes(response.getData());
        response.setData(OBJECT_MAPPER.readValue(dataBytes, response.getDataType()));
        return type.cast(response);
    }
}
