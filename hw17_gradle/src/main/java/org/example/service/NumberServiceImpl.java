package org.example.service;


import io.grpc.stub.StreamObserver;
import org.example.grpc.FirstLastRequest;
import org.example.grpc.NumberMessage;
import org.example.grpc.NumberServiceGrpc;

public class NumberServiceImpl extends NumberServiceGrpc.NumberServiceImplBase {

    @Override
    public void getNumbers(FirstLastRequest request, StreamObserver<NumberMessage> responseObserver) {
        long first = request.getFirstValue();
        long last = request.getLastValue();

        // Запускаем стрим в отдельном потоке, чтобы не блокировать gRPC-поток
        new Thread(() -> {
            try {
                for (long v = first + 1; v <= last; v++) {
                    responseObserver.onNext(
                            NumberMessage.newBuilder()
                                    .setValue(v)
                                    .build()
                    );
                    if (v < last) {
                        Thread.sleep(2000); // пауза только МЕЖДУ числами, не перед первым и не после последнего
                    }
                }
                responseObserver.onCompleted();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                responseObserver.onError(e);
            }
        }).start();
    }
}

