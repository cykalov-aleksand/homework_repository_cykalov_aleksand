
import org.example.AssignmentMethods;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class AssignmentMethodsTest {
    static Logger logger = LoggerFactory.getLogger(AssignmentMethodsTest.class);
    AssignmentMethods assignmentMethods = new AssignmentMethods();
    @BeforeEach
public void methodBefore(){
    logger.info("Загрузка метода перед тестом");
}
@AfterEach
public void methodAfter(){
        logger.info("Загрузка метода по окончанию теста\n");
}
    @Test
    public void replacingArrayElement() {
        Integer[] objects = {10, 20, 30};
        Integer[] objectsTest = {30, 20, 10};
        Object[] result;
        logger.info("Тест на проверку перестановки местами элементов массива.");
        result = assignmentMethods.replacingArrayElement(objects, 0, 2);
        assertArrayEquals(result, objectsTest);
    }

    @Test
    public void replacingArrayElementIllegalAccessExceptionTwo() {
        logger.info("Тест на проверку выбрасывания исключения при вводе элемента выше длины массива.");
        Integer[] objects = {10, 20, 30};
        Throwable exception = assertThrows(ArrayIndexOutOfBoundsException.class, () ->
                assignmentMethods.replacingArrayElement(objects, 0, 4));
        assertEquals("Ошибка, элемент в массиве отсутствует", exception.getMessage());
    }

    @Test
    public void replacingArrayElementArrayIndexOutOfBoundsException() {
        logger.info("Тест на проверку выбрасывания исключения при вводе отрицательного элемента.");
        Integer[] objects = {10, 20, 30};
        Throwable exception = assertThrows(ArrayIndexOutOfBoundsException.class, () ->
                assignmentMethods.replacingArrayElement(objects, -1, 2));
        assertEquals("Ошибка элемент массива не может быть отрицательным", exception.getMessage());
    }

    @Test
    public void convertArrayListIntegerTest() {
        logger.info("Тест на проверку преобразования массива в ArrayList.");
        Integer[] integers = {50, 40, 30};
        List<Integer> list = assignmentMethods.convertArrayList(integers);
        for (int i = 0; i < integers.length; i++) {
            assertEquals(integers[i], list.get(i));
        }
    }

    @Test
    public void listUniqueWordsSortedTestSize() {
        logger.info("Тест на проверку количества уникальных слов в массиве.");
        String[] words = {"Зима", "Зима", "утро", "утро ", "Вечер", "мир", "зима"};
        Map<String, Integer> wordsMap = assignmentMethods.listUniqueWordsSorted(words);
        assertEquals(4, wordsMap.size());
    }

    @Test
    public void listUniqueWordsSortedTestNumberElements() {
        logger.info("Тест на проверку подсчета количество одинаковых слов в массиве.");
        String[] words = {"Зима", "Зима", "утро", "утро ", "Вечер", "мир", "зима"};
        Map<String, Integer> wordsMap = assignmentMethods.listUniqueWordsSorted(words);
        assertEquals(3, wordsMap.get("зима").intValue());
        assertEquals(2, wordsMap.get("утро").intValue());
    }

    @Test
    public void listUniqueWordsSortedTestSorted() {
        logger.info("Тест на проверку размещения слов в алфавитном порядке.");
        String[] words = {"Зима", "Зима", "утро", "утро ", "Вечер", "мир", "зима"};
        Map<String, Integer> wordsMap = assignmentMethods.listUniqueWordsSorted(words);
        List<String> keyList = new ArrayList<>(wordsMap.keySet());
        assertEquals(List.of("вечер", "зима", "мир", "утро"), keyList);
    }
}
