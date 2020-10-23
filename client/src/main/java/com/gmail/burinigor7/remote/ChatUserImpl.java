package com.gmail.burinigor7.remote;

import com.gmail.burinigor7.remote.client.ChatUser;
import com.gmail.burinigor7.remote.server.RMIServer;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ChatUserImpl implements ChatUser {
    private final Registry registry;
    private final int id;

    public ChatUserImpl(int id) {
        this.id = id;
        String remoteObjectName = "ChatUser" + id;
        try {
            UnicastRemoteObject.exportObject(this, 0);
            registry = LocateRegistry.getRegistry(1099);
            registry.bind(remoteObjectName, this);
        } catch (RemoteException | AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String content, int recipientId) {
        try {
            RMIServer server = (RMIServer) registry.lookup("RMIServer");
            server.sendMessageToServer(content, this.id, recipientId);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    //remote method
    @Override
    public void sendMessageToUser(String content, int recipientId) {
        System.out.println(content + " from " + recipientId);
    }

    @Override
    public int getId() {
        return id;
    }
}
