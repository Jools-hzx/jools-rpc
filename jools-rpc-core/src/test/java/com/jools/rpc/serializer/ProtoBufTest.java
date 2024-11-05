package com.jools.rpc.serializer;

import com.jools.rpc.serializer.impl.ProtoBufSerializer;
import org.junit.Test;
import com.jools.rpc.protoc.*;

import java.io.IOException;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/5 10:29
 * @description: TODO
 */
public class ProtoBufTest {

    @Test
    public void testProtoBufSerialize() {

        try {
            // 创建一个 ProtoBufSerializer 实例
            Serializer serializer = new ProtoBufSerializer();

            // 创建一个 Person 对象
            UserOuterClass.User person = UserOuterClass.User.newBuilder()
                    .setName("Jools He")
                    .setAge(30)
                    .setEmail("jools666@example.com")
                    .build();

            // 序列化 Person 对象
            byte[] serializedData = serializer.serialize(person);
            System.out.println("Serialized Data: " + serializedData.length + " bytes");

            // 反序列化 Person 对象
            UserOuterClass.User deserializedPerson = serializer.deserialize(serializedData, UserOuterClass.User.class);
            System.out.println("Deserialized Person: " + deserializedPerson);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
