package org.example.homework.processor;


import org.example.homework.model.Message;

@SuppressWarnings("java:S1135")
public interface Processor {

    Message process(Message message);
}