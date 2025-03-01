package com.jools.rpc.proxy.valid;

import com.jools.rpc.model.RpcRequest;

/**
 * @author Jools He
 * @version 1.0
 * @date 2025/3/1 17:52
 * @description: TODO
 */
public class LoginValidator {

    public static boolean validate(RpcRequest request) {
        return !request.isNeedLogin();
    }
}
