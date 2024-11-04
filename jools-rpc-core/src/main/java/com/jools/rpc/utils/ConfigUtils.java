package com.jools.rpc.utils;


import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/1 11:13
 * @description: 配置类工具类
 */
public class ConfigUtils {

    /**
     * 修配默认 .properties 中的配置
     *
     * @param config
     * @param key
     * @param value
     */
    public static void modifyConfig(String config, String key, String value) {
        modifyConfig(config, "", key, value);
    }

    /**
     * 可修改 多环境 .properties 中的配置
     *
     * @param config
     * @param environment
     * @param key
     * @param value
     */
    public static void modifyConfig(String config, String environment, String key, String value) {
        if (StrUtil.isBlank(key)) {
            return;
        }
        if (StrUtil.isBlank(value)) {
            throw new RuntimeException("Empty value for key:" + key);
        }

        //多环境配置
        StringBuilder configFileBuilder = new StringBuilder(config);
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }
        String propsPath = configFileBuilder.toString();
        Props props = new Props(propsPath);

        try {
            //更新配置
            props.setProperty(key, value);
            props.store(propsPath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

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
                throw new RuntimeException("File: " + name.toString() + " NOT FOUND");
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
        Props props = new Props(configFileBuilder.toString(), StandardCharsets.UTF_8);
        props.autoLoad(true);
        return props.toBean(tClass, prefix);
    }
}
