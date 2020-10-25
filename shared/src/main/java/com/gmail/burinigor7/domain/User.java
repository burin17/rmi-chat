package com.gmail.burinigor7.domain;

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
    private static RMIServer SERVER;
    private static final Registry REGISTRY;
    private int id;
    private String username;
    private String password;
    private int age;
    private long sessionId;

    static {
        try {
            REGISTRY = LocateRegistry.getRegistry(1099);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public User() {
        initRemoteServerObject();
    }

    private static void initRemoteServerObject() {
        try {
            if (SERVER == null)
                SERVER = (RMIServer) REGISTRY.lookup("RMIServer");
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static User connectToServer(String username, String password) {
        try {
            initRemoteServerObject();
            return SERVER.connect(username, password);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean disconnectFromServer() {
        try {
            return SERVER.disconnect(this);
        } catch (RemoteException e) {
            throw new RuntimeException();
        }
    }

    public User(int id, String username, String password, int age) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.age = age;
        initRemoteServerObject();
    }

    public void sendMessage(String content, User recipient) {
        Message msg = new Message(this, recipient, content);
        try {
            SERVER.sendMessageToServer(msg);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendCommonMessage(String content) {
        Message msg = new Message(this, null, content);
        try {
            SERVER.sendCommonMessageToServer(msg);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Message> getDialog(User usr) {
        try {
             return SERVER.getDialog(this, usr);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Message> getCommonDialog() {
        try {
            return SERVER.getCommonDialog(this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public int getId() {
        return id;
    }

    public User setId(int id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public int getAge() {
        return age;
    }

    public User setAge(int age) {
        this.age = age;
        return this;
    }

    public long getSessionId() {
        return sessionId;
    }

    public User setSessionId(long sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
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
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}