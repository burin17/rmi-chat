package com.gmail.burinigor7.domain;

import java.io.Serializable;

public class PrivateMessage implements Message, Serializable {
    private static final long serialVersionUID = 333L;
    private String recipient;
    private String sender;
    private String content;

    public String getRecipient() {
        return recipient;
    }

    public PrivateMessage setRecipient(String recipient) {
        this.recipient = recipient;
        return this;
    }
    @Override
    public String getSender() {
        return sender;
    }
    @Override
    public void setSender(String sender) {
        this.sender = sender;
    }
    @Override
    public String getContent() {
        return content;
    }
    @Override
    public void setContent(String content) {
        this.content = content;
    }
}
