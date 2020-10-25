package com.gmail.burinigor7.remote;

import com.gmail.burinigor7.entity.Message;
import com.gmail.burinigor7.entity.User;
import com.gmail.burinigor7.remote.client.ClientRemote;
import com.gmail.burinigor7.remote.server.RMIServer;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RMIServerImpl implements RMIServer {
    private static int msgIdCounter;
    private final Registry registry;
    private final List<User> allUsers = new ArrayList<>() {{
        add(new User(102, "Igor", 20));
        add(new User(10, "Lena", 10));
        add(new User(101, "Vasya", 19));
        add(new User(103, "Pete", 29));
    }};
    private final List<User> online = new ArrayList<>();
    private Map<User, List<Message>> msgStorage = new HashMap<>() {{
        for(User usr : RMIServerImpl.this.allUsers)
            put(usr, new ArrayList<>());
    }};

    public RMIServerImpl() {
        try {
            UnicastRemoteObject.exportObject(this, 0);
            registry = LocateRegistry.createRegistry(1099);
            registry.bind("RMIServer", this);
        } catch (RemoteException | AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
    }

    //remote
    @Override
    public void sendMessageToServer(Message msg) {
        msg.setId(msgIdCounter++);
        String remoteObjectName = "User" + msg.getRecipient().getId();
        try {
            ClientRemote chatUser = (ClientRemote) registry.lookup(remoteObjectName);
            chatUser.sendMessageToUser(msg);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException ignore) {}
        System.out.println("Persistence message " + msg.getId() + " to database ... ");
        msgStorage.get(msg.getRecipient()).add(msg);
    }

    @Override
    public boolean authenticate(String username, String password) {
        return false;
    }

    @Override
    public List<Message> getAllObtainedMessages(User recipient) {
        return msgStorage.get(recipient);
    }
}