package org.example;

import lombok.extern.slf4j.Slf4j;

@Slf4j

public class Main {
    public static void main(String[] args) {
        logger.info("Создаем объект банкомата, где указываем используемую валюту и задаем наличие банкнот по убыванию в конструкторе");
        InterfaceCurrencyATM atmCashMoney = new CashMoneyATM(10, 10, 10, 10,
                10, 10, 10, 10, 10, 10);
        logger.info("Итоговая сумма в банкомате: {}", atmCashMoney.amountOfCash());
        logger.info("по банкнотам: {} ", atmCashMoney);
        logger.info(" создадим объект person, и перечислим банкноты которые мы планируем внести в банкомат");
        CashMoneyPerson person = new CashMoneyPerson(1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
        int deposit = atmCashMoney.acceptDeposit(person);
        logger.info("Выполним первый пункт домашнего задания и внесём наличные: {}", deposit);
        logger.info("теперь {} ", atmCashMoney);
        int cash = 5348;
        logger.info("Запросим выдачу банкоматом суммы в размере: {}", cash);
        try {
            logger.info("Банкомат выдаст: {}", atmCashMoney.withdrawAndUpdateAfterIssue(cash));
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
        } finally {
            logger.info("Остаток {}", atmCashMoney);
        }
        cash = 100000;
        logger.info("Запросим выдачу банкоматом суммы в размере: {}", cash);
        try {
            logger.info(" Банкомат выдаст: {}", atmCashMoney.withdrawAndUpdateAfterIssue(cash));
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
        } finally {
            logger.info(" Остаток {}", atmCashMoney);
        }
    }
}

