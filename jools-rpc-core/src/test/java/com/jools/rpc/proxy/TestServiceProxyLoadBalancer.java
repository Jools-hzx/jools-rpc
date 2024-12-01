package com.jools.rpc.proxy;

import cn.hutool.bloomfilter.filter.FNVFilter;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.jools.rpc.loadbalancer.*;
import com.jools.rpc.model.ServiceMetaInfo;
import com.jools.rpc.model.registryInfo.ServiceWeight;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/1 10:41
 * @description: 负载均衡器测试
 */
public class TestServiceProxyLoadBalancer {

    final LoadBalancer loadBalancer = new ConsistentHashLoadBalancer();

    final LoadBalancer randomLoadBalancer = new RandomLoadBalancer();
    final LoadBalancer roundLoadBalancer = new RoundRobinLoadBalancer();
    final LoadBalancer roundWeightLoadBalancer = new RoundWeightRoundBalancer();
    private List<ServiceMetaInfo> serviceMetaInfoList = new ArrayList<>();

    @Before
    public void registerServices() {

        //服务列表
        //服务一
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("testService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(6666);
        serviceMetaInfo.setServiceWeight(ServiceWeight.FOUR);
        serviceMetaInfo.setCurrentWeight(serviceMetaInfo.getServiceWeight());

        //服务二
        ServiceMetaInfo serviceMetaInfo2 = new ServiceMetaInfo();
        serviceMetaInfo2.setServiceName("testService");
        serviceMetaInfo2.setServiceVersion("1.0");
        serviceMetaInfo2.setServiceHost("127.0.0.1");
        serviceMetaInfo2.setServicePort(7777);
        serviceMetaInfo2.setServiceWeight(ServiceWeight.ONE);
        serviceMetaInfo2.setCurrentWeight(serviceMetaInfo2.getServiceWeight());

        this.serviceMetaInfoList = Arrays.asList(serviceMetaInfo, serviceMetaInfo2);
    }

    @Test
    public void testSelectByRoundWeight() {

        //连续选择 10 次
        for (int i = 0; i < 10; i++) {
            ServiceMetaInfo selected = roundWeightLoadBalancer.selectService(new HashMap<>(), serviceMetaInfoList);
        }
    }

    @Test
    public void testSelectByRandom() {
        //请求参数
        HashMap<String, Object> requestParams = new HashMap<>();
        requestParams.put("serviceName", "testService");
        requestParams.put("methodName", "eat");

        //依次选取第一个
        ServiceMetaInfo select1 = randomLoadBalancer.selectService(requestParams, serviceMetaInfoList);
        System.out.printf("ServiceName:%s -- ServiceHost:%s -- ServiceProt:%s",
                select1.getServiceName(),
                select1.getServiceHost(),
                select1.getServicePort());
        //第二个
        ServiceMetaInfo select2 = randomLoadBalancer.selectService(requestParams, serviceMetaInfoList);
        System.out.printf("\nServiceName:%s  --  ServiceHost:%s -- ServiceProt:%s",
                select2.getServiceName(),
                select2.getServiceHost(),
                select2.getServicePort());

        //轮询，回到第一个
        ServiceMetaInfo select3 = randomLoadBalancer.selectService(requestParams, serviceMetaInfoList);
        System.out.printf("\nServiceName:%s -- ServiceHost:%s -- ServiceProt:%s",
                select3.getServiceName(),
                select3.getServiceHost(),
                select3.getServicePort());

        System.out.println("\nTest Pass ~");

        /*
        第一次输出结果:
        ServiceName:testService -- ServiceHost:localhost -- ServiceProt:6666
        ServiceName:testService  --  ServiceHost:127.0.0.1 -- ServiceProt:7777
        ServiceName:testService -- ServiceHost:localhost -- ServiceProt:6666
        Test Pass ~

        第二次输出结果:
        ServiceName:testService -- ServiceHost:localhost -- ServiceProt:6666
        ServiceName:testService  --  ServiceHost:localhost -- ServiceProt:6666
        ServiceName:testService -- ServiceHost:127.0.0.1 -- ServiceProt:7777
        Test Pass ~

        第三次输出结果:
        ServiceName:testService -- ServiceHost:127.0.0.1 -- ServiceProt:7777
        ServiceName:testService  --  ServiceHost:localhost -- ServiceProt:6666
        ServiceName:testService -- ServiceHost:localhost -- ServiceProt:6666
        Test Pass ~
         */
    }

    @Test
    public void testSelectByRound() {
        //请求参数
        HashMap<String, Object> requestParams = new HashMap<>();
        requestParams.put("serviceName", "testService");
        requestParams.put("methodName", "eat");

        //依次选取第一个
        ServiceMetaInfo select1 = roundLoadBalancer.selectService(requestParams, serviceMetaInfoList);
        System.out.println(JSONUtil.toJsonStr(select1));

        //第二个
        ServiceMetaInfo select2 = roundLoadBalancer.selectService(requestParams, serviceMetaInfoList);
        System.out.println(JSONUtil.toJsonStr(select2));
        assert select1 != select2;

        //轮询，回到第一个
        ServiceMetaInfo select3 = roundLoadBalancer.selectService(requestParams, serviceMetaInfoList);
        System.out.println(JSONUtil.toJsonStr(select3));
        assert select1 == select3;

        System.out.println("Test Pass ~");

        /*
            {"serviceName":"testService","serviceVersion":"1.0","serviceHost":"127.0.0.1","servicePort":7777, ....}
            {"serviceName":"testService","serviceVersion":"1.0","serviceHost":"localhost","servicePort":6666, ....}
            {"serviceName":"testService","serviceVersion":"1.0","serviceHost":"127.0.0.1","servicePort":7777, ....}
            Test Pass ~
         */
    }

    @Test
    public void testSelectByConsistentHash() {
        //请求参数
        HashMap<String, Object> requestParams = new HashMap<>();
        requestParams.put("serviceName", "testService");
        requestParams.put("methodName", "eat");

        //重复调用，应该 Hash 值一致
        ServiceMetaInfo select1 = loadBalancer.selectService(requestParams, serviceMetaInfoList);
        System.out.println(JSONUtil.toJsonStr(select1));

        ServiceMetaInfo select2 = loadBalancer.selectService(requestParams, serviceMetaInfoList);
        System.out.println(JSONUtil.toJsonStr(select2));

        ServiceMetaInfo select3 = loadBalancer.selectService(requestParams, serviceMetaInfoList);
        System.out.println(JSONUtil.toJsonStr(select3));

        //校验是否相等
        Assert.equals(select1, select2);
        Assert.equals(select2, select3);
        System.out.println("Test Pass~");

        /*
         输出:
         {
            "serviceName":"testService",
            "serviceVersion":"1.0",
            "serviceHost":"localhost",
            "servicePort":6666,
            "serviceGroup":"default",
            "registerTime":"",
            "startTime":"",
            "protocol":"http",
            "serviceWeight":"0"
         }
         Test Pass~
         */
    }
}
