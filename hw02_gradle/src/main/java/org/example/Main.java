package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        //элементы метода замены элементов
        int numberOne = 0;
        int numberTwo = 2;
        String string = """
                Люблю гроозу в начале мая Люблю грозу в начале мая.
                Когда весенний, первый гром,
                Как бы резвяся и играя,
                Грохочет в небе голубом.""";
        ArraySimulator simulator = new ArraySimulator();
        AssignmentMethods assignmentMethods = new AssignmentMethods();
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