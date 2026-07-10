import org.example.AssignmentMethods;
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

    @Test
    public void replacingArrayElement() {
        Integer[] objects = {10, 20, 30};
        Integer[] objectsTest = {30, 20, 10};
        Object[] result;
        logger.info("Тест на проверку перестановки местами элементов массива.");
        try {
            result = assignmentMethods.replacingArrayElement(objects, 0, 2);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        assertArrayEquals(result, objectsTest);
    }

    @Test
    public void replacingArrayElementIllegalAccessException() {
        logger.info("Тест на проверку выбрасывания исключения при вводе одинаковых элементов.");
        Integer[] objects = {10, 20, 30};
        try {
            assignmentMethods.replacingArrayElement(objects, 0, 0);
        } catch (IllegalAccessException e) {
            assertEquals("Замена элемента не проведена, массив остался без изменений", e.getMessage());
            assertNull(e.getCause());
        }
    }

    @Test
    public void replacingArrayElementIllegalAccessExceptionTwo() {
        logger.info("Тест на проверку выбрасывания исключения при вводе элемента выше длины массива.");
        Integer[] objects = {10, 20, 30};
        try {
            assignmentMethods.replacingArrayElement(objects, 0, 4);
        } catch (IllegalAccessException e) {
            assertEquals("Ошибка, элемент в массиве отсутствует", e.getMessage());
            assertNull(e.getCause());
        }
    }

    @Test
    public void replacingArrayElementArrayIndexOutOfBoundsException() {
        logger.info("Тест на проверку выбрасывания исключения при вводе отрицательного элемента.");
        Integer[] objects = {10, 20, 30};
        try {
            assignmentMethods.replacingArrayElement(objects, -2, 1);
        } catch (ArrayIndexOutOfBoundsException | IllegalAccessException e) {
            assertEquals("Ошибка элемент массива не может быть отрицательным", e.getMessage());
            assertNull(e.getCause());
        }
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
