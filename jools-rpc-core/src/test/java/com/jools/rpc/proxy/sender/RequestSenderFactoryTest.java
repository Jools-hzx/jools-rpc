package com.jools.rpc.proxy.sender;

import com.jools.rpc.model.registryInfo.Protocol;
import junit.framework.TestCase;
import org.junit.Assert;

public class RequestSenderFactoryTest extends TestCase {

    public void testGetSender() {

        RequestSender defaultRequestSender = RequestSenderFactory.DEFAULT_REQUEST_SENDER;
        System.out.println(defaultRequestSender.getClass().getSimpleName());        // TcpRequestSender

        RequestSender sender01 = RequestSenderFactory.getSender(Protocol.TCP);
        Assert.assertNotNull(sender01);
        System.out.println(sender01.getClass().getSimpleName());                // TcpRequestSender

        RequestSender sender02 = RequestSenderFactory.getSender(Protocol.HTTP); // HttpRequestSender
        Assert.assertNotNull(sender02);
        System.out.println(sender02.getClass().getSimpleName());

        try {
            RequestSenderFactory.getSender(Protocol.Dubbo);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // 输出: Class:{com.jools.rpc.proxy.sender.RequestSender} doesn't have key:{dubbo} impl instance
        }
    }
}