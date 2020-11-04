package com.gmail.burinigor7.remote.server;

import com.gmail.burinigor7.domain.Message;
import com.gmail.burinigor7.exception.UsernameInUseException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

public interface RMIServer extends Remote {
    void sendMessageToServer(Message msg) throws RemoteException;

    void sendCommonMessageToServer(Message msg) throws RemoteException;

    long connect(String username) throws RemoteException, UsernameInUseException;

    List<Message> getCommonDialog(String username, long sessionId) throws RemoteException;

    Set<String> getActiveUsers(String username, long sessionId) throws RemoteException;

    boolean disconnect(String username, long sessionId) throws RemoteException;
}