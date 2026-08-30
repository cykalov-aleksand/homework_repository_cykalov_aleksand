package org.example.homework.processor;


import org.example.homework.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessorConcatFields implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(ProcessorConcatFields.class);

    @Override
    public Message process(Message message) {
        var newFieldValue = String.join(" ", "concat:", message.getField1(), message.getField2(), message.getField3());
        Message heir = message.toBuilder().field4(newFieldValue).build();
        logger.info("ProcessorConcatFields: {}", heir);
        return heir;
    }
}

