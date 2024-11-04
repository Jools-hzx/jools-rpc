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
 * @description: 请求类 Rpc Request 的作用是封装调用所需的信息，比如
 * - 服务名称
 * - 方法名称
 * - 调用参数的类型列表
 * - 参数列表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 请求服务名称
     */
    private String serviceName;

    /**
     * 请求方法名称
     */
    private String methodName;

    /**
     * 请求方法的类型列表
     */
    private Class<?>[] paramTypes;

    /**
     * 请求方法的参数列表
     */
    private Object[] params;
}
