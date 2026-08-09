package org.example.curency;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CashValidator {
    public static Map<Integer, Integer> validate(
            Map<Integer, Integer> input,
            List<Integer> validNominals,
            String currencyName) {
        if (input == null) {
            throw new IllegalArgumentException("Карта купюр не может быть null");
        }
        if (validNominals == null) {
            throw new IllegalArgumentException("Список допустимых номиналов не может быть null");
        }

        Set<Integer> validSet = Set.copyOf(validNominals);
        Map<Integer, Integer> result = new LinkedHashMap<>();

        for (Map.Entry<Integer, Integer> entry : input.entrySet()) {
            Integer nominal = entry.getKey();
            Integer count = entry.getValue();

            if (!validSet.contains(nominal)) {
                throw new IllegalArgumentException("Недопустимый номинал в " + currencyName + ": " + nominal +
                        ". Допустимые: " + validNominals);
            }
            if (count > 0) {
                result.put(nominal, count);
            }
        }

        for (Integer nominal : validNominals) {
            result.putIfAbsent(nominal, 0);
        }

        return result;
    }
}
