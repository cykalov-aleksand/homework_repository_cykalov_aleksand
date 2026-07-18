package org.example;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class AssignmentMethodsTest {
    static Logger logger = LoggerFactory.getLogger(AssignmentMethodsTest.class);
    AssignmentMethods assignmentMethods = new AssignmentMethods();

    @Before
    public void methodBefore() {
        logger.info("Загрузка метода перед тестом");
    }

    @After
    public void methodAfter() {
        logger.info("Загрузка метода по окончанию теста\n");
    }

    @Test
    public void replacingArrayElement() {
        Integer[] objects = {10, 20, 30};
        Integer[] objectsTest = {30, 20, 10};
        Object[] result;
        logger.info("Тест на проверку перестановки местами элементов массива.");
        result = assignmentMethods.replacingArrayElement(objects, 0, 2);
        for (int numberElement = 0; numberElement < result.length; numberElement++) {
            if (result[numberElement] != objectsTest[numberElement]) {
                throw new IllegalArgumentException("Тест на проверку перестановки местами элементов массива не пройден");
            }
        }
    }
      @Test
    public void replacingArrayElementIllegalAccessExceptionTwo() {
        logger.info("Тест на проверку выбрасывания исключения при вводе элемента выше длины массива.");
        Integer[] objects = {10, 20, 30};
        boolean checkingException=false;
        try {
            assignmentMethods.replacingArrayElement(objects, 0, 3);
        }catch (ArrayIndexOutOfBoundsException e){
            if("Ошибка, элемент в массиве отсутствует".equals(e.getMessage())) {
                checkingException = true;
            }
        }
        if(!checkingException){
            throw new IllegalArgumentException("Тест на проверку выбрасывания исключения при вводе элемента выше длины массива не пройден");
        }
       }

    @Test
    public void replacingArrayElementArrayIndexOutOfBoundsException() {
        logger.info("Тест на проверку выбрасывания исключения при вводе отрицательного элемента.");
        Integer[] objects = {10, 20, 30};
        boolean checkingException=false;
        try {
            assignmentMethods.replacingArrayElement(objects, -1, 2);
        }catch (ArrayIndexOutOfBoundsException e){
            if ("Ошибка элемент массива не может быть отрицательным".equals(e.getMessage())){
            checkingException=true;
            }
        }
        if(!checkingException){
            throw new IllegalArgumentException("Тест на проверку выбрасывания исключения при вводе отрицательного элемента не пройден");
        }
        }

    @Test
    public void convertArrayListIntegerTest() {
        logger.info("Тест на проверку преобразования массива в ArrayList.");
        Integer[] integers = {50, 40, 30};
        List<Integer> list = assignmentMethods.convertArrayList(integers);
        for (int i = 0; i < integers.length; i++) {
            if (!Objects.equals(integers[i], list.get(i))) {
                throw new IllegalArgumentException("Тест на проверку преобразования массива в ArrayList не пройден");
            }
        }
    }

    @Test
    public void listUniqueWordsSortedTestSize() {
        logger.info("Тест на проверку количества уникальных слов в массиве.");
        String[] words = {"Зима", "Зима", "утро", "утро ", "Вечер", "мир", "зима"};
        Map<String, Integer> wordsMap = assignmentMethods.listUniqueWordsSorted(words);
        if (wordsMap.size() != 4) {
            throw new IllegalArgumentException("Тест на проверку количества уникальных слов в массиве не пройден");
        }
    }

    @Test
    public void listUniqueWordsSortedTestNumberElements() {
        logger.info("Тест на проверку подсчета количество одинаковых слов в массиве.");
        String[] words = {"Зима", "Зима", "утро", "утро ", "Вечер", "мир", "зима"};
        Map<String, Integer> wordsMap = assignmentMethods.listUniqueWordsSorted(words);
        if (wordsMap.get("зима") != 3) {
            throw new IllegalArgumentException("Тест на проверку подсчета количество одинаковых слов в массиве не пройден");
        }
    }

    @Test
    public void listUniqueWordsSortedTestSorted() {
        logger.info("Тест на проверку размещения слов в алфавитном порядке.");
        String[] words = {"Зима", "Зима", "утро", "утро ", "Вечер", "мир", "зима"};
        List<String> etalonList = List.of("вечер", "зима", "мир", "утро");
        Map<String, Integer> wordsMap = assignmentMethods.listUniqueWordsSorted(words);
        List<String> keyList = new ArrayList<>(wordsMap.keySet());
        for (int number = 0; number < etalonList.size(); number++) {
            if (!etalonList.get(number).equals(keyList.get(number))) {
                throw new IllegalArgumentException("Тест на проверку размещения слов в алфавитном порядке не пройден");
            }
        }
    }
}
