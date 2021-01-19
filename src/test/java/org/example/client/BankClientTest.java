package org.example.client;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.example.models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankClientTest {

    private BankServiceGrpc.BankServiceBlockingStub blockingStub;

    private BankServiceGrpc.BankServiceStub bankServiceStub;

    @BeforeAll
    public void setup() {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        this.blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
        this.bankServiceStub = BankServiceGrpc.newStub(managedChannel);
    }

    @Test
    public void balanceTest() {
        BankServiceRequest bankServiceRequest = BankServiceRequest.newBuilder()
                .setAccountNumber(10)
                .build();

        Balance balance = blockingStub.getBalance(bankServiceRequest);

        System.out.println(
                "Recieved : " + balance.getBalance()
        );
    }

    @Test
    public void withdrawTest() {
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder().setAccountNumber(10).setAmount(50).build();
        this.blockingStub.withdrawMoney(withdrawRequest).forEachRemaining(money -> {
            System.out.println(
                    "Recieved Money :" + money.getValue()
            );
        });
    }

    @Test
    public void withDrawAsyncTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder().setAccountNumber(10).setAmount(20).build();
        this.bankServiceStub.withdrawMoney(withdrawRequest, new MoneyResponseObserver(latch));
        latch.await();
    }

    @Test
    public void depositStreamingTest() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<DepositRequest> depositRequestStreamObserver = this.bankServiceStub.depositMoney(new StreamObserver<Balance>() {

            @Override
            public void onNext(Balance balance) {
                System.out.println(
                        "Balance is :" + balance.getBalance()
                );
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println(
                        "Server processing completed!!!"
                );
                latch.countDown();
            }
        });

        for (int i = 0; i < 10; i++) {
            DepositRequest depositRequest = DepositRequest.newBuilder().setAccountNumber(10).setAmount(10).build();
            depositRequestStreamObserver.onNext(depositRequest);
        }
        depositRequestStreamObserver.onCompleted();
        latch.await();

    }
}
