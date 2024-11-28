package com.jools.rpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/26 21:00
 * @description: 协议消息状态
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Slf4j
public enum ProtocolMessageTypeEnum {

    /**
     * 请求类型
     */
    REQUEST(Byte.parseByte("0"), "Type-Request"),

    /**
     * 响应类型
     */
    RESPONSE(Byte.parseByte("1"), "Type-Response"),

    /**
     * 心跳监测报文类型
     */
    HEARTBEAT(Byte.parseByte("2"), "Type-HeartBeat"),

    /**
     * 重传报文类型
     */
    RETRANSFER(Byte.parseByte("3"), "Type-Retransmission"),

    /**
     * 其他待扩展类型
     */
    OTHER(Byte.parseByte("4"), "Type-Other");


    private byte messageType;
    private String text;

    /**
     * 基于消息 code 匹配消息类型
     *
     * @param code
     * @return
     */
    public static ProtocolMessageTypeEnum getMessageTypeEnum(byte type) {
        for (ProtocolMessageTypeEnum typeEnum : ProtocolMessageTypeEnum.values()) {
            if (typeEnum.getMessageType() == type) {
                return typeEnum;
            }
        }
        log.error("No message type match code:{}", type);
        return null;
    }
}
