package com.gmail.burinigor7.remote.client.impl;

import com.gmail.burinigor7.domain.Message;
import com.gmail.burinigor7.remote.client.ClientRemote;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ClientRemoteImpl implements ClientRemote {
    private final Registry registry;
    private final long sessionId;
    public ClientRemoteImpl(long sessionId) {
        this.sessionId = sessionId;
        String remoteObjectName = "User" + sessionId;
        try {
            UnicastRemoteObject.exportObject(this, 0);
            registry = LocateRegistry.getRegistry(1099);
            registry.bind(remoteObjectName, this);
        } catch (RemoteException | AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessageToUser(Message msg) {
        System.out.println("Message: " + msg.getContent() +
                "; Sender: " + msg.getSender().getUsername());
    }
}