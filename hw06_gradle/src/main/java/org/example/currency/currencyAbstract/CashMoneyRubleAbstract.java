package org.example.currency.currencyAbstract;

import lombok.Getter;
import org.example.currency.Currency;

@Getter
abstract public class CashMoneyRubleAbstract implements Currency {
    protected int fiveThousand;
    protected int twoThousand;
    protected int oneThousand;
    protected int fiveHundred;
    protected int hundred;
    protected int fifty;
    protected int ten;
    protected int five;
    protected int two;
    protected int one;

    public CashMoneyRubleAbstract(int fiveThousand, int twoThousand, int oneThousand, int fiveHundred, int hundred, int fifty,
                                  int ten, int five, int two, int one) {
        this.fiveThousand = validate(fiveThousand);
        this.twoThousand = validate(twoThousand);
        this.oneThousand = validate(oneThousand);
        this.fiveHundred = validate(fiveHundred);
        this.hundred = validate(hundred);
        this.fifty = validate(fifty);
        this.ten = validate(ten);
        this.five = validate(five);
        this.two = validate(two);
        this.one = validate(one);
    }

    private static int validate(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Количество купюр не может быть отрицательным");
        }
        return value;
    }
    @Override
    public int amountOfCash() {
        return fiveThousand * 5000 + twoThousand * 2000 + oneThousand * 1000 + fiveHundred * 500 + hundred * 100
                + fifty * 50 + ten * 10 + five * 5 + two * 2 + one;
    }
    @Override
    public String toString() {
        return "банкомат содержит:\n" +
                "  5000₽: " + fiveThousand + " шт\n" +
                "  2000₽: " + twoThousand + " шт\n" +
                "  1000₽: " + oneThousand + " шт\n" +
                "   500₽: " + fiveHundred + " шт\n" +
                "   100₽: " + hundred + " шт\n" +
                "    50₽: " + fifty + " шт\n" +
                "    10₽: " + ten + " шт\n" +
                "     5₽: " + five + " шт\n" +
                "     2₽: " + two + " шт\n" +
                "     1₽: " + one + " шт\n" +
                "Итого: " + amountOfCash() + "₽";
    }
   }
