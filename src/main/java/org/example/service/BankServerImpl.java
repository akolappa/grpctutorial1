package org.example.service;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.example.models.*;

public class BankServerImpl extends BankServiceGrpc.BankServiceImplBase {

    /**
     * @param request
     * @param responseObserver
     */

    private static int totalbalance = 80;
    @Override
    public void getBalance(BankServiceRequest request, io.grpc.stub.StreamObserver<Balance> responseObserver) {
        int accountNumber = request.getAccountNumber();
        System.out.println("Request for account number: " + accountNumber);
        Balance balance = Balance.newBuilder()
                .setBalance(accountNumber * 10)
                .build();

        responseObserver.onNext(balance);
        responseObserver.onCompleted();
    }

    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void withdrawMoney(WithdrawRequest request, StreamObserver<Money> responseObserver) {
        int account_number = request.getAccountNumber();
        int amount = request.getAmount();
        //int balance = 80;

        if(amount > totalbalance){
            Status status = Status.FAILED_PRECONDITION.withDescription("No sufficient balance !!!");
            responseObserver.onError(status.asRuntimeException());
            return;
        }

        for (int i = 0; i < amount/10; i++) {
            Money money = Money.newBuilder().setValue(10).build();
            totalbalance -= 10;
            responseObserver.onNext(money);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        responseObserver.onCompleted();
    }

    /**
     * @param responseObserver
     */
    @Override
    public StreamObserver<DepositRequest> depositMoney(StreamObserver<Balance> responseObserver) {
        return new StreamObserver<DepositRequest>() {

            @Override
            public void onNext(DepositRequest depositRequest) {
                int accountnum = depositRequest.getAccountNumber();
                int amount = depositRequest.getAmount();
                totalbalance += amount;
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                Balance balance = Balance.newBuilder().setBalance(totalbalance).build();
                responseObserver.onNext(balance);
                responseObserver.onCompleted();
            }
        };
    }
}
