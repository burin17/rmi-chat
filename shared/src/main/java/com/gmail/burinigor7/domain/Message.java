package com.gmail.burinigor7.domain;

public interface Message {
    void setContent(String content);
    String getContent();
    void setSender(String sender);
    String getSender();
}