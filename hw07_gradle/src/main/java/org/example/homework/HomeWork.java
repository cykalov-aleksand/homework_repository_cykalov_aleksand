package org.example.homework;

import org.example.homework.handler.ComplexProcessor;
import org.example.homework.listener.ListenerPrinterConsole;
import org.example.homework.listener.homework.HistoryListener;
import org.example.homework.model.Message;
import org.example.homework.model.ObjectForMessage;
import org.example.homework.processor.ChangeValuesOfField11And12;
import org.example.homework.processor.LoggerProcessor;
import org.example.homework.processor.ProcessorConcatFields;
import org.example.homework.processor.ProcessorUpperField10;
import org.example.homework.processor.ThrowOnExceptionEvenSecond;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

public class HomeWork {
    private static final Logger logger = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {
        var processorConcat = new ProcessorConcatFields();
        var processorUpper = new LoggerProcessor(new ProcessorUpperField10());
        var processorSwap = new ChangeValuesOfField11And12();

        // Для демонстрации исключения — зафиксируем секунду (например, 4 — чётная)
        var throwOnEven = new ThrowOnExceptionEvenSecond(3);

        // Цепочка процессоров
        var processors = List.of(processorConcat, processorUpper, processorSwap, throwOnEven);

        // Обработчик с обработкой ошибок
        var complexProcessor = new ComplexProcessor(processors, ex -> logger.error("Ошибка в процессоре: {}", ex.getMessage()));

        // Подключаем слушатели
        var listenerPrinter = new ListenerPrinterConsole();
        var historyListener = new HistoryListener();

        complexProcessor.addListener(listenerPrinter);
        complexProcessor.addListener(historyListener);

        // Создаём исходное сообщение
        ObjectForMessage object = new ObjectForMessage();
        object.setData(List.of("x", "y", "z"));

        var originalMessage = new Message.Builder(1L)
                .field1("hello")
                .field2("world")
                .field3("java")
                .field6("field6")
                .field10("field10_value")
                .field11("value11")
                .field12("value12")
                .field13(object)
                .build();

        logger.info("\n=== ДЕМОНСТРАЦИЯ РАБОТЫ КОМПЛЕКСНОГО ПРОЦЕССОРА ===");

        logger.info("Исходное сообщение: {}", originalMessage);

        // Обработка сообщения
        Message result;
        try {
            logger.info("Обработанное сообщение");
            result = complexProcessor.handle(originalMessage);
            logger.info(" Обработка завершена успешно.");
            logger.info("Результат: {}", result);
        } catch (Exception e) {
            logger.info(" Обработка прервана из-за исключения: {}", e.getMessage());
        }

        // Показываем историю изменений
        logger.info("\n=== ИСТОРИЯ ИЗМЕНЕНИЙ ===");
        var history = historyListener.getHistory();
        logger.info("Размер истории: {}", history.size());
        for (int i = 0; i < history.size(); i++) {
            logger.info("История [{}]: {}", i + 1, history.get(i));
        }

        // Проверка по ID
        long targetId = 1L;
        historyListener.findMessageById(targetId).ifPresentOrElse(
                msg -> logger.info(" Сообщение с ID {}: найдено в истории: {}", targetId, msg),
                () -> logger.info(" Сообщение с ID {} не найдено", targetId)
        );

        // Демонстрация: попробуем модифицировать field13 извне — и проверим, не сломается ли история
        object.setData(List.of("HACKED")); // Меняем оригинал
        logger.info("=== ПРОВЕРКА ИММУТАБЕЛЬНОСТИ ===");
        logger.info("Оригинальный objectForMessage теперь: {}", object);
        logger.info("Но сообщение в истории по-прежнему: {}", history.get(0).getField13()); // Должно быть старое значение

        // Убираем слушатели
        complexProcessor.removeListener(listenerPrinter);
        complexProcessor.removeListener(historyListener);
    }
}