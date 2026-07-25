package org.example.module_hw02_modified;

import org.example.ArraySimulator;
import org.example.AssignmentMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

public class MainModified {
    static Logger logger = LoggerFactory.getLogger(MainModified.class);

    public static void main(String[] args) {
        //элементы метода замены элементов
        int numberOne = 0;
        int numberTwo = 2;
        String string = """
                Люблю гроозу в начале мая Люблю грозу в начале мая.
                Когда весенний, первый гром,
                Как бы резвяся и играя,
                Грохочет в небе голубом.""";
        org.example.ArraySimulator simulator = new ArraySimulator();
        org.example.AssignmentMethods assignmentMethods = new AssignmentMethods();
        try {
            Integer[] integers = simulator.simulatorArrayInteger(3, 0, -10);
            logger.info("Иммитация случайных чисел \n{}", Arrays.toString(integers));
            logger.info("Метод, который меняет элемент массива - {}, на элемент массива - {}: \n{}", numberOne, numberTwo,
                    Arrays.toString(assignmentMethods.replacingArrayElement(integers, numberOne, numberTwo)));
            logger.info("Метод преобразования массива в ArrayList {}", assignmentMethods.convertArrayList(integers));
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            logger.error(e.getMessage());
        }
        String[] arrayString = simulator.simulatorArrayString(string);
        logger.info("Преобразование строки предложения в массив элементов {}", Arrays.toString(arrayString));
        logger.info("Выводим список уникальных слов ");
        Map<String, Integer> listMap = assignmentMethods.listUniqueWordsSorted(arrayString);
        listMap.forEach((key, value) -> logger.info("Слово - \"{}\" содержится -{}", key.toUpperCase(), value));
    }
}