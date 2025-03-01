package com.jools.rpc.proxy.valid;

/**
 * @author Jools He
 * @version 1.0
 * @date 2025/3/1 18:57
 * @description: 未登录异常
 */
public class NeedLoginException extends RuntimeException{

    public NeedLoginException(String message) {
        super(message);
    }
}
