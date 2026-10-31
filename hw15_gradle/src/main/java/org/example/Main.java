package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private final Object lock = new Object();
    private int totalSteps = 0;
    private static final int MAX_STEPS = 40;
    // true = сейчас ход у Потока-1, false = у Потока-2
    private boolean turnIsFirst = true;

    public static void main(String[] args) throws InterruptedException {
        var counter = new Main();
        counter.go();
    }

    private void go() throws InterruptedException {
        ExecutableObject object1 = new ExecutableObject();
        ExecutableObject object2 = new ExecutableObject();

        Runnable r1 = () -> runSequence(object1, true);
        Runnable r2 = () -> runSequence(object2, false);

        var t1 = new Thread(r1, "Поток-1");
        var t2 = new Thread(r2, "Поток-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        logger.info("Все потоки завершены. Программа завершается.");
    }

    private void runSequence(ExecutableObject obj, boolean iAmFirst) {
        while (true) {
            synchronized (lock) {
                // Если лимит уже достигнут — сразу выходим, но сначала будим остальных
                if (totalSteps >= MAX_STEPS) {
                    lock.notifyAll(); // разбудить всех, кто может быть в wait()
                    return;
                }
                while ((iAmFirst && !turnIsFirst) || (!iAmFirst && turnIsFirst)) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        lock.notifyAll(); // на случай, если другой поток всё ещё ждёт
                        return;
                    }
                }

                // Ещё раз проверяем лимит уже после того, как получили право на ход
                if (totalSteps >= MAX_STEPS) {
                    lock.notifyAll();
                    return;
                }

                int result = obj.result();
                logger.info("[{}] : {}", Thread.currentThread().getName(), result);
                totalSteps++;
                turnIsFirst = !turnIsFirst;
                lock.notifyAll();
            }
        }
    }
}