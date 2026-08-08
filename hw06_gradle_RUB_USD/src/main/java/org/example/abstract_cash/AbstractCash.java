package org.example.abstract_cash;


import org.example.curency.Currency;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractCash implements Currency {
        protected final Map<Integer, Integer> banknotes; // номинал -> количество

        protected AbstractCash(Map<Integer, Integer> banknotes) {
            this.banknotes = new LinkedHashMap<>();
            for (Map.Entry<Integer, Integer> entry : banknotes.entrySet()) {
                int count = validate(entry.getValue());
                if (count > 0) {
                    this.banknotes.put(entry.getKey(), count);
                }
            }
        }

        private int validate(int value) {
            if (value < 0) {
                throw new IllegalArgumentException("Количество купюр не может быть отрицательным");
            }
            return value;
        }

        @Override
        public int amountOfCash() {
            return banknotes.entrySet().stream()
                    .mapToInt(entry -> entry.getKey() * entry.getValue())
                    .sum();
        }

        public Map<Integer, Integer> getBanknotes() {
            return new LinkedHashMap<>(banknotes);
        }

        public int getCount(int nominal) {
            return banknotes.getOrDefault(nominal, 0);
        }
    }

