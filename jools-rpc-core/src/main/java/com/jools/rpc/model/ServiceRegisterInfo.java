package com.jools.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/13 19:21
 * @description: 服务注册信息
 * 服务名
 * 服务实现类
 */
@Data
@AllArgsConstructor
public class ServiceRegisterInfo<T> {

    private String serviceName;

    private Class<? extends T> implClass;
}
