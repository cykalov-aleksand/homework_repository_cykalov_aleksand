package org.example;

import java.util.Map;

public interface InterfaceCurrencyATM {
   int acceptDeposit(Person cashMoneyPerson);
    Map<String, Integer> withdrawAndUpdateAfterIssue(int cash);
}
