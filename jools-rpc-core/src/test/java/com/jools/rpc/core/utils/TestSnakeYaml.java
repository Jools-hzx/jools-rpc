package com.jools.rpc.core.utils;

import com.jools.rpc.core.config.RpcConfig;
import org.junit.Test;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/1 15:28
 * @description: TODO
 */
public class TestSnakeYaml {



    @Test
    public void testParseYamlToCustomizeType() {

        Yaml yaml = new Yaml(new Constructor(RpcConfig.class));

        RpcConfig rpcConfig = null;
        try (InputStream in = this.getClass()
                .getClassLoader()
                .getResourceAsStream("application.yml")) {
            //加载 yaml 格式配置
            rpcConfig = yaml.load(in);
            System.out.println(rpcConfig);

            System.out.println(rpcConfig.getName());
            System.out.println(rpcConfig.getVersion());
            System.out.println(rpcConfig.getServerHost());
            System.out.println(rpcConfig.getServerPort());

        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }


    @Test
    public void testParseYaml() {

        //基于 SankeYaml 工具类完成转换
        Yaml yaml = new Yaml();

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("application.yml");
        //封装成 Map
        Map<String, Map<String, Object>> map = yaml.load(inputStream);
        System.out.println(map);

        //key 为 rpc; value type 设置为 Object, 防止强转异常
        Map<String, Object> rpcConfigs = map.get("rpc");
        System.out.println(rpcConfigs);

        System.out.println(rpcConfigs.get("name"));
        System.out.println(rpcConfigs.get("version"));
        System.out.println(rpcConfigs.get("serverHost"));
        System.out.println(rpcConfigs.get("serverPort"));
    }

}
