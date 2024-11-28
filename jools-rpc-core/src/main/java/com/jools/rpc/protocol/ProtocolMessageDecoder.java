package com.jools.rpc.protocol;

import cn.hutool.log.Log;
import com.jools.rpc.model.RpcRequest;
import com.jools.rpc.model.RpcResponse;
import com.jools.rpc.serializer.Serializer;
import com.jools.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/27 10:54
 * @description: 消息解码器
 */
@Slf4j
public class ProtocolMessageDecoder {

    /**
     * 反序列化
     *
     * @param buffer 缓冲
     * @return ProtocolMessage 消息实例
     * @throws IOException
     */
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        ProtocolMessage.Header header = capsulateHeader(buffer);
        int bodySize = header.getBodySize();
        //解决 TCP 粘包问题，只截取并读取指定的数据包长度
        byte[] msgBody = buffer.getBytes(17, 17 + bodySize);
        log.info("Decode byte array size:{}", bodySize);

        ProtocolSerializerTypeEnum serializerTypeEnum = ProtocolSerializerTypeEnum.getSerializerTypeEnumByType(header.getSerializerType());
        if (serializerTypeEnum == null) {
            throw new RuntimeException("Unsupported serializer type:" + header.getSerializerType());
        }

        Serializer serializer = SerializerFactory.getInstance(serializerTypeEnum.getSerializerKey());

        //得到消息类型
        ProtocolMessageTypeEnum typeEnum = ProtocolMessageTypeEnum.getMessageTypeEnum(header.getMessageType());
        if (typeEnum == null) {
            throw new RuntimeException("Unsuported message type:" + header.getMessageType());
        }
        //匹配消息类型反序列化得到消息体
        switch (typeEnum) {
            case REQUEST:
                RpcRequest request = serializer.deserialize(msgBody, RpcRequest.class);
                return new ProtocolMessage<RpcRequest>(header, request);
            case RESPONSE:
                RpcResponse response = serializer.deserialize(msgBody, RpcResponse.class);
                return new ProtocolMessage<RpcResponse>(header, response);
            case HEARTBEAT:
            case OTHER:
            default:
                throw new RuntimeException("Unsupported Message type:" + typeEnum);
        }
    }


    private static ProtocolMessage.Header capsulateHeader(Buffer buffer) {
         /*
            消息体结构 - 顺序依次:
            1. 魔数 [1B]
            2. 版本号 [1B]
            3. 序列化方式 [1B]
            4. 类型 [1B]
            5. 状态 [1B]
            6. 请求id [8B]
            7. 请求数据长度[4B]
         */
        byte magic = buffer.getByte(0);

        //校验魔数
        if (magic != ProtocolConstant.MAGIC) {
            throw new RuntimeException("Magic is not valid!");
        }

        //基于定义的消息头字段长度截取
        byte version = buffer.getByte(1);

        //校验版本
        if (version != ProtocolConstant.VERSION) {
            throw new RuntimeException("Version Not Match! current version" + ProtocolConstant.VERSION +
                    "\tRequest version:" + version);
        }

        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(magic);
        header.setVersion(version);
        //分割: 序列化类型 + 消息类型 + 消息状态 + 消息id + 消息体长度
        header.setSerializerType(buffer.getByte(2));
        header.setMessageType(buffer.getByte(3));
        header.setMessageState(buffer.getByte(4));
        header.setMessageId(buffer.getByte(5));
        header.setBodySize(buffer.getInt(13));

        return header;
    }
}
