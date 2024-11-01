package com.jools.rpc.core;

import cn.hutool.core.util.StrUtil;
import com.jools.rpc.core.config.RpcConfig;
import com.jools.rpc.core.constant.RpcConstant;
import com.jools.rpc.core.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/1 11:55
 * @description: TODO
 */
@Slf4j
public class RpcApplication {

    private static RpcConfig rpcConfig;


    public static void init(RpcConfig newConfig) {
        rpcConfig = newConfig;
        log.info("Rpc Config init succeed!, config = {}", newConfig);
    }

    /**
     * 初始化自定义配置类或者默认配置
     * 默认配置文件 application.properties
     */
    public static void init() {
        RpcConfig newRpcConfig;

        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            //加载失败的话，启用默认配置
            newRpcConfig = new RpcConfig();
        }
        //赋值
        init(newRpcConfig);
    }

    /**
     * 基于多种格式解析配置文件; 支持 .yml .yaml .properties
     *
     * @param suffix
     */
    public static void init(String suffix) throws FileNotFoundException {
        //默认读取 .properties
        if (StrUtil.isBlank(suffix)) {
            init();
            return;
        }
        //加载基于 .properties
        if (suffix.equals(RpcConstant.PROP_CONFIG_SUFFIX)) {
            init();
            return;
        }
        //加载基于 .yaml / .yml
        String[] ymlSuffixes = RpcConstant.YAML_CONFIG_SUFFIX.split(";");
        for (String s : ymlSuffixes) {
            if (suffix.equals(s)) {
                RpcConfig ymlRpcConfig = ConfigUtils.loadConfigYaml(RpcConfig.class, s);
                init(ymlRpcConfig);
                return;
            }
        }
        throw new FileNotFoundException("Not RpcConfig File FOUND !!!");
    }


    /**
     * 单例模式 初始化配置类
     *
     * @return
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcConfig.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }

    /**
     * 单例模式 基于多种格式初始化配置类
     *
     * @return
     */
    public static RpcConfig getRpcConfig(String suffix) throws FileNotFoundException {
        if (rpcConfig == null) {
            synchronized (RpcConfig.class) {
                if (rpcConfig == null) {
                    init(suffix);
                }
            }
        }
        return rpcConfig;
    }
}
