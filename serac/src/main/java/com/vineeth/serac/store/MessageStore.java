package com.vineeth.serac.store;


import com.vineeth.serac.messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageStore {
    private static final String RECEIVED_MESSAGES_KEY = "receivedMessages";

    private Map<String, List<Message>> receivedMessages;

    public MessageStore() {
        receivedMessages = new ConcurrentHashMap<>();
        receivedMessages.putIfAbsent(RECEIVED_MESSAGES_KEY, new ArrayList<>());
    }

    public void add(Message message) {
        receivedMessages.compute(RECEIVED_MESSAGES_KEY, (key, value) -> {
            value.add(message);
            return value;
        });
    }

    public List<Message> clearAndGetAllMessages() {
        List<Message> messages = new ArrayList<>();
        receivedMessages.compute(RECEIVED_MESSAGES_KEY, (key, value) -> {
            messages.addAll(value);
            value.clear();
            return value;
        });

        return messages;
    }
}
