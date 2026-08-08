package org.example;

import lombok.Getter;
import org.example.abstract_cash.AbstractCash;
import org.example.curency.CurrencyType;
import org.example.curency.model.DollarCash;
import org.example.curency.model.RubleCash;

import java.util.*;

@Getter
public class ATM implements InterfaceCurrencyATM{
    private final CurrencyType currencyType;
    private AbstractCash cash;

    public ATM(CurrencyType currencyType, Map<Integer, Integer> banknotes) {
        this.currencyType = currencyType;

        this.cash = switch (currencyType) {
            case RUB -> new RubleCash(banknotes);
            case USD -> new DollarCash(banknotes);
            default -> throw new IllegalArgumentException("Неизвестная валюта: " + currencyType);
        };}

    @Override
    public int acceptDeposit(Person cashMoneyPerson) {
        if (cashMoneyPerson.getCurrencyType() != this.currencyType) {
            throw new IllegalArgumentException("Валюта клиента не совпадает с валютой банкомата: " + cashMoneyPerson.getCurrencyType());
        }
        Map<Integer,Integer>depositedCash=cashMoneyPerson.getCash().getBanknotes();
        Map<Integer,Integer>updateBanknote=new LinkedHashMap<>(this.cash.getBanknotes());
        for(Map.Entry<Integer,Integer>entity:depositedCash.entrySet()){
            int currentCount = updateBanknote.getOrDefault(entity.getKey(), 0);
            int newCount = currentCount + entity.getValue();
            updateBanknote.put(entity.getKey(), newCount);       }
        this.cash= createNewCash(updateBanknote);
        return cashMoneyPerson.getCash().amountOfCash() ;
    }
       @Override
    public Map<String, Integer> withdrawAndUpdateAfterIssue(int cash) {
           if (cash <= 0) {
               throw new IllegalArgumentException("Запрашиваемая сумма должна быть положительной");
           }

           if (cash > this.cash.amountOfCash()) {
               throw new IllegalArgumentException("Недостаточно средств в банкомате");
           }
           Map<Integer, Integer> availableBanknotes = new LinkedHashMap<>(this.cash.getBanknotes());
           // получаем список всех ключей для валюты которая может быть в банкомате
           List<Integer> listKey = new ArrayList<>(availableBanknotes.keySet());
           //сортируем данный список по возрастанию
           listKey.sort(Comparator.reverseOrder());
           int remaining = cash;
           Map<Integer, Integer> transferredBanknotes = new LinkedHashMap<>();
           for (int nominal : listKey) {
               int availableCount = availableBanknotes.get(nominal);
               int neededCount = remaining / nominal;
               int count = Math.min(availableCount, neededCount);
               transferredBanknotes.put(nominal, count);
               remaining -= nominal * count;
               availableBanknotes.put(nominal, availableCount - count);
           }
           if (remaining > 0) {
               throw new IllegalArgumentException("Невозможно выдать точную сумму: не хватает мелких купюр/монет");
           }
this.cash=createNewCash(availableBanknotes);
           Map<String, Integer> result = new LinkedHashMap<>();
           transferredBanknotes.forEach((nominal, count) -> result.put(nominal.toString() , count));
        return result;
    }
    private AbstractCash createNewCash(Map<Integer, Integer> banknotes) {
        return switch (currencyType) {
            case RUB -> new RubleCash(banknotes);
            case USD -> new DollarCash(banknotes);
            default -> throw new IllegalArgumentException("Неизвестная валюта: " + currencyType);
        };
    }

}
