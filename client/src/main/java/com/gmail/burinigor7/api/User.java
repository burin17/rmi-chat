package com.gmail.burinigor7.api;

import com.gmail.burinigor7.domain.CommonMessage;
import com.gmail.burinigor7.domain.Message;
import com.gmail.burinigor7.domain.PrivateMessage;
import com.gmail.burinigor7.exception.ServersUnavailableException;
import com.gmail.burinigor7.exception.SpecifiedServerUnavailableException;
import com.gmail.burinigor7.exception.UsernameInUseException;
import com.gmail.burinigor7.gui.UserForm;
import com.gmail.burinigor7.remote.client.ClientRemote;
import com.gmail.burinigor7.remote.server.RMIServer;

import javax.swing.*;
import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class User implements Serializable, ClientRemote {
    private static final long serialVersionUID = 777L;
    public static final String COMMON_DIALOG_KEY = "Common dialog";
    private final RMIServer server;
    private final String serverName;
    private final String username;
    private final Map<String, List<String>> messageStorage = new ConcurrentHashMap<>();
    private UserForm userForm;

    public void disconnectFromServer() {
        try {
            String remoteObjectName = "User" + serverName + username;
            LocateRegistry.getRegistry(1099)
                    .unbind(remoteObjectName);
            server.disconnect(username);
        } catch (RemoteException | NotBoundException ignore) {}
    }

    public static List<String> availableServers() throws ServersUnavailableException {
        try {
            return Arrays.stream(LocateRegistry.getRegistry(1099).list())
                    .filter(remote -> remote.contains("RMIServer"))
                    .map(name -> name.replace("RMIServer", ""))
                    .collect(Collectors.toList());
        } catch (RemoteException e) {
            throw new ServersUnavailableException(e);
        }
    }

    public User(String username, String serverName)
            throws SpecifiedServerUnavailableException, UsernameInUseException,
            ServersUnavailableException {
        try {
            String remoteObjectName = "User" + serverName + username;
            UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry(1099);
            registry.bind(remoteObjectName, this);
            String remoteServerObjectName = "RMIServer" + serverName;
            RMIServer server = (RMIServer) LocateRegistry.getRegistry(1099)
                    .lookup(remoteServerObjectName);
            server.connect(username);
            this.serverName = serverName;
            String serverRemoteObject = "RMIServer" + serverName;
            this.username = username;
            this.server = (RMIServer) LocateRegistry.getRegistry(1099)
                    .lookup(serverRemoteObject);
            messageStorage.put(COMMON_DIALOG_KEY,
                    server.getDialog(username, COMMON_DIALOG_KEY));
            System.out.println(messageStorage.get(COMMON_DIALOG_KEY));
        } catch (RemoteException e)  {
            throw new SpecifiedServerUnavailableException(e);
        } catch (AlreadyBoundException e) {
            throw new UsernameInUseException();
        } catch (NotBoundException e) {
            throw new ServersUnavailableException(e);
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

    public void setUserForm(UserForm userForm) {
        this.userForm = userForm;
    }

    @Override
    public void sendMessageToUser(Message msg) {
        addMessageToUserStorage(msg);
        String recipientSelected = userForm.getDialogsList().getSelectedValue();
        if(recipientSelected != null && ((msg instanceof CommonMessage) &&
                recipientSelected.equals(User.COMMON_DIALOG_KEY) ||
                recipientSelected.equals(msg.getSender()))) {
            userForm.refreshChat();
        }
    }

    private void addMessageToUserStorage(Message msg) {
        String msgLine;
        if(msg.getSender().equals(userForm.getUser().getUsername()))
            msgLine = "You : " + msg.getContent() + "\n";
        else msgLine = msg.getSender() + " : " + msg.getContent() + "\n";
        if(msg instanceof CommonMessage) {
            userForm.getUser().getMessageStorage()
                    .putIfAbsent(User.COMMON_DIALOG_KEY,
                            Collections.synchronizedList(new ArrayList<>()));
            userForm.getUser().getMessageStorage()
                    .get(User.COMMON_DIALOG_KEY).add(msgLine);
        } else {
            userForm.getUser().getMessageStorage()
                    .putIfAbsent(msg.getSender(),
                            Collections.synchronizedList(new ArrayList<>()));
            userForm.getUser().getMessageStorage()
                    .get(msg.getSender()).add(msgLine);
        }
    }

    @Override
    public void refreshAvailableDialogsList() {
        JList<String> dialogs = userForm.getDialogsList();
        DefaultListModel<String> dialogsModel = new DefaultListModel<>();
        try {
            synchronized (this) {
                Set<String> availableDialogs = userForm.getUser().getActiveUsers();
                for (String username : availableDialogs)
                    if (!username.equals(userForm.getUser().getUsername()))
                        dialogsModel.addElement(username);
                dialogs.setModel(dialogsModel);
            }
        } catch (SpecifiedServerUnavailableException e) {
            userForm.serverUnavailable();
        }
    }
}