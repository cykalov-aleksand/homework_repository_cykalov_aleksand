package ru.otus.processor;

import org.example.homework.model.Message;
import org.example.homework.processor.Processor;
import org.example.homework.processor.homework.ThrowOnExceptionEvenSecond;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class ThrowOnExceptionEvenSecondTest {

    private Message message;
    private final Supplier<Integer> evenSecond = () -> 10;   // чётная
    private final Supplier<Integer> oddSecond  = () -> 11;   // нечётная

    @BeforeEach
    void setUp() {
        // Создаём тестовое сообщение
        message = new Message.Builder(1L)
                .field1("test")
                .field2("data")
                .field11("value11")
                .field12("value12")
                .field13(null) // можно заменить на реальный объект при необходимости
                .build();
    }

    @Test
    void shouldThrowExceptionOnEvenSecond() {
        // Given
        Processor processor = new ThrowOnExceptionEvenSecond(evenSecond);

        // When & Then
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
        // Given
        Processor processor = new ThrowOnExceptionEvenSecond(oddSecond);

        // When & Then
        assertDoesNotThrow(
                () -> processor.process(message),
                "В нечётную секунду исключение не должно выбрасываться"
        );
    }

    @Test
    void shouldUseCurrentTimeInDefaultConstructor() {
        // Given
        Processor processor = new ThrowOnExceptionEvenSecond();

        // When
        int currentSecond = java.time.LocalTime.now().getSecond();

        // Then
        if (currentSecond % 2 == 0) {
            assertThrows(IllegalStateException.class, () -> processor.process(message));
        } else {
            assertDoesNotThrow(() -> processor.process(message));
        }
    }

    @Test
    void shouldWorkWithFixedSecondViaIntConstructor() {
        // Given
        Processor evenProcessor = new ThrowOnExceptionEvenSecond(4);
        Processor oddProcessor  = new ThrowOnExceptionEvenSecond(5);

        // Then
        assertThrows(IllegalStateException.class, () -> evenProcessor.process(message));
        assertDoesNotThrow(() -> oddProcessor.process(message));
    }

    @Test
    void shouldNotAllowNullSupplier() {
        // When & Then
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new ThrowOnExceptionEvenSecond((Supplier<Integer>) null),
                "Конструктор должен отвергать null"
        );

        // Проверяем, что сообщение содержит наш текст (если вы добавили Objects.requireNonNull)
        // Если вы НЕ добавили проверку, этот тест упадёт — значит, нужно добавить!
    }

    @Test
    void shouldReturnSameMessageWhenNoException() {
        // Given
        Processor processor = new ThrowOnExceptionEvenSecond(oddSecond);

        // When
        Message result = processor.process(message);

        // Then
        assertSame(message, result, "При нечётной секунде должно возвращаться исходное сообщение");
    }
}
