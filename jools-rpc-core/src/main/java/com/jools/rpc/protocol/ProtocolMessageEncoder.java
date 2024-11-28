package com.jools.rpc.protocol;

import cn.hutool.core.util.ObjectUtil;
import com.jools.rpc.serializer.Serializer;
import com.jools.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/27 10:33
 * @description: 消息编码器
 */
@Slf4j
public class ProtocolMessageEncoder {

    /**
     * 编码
     *
     * @param protocolMessage 自定义协议请求消息
     * @return Buffer 缓冲数据
     */
    public static Buffer encode(ProtocolMessage<?> protocolMessage) {
        //检查输入
        if (ObjectUtil.isNull(protocolMessage) || ObjectUtil.isNull(protocolMessage.getHeader())) {
            return Buffer.buffer();
        }
        //依次将消息头内的字段写入 Buffer; 基于消息头选择的序列化器编码
        Buffer buffer = Buffer.buffer();
        //消息体结构: 顺序依次是 魔数 + 版本号 + 序列化方式 + 类型 + 状态 + 请求 id + 请求数据长度
        ProtocolMessage.Header header = protocolMessage.getHeader();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializerType());
        buffer.appendByte(header.getMessageType());
        buffer.appendByte(header.getMessageState());
        buffer.appendLong(header.getMessageId());

        //获取序列化协议
        byte type = header.getSerializerType();
        ProtocolSerializerTypeEnum serializerType = ProtocolSerializerTypeEnum.getSerializerTypeEnumByType(type);

        //检查序列化协议
        Serializer serializer;
        if (serializerType == null) {
            throw new RuntimeException("Invalid Serializer type:" + type);
        }
        //获取序列化器
        serializer = SerializerFactory.getInstance(serializerType.getSerializerKey());

        //基于指定的序列化器完成数据体序列化
        byte[] body;
        try {
            body = serializer.serialize(protocolMessage.getBody());
        } catch (IOException e) {
            log.error("Fail to serialize a message body to bytes, message id:{}", header.getMessageId());
            throw new RuntimeException(e);
        }
        //序列化后的字节数组长度
        buffer.appendInt(body.length);
        log.debug("Byte array length after serializing:{}", body.length);
        //序列化后的字节数组
        buffer.appendBytes(body);
        return buffer;
    }
}










