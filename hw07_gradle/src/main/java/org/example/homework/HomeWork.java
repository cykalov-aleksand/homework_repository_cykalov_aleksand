package org.example.homework;

import org.example.homework.handler.ComplexProcessor;
import org.example.homework.listener.ListenerPrinterConsole;
import org.example.homework.model.Message;
import org.example.homework.model.ObjectForMessage;
import org.example.homework.processor.ChangeValuesOfSeats11And12;
import org.example.homework.processor.LoggerProcessor;
import org.example.homework.processor.ProcessorConcatFields;
import org.example.homework.processor.ProcessorUpperField10;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

public class HomeWork {

    /*
    Реализовать to do:
      1. Добавить поля field11 - field13 (для field13 используйте класс ObjectForMessage)
      2. Сделать процессор, который поменяет местами значения field11 и field12
      3. Сделать процессор, который будет выбрасывать исключение в четную секунду (сделайте тест с гарантированным результатом)
            Секунда должна определяьться во время выполнения.
            Тест - важная часть задания
            Обязательно посмотрите пример к паттерну Мементо!
      4. Сделать Listener для ведения истории (подумайте, как сделать, чтобы сообщения не портились)
         Уже есть заготовка - класс HistoryListener, надо сделать его реализацию
         Для него уже есть тест, убедитесь, что тест проходит
    */
    private static final Logger logger = LoggerFactory.getLogger(HomeWork.class);
    public static void main(String[] args) {
        var processors = List.of(new ProcessorConcatFields(), new LoggerProcessor(new ProcessorUpperField10()),new ChangeValuesOfSeats11And12());

        var complexProcessor = new ComplexProcessor(processors, ex -> {});
        var listenerPrinter = new ListenerPrinterConsole();
        complexProcessor.addListener(listenerPrinter);
        ObjectForMessage object=new ObjectForMessage();
        object.setData(List.of("x","y","z"));
        var message = new Message.Builder(1L)
                .field1("field1")
                .field2("field2")
                .field3("field3")
                .field6("field6")
                .field10("field10")
                .field11("field11")
                .field12("field12")
                .field13(object)
                .build();

        var result = complexProcessor.handle(message);
        logger.info("result:{}", result);

        complexProcessor.removeListener(listenerPrinter);
    }
}
