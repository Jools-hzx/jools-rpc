package com.jools.rpc.jedis;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/24 18:56
 * @description: TODO
 */
public class JedisTest {

    private Jedis jedis;

    @Before
    public void testConnect() {

        jedis = new Jedis("192.168.23.128", 6379);

        jedis.auth("hzx2001");

        String response = jedis.ping();
        System.out.println("连接成功，返回的结果:" + response);
        //关闭连接
    }

    @Test
    public void testJedisRegistry() {

        jedis.set("serviceName:version/localhost:8888", "value01");
        jedis.set("serviceName:version/localhost:8881", "value02");
        jedis.set("serviceName:version/localhost:8882", "value03");

        Set<String> keys = jedis.keys("serviceName:version/*");
        for (String key : keys) {
            System.out.print(key + " ---> ");
            String value = jedis.get(key);
            System.out.println(value);
        }

    }

    //Jedis 操作 Hah
    @Test
    public void jedisModifyHash() {
        jedis.hset("hash01", "name", "CR7");
        jedis.hset("hash01", "age", "40");
        jedis.hset("hash01", "club", "Real Madrid");

        String name = jedis.hget("hash01", "name");
        System.out.println(name);
        String age = jedis.hget("hash01", "age");
        System.out.println(age);

        //基于 HashMap 完成
        Map<String, String> map = new HashMap<>();
        map.put("name", "MESSI");
        map.put("age", "35");
        jedis.hset("hash02", map);

        name = jedis.hget("hash02", "name");
        System.out.println(name);
    }

    @After
    public void close() {
        this.jedis.close();
    }
}
