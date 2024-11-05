package com.jools.exp.common.service.proxy;


/**
 * @author Jools He
 * @version 1.0
 * @date 2024/10/30 11:23
 * @description: TODO
 */
//public class UserServiceStaticProxy implements UserService {
//
//    @Override
//    public com.jools.rpc.protoc.User getUser(com.jools.rpc.protoc.User user) {
//
//        //指定序列化器
//        Serializer serializer = new JdkSerializer();
//
//        //构建请求
//        RpcRequest rpcRequest = RpcRequest.builder()
//                .serviceName(UserService.class.getName())
//                .methodName("getUser")
//                .paramTypes(new Class[]{com.jools.rpc.protoc.User.class})
//                .params(new Object[]{user})
//                .build();
//
//        //序列化请求对象
//        //发送请求
//        try {
//            byte[] bytes = serializer.serialize(rpcRequest);
//            byte[] result;
//            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8888")
//                    .body(bytes)
//                    .execute()) {
//                //获取响应结果
//                result = httpResponse.bodyBytes();
//            }
//
//            //反序列化响应结果
//            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
//            return (com.jools.rpc.protoc.User) rpcResponse.getData();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}





