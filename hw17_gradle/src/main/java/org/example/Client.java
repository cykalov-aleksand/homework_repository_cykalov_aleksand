package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.example.grpc.FirstLastRequest;
import org.example.grpc.NumberMessage;
import org.example.grpc.NumberServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import static org.example.Constants.*;
import static org.example.Constants.FIRST_VALUE;
import static org.example.Constants.LAST_VALUE;

public class Client {
    private static final String HOST = "localhost";
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    // Последнее число от сервера. Стрим пишет сюда, тик клиента читает и сбрасывает.
    private static final AtomicLong lastValueFromServer = new AtomicLong(0);

    private static long takeLastValue() {
        // Забираем значение и сразу сбрасываем в 0 — число используется только один раз
        return lastValueFromServer.getAndSet(0);
    }

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(HOST, PORT.getNumber())
                .usePlaintext()
                .build();

        var stub = NumberServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        logger.info("Отправляем запрос: first = {}, last = {}",
                FIRST_VALUE.getNumber(), LAST_VALUE.getNumber());

        stub.getNumbers(
                FirstLastRequest.newBuilder()
                        .setFirstValue(FIRST_VALUE.getNumber())
                        .setLastValue(LAST_VALUE.getNumber())
                        .build(),
                new StreamObserver<>() {
                    @Override
                    public void onNext(NumberMessage msg) {
                        logger.info("Получено число: {}", msg.getValue());
                        lastValueFromServer.set(msg.getValue());
                    }

                    @Override
                    public void onError(Throwable t) {
                        logger.error("Ошибка стрима: ", t);
                        latch.countDown();
                    }

                    @Override
                    public void onCompleted() {
                        logger.info("Стрим завершён.");
                        latch.countDown();
                    }
                }
        );

        // Цикл клиента: 0..50, раз в секунду
        long currentValue = 0;
        for (int i = 0; i <= 50; i++) {
            currentValue = currentValue + takeLastValue() + 1;
            logger.info("currentValue = {}", currentValue);
            Thread.sleep(1000);
        }

        channel.shutdown();
    }
}