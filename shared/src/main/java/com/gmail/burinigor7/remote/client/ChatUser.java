package com.gmail.burinigor7.remote.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatUser extends Remote {
    void sendMessageToUser(String content, int senderId)
            throws RemoteException;
    int getId() throws RemoteException;
}
