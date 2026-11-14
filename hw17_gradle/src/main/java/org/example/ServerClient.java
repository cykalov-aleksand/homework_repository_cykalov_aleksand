package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.example.service.NumberServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.example.Constants.PORT;


public class ServerClient {
    private static final Logger logger= LoggerFactory.getLogger(ServerClient.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(PORT.getNumber())
                .addService(new NumberServiceImpl())
                .build();

        server.start();
        logger.info("Сервер запущен на порту {}", PORT.getNumber());

        // Корректное завершение при Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.awaitTermination();
    }
}

