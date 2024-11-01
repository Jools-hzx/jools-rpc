package com.jools.rpc.core.utils;


import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import com.jools.rpc.core.config.RpcConfig;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/1 11:13
 * @description: 配置类工具类
 */
public class ConfigUtils {

    /**
     * 基于 YAML 格式配置文件
     *
     * @param tClass
     * @param suffix
     * @param <T>
     * @return
     */
    public static <T> T loadConfigYaml(Class<T> tClass, String suffix) {
        return loadConfigYaml(tClass, suffix, "");
    }

    /**
     * 加载 YAML 格式配置文件，基于不同环境
     *
     * @param tClass
     * @param suffix
     * @param environment
     * @param <T>
     * @return
     */
    public static <T> T loadConfigYaml(Class<T> tClass, String suffix, String environment) {
        StringBuilder name = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            name.append("-").append(environment);
        }
        //传入参数携带 .
        name.append(suffix);

        //解析类
        Yaml yaml = new Yaml(new Constructor(tClass, new LoaderOptions()));

        try (InputStream in = ConfigUtils.class
                .getClassLoader()
                .getResourceAsStream(name.toString())) {
            if (in == null) {
                throw new RuntimeException("File: " + name.toString() + "NOT FOUND");
            }

            //加载 yaml 格式配置
            return yaml.load(in);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Loading .yml/yaml config file fail!! - " + e);
        }
    }

    /**
     * 加载配置对象
     *
     * @param tClass
     * @param prefix
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 加载配置对象，支持区分环境
     *
     * @param tClass
     * @param prefix
     * @param environment
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".properties");
        Props props = new Props(configFileBuilder.toString());
        return props.toBean(tClass, prefix);
    }
}
