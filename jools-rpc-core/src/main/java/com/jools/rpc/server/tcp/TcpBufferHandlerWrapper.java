package com.jools.rpc.server.tcp;

import com.jools.rpc.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/29 10:45
 * @description: Tcp 半包粘包处理器
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    private final RecordParser parser;

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        this.parser = initRecordParser(bufferHandler);
    }

    @Override
    public void handle(Buffer buffer) {
        this.parser.handle(buffer);
    }

    /**
     * 构造 - 解析固定长度的 Buffer 解析器；基于消息头内的第 13 个字节开始的 bodySize 记录
     *
     * @param bufferHandler
     * @return
     */
    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        //构造 recordParser
        RecordParser recordParser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_SIZE);
        recordParser.setOutput(new Handler<Buffer>() {
            //初始化
            int size = -1;
            //一次完整的读取(头 + 消息体)
            Buffer resultBuffer = Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                if (-1 == size) {
                    //获取到消息头，解析第 13 字节 ~ 16 个字节内的数据[基于自定义消息体 ProtocolMessage 规定]，记录消息体长度
                    size = buffer.getInt(13);
                    recordParser.fixedSizeMode(size);
                    //写入消息头
                    resultBuffer.appendBuffer(buffer);
                } else {
                    //写入消息体
                    resultBuffer.appendBuffer(buffer);
                    //拼接为完整的 Buffer
                    bufferHandler.handle(resultBuffer);
                    //重置解析size，先获取下一个消息包的消息头
                    recordParser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_SIZE);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });

        //返回构造的 RecordParser 作为属性被 TcpBufferHandlerWrapper 持有
        return recordParser;
    }
}
