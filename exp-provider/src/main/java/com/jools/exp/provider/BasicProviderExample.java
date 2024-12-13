package com.jools.exp.provider;

import com.jools.exp.common.service.UserService;
import com.jools.rpc.bootstrap.ProviderBootstrap;
import com.jools.rpc.model.ServiceRegisterInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Jools He
 * @version 1.0
 * @description: 简易服务提供者示例
 */
@Slf4j
public class BasicProviderExample {

    public static void main(String[] args) throws FileNotFoundException, ExecutionException, InterruptedException {

        //注册服务
        ServiceRegisterInfo serviceRegisterInfo = new ServiceRegisterInfo(UserService.class.getName(), UserServiceImpl.class);

        List<ServiceRegisterInfo<?>> serviceRegisterInfos = new ArrayList<>();
        serviceRegisterInfos.add(serviceRegisterInfo);

        //启动
        ProviderBootstrap.init(serviceRegisterInfos);
    }
}
