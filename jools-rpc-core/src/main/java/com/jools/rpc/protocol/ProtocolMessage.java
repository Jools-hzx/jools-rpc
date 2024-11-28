package com.jools.rpc.protocol;

import com.jools.rpc.serializer.Serializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/26 20:57
 * @description: TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProtocolMessage<T> {

    @Builder.Default
    private Header header = new Header(); // 默认初始化

    /**
     * 消息体
     */
    private T body;


    /**
     * 自定义协议请求消息头 - 总共 17 个字节
     */

    @Data
    @NoArgsConstructor
    public static class Header {

        /**
         * 消息头魔数 - 1 个字节
         */
        byte magic;

        /**
         * 版本号 - 1 个字节
         */
        byte version;

        /**
         * 序列化方式 - 1 个字节
         */
        byte serializerType;

        /**
         * 消息类型 - 一个字节
         */
        byte messageType;

        /**
         * 消息状态 - 一个字节
         */
        byte messageState;

        /**
         * 请求体数据长度 - 4 个字节
         */
        int bodySize;

        /**
         * 请求唯一标识 id - 8个字节
         */
        long messageId;
    }
}