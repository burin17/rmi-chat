package com.gmail.burinigor7.remote;

import com.gmail.burinigor7.api.User;
import com.gmail.burinigor7.domain.Message;
import com.gmail.burinigor7.gui.UserForm;
import com.gmail.burinigor7.remote.client.ClientRemote;

import javax.swing.*;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;

public class ClientRemoteImpl implements ClientRemote {
    private final Registry registry;
    private final long sessionId;
    private final UserForm userForm;
    public ClientRemoteImpl(UserForm userForm) {
        this.userForm = userForm;
        this.sessionId = userForm.getUser().getSessionId();
        String remoteObjectName = "User" + sessionId;
        try {
            UnicastRemoteObject.exportObject(this, 0);
            registry = LocateRegistry.getRegistry(1099);
            registry.bind(remoteObjectName, this);
        } catch (RemoteException | AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessageToUser(Message msg) {
        userForm.getUser().addObtainedMessage(msg.getContent(),
                msg.getSenderUsername());
        String recipientSelected = userForm.getDialogsList().getSelectedValue();
        if(recipientSelected != null && ((msg.getRecipientUsername() == null
                && userForm.getDialogsList().getSelectedValue()
                .equals(User.COMMON_DIALOG_KEY)) || userForm.getUser().getUsername()
                .equals(msg.getRecipientUsername()))) {
            userForm.refreshChat();
        }
        System.out.println("Message: " + msg.getContent() +
                "; Sender: " + msg.getSenderUsername());
    }

    @Override
    public void refreshAvailableDialogsList() {
        JList<String> dialogs = userForm.getDialogsList();
        DefaultListModel<String> dialogsModel = new DefaultListModel<>();
        Set<String> availableDialogs = userForm.getUser().getActiveUsers();
        for(String username : availableDialogs)
            if(!username.equals(userForm.getUser().getUsername()))
                dialogsModel.addElement(username);
        dialogs.setModel(dialogsModel);
    }
}