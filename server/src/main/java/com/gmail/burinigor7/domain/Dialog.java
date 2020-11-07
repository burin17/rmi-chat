package com.gmail.burinigor7.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Dialog {
    private final List<String> users;
    private final List<? super Message> messages = Collections.synchronizedList(new ArrayList<>());

    public Dialog(String username1, String username2) {
        users = new ArrayList<>(){{
            add(username1);
            add(username2);
        }};
    }

    public List<String> getUsers() {
        return users;
    }

    public boolean isNew() {
        return messages.size() == 0;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public List<? super Message> getMessages() {
        return new ArrayList<>(messages);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dialog dialog = (Dialog) o;
        return users.containsAll(dialog.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(users.get(0), users.get(1));
    }
}