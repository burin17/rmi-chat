package com.gmail.burinigor7.remote.server;

import com.gmail.burinigor7.domain.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface RMIServer extends Remote {
    void sendMessageToServer(Message msg) throws RemoteException;

    void connect(String username) throws RemoteException;

    Set<String> getActiveUsers(String username) throws RemoteException;

    boolean disconnect(String username) throws RemoteException;
}