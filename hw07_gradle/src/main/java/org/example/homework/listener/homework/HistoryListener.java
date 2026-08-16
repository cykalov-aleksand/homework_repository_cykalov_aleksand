package org.example.homework.listener.homework;

import org.example.homework.listener.Listener;
import org.example.homework.model.Message;
import org.example.homework.model.ObjectForMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HistoryListener implements Listener, HistoryReader {
    private final List<Message> history = new ArrayList<>();
    private final Map<Long, Message> messageById = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(HistoryListener.class);

    @Override
    public void onUpdated(Message msg) {
        if (msg == null) {
            return;
        }

        // Создаём глубокую копию сообщения
        Message copy = deepCopyMessage(msg);

        history.add(copy);
        messageById.put(copy.getId(), copy);
        logger.info("HistoryListener: сохранено сообщение ID={}", msg.getId());
    }

    @Override
    public Optional<Message> findMessageById(long id) {

        Message message = messageById.get(id);
        if (message == null) {
            return Optional.empty();
        }
        return Optional.of(deepCopyMessage(message));
    }

    private Message deepCopyMessage(Message original) {
        ObjectForMessage originalField13 = original.getField13();
        ObjectForMessage copyField13 = null;

        if (originalField13 != null) {
            List<String> originalData = originalField13.getData();
            List<String> copyData = (originalData != null)
                    ? new ArrayList<>(originalData)  // копируем список
                    : null;
            copyField13 = new ObjectForMessage();
            copyField13.setData(copyData);
        }

        return original.toBuilder()
                .field13(copyField13)
                .build();
    }

    /**
     * Возвращает неизменяемую копию всей истории.
     */
    public List<Message> getHistory() {
        List<Message> result = new ArrayList<>();
        for (Message m : history) {
            result.add(deepCopyMessage(m));
        }
        return Collections.unmodifiableList(result);
    }

    public void clear() {
        history.clear();
        messageById.clear();
    }

    /**
     * Количество сообщений в истории.
     */
    public int size() {
        return history.size();
    }
}