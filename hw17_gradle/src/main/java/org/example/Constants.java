package org.example;

public enum Constants {
    PORT(8190),
    FIRST_VALUE(0),
    LAST_VALUE(30);

    private final int number;

    Constants(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
