package com.gmail.burinigor7.remote.client;

import com.gmail.burinigor7.domain.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRemote extends Remote {
    void sendMessageToUser(Message msg) throws RemoteException;
    void refreshAvailableDialogsList() throws RemoteException;
}