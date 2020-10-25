package com.gmail.burinigor7.remote.server;

import com.gmail.burinigor7.domain.Message;
import com.gmail.burinigor7.domain.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMIServer extends Remote {
    void sendMessageToServer(Message msg) throws RemoteException;

    void sendCommonMessageToServer(Message msg) throws RemoteException;

    User connect(String username, String password) throws RemoteException;

    List<Message> getDialog(User usr1, User usr2) throws RemoteException;

    List<Message> getCommonDialog(User client) throws RemoteException;

    List<User> getActiveUsers(User client) throws RemoteException;

    boolean disconnect(User client) throws RemoteException;
}