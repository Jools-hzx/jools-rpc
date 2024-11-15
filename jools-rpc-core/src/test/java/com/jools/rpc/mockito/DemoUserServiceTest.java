package com.jools.rpc.mockito;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DemoUserServiceTest {


    @Test
    public void testMockClass() {

        Random mockRandom = mock(Random.class);

        //返回值默认式该类型的默认值
        Assert.assertFalse(mockRandom.nextBoolean());
        Assert.assertEquals(0, mockRandom.nextInt());
        Assert.assertEquals(0.0, mockRandom.nextDouble());

        //打桩，后续始终返回 -1
        when(mockRandom.nextLong()).thenReturn(-1L);
        Assert.assertEquals(-1L, mockRandom.nextLong());
        Assert.assertNotEquals(0L, mockRandom.nextLong());
    }

    @Test
    public void setUp() throws Exception {
        //Mock Dao instnce
        DemoUserDao mockDao = mock(DemoUserDao.class);

        //打桩
        Mockito.when(mockDao.getName()).thenReturn("Elon Mask - AAA");

        //调用该方法，结果仅会返回 Elon Mask - AAA
        Assert.assertEquals(mockDao.getName(), "Elon Mask - AAA");
        System.out.println(mockDao.getName());

        //构建 mockService
        DemoUserService mockService = new DemoUserService(mockDao);

        //同样仅返回 Elon Mask - AAA
        Assert.assertEquals(mockService.getUserName(), "Elon Mask - AAA");
        System.out.println(mockService.getUserName());
    }
}