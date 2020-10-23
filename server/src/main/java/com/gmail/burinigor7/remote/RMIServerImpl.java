package com.gmail.burinigor7.remote;

import com.gmail.burinigor7.remote.client.ChatUser;
import com.gmail.burinigor7.remote.server.RMIServer;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServerImpl implements RMIServer {
    private final Registry registry;

    public RMIServerImpl() {
        try {
            UnicastRemoteObject.exportObject(this, 0);
            registry = LocateRegistry.createRegistry(1099);
            registry.bind("RMIServer", this);
        } catch (RemoteException | AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessageToServer(String content,
                    int senderId, int recipientId) {
        String remoteObjectName = "ChatUser" + recipientId;
        try {
            ChatUser chatUser = (ChatUser) registry.lookup(remoteObjectName);
            chatUser.sendMessageToUser(content, senderId);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }
}