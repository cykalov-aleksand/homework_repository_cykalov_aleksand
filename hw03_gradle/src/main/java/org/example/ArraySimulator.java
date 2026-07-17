package org.example;


import com.google.common.base.CharMatcher;

import java.util.Random;

public class ArraySimulator {
    public Integer[] simulatorArrayInteger(int size, int min, int max) throws IllegalArgumentException {
        Integer[] integersArray = new Integer[size];
        Random random = new Random();
        if (min > max) {
            int variable = max;
            max = min;
            min = variable;
        }
        if ((size < 2)) {
            throw new IllegalArgumentException("Не верно введена переменная size, она должна быть равна не менее 2");
        }
        for (int i = 0; i < integersArray.length; i++) {
            integersArray[i] = random.nextInt((max - min) + 1) + min;
        }
        return integersArray;
    }

    public String[] simulatorArrayString(String string) {
        return CharMatcher.is('\n').replaceFrom(string, ' ').replaceAll("\\p{Punct}", "")
                .split(" ");
    }
}
