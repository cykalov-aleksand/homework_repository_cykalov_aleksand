package org.example.homework.processor;

import org.example.homework.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeValuesOfField11And12 implements Processor{
    private static final Logger logger= LoggerFactory.getLogger(ChangeValuesOfField11And12.class);
    @Override
    public Message process(Message message) {
        String field11=message.getField11();
        String field12=message.getField12();
        Message heir= message.toBuilder()
                .field11(field12)
                .field12(field11)
                .build();
        logger.info("ChangeValuesOfFields11And12: {}",heir);
        return heir;
    }
}
