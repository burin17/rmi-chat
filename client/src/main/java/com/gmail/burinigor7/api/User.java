package com.gmail.burinigor7.api;

import com.gmail.burinigor7.domain.CommonMessage;
import com.gmail.burinigor7.domain.PrivateMessage;
import com.gmail.burinigor7.exception.SpecifiedServerUnavailableException;
import com.gmail.burinigor7.remote.server.RMIServer;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class User implements Serializable {
    public static final String COMMON_DIALOG_KEY = "Common dialog";
    private static final long serialVersionUID = 777L;
    private final RMIServer server;
    private final String serverName;
    private final String username;
    private final Map<String, List<String>> messageStorage = new ConcurrentHashMap<>();

    public void disconnectFromServer() {
        try {
            String remoteObjectName = "User" + serverName + username;
            LocateRegistry.getRegistry(1099)
                    .unbind(remoteObjectName);
            server.disconnect(username);
        } catch (RemoteException | NotBoundException ignore) {}
    }

    public User(String username, String serverName)
            throws SpecifiedServerUnavailableException {
        try {
            this.serverName = serverName;
            String serverRemoteObject = "RMIServer" + serverName;
            this.username = username;
            this.server = (RMIServer) LocateRegistry.getRegistry(1099)
                    .lookup(serverRemoteObject);
            messageStorage.put(COMMON_DIALOG_KEY,
                    server.getDialog(username, COMMON_DIALOG_KEY));
            System.out.println(messageStorage.get(COMMON_DIALOG_KEY));
        } catch (RemoteException | NotBoundException e) {
            throw new SpecifiedServerUnavailableException(e);
        }
    }

    public void sendMessage(String content, String dialogName)
            throws SpecifiedServerUnavailableException {
        if (!dialogName.equals(COMMON_DIALOG_KEY)) {
            sendPrivateMessage(content, dialogName);
        } else {
            sendCommonMessage(content);
        }
    }

    private void sendCommonMessage(String content)
            throws SpecifiedServerUnavailableException {
        CommonMessage msg = new CommonMessage();
        msg.setContent(content);
        msg.setSender(username);
        try {
            server.sendMessageToServer(msg);
        } catch (RemoteException e) {
            throw new SpecifiedServerUnavailableException(e);
        }
    }

    private void addMessage(String content, String dialogName) {
        messageStorage.putIfAbsent(dialogName,
                Collections.synchronizedList(new ArrayList<>()));
        messageStorage.get(dialogName).add("You : " + content + "\n");
    }

    private void sendPrivateMessage(String content, String recipient)
            throws SpecifiedServerUnavailableException {
        PrivateMessage msg = new PrivateMessage();
        msg.setRecipient(recipient);
        msg.setSender(username);
        msg.setContent(content);
        try {
            server.sendMessageToServer(msg);
        } catch (RemoteException e) {
            throw new SpecifiedServerUnavailableException(e);
        }
        addMessage(content, recipient);
    }


    public Set<String> getActiveUsers()
            throws SpecifiedServerUnavailableException{
        try {
            return server.getActiveUsers(username);
        } catch (RemoteException e) {
            throw new SpecifiedServerUnavailableException(e);
        }
    }

    public String getUsername() {
        return username;
    }


    @Override
    public String toString() {
        return "User{" +
                "serverName='" + serverName + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return serverName.equals(user.serverName) &&
                username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverName, username);
    }

    public RMIServer getServer() {
        return server;
    }

    public List<String> getDialog(String dialogName)
            throws SpecifiedServerUnavailableException {
        List<String> localDialog = messageStorage.get(dialogName);
        if(localDialog == null) {
            try {
                List<String> dialog;
                if(!dialogName.equals(COMMON_DIALOG_KEY))
                    dialog = server.getDialog(this.username, dialogName);
                else dialog = server.getDialog(COMMON_DIALOG_KEY, null);
                messageStorage.put(dialogName, dialog);
                return dialog;
            } catch (RemoteException e) {
                throw new SpecifiedServerUnavailableException(e);
            }
        } else return localDialog;
    }

    public Map<String, List<String>> getMessageStorage() {
        return messageStorage;
    }
}