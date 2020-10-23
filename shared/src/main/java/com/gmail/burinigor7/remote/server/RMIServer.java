package com.gmail.burinigor7.remote.server;

import com.gmail.burinigor7.remote.client.ChatUser;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServer extends Remote {
    void sendMessageToServer(String content,
            int senderId, int recipientId) throws RemoteException;
}