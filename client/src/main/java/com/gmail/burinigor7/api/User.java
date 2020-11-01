package com.gmail.burinigor7.api;

import com.gmail.burinigor7.domain.Message;
import com.gmail.burinigor7.remote.client.ClientRemote;
import com.gmail.burinigor7.remote.server.RMIServer;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;

public class User implements Serializable {
    public static final String COMMON_DIALOG_KEY = "Common dialog";
    private static final long serialVersionUID = 777L;
    private final RMIServer server;
    private long sessionId;
    private String username;
    private ClientRemote clientRemote;
    private final Map<String, List<Message>> messagesStorage = new HashMap<>() {{
        put(COMMON_DIALOG_KEY, new ArrayList<>());
    }};

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


    public void addSentMessage(String content, String recipient) {
        Message msg = new Message()
                .setContent(content)
                .setSenderUsername(username)
                .setSenderSessionId(sessionId)
                .setRecipientUsername(recipient);
        messagesStorage.putIfAbsent(recipient, new ArrayList<>());
        messagesStorage.get(recipient).add(msg);
    }
    public void addObtainedMessage(String content, String sender) {
        Message msg = new Message()
                .setContent(content)
                .setSenderUsername(sender)
                .setSenderSessionId(sessionId)
                .setRecipientUsername(username);
        messagesStorage.putIfAbsent(sender, new ArrayList<>());
        messagesStorage.get(sender).add(msg);
    }

    public User(String username, long sessionId, String serverName) {
        try {
            String serverRemoteObject = "RMIServer" + serverName;
            this.sessionId = sessionId;
            this.username = username;
            this.server = (RMIServer) LocateRegistry.getRegistry(1099)
                    .lookup(serverRemoteObject);
            messagesStorage.get(COMMON_DIALOG_KEY)
                    .addAll(server.getCommonDialog(username, sessionId));
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String content, String recipient) {
        if(recipient != null) {
            System.out.println("User.sendMessage()");
            if (!recipient.equals(COMMON_DIALOG_KEY)) {
                Message msg = new Message()
                        .setRecipientUsername(recipient)
                        .setSenderSessionId(sessionId)
                        .setSenderUsername(username)
                        .setContent(content);
                try {
                    server.sendMessageToServer(msg);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                addSentMessage(content, recipient);
            } else {
                sendCommonMessage(content);
            }
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
            return new ArrayList<>(server.getCommonDialog(username, sessionId));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<String> getActiveUsers() {
        try {
            return new HashSet<>(server.getActiveUsers(username, sessionId));
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

    public RMIServer getServer() {
        return server;
    }

    public List<Message> getDialog(String dialogName) {
        if(dialogName != null) {
            if (!dialogName.equals(COMMON_DIALOG_KEY)) {
                messagesStorage.putIfAbsent(dialogName, new ArrayList<>());
                return messagesStorage.get(dialogName);
            }
            try {
                return server.getCommonDialog(username, sessionId);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        return new ArrayList<>();
    }
}