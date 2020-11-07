package com.gmail.burinigor7.domain;

import java.io.Serializable;

public class CommonMessage implements Message, Serializable {
    private static final long serialVersionUID = 555L;
    private String sender;
    private String content;

    public CommonMessage() {
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

    @Override
    public String toString() {
        return "Message{" +
                "senderUsername='" + sender + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}