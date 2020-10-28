package com.gmail.burinigor7.domain;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 555L;
    private String senderUsername;
    private Long senderSessionId;
    private String recipientUsername;
    private String content;

    public Message() {
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public Message setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
        return this;
    }

    public Long getSenderSessionId() {
        return senderSessionId;
    }

    public Message setSenderSessionId(Long senderSessionId) {
        this.senderSessionId = senderSessionId;
        return this;
    }

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public Message setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Message setContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public String toString() {
        return "Message{" +
                "senderUsername='" + senderUsername + '\'' +
                ", senderSessionId=" + senderSessionId +
                ", recipientUsername='" + recipientUsername + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}