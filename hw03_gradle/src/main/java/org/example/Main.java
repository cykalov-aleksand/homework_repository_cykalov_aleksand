package org.example;


import org.example.annotations.After;
import org.example.annotations.Before;
import org.example.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.*;
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
                processingTestClass(className).forEach((key, value) -> logger.info("{} = {}",key, value));

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

      private static List<Method> getJUnitMethods(Class<?> clazz,
                                                Class<? extends java.lang.annotation.Annotation> annotationClass) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(annotationClass))
                .filter(m -> Modifier.isPublic(m.getModifiers()))
                .filter(m -> m.getParameterCount() == 0)
                .collect(Collectors.toList());
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
                    "Успешно прошло тестов", 0,"Всего пройдено тестов", 0,"Упало тестов", 0);
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

    //Метод, согласно задания не требовался на нем, тренировался в доступе к классам с помощью рефлексии
    private static void processingUsualClass(Class<?> clazz) {
        logger.info("Исследуемый класс: {}", clazz.getName());
        Constructor<?>[] constructors = clazz.getConstructors();
        if (constructors.length == 0) {
            logger.info("Публичные конструкторы отсутствуют.");
        } else {
            for (Constructor<?> constructor : constructors) {
                logger.info("Публичный конструктор: {}", constructor.toGenericString());
            }
        }

        Object instance;
        try {
            Constructor<?> defaultConstructor = clazz.getConstructor();
            instance = defaultConstructor.newInstance();
            logger.info("Экземпляр класса {} успешно создан.", clazz.getSimpleName());
        } catch (NoSuchMethodException e) {
            String msg = "У класса нет публичного конструктора без параметров";
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            String msg = "Не удалось создать экземпляр класса";
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        }

        // Анализ полей
        logger.debug("Анализ объявленных полей (включая приватные и из суперклассов):");
        List<Field> allFields = getAllFields(clazz);

        for (Field field : allFields) {
            Class<?> fieldType = field.getType();
            try {
                field.setAccessible(true);
                Object value = field.get(instance);

                String formattedValue;
                if (value == null) {
                    formattedValue = "null";
                } else if (fieldType.isPrimitive()) {
                    formattedValue = value.toString();
                } else {
                    formattedValue = value.toString();
                }

                logger.debug("Поле: {} {} = {}", fieldType.getTypeName(), field.getName(), formattedValue);
            } catch (IllegalAccessException e) {
               logger.error("Не удалось прочитать поле {} из-за нарушения доступа", field.getName(), e);
                throw new RuntimeException("Ошибка доступа к полю " + field.getName(), e);
            }
        }
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && !clazz.equals(Object.class)) {
            Field[] declared = clazz.getDeclaredFields();
            Collections.addAll(fields, declared);
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
   }