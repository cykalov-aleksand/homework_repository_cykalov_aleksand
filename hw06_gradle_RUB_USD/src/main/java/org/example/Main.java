package org.example;

import java.util.HashMap;
import java.util.Map;

import static org.example.curency.CurrencyType.USD;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
            Map<Integer, Integer> currency = new HashMap<>();
            currency.put(101, 3);

            try {
                Person person = new Person(USD,currency);
                System.out.println(person.getCash().getBanknotes());
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e);
            }
        }
}