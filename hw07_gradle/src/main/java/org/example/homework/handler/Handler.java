package org.example.homework.handler;

import org.example.homework.listener.Listener;
import org.example.homework.model.Message;

public interface Handler {
    Message handle(Message msg);

    void addListener(Listener listener);

    void removeListener(Listener listener);
}
