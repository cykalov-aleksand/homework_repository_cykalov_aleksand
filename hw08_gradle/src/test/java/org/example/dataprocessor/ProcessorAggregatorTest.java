package org.example.dataprocessor;

import org.example.dataprocessor.processor.ProcessorAggregator;
import org.example.model.Measurement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcessorAggregatorTest {
    private static final Logger logger=LoggerFactory.getLogger(ProcessorAggregatorTest.class);
    private final ProcessorAggregator processor = new ProcessorAggregator();
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private String getOutput() {
        return outContent.toString().trim();
    }

    private void clearOutput() {
        outContent.reset();
    }

    @Test
    void shouldReturnEmptyMapWhenDataIsNull() {
        logger.info("data = null - пустой результат и сообщение в консоль");
        Map<String, Double> result = processor.process(null);
        assertThat(result).isEmpty();
        assertTrue(getOutput().contains("Список измерений пуст или null. Возвращён пустой результат."));
    }

    @Test
    void shouldReturnEmptyMapWhenDataIsEmpty() {
        logger.info("data пустой список - пустой результат и сообщение в консоль");
        clearOutput();
        List<Measurement> data = List.of();
        Map<String, Double> result = processor.process(data);
        assertThat(result).isEmpty();
        assertTrue(getOutput().contains("Список измерений пуст или null. Возвращён пустой результат."));
    }

    @Test
    void shouldIncludeValidMeasurement() {
        logger.info("Одно валидное измерение - добавляется в результат, сообщений нет");
        clearOutput();
        List<Measurement> data = List.of(new Measurement("cpu", 1.5));
        Map<String, Double> result = processor.process(data);
        assertThat(result)
                .containsExactly(Map.entry("cpu", 1.5));
        assertTrue(getOutput().isEmpty());
    }

    @Test
    void shouldAggregateValuesByName() {
        logger.info("Несколько измерений с одинаковыми именами - значения суммируются, порядок сохраняется");
        clearOutput();
        List<Measurement> data = List.of(
                new Measurement("temp", 10.0),
                new Measurement("pressure", 750.0),
                new Measurement("temp", 20.0),
                new Measurement("humidity", 60.0),
                new Measurement("pressure", 50.0)
        );
        Map<String, Double> result = processor.process(data);
        assertThat(result)
                .containsExactly(
                        Map.entry("temp", 30.0),        // 10 + 20
                        Map.entry("pressure", 800.0),   // 750 + 50
                        Map.entry("humidity", 60.0)
                );
        assertTrue(getOutput().isEmpty());
    }

    @Test
    void shouldSkipNullElementInList() {
        logger.info("Элемент списка = null - пропускается, выводится сообщение с индексом");
        clearOutput();
        List<Measurement> data = Arrays.asList(
                new Measurement("cpu", 1.0),
                null,
                new Measurement("gpu", 2.0)
        );
        Map<String, Double> result = processor.process(data);
        assertThat(result)
                .containsExactly(
                        Map.entry("cpu", 1.0),
                        Map.entry("gpu", 2.0)
                );
        assertTrue(getOutput().contains("Элемент с индексом 1 в списке data равен null. Пропущен."));
    }

    @Test
    void shouldSkipWhenNameIsNull() {
        logger.info("Измерение с name = null - пропускается, выводится сообщение");
        clearOutput();
        List<Measurement> data = List.of(new Measurement(null, 99.9));
        Map<String, Double> result = processor.process(data);
        assertThat(result).isEmpty();
        assertTrue(getOutput().contains("поле name равно null"));
    }

    @Test
    void shouldProcessOnlyValidMeasurements() {
        logger.info("Смешанные данные: валидные + null - обрабатываются только валидные");
        clearOutput();
        List<Measurement> data = Arrays.asList(
                new Measurement("cpu", 1.0),
                new Measurement(null, 2.0),
                new Measurement("cpu", 3.0),
                null,
                new Measurement("ram", 4.0)
        );
        Map<String, Double> result = processor.process(data);
        assertThat(result)
                .containsExactly(
                        Map.entry("cpu", 4.0),
                        Map.entry("ram", 4.0)
                );
        String output = getOutput();
        assertTrue(output.contains("Элемент с индексом 3 в списке data равен null. Пропущен."));
        assertTrue(output.contains("поле name равно null"));
    }
}
