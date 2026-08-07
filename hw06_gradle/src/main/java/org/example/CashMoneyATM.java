package org.example;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.currencyAbstract.CashMoneyRubleAbstract;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class CashMoneyATM extends CashMoneyRubleAbstract implements InterfaceCurrencyATM {

    public CashMoneyATM(int fiveThousand, int twoThousand, int oneThousand, int fiveHundred, int hundred, int fifty, int ten, int five, int two, int one) {
        super(fiveThousand, twoThousand, oneThousand, fiveHundred, hundred, fifty, ten, five, two, one);
    }
    public int acceptDeposit(CashMoneyPerson cashMoneyPerson){
        this.fiveThousand += cashMoneyPerson.getFiveThousand();
        this.twoThousand += cashMoneyPerson.getTwoThousand();
        this.oneThousand += cashMoneyPerson.getOneThousand();
        this.fiveHundred += cashMoneyPerson.getFiveHundred();
        this.hundred += cashMoneyPerson.getHundred();
        this.fifty += cashMoneyPerson.getFifty();
        this.ten += cashMoneyPerson.getTen();
        this.five += cashMoneyPerson.getFive();
        this.two += cashMoneyPerson.getTwo();
        this.one += cashMoneyPerson.getOne();
        return cashMoneyPerson.amountOfCash();
    }
    public Map<String, Integer> withdrawAndUpdateAfterIssue(int cash){
        Map<String, Integer> issued = giveOutCash(cash);
        this.fiveThousand -= issued.getOrDefault("5000", 0);
        this.twoThousand -= issued.getOrDefault("2000", 0);
        this.oneThousand -= issued.getOrDefault("1000", 0);
        this.fiveHundred -= issued.getOrDefault("500", 0);
        this.hundred -= issued.getOrDefault("100", 0);
        this.fifty -= issued.getOrDefault("50", 0);
        this.ten -= issued.getOrDefault("10", 0);
        this.five -= issued.getOrDefault("5", 0);
        this.two -= issued.getOrDefault("2", 0);
        this.one -= issued.getOrDefault("1", 0);
        return issued;
    }

    private LinkedHashMap<String, Integer> giveOutCash(int cash) {

        if (cash < 0) {
            throw new IllegalArgumentException("Сумма не может быть отрицательной");
        }
        if (amountOfCash() < cash) {
            throw new IllegalArgumentException("В банкомате недостаточно денег");
        }

        LinkedHashMap<String, Integer> cashWithdrawal = new LinkedHashMap<>();
        int sum = cash;

        sum = recordMapElement(sum, 5000, fiveThousand, "5000", cashWithdrawal);
        sum = recordMapElement(sum, 2000, twoThousand, "2000", cashWithdrawal);
        sum = recordMapElement(sum, 1000, oneThousand, "1000", cashWithdrawal);
        sum = recordMapElement(sum, 500, fiveHundred, "500", cashWithdrawal);
        sum = recordMapElement(sum, 100, hundred, "100", cashWithdrawal);
        sum = recordMapElement(sum, 50, fifty, "50", cashWithdrawal);
        sum = recordMapElement(sum, 10, ten, "10", cashWithdrawal);
        sum = recordMapElement(sum, 5, five, "5", cashWithdrawal);
        sum = recordMapElement(sum, 2, two, "2", cashWithdrawal);
        int countOne = Math.min(sum, one);
        cashWithdrawal.put("1", countOne);
        sum -= countOne;

        if (sum != 0) {
            throw new IllegalArgumentException("Невозможно выдать точную сумму имеющимися купюрами");
        }


        return cashWithdrawal;
    }

    private int recordMapElement(int sum, int nominal, int field, String nominalString, Map<String, Integer> cashWithdrawal) {
        int needed = sum / nominal;
        int count = Math.min(needed, field);
        cashWithdrawal.put(nominalString, count);
        return sum - count * nominal;
    }

}