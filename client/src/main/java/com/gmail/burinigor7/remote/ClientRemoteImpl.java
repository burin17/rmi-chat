package com.gmail.burinigor7.remote;

import com.gmail.burinigor7.api.User;
import com.gmail.burinigor7.domain.CommonMessage;
import com.gmail.burinigor7.domain.Message;
import com.gmail.burinigor7.exception.SpecifiedServerUnavailableException;
import com.gmail.burinigor7.exception.UsernameInUseException;
import com.gmail.burinigor7.gui.UserForm;
import com.gmail.burinigor7.remote.client.ClientRemote;

import javax.swing.*;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class ClientRemoteImpl implements ClientRemote {
    private UserForm userForm;

    public ClientRemoteImpl(String remoteObjectName) throws UsernameInUseException {
        try {
            UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry(1099);
            registry.bind(remoteObjectName, this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (AlreadyBoundException e) {
            throw new UsernameInUseException();
        }
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