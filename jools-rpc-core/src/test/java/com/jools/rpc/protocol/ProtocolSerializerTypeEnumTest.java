package com.jools.rpc.protocol;

import com.jools.rpc.serializer.SerializerKeys;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLOutput;
import java.util.List;

import static org.junit.Assert.*;

public class ProtocolSerializerTypeEnumTest {

    @Test
    public void getSerializerKey() {
        String serializerKey = ProtocolSerializerTypeEnum.getSerializerKey(Byte.parseByte("0"));
        Assert.assertEquals(serializerKey, "jdk");


        serializerKey = ProtocolSerializerTypeEnum.getSerializerKey(Byte.parseByte("4"));
        Assert.assertEquals(serializerKey, "protobuf");
    }

    @Test
    public void getSupportedSerializersList() {

        List<String> serializersList = ProtocolSerializerTypeEnum.getSupportedSerializersList();
        Assert.assertEquals(serializersList.size(), 5);
        for (String s : serializersList) {
            System.out.println(s);
        }
    }

    @Test
    public void getSerializerTypeEnumByKey() {

        ProtocolSerializerTypeEnum serializerType = ProtocolSerializerTypeEnum.getSerializerTypeEnumByType(Byte.parseByte("0"));
        assertNotNull(serializerType);
        assertEquals(serializerType.getSerializerKey(), "jdk");

        serializerType = ProtocolSerializerTypeEnum.getSerializerTypeEnumByType(Byte.parseByte("-1"));
        assertNull(serializerType);
    }

    @Test
    public void getSerializerTypeByKey() {

        ProtocolSerializerTypeEnum serializerTypeByKey = ProtocolSerializerTypeEnum.getSerializerTypeByKey(SerializerKeys.JDK);
        assertNotNull(serializerTypeByKey);
        assertEquals(serializerTypeByKey.getType(), Byte.parseByte("0"));
    }
}