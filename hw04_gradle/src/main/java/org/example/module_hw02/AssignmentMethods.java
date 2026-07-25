package org.example.module_hw02;

import java.util.*;
import java.util.stream.Collectors;

public class AssignmentMethods {
       public <T> T[] replacingArrayElement(T[] arrays, int elementOne, int elementTwo) throws ArrayIndexOutOfBoundsException {
        if ((elementOne < 0) || (elementTwo < 0)) {
            throw new ArrayIndexOutOfBoundsException("Ошибка элемент массива не может быть отрицательным");
        }
        if ((elementOne >= arrays.length) || (elementTwo >= arrays.length)) {
            throw new ArrayIndexOutOfBoundsException("Ошибка, элемент в массиве отсутствует");
        }
        T transferElement = arrays[elementTwo];
        arrays[elementTwo] = arrays[elementOne];
        arrays[elementOne] = transferElement;
        return arrays;
    }

    public <T> List<T> convertArrayList(T[] array) {
       return new ArrayList<>(Arrays.asList(array));
    }

    public Map<String, Integer> listUniqueWordsSorted(String[] arrayWords) {
        if (arrayWords == null || arrayWords.length == 0) {
            return Collections.emptyMap();
        }
       Map<String, Integer> words = new HashMap<>(arrayWords.length);
        for (String word : arrayWords) {
            if (word == null) continue;
            String w = word.trim().toLowerCase();
            words.put(w, words.getOrDefault(w, 0) + 1);
        }
        return words.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }
}
