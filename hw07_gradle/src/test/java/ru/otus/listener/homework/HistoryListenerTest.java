package ru.otus.listener.homework;

import org.example.homework.listener.homework.HistoryListener;
import org.example.homework.model.Message;
import org.example.homework.model.ObjectForMessage;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;


@SuppressWarnings({"java:S1135", "java:S125"})
class HistoryListenerTest {

    @Test
        //@Disabled("удалить для запуска тест")
    void listenerTest() {
        // given
        var historyListener = new HistoryListener();

        var id = 100L;
        var data = "33";
        var field13 = new ObjectForMessage();
        var field13Data = new ArrayList<String>();
        field13Data.add(data);
        field13.setData(field13Data);

        var message = new Message.Builder(id)
                .field10("field10")
                .field13(field13)
                .build();

        historyListener.onUpdated(message);
        message.getField13().setData(new ArrayList<>()); //меняем исходное сообщение
        field13Data.clear(); //меняем исходный список
        var messageFromHistory = historyListener.findMessageById(id);
        assertThat(messageFromHistory).isPresent();
        assertThat(messageFromHistory.get().getField13().getData()).containsExactly(data);
    }
}

