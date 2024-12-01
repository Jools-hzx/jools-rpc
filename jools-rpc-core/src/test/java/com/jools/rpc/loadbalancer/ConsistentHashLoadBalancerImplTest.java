package com.jools.rpc.loadbalancer;

import com.jools.rpc.model.ServiceMetaInfo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class ConsistentHashLoadBalancerImplTest {

    private ConsistentHashLoadBalancer loadBalancer = new ConsistentHashLoadBalancer();

    @Test
    public void selectService() {
        ServiceMetaInfo serviceMetaInfo1 = new ServiceMetaInfo();
        serviceMetaInfo1.setServiceHost("127.0.0.1");
        serviceMetaInfo1.setServicePort(8888);
        serviceMetaInfo1.setServiceName("Test Service Name");
        serviceMetaInfo1.setMetadata(new HashMap<>());


        ServiceMetaInfo serviceMetaInfo2 = new ServiceMetaInfo();
        serviceMetaInfo2.setServiceHost("192.168.23.128");
        serviceMetaInfo2.setServicePort(8989);
        serviceMetaInfo2.setServiceName("Test Service Name");
        serviceMetaInfo2.setMetadata(new HashMap<>());

        List<ServiceMetaInfo> list = new ArrayList<>();
        list.add(serviceMetaInfo1);
        list.add(serviceMetaInfo2);

        ServiceMetaInfo selectServiceMetaInfo = loadBalancer.selectService(new HashMap<>() {{
            put("param1", 1);
            put("param2", 2);
        }}, list);

        System.out.println(selectServiceMetaInfo.toString());
        /*
            输出:
            ServiceMetaInfo(
                serviceName=Test Service Name,
                serviceVersion=1.0,
                serviceHost=192.168.23.128,
                servicePort=8989,
                serviceGroup=default,
                registerTime=,
                startTime=,
                protocol=http,
                serviceWeight=0,
                metadata={}
             )
         */
    }
}