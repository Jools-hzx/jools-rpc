package com.jools.rpc.protocol;

import cn.hutool.core.util.IdUtil;
import com.jools.rpc.constant.RpcConstant;
import com.jools.rpc.model.RpcRequest;
import com.jools.rpc.model.RpcResponse;
import io.vertx.core.buffer.Buffer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/27 11:54
 * @description: TODO
 */
public class ProtocolMessageDecodeEncoderTest {

    ProtocolMessage<RpcRequest> requestProtocolMessage;
    ProtocolMessage<RpcResponse> responseProtocolMessage;

    @Before
    public void init() throws IOException {
        requestProtocolMessage = new ProtocolMessage<>();
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName("testService");
        rpcRequest.setMethodName("testMethod");
        rpcRequest.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        rpcRequest.setParamTypes(new Class[]{String.class});
        rpcRequest.setParams(new Object[]{"param1", "param2"});
        requestProtocolMessage.setBody(rpcRequest);

        //构建协议请求消息头
        ProtocolMessage.Header reqheader = new ProtocolMessage.Header();
        reqheader.setMessageId(IdUtil.getSnowflakeNextId());    //随机 id
        reqheader.setMagic(ProtocolConstant.MAGIC);
        reqheader.setVersion(ProtocolConstant.VERSION);
        reqheader.setSerializerType(ProtocolSerializerTypeEnum.JDK_SERIALIZER.getType());
        reqheader.setMessageType(ProtocolMessageTypeEnum.REQUEST.getMessageType());
        reqheader.setMessageState(ProtocolMessageStateEnum.REQUEST_SUCCESS.getVal());
        reqheader.setBodySize(0);
        //填充消息体长度
        requestProtocolMessage.setHeader(reqheader);

        //构建请求响应对象
        responseProtocolMessage = new ProtocolMessage<>();
        RpcResponse response = RpcResponse.builder().
                msg("Test RpcResponse").
                dataType(Object.class).
                data("Test RpcResponse").
                exception(new RuntimeException()).
                build();

        responseProtocolMessage.setBody(response);

        ProtocolMessage.Header respheader = new ProtocolMessage.Header();
        respheader.setMessageId(0L);
        respheader.setMagic(ProtocolConstant.MAGIC);
        respheader.setVersion(ProtocolConstant.VERSION);
        respheader.setMessageType(ProtocolMessageTypeEnum.RESPONSE.getMessageType());
        respheader.setMessageState(ProtocolMessageStateEnum.REQUEST_SUCCESS.getVal());
        respheader.setBodySize(responseProtocolMessage.getBody().toString().getBytes().length);
        responseProtocolMessage.setHeader(respheader);
    }

    @Test
    public void testRequestEncode() throws IOException {
        int bodySize = requestProtocolMessage.getHeader().getBodySize();
        System.out.println(bodySize);   //未序列化之前消息体长度未知: 设置为 0
        Buffer reqBuf = ProtocolMessageEncoder.encode(this.requestProtocolMessage);
        ProtocolMessage<?> decodeMessage = ProtocolMessageDecoder.decode(reqBuf);
        Assert.assertNotNull(decodeMessage);
    }
}
