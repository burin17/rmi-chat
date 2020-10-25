package com.gmail.burinigor7.remote.client;

import com.gmail.burinigor7.entity.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRemote extends Remote {
    void sendMessageToUser(Message msg)
            throws RemoteException;
}