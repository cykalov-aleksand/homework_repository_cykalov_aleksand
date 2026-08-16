package ru.otus.processor;

import org.example.homework.model.Message;
import org.example.homework.model.ObjectForMessage;
import org.example.homework.processor.ChangeValuesOfSeats11And12;
import org.example.homework.processor.Processor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ChangeValuesOfSeats11And12Test {
    Logger logger = LoggerFactory.getLogger(ChangeValuesOfSeats11And12Test.class);
    private Processor processor;
    private Message message;

    @BeforeEach
    void setUp() {
        ObjectForMessage object = new ObjectForMessage();
        object.setData(List.of("x", "y", "z"));
        processor = new ChangeValuesOfSeats11And12();


        message = new Message.Builder(1L)
                .field1("value1")
                .field2("value2")
                .field10("value10")
                .field11("ABC")
                .field12("XYZ")
                .field13(object)
                .build();
    }

    @Test
    void shouldSwapField11AndField12() {
        logger.info("Тест на проведение замены field11 на field12");
        Message result = processor.process(message);

        assertEquals("XYZ", result.getField11(), "field11 должно стать прежним field12");
        assertEquals("ABC", result.getField12(), "field12 должно стать прежним field11");
    }

    @Test
    void shouldNotModifyOtherFields() {
        logger.info("Тест на проверку неизменяемости остальных полей");
        Message result = processor.process(message);

        assertAll("Проверка, что только field11 и field12 изменились",
                () -> assertEquals(1L, result.getId(), "ID должен остаться прежним"),
                () -> assertEquals("value1", result.getField1(), "field1 не должно измениться"),
                () -> assertEquals("value2", result.getField2(), "field2 не должно измениться"),
                () -> assertEquals("value10", result.getField10(), "field10 не должно измениться"),
                () -> assertEquals("XYZ", result.getField11(), "field11 должно стать field12"),
                () -> assertEquals("ABC", result.getField12(), "field12 должно стать field11"),
                () -> assertEquals(message.getField13(), result.getField13(), "field13 должно остаться без изменений")
        );
    }

    @Test
    void shouldHandleNullValues() {
        logger.info("Тест на проверку работы метода если одно из значений field11 или field12 имеет значений null");
        Message messageWithNulls = new Message.Builder(2L)
                .field11(null)
                .field12("SOME")
                .build();

        Message result = processor.process(messageWithNulls);
        assertEquals("SOME", result.getField11(), "field11 должно стать 'SOME'");
        assertNull(result.getField12(), "field12 должно стать null");
    }

    @Test
    void shouldHandleEmptyStrings() {
        logger.info("Тест на проверку работы метода если одно из значений field11 или field12 пустое");
        Message emptyMessage = new Message.Builder(3L)
                .field11("")
                .field12("DATA")
                .build();

        Message result = processor.process(emptyMessage);

        assertEquals("DATA", result.getField11(), "field11 должно стать 'DATA'");
        assertEquals("", result.getField12(), "field12 должно стать пустой строкой");
    }

    @Test
    void shouldReturnNewInstance() {
        logger.info("Тест на проверку того, что по завершению метод возвращает новый объект");
        Message result = processor.process(message);
        assertNotSame(message, result, "Должен быть создан новый экземпляр Message");
    }
}
