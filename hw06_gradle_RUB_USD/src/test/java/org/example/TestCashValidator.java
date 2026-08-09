package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.curency.CashValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class TestCashValidator {
        private List<Integer> validNominals;
        private static final String CURRENCY_NAME = "DollarCash";

        @BeforeEach
        void setUp() {
            validNominals = Arrays.asList(100, 50, 10, 5, 1); // для USD
        }

        @Test
        void shouldAcceptValidNominalsAndFillMissingWithZero() {
            logger.info("Должен принять корректные купюры и вернуть заполненную карту");
            Map<Integer, Integer> input = new HashMap<>();
            input.put(100, 2);
            input.put(50, 1);
            input.put(10, 3);

            Map<Integer, Integer> result = CashValidator.validate(input, validNominals, CURRENCY_NAME);

            assertAll(
                    () -> assertEquals(2, result.get(100)),
                    () -> assertEquals(1, result.get(50)),
                    () -> assertEquals(3, result.get(10)),
                    () -> assertEquals(0, result.get(5)),
                    () -> assertEquals(0, result.get(1))
            );
            assertEquals(5, result.size());
        }

        @Test
        void shouldThrowOnNullInput() {
            logger.info("Проверка выполнения теста если input в методе validate равен null");
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> CashValidator.validate(null, validNominals, CURRENCY_NAME)
            );
            assertTrue(exception.getMessage().contains("Карта купюр не может быть null"));
        }

        @Test
         void shouldThrowOnNullValidNominals() {
            logger.info("Проверка выполнения теста если validNominals в методе validate равен null");
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> CashValidator.validate(new HashMap<>(), null, CURRENCY_NAME)
            );
            assertTrue(exception.getMessage().contains("Список допустимых номиналов не может быть null"));
        }

        @Test
        void shouldThrowOnInvalidNominal() {
            logger.info("Проверка выбрасывания исключения при вводе недопустимой купюры");
            Map<Integer, Integer> input = new HashMap<>();
            input.put(200, 1);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> CashValidator.validate(input, validNominals, CURRENCY_NAME)
            );
            assertTrue(exception.getMessage().contains("Недопустимый номинал"));
            assertTrue(exception.getMessage().contains("200"));
            assertTrue(exception.getMessage().contains("Допустимые: [100, 50, 10, 5, 1]"));
        }

        @Test
        void shouldIgnoreZeroAndNegativeCounts() {
            logger.info("Тестирование метода при вводе недействительных номиналов");
            Map<Integer, Integer> input = new HashMap<>();
            input.put(100, 0);
            input.put(50, -5);
            input.put(10, 2);

            Map<Integer, Integer> result = CashValidator.validate(input, validNominals, CURRENCY_NAME);

            assertAll(
                    () -> assertEquals(0, result.get(100)),
                    () -> assertEquals(0, result.get(50)),
                    () -> assertEquals(2, result.get(10)),
                    () -> assertEquals(0, result.get(5)),
                    () -> assertEquals(0, result.get(1))
            );
        }

     @Test
    void shouldPreserveOrderOfValidNominals() {
            logger.info(" Тест на сохранения порядка номиналов, при введении данных в метод не соответствующего порядка");
       Map<Integer, Integer> input = new LinkedHashMap<>();
        input.put(100, 1);
        input.put(50, 1);
        input.put(10, 1);

        Map<Integer, Integer> result = CashValidator.validate(input, validNominals, CURRENCY_NAME);

        List<Integer> resultKeys = new ArrayList<>(result.keySet());
        assertEquals(Arrays.asList(100, 50, 10, 5, 1), resultKeys);
    }
        @Test
       void shouldWorkWithRubleNominals() {
            logger.info("Тест на корректную работу метода с другой валютой");
            List<Integer> rubNominals = Arrays.asList(5000, 2000, 1000, 500, 100, 50, 10, 5, 2, 1);
            Map<Integer, Integer> input = new HashMap<>();
            input.put(1000, 2);
            input.put(100, 5);
            input.put(10, 1);

            Map<Integer, Integer> result = CashValidator.validate(input, rubNominals, "RubleCash");

            assertAll(
                    () -> assertEquals(2, result.get(1000)),
                    () -> assertEquals(5, result.get(100)),
                    () -> assertEquals(1, result.get(10)),
                    () -> assertEquals(0, result.get(5000)),
                    () -> assertEquals(0, result.get(2))
            );
        }
   }