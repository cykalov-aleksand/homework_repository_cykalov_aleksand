package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    static Logger logger= LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        HelloOtus helloOtus=new HelloOtus();
        logger.info("Демонстрация работы метода при вводе поле element= {}",helloOtus.getElement());
        System.out.println(helloOtus.demonstration());
        helloOtus.setElement("Hello Otus!");
        logger.info("Демонстрация работы метода при вводе поле element= {}",helloOtus.getElement());
        System.out.println(helloOtus.demonstration());
    }
}