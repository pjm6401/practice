package com.project.api.service;

import com.project.grpc.HelloRequest;
import com.project.grpc.HelloResponse;
import com.project.grpc.HelloServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class GrpcClientService {

    @GrpcClient("local-grpc-server")
    private HelloServiceGrpc.HelloServiceBlockingStub helloServiceBlockingStub;

    public String sendMessage(String name) {
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloResponse response = helloServiceBlockingStub.sayHello(request);
        return response.getMessage();
    }
}
