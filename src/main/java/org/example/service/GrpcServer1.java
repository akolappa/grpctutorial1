package org.example.service;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer1 {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(7575)
                .addService(new BankServerImpl())
                .build();

        server.start();

        server.awaitTermination();
    }
}
