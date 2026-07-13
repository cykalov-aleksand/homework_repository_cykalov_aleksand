package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

public class AssignmentMethods {
    static Logger logger = LoggerFactory.getLogger(AssignmentMethods.class);

    public <T> T[] replacingArrayElement(T[] arrays, int elementOne, int elementTwo) throws IllegalAccessException,
            ArrayIndexOutOfBoundsException {
        if ((elementOne < 0) || (elementTwo < 0)) {
            throw new ArrayIndexOutOfBoundsException("Ошибка элемент массива не может быть отрицательным");
        }
        if ((arrays.length<2)||(elementOne >= arrays.length) || (elementTwo >= arrays.length)) {
            throw new IllegalAccessException("Ошибка, элемент в массиве отсутствует");
        }
        if ((elementOne == elementTwo)) {
            throw new IllegalAccessException("Замена элемента не проведена, массив остался без изменений");
        }
        T transferElement = arrays[elementTwo];
        arrays[elementTwo] = arrays[elementOne];
        arrays[elementOne] = transferElement;
        return arrays;
    }

    public <T> List<T> convertArrayList(T[] array) {
        ArrayList<T> list = new ArrayList<>();
        Collections.addAll(list, array);
        return list;
    }

    public Map<String, Integer> listUniqueWordsSorted(String[] arrayWords) {
        // проходим по принятому массиву удаляем возможные пробелы и преобразуем слова в нижний регистр с целью дальнейшего поиска однотипных
        Stream<String> stringStream = Arrays.stream(arrayWords).map(o -> o.trim().toLowerCase());
        logger.debug("количество слов в предложении - {}", arrayWords.length);
        Map<String, Integer> words = new TreeMap<>();
        stringStream.forEach(o -> words.put(o, words.getOrDefault(o, 0) + 1));
        logger.debug("Количество уникальных слов в массиве - {}",words.size());
        return words;
    }
}
