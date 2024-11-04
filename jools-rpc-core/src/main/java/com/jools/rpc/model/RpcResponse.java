package com.jools.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/10/30 10:19
 * @description: TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcResponse implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 返回响应的数据
     */
    private Object data;

    /**
     * 返回响应的数据类型
     */
    private Class<?> dataType;

    /**
     * 返回响应的信息
     */
    private String msg;

    /**
     * 异常信息
     */
    private Exception exception;
}
