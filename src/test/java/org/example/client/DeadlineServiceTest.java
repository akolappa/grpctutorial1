package org.example.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.example.models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeadlineServiceTest {

    private BankServiceGrpc.BankServiceBlockingStub blockingStub;

    private BankServiceGrpc.BankServiceStub bankServiceStub;

    @BeforeAll
    public void setup(){
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        this.blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
        this.bankServiceStub = BankServiceGrpc.newStub(managedChannel);
    }
    @Test
    public void balanceTest(){
        BankServiceRequest bankServiceRequest = BankServiceRequest.newBuilder()
                .setAccountNumber(10)
                .build();

        Balance balance = blockingStub
                            .withDeadlineAfter(2, TimeUnit.SECONDS)
                            .getBalance(bankServiceRequest);

        System.out.println(
                "Recieved : "+balance.getBalance()
        );
    }

    @Test
    public void withdrawTest(){
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder().setAccountNumber(10).setAmount(50).build();
        try{
            this.blockingStub
                    .withDeadlineAfter(4,TimeUnit.SECONDS)
                    .withdrawMoney(withdrawRequest)
                    .forEachRemaining(money -> {
                        System.out.println(
                                "Recieved Money :" + money.getValue()
                        );
                    });
        }catch (StatusRuntimeException se){
            System.out.println("DEADLINE _ EXCEEDED");
        }

    }

    @Test
    public void withDrawAsyncTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder().setAccountNumber(10).setAmount(20).build();
        this.bankServiceStub.withdrawMoney(withdrawRequest,new MoneyResponseObserver(latch));
        latch.await();
    }
}
