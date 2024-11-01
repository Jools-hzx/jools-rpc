package joolsrpc;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.setting.dialect.Props;

import javax.sound.sampled.Port;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/1 10:34
 * @description: TODO
 */
public class TestProps {

    public static void main(String[] args) {

        ClassPathResource classPathResource = new ClassPathResource("test.properties");
        String absolutePath = classPathResource.getAbsolutePath();
        System.out.println(absolutePath);

        //读取配置文件;
        Props props = new Props("test.properties");
        String user = props.getProperty("user");
        String driver = props.getProperty("driver");

        System.out.println("User:" + user);
        System.out.println("Driver:" + driver);

        //写入配置
        props.setProperty("Report", "jools-rpc");
        props.setProperty("version", "1.0");

        //遍历所有 k-v
        System.out.println("--- 所有配置 ---");
        Set<Map.Entry<Object, Object>> entries = props.entrySet();
        for (Map.Entry<Object, Object> entry : entries) {
            System.out.print("key:" + entry.getKey());
            System.out.print("\tvalue:" + entry.getValue() + "\n");
        }
    }
}
