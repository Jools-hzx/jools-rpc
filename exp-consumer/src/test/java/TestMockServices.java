import com.jools.exp.common.service.TestMockService;
import com.jools.joolsrpc.model.RpcRequest;
import com.jools.joolsrpc.model.RpcResponse;
import com.jools.joolsrpc.proxy.ServiceProxyFactory;
import com.jools.joolsrpc.server.HttpServer;
import com.jools.rpc.core.config.RpcConfig;
import org.junit.Test;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/4 11:02
 * @description: TODO
 */
public class TestMockServices {

    @Test
    public void testMockServices() {

        TestMockService mockServiceProxy = ServiceProxyFactory.getProxy(TestMockService.class);

        System.out.println("测试 Mock RpcRequest / RpcResponse");
        RpcRequest mockRpcRequest = mockServiceProxy.getRpcRequest();
        RpcResponse mockResponse = mockServiceProxy.getRpcResponse();
        System.out.println(mockRpcRequest);
        System.out.println(mockResponse + "\n");

        System.out.println("测试 Mock RpcConfig");
        RpcConfig mockRpcConfig = mockServiceProxy.getRpcConfig();
        System.out.println(mockRpcConfig +"\n");

        System.out.println("测试 Mock HttpServer");
        HttpServer mockHttpServer = mockServiceProxy.getHttpServer();
        mockHttpServer.doStart(6666);
    }
}
