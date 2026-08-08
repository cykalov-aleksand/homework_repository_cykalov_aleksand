package org.example;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static org.example.curency.CurrencyType.USD;
@Slf4j
public class Main {
    public static void main(String[] args) {
            Map<Integer, Integer> currency = new HashMap<>();
            currency.put(101, 3);

            try {
                Person person = new Person(USD,currency);
                System.out.println(person.getCash().getBanknotes());
            } catch (IllegalArgumentException e) {
                logger.error("Ошибка: {}", e.getMessage());
            }
        }
}