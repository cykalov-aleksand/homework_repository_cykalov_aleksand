package org.example.homework;

import org.example.homework.handler.ComplexProcessor;
import org.example.homework.listener.ListenerPrinterConsole;
import org.example.homework.model.Message;
import org.example.homework.processor.LoggerProcessor;
import org.example.homework.processor.ProcessorConcatFields;
import org.example.homework.processor.ProcessorUpperField10;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Demo {
    private static final Logger logger = LoggerFactory.getLogger(Demo.class);

    public static void main(String[] args) {
        var processors = List.of(new ProcessorConcatFields(), new LoggerProcessor(new ProcessorUpperField10()));

        var complexProcessor = new ComplexProcessor(processors, ex -> {});
        var listenerPrinter = new ListenerPrinterConsole();
        complexProcessor.addListener(listenerPrinter);

        var message = new Message.Builder(1L)
                .field1("field1")
                .field2("field2")
                .field3("field3")
                .field6("field6")
                .field10("field10")
                .build();

        var result = complexProcessor.handle(message);
        logger.info("result:{}", result);

        complexProcessor.removeListener(listenerPrinter);
    }
}
