package org.example;


import org.example.anatations.After;
import org.example.anatations.Before;
import org.example.anatations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        String stringClassName;
        if (args.length == 0) {
            stringClassName = "org.example.AssignmentMethodsTest";
        } else {
            stringClassName = "org.example." + args[0].trim();
        }
        try {
            Class<?> className = Class.forName(stringClassName);
            if (classAnalysis(className).isEmpty()) {
                logger.info("{} - обычный класс", className.getName());
                processingUsualClass(className);
            } else {
                logger.info("{} - тестовый класс", className.getName());
                processingTestClass(className).forEach((key, value) -> System.out.println(key + " = " + value));

            }
        } catch (ClassNotFoundException e) {
            logger.error("Ошибка при динамическом доступе к классу: {}", stringClassName, e);
        } catch (Exception e) {
            // Сюда попадают ошибки самого раннера (рефлексия, конструктор и т.п.)
            logger.error("Ошибка раннера при запуске тестов", e);
        }
    }

    private static List<Method> classAnalysis(Class<?> className) {
        return Arrays.stream(className.getDeclaredMethods()).filter(element -> element.isAnnotationPresent(Test.class)).toList();
    }

    private static void processingUsualClass(Class<?> className) {
    }

    private static Map<String, Integer> processingTestClass(Class<?> className){
        List<Method> testMethods = getJUnitMethods(className, Test.class);
        List<Method> beforeMethods = getJUnitMethods(className, Before.class);
        List<Method> afterMethods = getJUnitMethods(className, After.class);
        int calculatedSuccessfullyPassedTest = 0;
        int countFailedTest = 0;
        if (testMethods.isEmpty()) {
            logger.warn("В классе {} нет методов с аннотацией @Test", className.getName());
            return Map.of("Успешно прошло тестов", 0, "Всего пройдено тестов", 0, "Упало тестов", 0);
        }

        Constructor<?> constructor;
        try {
            constructor = className.getDeclaredConstructor();
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            logger.error("У класса {} нет конструктора без аргументов. Тесты запустить невозможно.", className.getName(), e);
            return Map.of(
                    "Успешно прошло тестов", 0,
                    "Всего пройдено тестов", testMethods.size(),
                    "Упало тестов", testMethods.size()
            );
        }


        for (Method testMethod : testMethods) {
            Object testInstance;
            try {
                testInstance = constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                logger.error("Не удалось создать экземпляр для теста {}. Считаем тест упавшим.", testMethod.getName(), e);
                countFailedTest++;
                continue; // переходим к следующему тесту
            }

            boolean beforeFailed = false;
            for (Method beforeMethod : beforeMethods) {
                try {
                    beforeMethod.invoke(testInstance);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    Throwable cause = e.getCause();
                    logger.error("@Before '{}' упал перед тестом '{}': {}",
                            beforeMethod.getName(), testMethod.getName(), cause.getMessage(), cause);
                    beforeFailed = true;
                    break;
                }
            }

            if (beforeFailed) {
                countFailedTest++;
                runAfterMethods(afterMethods, testInstance, testMethod.getName());
                continue;
            }

            try {
                logger.info("Выполнение теста: {}", testMethod.getName());
                testMethod.invoke(testInstance);
                calculatedSuccessfullyPassedTest++;
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                logger.error("Тест не пройден - {}: {}", testMethod.getName(), cause.getMessage(), cause);
                countFailedTest++;
            } catch (Exception e) {
                logger.error("Неожиданная ошибка при вызове теста {}", testMethod.getName(), e);
                countFailedTest++;
            } finally {
                // @After должен запускаться всегда, если есть экземпляр
                runAfterMethods(afterMethods, testInstance, testMethod.getName());
            }
        }
        Map<String, Integer> result = new HashMap<>();
        result.put("Успешно прошло тестов", calculatedSuccessfullyPassedTest);
        result.put("Всего пройдено тестов", calculatedSuccessfullyPassedTest + countFailedTest);
        result.put("Упало тестов", countFailedTest);
        return result;
    }
    private static void runAfterMethods(List<Method> afterMethods, Object instance, String testName) {
        // Если экземпляр не создан, @After вызывать нельзя — это предотвращает NullPointerException
        if (instance == null) {
            return;
        }
        for (Method afterMethod : afterMethods) {
            try {
                afterMethod.invoke(instance);
            } catch (Exception e) {
                logger.error("@After '{}' упал после теста '{}'", afterMethod.getName(), testName, e);
            }
        }
    }
    private static List<Method> getJUnitMethods(Class<?> clazz,
                                                Class<? extends java.lang.annotation.Annotation> annotationClass) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(annotationClass))
                .filter(m -> Modifier.isPublic(m.getModifiers()))
                .filter(m -> m.getParameterCount() == 0)
                .collect(Collectors.toList());
    }
}