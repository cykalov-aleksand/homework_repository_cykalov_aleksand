package org.example.time;


import org.example.Main;
import org.example.module_hw02.MainModified;
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

public class TestTime {
    private static final Logger logger = LoggerFactory.getLogger(TestTime.class);

    public static void main(String[] args) {
        double averageDirect = executionTimeAnalysis();
        double averageDirectModifiedApplication=analyzingExecutionTimeModifiedApplication();

        ResultReflection resultReflection = analysisExecutionTimeDuringReflection();

        if (averageDirect >= 0) {
            logger.info("Среднее время при прямом вызове Main: {} мс", averageDirect);
        } else {
            logger.warn("Замер прямого вызова не удался");
        }
        if (averageDirectModifiedApplication >= 0) {
            logger.info("Среднее время при прямом вызове MainModified (модифицированное приложение): {} мс", averageDirectModifiedApplication);
        } else {
            logger.warn("Замер прямого вызова не удался");
        }
        if (resultReflection != null) {
            logger.info("Среднее время вызова через рефлексию (только invoke): {} мс", resultReflection.getAverageTimeMs());
            logger.info("Разовые накладные расходы (загрузка JAR + поиск класса + поиск метода): {} мс", resultReflection.getSetupTimeMs());
        } else {
            logger.warn("Замер через рефлексию не удался");
        }
    }

    private static void informationLogger(List<Double> listTime, long maxMemory, long totalMemory, long freeMemory) {
        double avg = averageAccessTimeTenSamples(listTime);
        long usedMemory = totalMemory - freeMemory;

        logger.info("{} мс — среднее время (20 выборок)", avg);
        logger.info("{} МБ — максимальный размер кучи (-Xmx)", maxMemory);
        logger.info("{} МБ — занятая память", usedMemory);
        logger.info("{} МБ — свободная память в куче", freeMemory);
    }
private static double analyzingExecutionTimeModifiedApplication(){
    logger.info("\nСравнение: прямой вызов модифицированного приложения (20 итераций, без прогрева)");
    String[] otherArgs = {};
    List<Double> listTime = new ArrayList<>();

    for (int i = 0; i < 20; i++) {
        long startProject = System.nanoTime();
        MainModified.main(otherArgs);
        double elapsedMs = (System.nanoTime() - startProject) / 1_000_000.0;
        listTime.add(elapsedMs);
    }

    Runtime runtime = Runtime.getRuntime();
    long maxMemory = runtime.maxMemory() / 1048576;
    long totalMemory = runtime.totalMemory() / 1048576;
    long freeMemory = runtime.freeMemory() / 1048576;

    informationLogger(listTime, maxMemory, totalMemory, freeMemory);

    return averageAccessTimeTenSamples(listTime);
}
    private static double executionTimeAnalysis() {
        logger.info("\nСравнение: прямой вызов приложения (20 итераций, без прогрева)");
        String[] otherArgs = {};
        List<Double> listTime = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            long startProject = System.nanoTime();
            Main.main(otherArgs);
            double elapsedMs = (System.nanoTime() - startProject) / 1_000_000.0;
            listTime.add(elapsedMs);
        }

        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / 1048576;
        long totalMemory = runtime.totalMemory() / 1048576;
        long freeMemory = runtime.freeMemory() / 1048576;

        informationLogger(listTime, maxMemory, totalMemory, freeMemory);

        return averageAccessTimeTenSamples(listTime);
    }

    private static ResultReflection analysisExecutionTimeDuringReflection() {
        logger.info("\nСравнение: прямой вызов vs рефлексия (20 итераций, без прогрева)");
        File jarFile = new File("hw02_gradle.jar");

        try {
            // 1. Подготовка (ОДИН РАЗ): загрузка JAR, класса и получение Method
            long startSetup = System.nanoTime();

            URL jarUrl = jarFile.toURI().toURL();
            URLClassLoader loader = new URLClassLoader(new URL[]{jarUrl});
            Class<?> clazz = loader.loadClass("org.example.Main");
            Method mainMethod = clazz.getMethod("main", String[].class);

            double setupTimeMs = (System.nanoTime() - startSetup) / 1_000_000.0;

            // 2. Только замер вызова (много раз)
            List<Double> listTime = new ArrayList<>();
            String[] emptyArgs = {};

            for (int i = 0; i < 20; i++) {
                long startProject = System.nanoTime();
                mainMethod.invoke(null, (Object) emptyArgs);
                double elapsedMs = (System.nanoTime() - startProject) / 1_000_000.0;
                listTime.add(elapsedMs);
            }

            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory() / 1048576;
            long totalMemory = runtime.totalMemory() / 1048576;
            long freeMemory = runtime.freeMemory() / 1048576;

            informationLogger(listTime, maxMemory, totalMemory, freeMemory);

            double averageTimeMs = averageAccessTimeTenSamples(listTime);

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
        } catch (IllegalAccessException e) {
            logger.error("Нет доступа к методу main", e);
            return null;
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            logger.error("Ошибка внутри вызванного метода main", cause != null ? cause : e);
            return null;
        }
    }

    private static double averageAccessTimeTenSamples(List<Double> variables) {
        if (variables.isEmpty()) {
            return 0.0; // защита от деления на ноль
        }
        return variables.stream().mapToDouble(Double::doubleValue).sum() / variables.size();
    }
}