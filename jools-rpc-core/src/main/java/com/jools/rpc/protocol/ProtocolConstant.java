package com.jools.rpc.protocol;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/26 21:01
 * @description: 与自定义协议静态常量信息：
 * 消息头长度
 * 魔数
 * 版本号
 */
public interface ProtocolConstant {

    /**
     * 默认魔数
     */
    byte MAGIC = 0x1;

    /**
     * 消息头 - 固定 17 个字节
     */
    byte MESSAGE_HEADER_SIZE = 17;

    /**
     * 版本号 - 默认版本号
     */
    byte VERSION = 0x1;
}
