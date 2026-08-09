package org.example.curency.model;

import org.example.abstract_cash.AbstractCash;
import org.example.curency.CashValidator;

import java.util.List;
import java.util.Map;

public class DollarCash extends AbstractCash {
    private static final List<Integer> VALID_NOMINALS_ORDERED = List.of(100, 50, 10, 5, 2, 1);
       public DollarCash(Map<Integer, Integer> banknotes) {
        super(CashValidator.validate(banknotes, VALID_NOMINALS_ORDERED, "DollarCash"));
    }
}
