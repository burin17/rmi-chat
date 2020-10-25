package com.gmail.burinigor7.remote.server;

import com.gmail.burinigor7.entity.Message;
import com.gmail.burinigor7.entity.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMIServer extends Remote {
    void sendMessageToServer(Message msg) throws RemoteException;
    boolean authenticate(String username, String password)
            throws RemoteException;
    List<Message> getAllObtainedMessages(User recipient)
            throws RemoteException;
}