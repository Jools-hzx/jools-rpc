package com.jools.rpc.fault.tolerant.queue;

import com.jools.rpc.fault.tolerant.ErrorTolerantKeys;
import com.jools.rpc.model.RpcRequest;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FailBackMessageQueueFactoryTest {

    @Test
    public void testGetQueue() {

        //Fail Back
        FailTolerantMessageQueue messageQueue = FailBackMessageQueueFactory.getMessageQueue(ErrorTolerantKeys.FAIL_BACK);
        assertNotNull(messageQueue);

        //Fail Over
        FailTolerantMessageQueue failOverMessageQueue = FailBackMessageQueueFactory.getMessageQueue(ErrorTolerantKeys.FAIL_OVER);
        assertNull(failOverMessageQueue);
    }

    @Test
    public void testFailBackQueue() {
        FailTolerantMessageQueue failBackMessageQueue = FailBackMessageQueueFactory.getMessageQueue(ErrorTolerantKeys.FAIL_BACK);
        List<RpcRequest> list = failBackMessageQueue.listAll();
        assertEquals(0, list.size());

        assertTrue(list.isEmpty());

        RpcRequest request = new RpcRequest();
        request.setServiceName("Mock Test Message");

        //Offer one message
        failBackMessageQueue.offer(request);
        List<RpcRequest> requests = failBackMessageQueue.listAll();
        assertEquals(1, requests.size());

        RpcRequest polled = failBackMessageQueue.poll();
        assertEquals(polled.getServiceName(), "Mock Test Message");

        assertTrue(failBackMessageQueue.isEmpty());
        assertTrue(failBackMessageQueue.listAll().isEmpty());
    }
}