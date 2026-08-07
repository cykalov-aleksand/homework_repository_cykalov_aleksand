package org.example;

public class Main {
    public static void main(String[] args) {
        Currency atmCashmoney=new CashMoneyATM(10,10,10,10,
                10,10,10,10,10,10);
        System.out.println("Итоговая сумма в банкомате  "+atmCashmoney.amountOfCash());
        System.out.println("по банкнотам "+atmCashmoney.toString());
        try {
            System.out.println(((CashMoneyATM) atmCashmoney).withdrawAndUpdateAfterIssue(11555));
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
        System.out.println("Остаток в банкомате "+atmCashmoney.toString());
    }
    }