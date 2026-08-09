package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.curency.CurrencyType;

import java.util.HashMap;
import java.util.Map;

import static org.example.curency.CurrencyType.RUB;
import static org.example.curency.CurrencyType.USD;
@Slf4j
public class Main {
    public static void main(String[] args) {
        Map<Integer, Integer> currency = new HashMap<>();
        currency.put(5000, 10);
        currency.put(2000, 10);
        currency.put(1000, 10);
        currency.put(500, 10);
        currency.put(100, 10);
        currency.put(50, 10);
        currency.put(10, 10);
        currency.put(5, 10);
        currency.put(2, 10);
        currency.put(1, 10);
        Map<Integer, Integer> personCurrency = new HashMap<>();
        personCurrency.put(1000, 1);
        personCurrency.put(500, 2);
        logger.info("РАБОТА БАНКОМАТА С РУБЛЯМИ");
        test(RUB,currency,personCurrency);
        Map<Integer,Integer>currencyUSD=new HashMap<>();
        currencyUSD.put(100,10);
        currencyUSD.put(50, 10);
        currencyUSD.put(10, 10);
        currencyUSD.put(5, 10);
        currencyUSD.put(2, 10);
        currencyUSD.put(1, 10);
        Map<Integer, Integer> personCurrencyUSD = new HashMap<>();
        personCurrency.put(100, 1);
        personCurrency.put(50, 2);
        logger.info("РАБОТА БАНКОМАТА С ДОЛЛАРАМИ\n");
        test(USD,currencyUSD,personCurrencyUSD);
    }

    public static void test(CurrencyType type,Map<Integer,Integer>atm,Map<Integer,Integer>personCurrency){
        logger.info("Создаем объект банкомата, где указываем используемую валюту и задаем наличие банкнот по убыванию в конструкторе");
        ATM atmCashMoney = new ATM(type,atm);
        logger.info("Итоговая сумма в банкомате: {}", atmCashMoney.getCash().amountOfCash());
        logger.info("по банкнотам: {} ", atmCashMoney.getCash().toString());
        logger.info(" создадим объект person, и перечислим банкноты которые мы планируем внести в банкомат");
        Person person = new Person(type,personCurrency);
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
    }}
