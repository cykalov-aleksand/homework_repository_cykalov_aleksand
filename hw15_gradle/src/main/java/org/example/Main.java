package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int LIMIT = 10; // вершина счёта
    private static final int TOTAL_CYCLES = 2;

    private final Object lock = new Object();

    // ОБЩЕЕ СОСТОЯНИЕ
    private boolean turnIsFirst = true; // чья очередь
    private int cyclesDone = 0;          // сколько полных циклов (1->10->1) сделано
    private int step = 1;                // текущее число
    private boolean ascending = true;    // текущее направление счёта

    public static void main(String[] args) throws InterruptedException {
        var counter = new Main();
        counter.go();
    }

    private void go() throws InterruptedException {
        synchronized (lock) {
            turnIsFirst = true;
            cyclesDone = 0;
            step = 1;
            ascending = true;
        }

        Runnable r1 = () -> runSequence("Поток 1", true);
        Runnable r2 = () -> runSequence("Поток 2", false);

        var t1 = new Thread(r1, "Поток-1");
        var t2 = new Thread(r2, "Поток-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        logger.info("Все потоки завершены. Программа завершается.");
    }
    private void runSequence(String name, boolean iAmFirst) {
        while (true) {
            synchronized (lock) {
                if (cyclesDone >= TOTAL_CYCLES) {
                    lock.notifyAll(); // будим всех, для выхода
                    logger.info("[{}] {}: ЗАВЕРШЕНИЕ РАБОТЫ (циклы={})",
                            Thread.currentThread().getName(), name, cyclesDone);
                    return;
                }

                // Ждём своей очереди только если задача ещё не завершена
                while (turnIsFirst != iAmFirst) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                // Печатаем текущее значение
                logger.info("[{}] {}: {}", Thread.currentThread().getName(), name, step);

                // Обновляем состояние
                if (ascending) {
                    if (step == LIMIT) {
                        ascending = false;
                    }
                } else {
                    if (step == 1) {
                        ascending = true;
                        cyclesDone++;
                    }
                }
                step = ascending ? step + 1 : step - 1;

                // Передаём ход
                turnIsFirst = !turnIsFirst;
                lock.notify();
            }
        }
    }
}