package com.jools.rpc.registry;

import com.jools.rpc.config.RegistryConfig;
import com.jools.rpc.model.ServiceMetaInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class EtcdRegistryTest {

    /**
     * Etcd 作为注册中心实现类
     */
    private Registry registry = new EtcdRegistry();

    @Before
    public void setUp() {

        /*
          RegistryConfig 默认配置 etcd 类型，无用户名和密码，默认超时时间 1s
         */
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("http://localhost:2379");

        /*
            启动
            基于 [配置中ip + 端口 + 过期时间创建 client]

            this.client = Client.builder()
                    .endpoints(registryConfig.getAddress())
                    .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                    .build();
         */
        registry.init(registryConfig);
    }

    @Test
    public void registry() throws ExecutionException, InterruptedException {
        //服务元数据 01
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("v1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(6666);
        registry.registry(serviceMetaInfo);

        //服务元数据 02
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("v1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(7777);
        registry.registry(serviceMetaInfo);

        //服务元数据 03
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("v2.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(6666);

        //key - /rpc/myService:port 因此总共注册上的服务应该有 3 个
        /*
            实际存储的键值对: /rpc/ + ServiceKey(服务名 + 版本号)
            registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceKey();
         */
        registry.registry(serviceMetaInfo);
    }

    @Test
    public void unRegistry() {
        //下线服务，服务 key
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("v1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(6666);

        /*
        下线服务: 实际下线服务的 key 为键值对: /rpc/ + ServiceKey(服务名 + 版本号)
        this.kvClient.delete(ByteSequence.from(
                    ETCD_ROOT_PATH + serviceMetaInfo.getServiceNode(),
                    StandardCharsets.UTF_8));
         */
        registry.unRegistry(serviceMetaInfo);
    }

    @Test
    public void serviceDiscovery() throws ExecutionException, InterruptedException {

        //先注册一个
        //服务元数据 02
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("v1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(7777);
        registry.registry(serviceMetaInfo);

        List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());

        //确认仅有一个服务
        Assert.assertEquals(1, serviceMetaInfos.size());

        for (ServiceMetaInfo metaInfo : serviceMetaInfos) {
            System.out.println(metaInfo);
        }
    }

    @Test
    public void destory() {
        registry.destory();
        System.out.println("Etcd Registry Service destroy complete!");
    }
}