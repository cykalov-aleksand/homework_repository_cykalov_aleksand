package org.example.currencyAbstract;

import org.example.Currency;

public abstract class CashMoneyDollarAbstract implements Currency {
        protected int hundred;
    protected int fifty;
    protected int twenty;
    protected int ten;
    protected int five;
    protected int two;
    protected int one;
    public CashMoneyDollarAbstract ( int hundred, int fifty,int twenty,
                                  int ten, int five, int two, int one) {
        this.hundred = validate(hundred);
        this.fifty = validate(fifty);
        this.twenty=validate(twenty);
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
        return hundred * 100 + fifty * 50+twenty*20 + ten * 10 + five * 5 + two * 2 + one;
    }
}
