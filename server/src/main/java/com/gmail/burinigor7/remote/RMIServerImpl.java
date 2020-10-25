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
        add(new User(102, "Igor", "123" , 20));
        add(new User(10, "Lena", "123" , 10));
        add(new User(101, "Vasya", "123" , 19));
        add(new User(103, "Pete", "123" , 29));
    }};
    private final List<User> activeUsers = new ArrayList<>();
    private Map<User, List<Message>> msgStorage = new HashMap<>() {{
        for(User usr : RMIServerImpl.this.allUsers)
            put(usr, new ArrayList<>());
    }};
    private List<Message> commonMessages = new ArrayList<>();
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
    public void sendMessageToServer(Message msg) {
        msg.setId(msgIdCounter++);
        if(activeUsers.contains(msg.getRecipient())) {
            String remoteObjectName = "User" + msg.getRecipient().getId();
            try {
                ClientRemote chatUser = (ClientRemote) registry.lookup(remoteObjectName);
                chatUser.sendMessageToUser(msg);
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Persistence message " + msg.getId() + " to database ... ");
        msgStorage.get(msg.getRecipient()).add(msg);
    }

    @Override
    public void sendCommonMessageToServer(Message msg) {
        msg.setId(msgIdCounter++);
        for(User active : activeUsers) {
            String remoteObjectName = "User" + active.getId();
            try {
                ClientRemote chatUser = (ClientRemote) registry.lookup(remoteObjectName);
                chatUser.sendMessageToUser(msg);
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Persistence message " + msg.getId() + " to database ... ");
        commonMessages.add(msg);
    }

    @Override
    public User connect(String username, String password) {
        for(User usr : allUsers) {
            if(usr.getUsername().equals(username)) {
                if(usr.getPassword().equals(password)) {
                    activeUsers.add(usr);
                    return usr;
                } else return null;
            }
        }
        return null;
    }

    @Override
    public List<Message> getDialog(User sender, User recipient) {
        return msgStorage.get(sender);
    }

    @Override
    public List<Message> getCommonDialog() {
        return commonMessages;
    }

    @Override
    public List<User> getActiveUsers() {
        return null;
    }

    @Override
    public boolean disconnect() {
        return false;
    }
}