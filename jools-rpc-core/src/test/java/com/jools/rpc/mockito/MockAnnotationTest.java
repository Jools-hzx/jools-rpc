package com.jools.rpc.mockito;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.internal.matchers.Matches;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;

import java.util.List;
import java.util.Random;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/15 20:58
 * @description: 测试 @Mock 注解
 */
@RunWith(MockitoJUnitRunner.class)
public class MockAnnotationTest {

    @Mock
    private List<String> list;

    @Test
    public void testConsecutiveCalls() {
        when(list.get(anyInt()))
                .thenThrow(new RuntimeException("First call throws exception"))
                .thenReturn("foo");

        //第一次调用抛出 RuntimException
        try {
            list.get(1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //第二次调用及以后返回 foo
        System.out.println(list.get(1));
        System.out.println(list.get(1));
    }

    @Test
    public void testOrder() {

        //Single Mock
        list.add("Add first");
        list.add("Add second");

        InOrder inOrder = inOrder(list);
        inOrder.verify(list).add("Add first");
        inOrder.verify(list).add("Add second");

        //Multi mock
        List list2 = mock(List.class);
        list2.add("New added first");
        list.add("New added second");

        inOrder = inOrder(list2, list);
        inOrder.verify(list2).add("New added first");
        inOrder.verify(list).add("New added second");

        //verify that inOrder is not interacted
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testMockException() {

        doThrow(new RuntimeException("Can not be cleared")).when(list).clear();

        try {
            list.clear();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /*
    验证调用的确切数量 / 至少 x / 从不
     */
    @Test
    public void testVerify() {
        list.add("once");

        list.add("twice");
        list.add("twice");

        list.add("Three times");
        list.add("Three times");
        list.add("Three times");

        verify(list).add("once");
        verify(list, times(1)).add("once");

        verify(list, times((2))).add("twice");
        verify(list, times((3))).add("Three times");

        //never() 校验调用次数为 0 = times(0)
//        public static VerificationMode never() {
//            return times(0);
//        }
        verify(list, never()).add("Never added");

        //校验至少 / 至多
        verify(list, atLeastOnce()).add("Three times");
        verify(list, atLeast(2)).add("twice");
        verify(list, atMost(3)).add("Three times");
    }

    @Test
    public void testMockAnnotation() {
        when(list.get(0)).thenReturn("A");
        Assert.assertEquals("A", list.get(0));


        when(list.get(0)).thenReturn("B");
        Assert.assertEquals("B", list.get(0));

        //模糊匹配
        when(list.get(anyInt())).thenReturn("CCC");
        Assert.assertEquals("CCC", list.get(1));
        Assert.assertEquals("CCC", list.get(3));
        Assert.assertEquals("CCC", list.get(5));
    }
}
