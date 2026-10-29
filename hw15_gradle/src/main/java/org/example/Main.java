package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int LIMIT = 10;
    private static final int TOTAL_CYCLES = 2;

    private final Object lock = new Object();
    private boolean turnIsFirst = true;
    private volatile boolean finished = false; // общий флаг завершения

    public static void main(String[] args) throws InterruptedException {
        var counter = new Main();
        counter.go();
    }

    private void go() throws InterruptedException {
        synchronized (lock) {
            turnIsFirst = true;
            finished = false;
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
        int step = 1;
        boolean ascending = true;
        int cyclesDone = 0;

        while (true) {
            synchronized (lock) {
                // если уже завершили — сразу выходим
                if (finished) {
                    return;
                }

                // Ждём своей очереди
                while (turnIsFirst != iAmFirst && !finished) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                // Повторная проверка после wait
                if (finished) {
                    return;
                }

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

                        // ПРОВЕРКА ЗАВЕРШЕНИЯ СРАЗУ ПОСЛЕ УВЕЛИЧЕНИЯ cyclesDone
                        if (cyclesDone >= TOTAL_CYCLES) {
                            finished = true;      // ставим общий флаг
                            lock.notifyAll();     // будим ВСЕХ, чтобы они увидели finished=true
                            logger.info("[{}] {}: ЗАВЕРШЕНИЕ РАБОТЫ (циклы={})",
                                    Thread.currentThread().getName(), name, cyclesDone);
                            return;               // выходим из этого потока
                        }
                    }
                }
                step = ascending ? step + 1 : step - 1;

                // Передаём ход другому потоку
                turnIsFirst = !turnIsFirst;
                lock.notify();
            }
        }
    }
}