package com.jools.rpc.serializer;

import org.junit.Assert;
import org.junit.Test;

public class SerializerFactoryTest {


    @Test
    public void testSingletonFactory() {

        SerializerFactory factory = SerializerFactory.getInstance();
        SerializerFactory newFactory = SerializerFactory.getInstance();

        Assert.assertEquals(factory, newFactory);
    }

}