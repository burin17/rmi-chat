package com.gmail.burinigor7.domain;

import com.gmail.burinigor7.remote.server.RMIServer;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class User implements Serializable {
    private static final long serialVersionUID = 777L;
    private static int idCounter;
    private final RMIServer server;
    private long sessionId;
    private String username;

    public boolean disconnectFromServer() {
        try {
            String remoteObjectName = "User" + sessionId;
            LocateRegistry.getRegistry(1099)
                    .unbind(remoteObjectName);
            return server.disconnect(username, sessionId);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException();
        }
    }

    public User(String username, long sessionId, String serverName) {
        try {
            this.sessionId = sessionId;
            this.username = username;
            this.server = (RMIServer) LocateRegistry.getRegistry(1099)
                    .lookup(serverName);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String content, String recipient) {
        Message msg = new Message()
                .setRecipientUsername(username)
                .setSenderSessionId(sessionId)
                .setSenderUsername(recipient)
                .setContent(content);
        try {
            server.sendMessageToServer(msg);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendCommonMessage(String content) {
        Message msg = new Message()
                .setSenderUsername(username)
                .setSenderSessionId(sessionId)
                .setContent(content);
        try {
            server.sendCommonMessageToServer(msg);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Message> getCommonDialog() {
        try {
            return server.getCommonDialog(username, sessionId);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<String> getActiveUsers() {
        try {
            return server.getActiveUsers(username, sessionId);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "User{" +
                "sessionId=" + sessionId +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return sessionId == user.sessionId &&
                username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, username);
    }
}