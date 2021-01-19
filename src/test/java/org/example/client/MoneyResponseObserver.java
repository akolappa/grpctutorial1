package org.example.client;

import io.grpc.stub.StreamObserver;
import org.example.models.Money;

import java.util.concurrent.CountDownLatch;

public class MoneyResponseObserver implements StreamObserver<Money> {

    private CountDownLatch latch;

    public MoneyResponseObserver(CountDownLatch latch) {
        this.latch = latch;
        latch.countDown();
    }

    @Override
    public void onNext(Money money) {
        System.out.println(
                "Recieved :" + money.getValue()
        );

    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println(
                throwable.getMessage()
        );
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println(
                "Server processing completed!!!"
        );
        latch.countDown();
    }
}
