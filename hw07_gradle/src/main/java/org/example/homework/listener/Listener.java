package org.example.homework.listener;

import org.example.homework.model.Message;

@SuppressWarnings("java:S1135")
public interface Listener {

    void onUpdated(Message msg);
}

