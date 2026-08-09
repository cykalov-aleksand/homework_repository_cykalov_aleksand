package org.example;

import lombok.Getter;
import org.example.abstract_cash.AbstractCash;
import org.example.curency.CurrencyType;
import org.example.curency.model.DollarCash;
import org.example.curency.model.RubleCash;

import java.util.Map;
@Getter
public class Person  {
    private final CurrencyType currencyType;
    private final AbstractCash cash;

    public Person(CurrencyType currencyType, Map<Integer, Integer> banknotes) {
        this.currencyType = currencyType;

            this.cash = switch (currencyType) {
            case RUB -> new RubleCash(banknotes);
            case USD -> new DollarCash(banknotes);
            default -> throw new IllegalArgumentException("Неизвестная валюта: " + currencyType);
        };
    }

    @Override
    public String toString() {
        return "Person{" +
                "currencyType=" + currencyType +
                ", cash=" + cash +
                '}';
    }
}
