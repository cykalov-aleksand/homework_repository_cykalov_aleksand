package org.example.homework.processor.homework;

import org.example.homework.model.Message;
import org.example.homework.processor.Processor;

import java.time.LocalTime;
import java.util.Objects;
import java.util.function.Supplier;

public class ThrowOnExceptionEvenSecond implements Processor {
    private final Supplier<Integer> secondSupplier;

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
            throw new IllegalStateException("Чётная секунда: " + second);
        }
        return message;
    }

    public Supplier<Integer> getSecondSupplier() {
        return secondSupplier;
    }
}
