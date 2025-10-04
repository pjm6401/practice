package com.project.api.controller;


import com.project.grpc.HelloRequest;
import com.project.grpc.HelloResponse;
import com.project.grpc.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        String reply = "Hello " + request.getName() + " from gRPC!";
        HelloResponse response = HelloResponse.newBuilder().setMessage(reply).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

