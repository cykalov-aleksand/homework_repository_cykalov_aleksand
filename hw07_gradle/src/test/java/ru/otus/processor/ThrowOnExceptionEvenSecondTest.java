package ru.otus.processor;

import org.example.homework.model.Message;
import org.example.homework.processor.Processor;
import org.example.homework.processor.ThrowOnExceptionEvenSecond;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class ThrowOnExceptionEvenSecondTest {

    private Message message;
    private final Supplier<Integer> evenSecond = () -> 10;   // чётная
    private final Supplier<Integer> oddSecond = () -> 11;   // нечётная
    private static final Logger logger = LoggerFactory.getLogger(ThrowOnExceptionEvenSecondTest.class);

    @BeforeEach
    void setUp() {
        // Создаём тестовое сообщение
        message = new Message.Builder(1L)
                .field1("test")
                .field2("data")
                .field11("value11")
                .field12("value12")
                .field13(null)
                .build();
    }

    @Test
    void shouldThrowExceptionOnEvenSecond() {
        logger.info("Проводим тест на работу метода при четной секунде");
        Processor processor = new ThrowOnExceptionEvenSecond(evenSecond);
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> processor.process(message),
                "Ожидалось исключение в чётную секунду"
        );

        assertTrue(exception.getMessage().contains("Чётная секунда"));
        assertTrue(exception.getMessage().contains("10"));
    }

    @Test
    void shouldNotThrowOnOddSecond() {
        logger.info("Проводим тест на работу метода при не четной секунде");
        Processor processor = new ThrowOnExceptionEvenSecond(oddSecond);
        assertDoesNotThrow(
                () -> processor.process(message),
                "В нечётную секунду исключение не должно выбрасываться"
        );
    }

    @Test
    void shouldUseCurrentTimeInDefaultConstructor() {
        logger.info("Тест на работу метода с реальным временем");
        Processor processor = new ThrowOnExceptionEvenSecond();
        int currentSecond = java.time.LocalTime.now().getSecond();
        if (currentSecond % 2 == 0) {
            assertThrows(IllegalStateException.class, () -> processor.process(message));
        } else {
            assertDoesNotThrow(() -> processor.process(message));
        }
    }

    @Test
    void shouldWorkWithFixedSecondViaIntConstructor() {
        logger.info("Тест на проверку работы метода по времени заданному через конструктор");
        Processor evenProcessor = new ThrowOnExceptionEvenSecond(4);
        Processor oddProcessor = new ThrowOnExceptionEvenSecond(5);

        // Then
        assertThrows(IllegalStateException.class, () -> evenProcessor.process(message));
        assertDoesNotThrow(() -> oddProcessor.process(message));
    }

    @Test
    void shouldNotAllowNullSupplier() {
        logger.info("Тест на проверку выбрасывания исключения при задании в конструкторе времени равном null");
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new ThrowOnExceptionEvenSecond((Supplier<Integer>) null),
                "Конструктор должен отвергать null"
        );
    }

    @Test
    void shouldReturnSameMessageWhenNoException() {
        logger.info("Тест на проверку возвращения исходного объекта при нечётной секунде");
        Processor processor = new ThrowOnExceptionEvenSecond(oddSecond);
        Message result = processor.process(message);
        assertSame(message, result, "При нечётной секунде должно возвращаться исходное сообщение");
    }
}
