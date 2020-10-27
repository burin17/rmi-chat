package com.gmail.burinigor7.domain;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 555L;
    private User sender;
    private User recipient;
    private String content;

    public Message(User sender, User recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    }

    public Message() {
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        if(recipient != null)
            return "Message{" +
                    ", sender=" + sender.getId() +
                    ", recipient=" + recipient.getId() +
                    ", content='" + content + '\'' +
                    '}';
        else return "Message{" +
                    ", sender=" + sender.getId() +
                    ", common message" +
                    ", content='" + content + '\'' +
                    '}';
    }
}