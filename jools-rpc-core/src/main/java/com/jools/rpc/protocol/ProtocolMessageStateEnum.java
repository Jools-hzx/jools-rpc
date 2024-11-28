package com.jools.rpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息状态
 *
 * @author Jools
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public enum ProtocolMessageStateEnum {

    /**
     * 请求成功状态
     */
    REQUEST_SUCCESS(Byte.parseByte("20"), "Request Success"),

    /**
     * 请求失败状态
     */
    REQUEST_FAIL(Byte.parseByte("40"), "Request Fail"),

    /**
     * 响应失败状态
     */
    RESPONSE_FAIL(Byte.parseByte("50"), "Response Success");


    private byte val;
    private String text;

    /**
     * 基于 val 查询其对应的消息
     *
     * @param val
     * @return 消息状态信息
     */
    public static ProtocolMessageStateEnum getStateMsgByCode(byte val) {
        for (ProtocolMessageStateEnum state : ProtocolMessageStateEnum.values()) {
            if (state.getVal() == val) {
                return state;
            }
        }
        log.error("Unknown Message State for state code:{}", val);
        return null;
    }
}
