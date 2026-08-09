package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.curency.CurrencyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class TestATM {
    private ATM atm;
    private Person person;

    @BeforeEach
    void setUp() {
        // Начальное состояние банкомата: 10 купюр по 100, 10 по 50, 20 по 10
        Map<Integer, Integer> initialAtmCash = new HashMap<>();
        initialAtmCash.put(100, 10);
        initialAtmCash.put(50, 10);
        initialAtmCash.put(10, 20);
        initialAtmCash.put(5, 10);
        initialAtmCash.put(1, 50);

        // Деньги клиента
        Map<Integer, Integer> clientCash = new HashMap<>();
        clientCash.put(100, 1);
        clientCash.put(50, 2);
        clientCash.put(10, 5);

        // Создаём банкомат и клиента
        atm = new ATM(CurrencyType.USD, new HashMap<>(initialAtmCash));
        person = new Person(CurrencyType.USD, clientCash);
    }

          @Test
        void shouldAcceptDepositAndIncreaseCash() {
        logger.info("Тест на проверку ввода денежных средств и возврат введенной суммы");
            int depositedAmount = atm.acceptDeposit(person);

            // Проверяем введенную сумму
            assertEquals(250, depositedAmount); // 100 + 2*50 + 5*10

            // Проверяем увеличение денежных банкнот в банкомате
            Map<Integer, Integer> updated = atm.getCash().getBanknotes();
            assertEquals(11, updated.get(100));
            assertEquals(12, updated.get(50));
            assertEquals(25, updated.get(10));
        }

        @Test
        void shouldThrowOnCurrencyMismatch() {
        logger.info("Проверяем выполнения логики при несоответствии валюты клиента и банкомата");
            Person rubPerson = new Person(CurrencyType.RUB, Map.of(100, 1));
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> atm.acceptDeposit(rubPerson)
            );
            assertTrue(exception.getMessage().contains("Валюта клиента не совпадает"));
        }
           @Test
        void shouldWithdrawExactAmount() {
        logger.info("Тест на выдачу запрошенной суммы");
            Map<String, Integer> result = atm.withdrawAndUpdateAfterIssue(185);

            assertEquals(1, result.get("100"));
            assertEquals(1, result.get("50"));
            assertEquals(3, result.get("10"));
            assertEquals(1, result.get("5"));

            // Проверяем остаток в банкомате
            Map<Integer, Integer> remaining = atm.getCash().getBanknotes();
            assertEquals(9, remaining.get(100));
            assertEquals(9, remaining.get(50));
            assertEquals(17, remaining.get(10));
            assertEquals(9, remaining.get(5));
        }

        @Test
       void shouldRejectNegativeWithdraw() {
        logger.info("Тест на проверку запроса отрицательной суммы");
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> atm.withdrawAndUpdateAfterIssue(-100)
            );
            assertTrue(exception.getMessage().contains("Запрашиваемая сумма должна быть положительной"));
        }

        @Test
        void shouldRejectWithdrawExceedingBalance() {
        logger.info("Тест на выдачу суммы больше чем есть в банкомате");
            int totalCash = atm.getCash().amountOfCash(); // ~1800
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> atm.withdrawAndUpdateAfterIssue(totalCash + 1)
            );
            assertTrue(exception.getMessage().contains("Недостаточно средств в банкомате"));
        }

        @Test
       void shouldRejectIfCannotGiveExactChange() {
             logger.info("Тест: невозможность выдачи 75 при наличии только 100 и 50");

                ATM localATM = new ATM(CurrencyType.USD, Map.of(100, 5, 50, 5));

                IllegalArgumentException exception = assertThrows(
                        IllegalArgumentException.class,
                        () -> localATM.withdrawAndUpdateAfterIssue(75)
                );
                assertTrue(exception.getMessage().contains("Невозможно выдать точную сумму"));
            }

        @Test
        void shouldCreateUSDATM() {
        logger.info("Проверяем работу конструктора при создании ATM с долларовой валютой");
            assertEquals(CurrencyType.USD, atm.getCurrencyType());
            assertEquals(1800, atm.getCash().amountOfCash()); // 10*100 + 10*50 + 20*10 + 10*5 + 50*1
        }

        @Test
       void shouldCreateRUBATM() {
            logger.info("Проверяем работу конструктора при создании ATM с рублями");
            Map<Integer, Integer> rubCash = new HashMap<>();
            rubCash.put(5000, 1);
            rubCash.put(100, 3);
            rubCash.put(10, 1);

            ATM rubATM = new ATM(CurrencyType.RUB, rubCash);

            assertEquals(CurrencyType.RUB, rubATM.getCurrencyType());
            assertEquals(5310, rubATM.getCash().amountOfCash());
        }
    @Test
    void shouldThrowWhenCurrencyTypeIsNull() {
        logger.info("Тест: передача в currencyType=null в конструкторе ATM");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ATM(null, new HashMap<>())
        );
        assertTrue(exception.getMessage().contains("Валюта не может быть null"));
    }
    }
