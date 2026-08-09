package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.abstract_cash.AbstractCash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class TestAbstractCash {

    private TestCash testCash;
    private Map<Integer, Integer> input;

    // Тестовая реализация абстрактного класса
    private static class TestCash extends AbstractCash {
        TestCash(Map<Integer, Integer> banknotes) {
            super(banknotes);
        }
    }

    @BeforeEach
    void setUp() {
        input = new LinkedHashMap<>();
        input.put(100, 2);
        input.put(50, 3);
        input.put(10, 0);   // будет проигнорировано
        input.put(5, 0);   // будет проигнорировано (ошибка при валидации)
    }

    @Test
   void shouldCreateWithValidCountsAndIgnoreZero() {
        logger.info("Тест на внесение положительных значений мапой и игнорирование нулевых значений");
        testCash = new TestCash(input);

        Map<Integer, Integer> banknotes = testCash.getBanknotes();

        assertAll(
                () -> assertEquals(2, banknotes.get(100)),
                () -> assertEquals(3, banknotes.get(50)),
                () -> assertFalse(banknotes.containsKey(10)),
                () -> assertFalse(banknotes.containsKey(5))
        );
    }

    @Test
    void shouldThrowOnNegativeCount() {
        logger.info("Тест на выброс исключения при вводе отрицательных значений");
        Map<Integer, Integer> invalid = new HashMap<>();
        invalid.put(100, -5);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new TestCash(invalid)
        );
        assertTrue(exception.getMessage().contains("Количество купюр не может быть отрицательным"));
    }

    @Test
    void shouldCalculateTotalAmountCorrectly() {
        logger.info("Проверка корректности расчета суммы имеющейся у объекта");
        testCash = new TestCash(Map.of(100, 1, 50, 2, 10, 5));

        int expected = 100 + 2*50 + 5*10;
        assertEquals(expected, testCash.amountOfCash());
    }

    @Test
    void shouldReturnZeroForMissingNominal() {
        logger.info("Тест на вывод нулевого значения при отсутствии номинала");
        testCash = new TestCash(Map.of(100, 1));

        assertEquals(0, testCash.getCount(50));
        assertEquals(0, testCash.getCount(10));
    }

    @Test
    void shouldReturnCorrectCountForExistingNominal() {
        logger.info("Тест на проверку корректности вывода количества имеющихся купюр");
        testCash = new TestCash(Map.of(50, 4));

        assertEquals(4, testCash.getCount(50));
    }

}
