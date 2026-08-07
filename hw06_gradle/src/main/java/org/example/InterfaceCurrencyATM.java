package org.example;

import java.util.Map;

public interface InterfaceCurrencyATM {
    int amountOfCash();
    int acceptDeposit(CashMoneyPerson cashMoneyPerson);
    Map<String, Integer> withdrawAndUpdateAfterIssue(int cash);
}
