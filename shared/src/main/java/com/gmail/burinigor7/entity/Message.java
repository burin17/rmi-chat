package com.gmail.burinigor7.entity;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 555L;
    private int id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return "Message{" +
                "id=" + id +
                ", sender=" + sender.getUsername() +
                ", recipient=" + recipient.getUsername() +
                ", content='" + content + '\'' +
                '}';
    }
}