package com.jools.rpc.protocol;

import com.jools.rpc.serializer.SerializerKeys;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/26 21:01
 * @description: String JDK = "jdk";
 * String JSON = "json";
 * String KRYO = "kryo";
 * String HESSIAN = "hessian";
 * String PROTOBUF = "protobuf";
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public enum ProtocolSerializerTypeEnum {
    /**
     * JDK 序列化器
     */
    JDK_SERIALIZER(Byte.parseByte("0"), SerializerKeys.JDK),

    /**
     * JSON 序列化器
     */
    JSON_SERIALIZER(Byte.parseByte("1"), SerializerKeys.JSON),

    /**
     * KRYO 序列化器
     */
    KRYO_SERIALIZER(Byte.parseByte("2"), SerializerKeys.KRYO),

    /***
     * HESSIAN 序列化器
     */
    HESSIAN_SERIALIZER(Byte.parseByte("3"), SerializerKeys.HESSIAN),

    /**
     * PROTOBUF 序列化器
     */
    PROTOBUF_SERIALIZER(Byte.parseByte("4"), SerializerKeys.PROTOBUF);

    /**
     * 序列化器标识 - 一个字节
     */
    private byte type;
    private String serializerKey;


    /**
     * 基于 code 返回对应的 SerializerKeys
     *
     * @param code 序列化器标识code
     */
    public static String getSerializerKey(byte code) {
        for (ProtocolSerializerTypeEnum value : ProtocolSerializerTypeEnum.values()) {
            if (value.getType() == code) {
                return value.getSerializerKey();
            }
        }
        log.error("No match Serializer for code:{}", code);
        return null;
    }

    /**
     * 获取所有指出的序列化器列表
     *
     * @return 字符串
     */
    public static List<String> getSupportedSerializersList() {
        return Arrays.stream(ProtocolSerializerTypeEnum.values())
                .map(ProtocolSerializerTypeEnum::getSerializerKey)
                .collect(Collectors.toList());
    }

    /**
     * 根据 key 获取枚举
     *
     * @param type 枚举类型
     * @return 枚举实例
     */
    public static ProtocolSerializerTypeEnum getSerializerTypeEnumByType(byte type) {
        for (ProtocolSerializerTypeEnum typeEnum : ProtocolSerializerTypeEnum.values()) {
            if (typeEnum.getType() == type) {
                return typeEnum;
            }
        }
        log.error("No match Serializer for type:{}", type);
        return null;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param key 序列化器的 key
     * @return 枚举实例
     */
    public static ProtocolSerializerTypeEnum getSerializerTypeByKey(String key) {
        for (ProtocolSerializerTypeEnum typeEnum : ProtocolSerializerTypeEnum.values()) {
            if (typeEnum.getSerializerKey().equals(key)) {
                return typeEnum;
            }
        }
        log.error("No match Serializer for key:{}", key);
        return null;
    }
}
