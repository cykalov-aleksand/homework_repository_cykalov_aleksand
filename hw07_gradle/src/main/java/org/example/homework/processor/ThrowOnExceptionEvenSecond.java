package org.example.homework.processor;

import org.example.homework.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.Objects;
import java.util.function.Supplier;

public class ThrowOnExceptionEvenSecond implements Processor {
    private final Supplier<Integer> secondSupplier;
    private static final Logger logger= LoggerFactory.getLogger(ThrowOnExceptionEvenSecond.class);

    public ThrowOnExceptionEvenSecond(Supplier<Integer> secondSupplier) {
        this.secondSupplier = Objects.requireNonNull(secondSupplier,
                "Поставщик секунд не может быть null");
    }

    public ThrowOnExceptionEvenSecond() {
        this(() -> LocalTime.now().getSecond());
    }

    public ThrowOnExceptionEvenSecond(int fixedSecond) {
        this(() -> fixedSecond);
    }

    @Override
    public Message process(Message message) {
        int second = secondSupplier.get();
        if (second % 2 == 0) {
logger.error("ThrowOnExceptionEvenSecond: исключение по чётной секунде {}",second);
            throw new IllegalStateException("Чётная секунда: " + second);
        }
        logger.info("ThrowOnExceptionEvenSecond: {}",message);
        return message;
    }

    public Supplier<Integer> getSecondSupplier() {
        return secondSupplier;
    }
}
