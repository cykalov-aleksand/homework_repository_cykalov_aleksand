package org.example.homework.listener.homework;

import org.example.homework.model.Message;

import java.util.Optional;

public interface HistoryReader {

    Optional<Message> findMessageById(long id);
}

