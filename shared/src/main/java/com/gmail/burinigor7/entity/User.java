package com.gmail.burinigor7.entity;

import com.gmail.burinigor7.remote.server.RMIServer;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Objects;

public class User implements Serializable {
    private static final long serialVersionUID = 777L;
    private final Registry registry;
    private final int id;
    private String username;
    private int age;

    public User(int id, String username, int age) {
        this.id = id;
        this.username = username;
        this.age = age;
        try {
            registry = LocateRegistry.getRegistry(1099);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String content, User recipient) {
        Message msg = new Message(this, recipient, content);
        try {
            RMIServer server = (RMIServer) registry.lookup("RMIServer");
            server.sendMessageToServer(msg);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Message> allObtainedMessages() {
        try {
             RMIServer server = (RMIServer) registry.lookup("RMIServer");
             return server.getAllObtainedMessages(this);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", age=" + age +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                age == user.age &&
                username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, age);
    }
}