package org.example.client.loadbalancing;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolverRegistry;
import io.grpc.stub.StreamObserver;
import org.example.client.MoneyResponseObserver;
import org.example.models.*;
import org.example.service.serviceregistry.BankNameResolverProvider;
import org.example.service.serviceregistry.ServiceRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankClientLoadBalancingTest {

    private BankServiceGrpc.BankServiceBlockingStub blockingStub;

    @BeforeAll
    public void setup(){

        ServiceRegistry.register("bank-service", Arrays.asList("127.0.0.1:6565","127.0.0.1:7575"));

        NameResolverRegistry.getDefaultRegistry().register(new BankNameResolverProvider());

        ManagedChannel managedChannel = ManagedChannelBuilder
                //.forAddress("localhost", 8585)
                .forTarget("bank-service")
                .defaultLoadBalancingPolicy("round_robin")
                .usePlaintext()
                .build();

        this.blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
    }
    @Test
    public void balanceTest(){
        for (int i = 0; i < 100; i++) {
            BankServiceRequest bankServiceRequest = BankServiceRequest.newBuilder()
                    .setAccountNumber(i)
                    .build();
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            Balance balance = blockingStub.getBalance(bankServiceRequest);
            System.out.println(
                    "Recieved : "+balance.getBalance()
            );

        }
    }
}
