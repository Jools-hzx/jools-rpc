package com.jools.rpc.server.tcp;

import com.jools.rpc.RpcApplication;
import com.jools.rpc.interceptor.InterceptorFactory;
import com.jools.rpc.interceptor.InterceptorKeys;
import com.jools.rpc.interceptor.RpcHandlerInterceptor;
import com.jools.rpc.model.RpcRequest;
import com.jools.rpc.model.RpcResponse;
import com.jools.rpc.protocol.*;
import com.jools.rpc.registry.LocalRegistry;
import com.jools.rpc.serializer.Serializer;
import com.jools.rpc.serializer.SerializerFactory;
import com.jools.rpc.spi.SpiLoader;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jools He
 * @description: Tcp 请求处理器 - 服务提供者用于处理消费者的请求和响应回复
 * 优化: 基于装饰者模式引入 tcp 半包粘包处理器
 */

@Slf4j
public class TcpServerHandler implements Handler<NetSocket> {

    private final List<RpcHandlerInterceptor> INTERCEPTOR_LIST = initInterceptor();

    private List<RpcHandlerInterceptor> initInterceptor() {
        List<RpcHandlerInterceptor> interceptors = new ArrayList<>();
        if (RpcApplication.getRpcConfig().isEnableInterceptor()) {
            try {
                return SpiLoader.getAllInstance(RpcHandlerInterceptor.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return interceptors;
    }

    @Override
    public void handle(NetSocket socket) {

        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            //解码反序列化，由 Buffer 获取 ProtocolMessage
            ProtocolMessage<RpcRequest> requestProtocolMessage;
            try {
                requestProtocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                log.error("Decode error --- TcpServerHandler --- handle()");
                throw new RuntimeException(e);
            }

            //获取 ProtocolMessage 请求; 请求的消息体为 RpcRequest
            RpcRequest rpcRequest = requestProtocolMessage.getBody();

            // 拦截器拦截
            if (!INTERCEPTOR_LIST.isEmpty()) {
                for (RpcHandlerInterceptor interceptor : INTERCEPTOR_LIST) {
                    try {
                        if (!interceptor.preHandle(rpcRequest)) {
                            log.error("RpcHandlerInterceptor preHandle fail");
                            //编码序列化 - 由 ProtocolMessage 获取 Buffer
                            ProtocolMessage<RpcResponse> interceptorPreHandleFail =
                                    getErrorResponse(requestProtocolMessage.getHeader(), new RuntimeException("RpcHandlerInterceptor preHandle fail"));
                            Buffer buf = ProtocolMessageEncoder.encode(interceptorPreHandleFail);
                            socket.write(buf);
                            return;
                        }
                    } catch (Exception e) {
                        ProtocolMessage<RpcResponse> errorResponse = getErrorResponse(requestProtocolMessage.getHeader(), e);
                        Buffer buf = ProtocolMessageEncoder.encode(errorResponse);
                        socket.write(buf);
                        return;
                    }
                }
            }

            String serviceName = rpcRequest.getServiceName();   //调用全类名
            String methodName = rpcRequest.getMethodName();     //调用方法名
            Object[] params = rpcRequest.getParams();           //调用实参
            Class<?>[] paramTypes = rpcRequest.getParamTypes(); //调用形参

            //反射调用
            Object result;
            Object serviceInstance;
            RpcResponse rpcResponse = new RpcResponse();
            try {
                //查询本地注册服务，反射调用
                serviceInstance = LocalRegistry.getService(serviceName);
                Method method = serviceInstance.getClass().getDeclaredMethod(methodName, paramTypes);
                result = method.invoke(serviceInstance, params);

                //构建 RpcResponse - 成功
                //优化，基于 State 枚举类的内容
                rpcResponse.setMsg(ProtocolMessageStateEnum.REQUEST_SUCCESS.getText());
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setData(result);
            } catch (Exception e) {
                log.error("Error when reflecting service instance for rpc request service:{}", rpcRequest.getServiceName());
                //构建 RcpResponse - 失败
                //优化，基于 State 枚举类内容
                rpcResponse.setMsg(ProtocolMessageStateEnum.RESPONSE_FAIL.getText());
                rpcResponse.setException(e);
            }

            //基于消费者发送的 ProtocolMessage 获取消息头，复用字段保证: 魔数 + 版本号 一致
            ProtocolMessage.Header header = requestProtocolMessage.getHeader();
            header.setMessageType(ProtocolMessageTypeEnum.RESPONSE.getMessageType());
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);

            try {
                //编码序列化 - 由 ProtocolMessage 获取 Buffer
                Buffer buf = ProtocolMessageEncoder.encode(responseProtocolMessage);
                socket.write(buf);
            } catch (Exception e) {
                throw new RuntimeException("Encode fail at TcpServerHandler --- handle()");
            }
        });

        //处理连接
        socket.handler(bufferHandlerWrapper);
    }


    private ProtocolMessage<RpcResponse> getErrorResponse(ProtocolMessage.Header header, Exception e) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setMsg(ProtocolMessageStateEnum.RESPONSE_FAIL.getText());
        rpcResponse.setException(e);
        //基于消费者发送的 ProtocolMessage 获取消息头，复用字段保证: 魔数 + 版本号 一致
        header.setMessageType(ProtocolMessageTypeEnum.RESPONSE.getMessageType());
        ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
        return responseProtocolMessage;
    }
}
