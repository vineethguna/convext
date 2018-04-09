package com.vineeth.serac.manager.processors;

import com.vineeth.serac.messages.Message;

import java.util.List;

public class ProcessorContext {
    private List<Message> messages;

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
