package org.example.time;


import org.example.Main;
import org.example.module_hw02_modified.MainModified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TestTime {
    private static final Logger logger = LoggerFactory.getLogger(TestTime.class);

    public static void main(String[] args) {
        double averageDirect = analyzingExecutionTimeClean(new String[]{}, Main::main);
        double averageDirectModifiedApplication = analyzingExecutionTimeClean(new String[]{}, MainModified::main);

        ResultReflection resultReflection = analysisExecutionTimeDuringReflection();

        logResults(averageDirect, averageDirectModifiedApplication, resultReflection);
    }

    private static void logResults(
            double averageDirect,
            double averageDirectModified,
            ResultReflection resultReflection
    ) {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / 1048576;
        long totalMemory = runtime.totalMemory() / 1048576;
        long freeMemory = runtime.freeMemory() / 1048576;
        long usedMemory = totalMemory - freeMemory;

        logger.info("\n=== ИТОГОВЫЕ РЕЗУЛЬТАТЫ ===");
        if (averageDirect >= 0) {
            logger.info("Среднее время при прямом вызове Main: {} мс", averageDirect);
        } else {
            logger.warn("Замер прямого вызова Main не удался");
        }
        if (averageDirectModified >= 0) {
            logger.info("Среднее время при прямом вызове MainModified: {} мс", averageDirectModified);
        } else {
            logger.warn("Замер прямого вызова MainModified не удался");
        }
        if (resultReflection != null) {
            logger.info("Среднее время вызова через рефлексию (только invoke): {} мс", resultReflection.getAverageTimeMs());
            // Исправили формулировку: убрали «загрузка JAR», потому что отдельной операции нет
            logger.info("Разовые накладные расходы (поиск класса + поиск метода): {} мс", resultReflection.getSetupTimeMs());
        } else {
            logger.warn("Замер через рефлексию не удался");
        }

        logger.info("{} МБ — максимальный размер кучи (-Xmx)", maxMemory);
        logger.info("{} МБ — занятая память", usedMemory);
        logger.info("{} МБ — свободная память в куче", freeMemory);
    }

    /**
     * «Чистый» замер времени: замеряем каждую итерацию отдельно.
     */
    private static double analyzingExecutionTimeClean(String[] args, Consumer<String[]> consumer) {
        int warmupIterations = 10;
        int measurementIterations = 50; // Увеличили для лучшей статистики

        // Прогрев (не идёт в статистику)
        for (int i = 0; i < warmupIterations; i++) {
            consumer.accept(args);
        }

        List<Double> listTime = new ArrayList<>(measurementIterations);
        for (int i = 0; i < measurementIterations; i++) {
            long start = System.nanoTime();
            consumer.accept(args);
            double elapsedMs = (System.nanoTime() - start) / 1_000_000.0;
            listTime.add(elapsedMs);
        }

        return averageAccessTimeTenSamples(listTime);
    }

    private static ResultReflection analysisExecutionTimeDuringReflection() {
        logger.info("\nСравнение: прямой вызов vs рефлексия (с прогревом, 50 выборок)");
        File jarFile = new File("hw02_gradle.jar");

        try {
            URL jarUrl = jarFile.toURI().toURL();
            // Создаём loader ДО замера: его инициализация не должна входить в накладные расходы поиска
            URLClassLoader loader = new URLClassLoader(new URL[]{jarUrl});

            // Замер накладных расходов: только поиск класса и метода
            long startSetup = System.nanoTime();

            Class<?> clazz = loader.loadClass("org.example.Main");
            Method mainMethod = clazz.getMethod("main", String[].class);

            double setupTimeMs = (System.nanoTime() - startSetup) / 1_000_000.0;

            Consumer<String[]> reflectiveConsumer = args -> {
                try {
                    mainMethod.invoke(null, (Object) args);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Нет доступа к методу main", e);
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    if (cause != null) {
                        throw new RuntimeException("Ошибка внутри вызванного метода main", cause);
                    } else {
                        throw new RuntimeException("Ошибка вызова main через рефлексию", e);
                    }
                }
            };

            double averageTimeMs = analyzingExecutionTimeClean(new String[]{}, reflectiveConsumer);

            loader.close();

            return new ResultReflection(averageTimeMs, setupTimeMs);

        } catch (IOException e) {
            logger.error("Не удалось найти или прочитать JAR-файл: {}", jarFile.getAbsolutePath(), e);
            return null;
        } catch (ClassNotFoundException e) {
            logger.error("Класс org.example.Main не найден в JAR", e);
            return null;
        } catch (NoSuchMethodException e) {
            logger.error("Метод main(String[]) не найден в классе", e);
            return null;
        } catch (RuntimeException e) {
            logger.error("Ошибка при вызове main через рефлексию", e);
            return null;
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при анализе рефлексии", e);
            return null;
        }
    }

    private static double averageAccessTimeTenSamples(List<Double> variables) {
        if (variables.isEmpty()) {
            return 0.0;
        }
        return variables.stream().mapToDouble(Double::doubleValue).sum() / variables.size();
    }
}