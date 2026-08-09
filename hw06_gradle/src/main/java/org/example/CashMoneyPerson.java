package org.example;

import lombok.Getter;
import lombok.Setter;
import org.example.currency.currencyAbstract.CashMoneyRubleAbstract;

@Getter
@Setter
public class CashMoneyPerson extends CashMoneyRubleAbstract {
    public CashMoneyPerson(int fiveThousand, int twoThousand, int oneThousand, int fiveHundred, int hundred, int fifty, int ten, int five, int two, int one) {
        super(fiveThousand, twoThousand, oneThousand, fiveHundred, hundred, fifty, ten, five, two, one);
    }
    }
