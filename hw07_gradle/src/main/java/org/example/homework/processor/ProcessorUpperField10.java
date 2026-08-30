package org.example.homework.processor;


import org.example.homework.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessorUpperField10 implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(ProcessorUpperField10.class);

    @Override
    public Message process(Message message) {
        Message heir = message.toBuilder().field4(message.getField10().toUpperCase()).build();
        logger.info("ProcessorUpperField10: {}", heir);
        return heir;
    }
}
