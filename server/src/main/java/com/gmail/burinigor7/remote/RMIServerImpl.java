package com.gmail.burinigor7.remote;

import com.gmail.burinigor7.domain.Message;
import com.gmail.burinigor7.domain.User;
import com.gmail.burinigor7.remote.client.ClientRemote;
import com.gmail.burinigor7.remote.server.RMIServer;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class RMIServerImpl implements RMIServer {
    private static int msgIdCounter;
    private final Registry registry;

    private final List<User> allUsers = new ArrayList<>();
    private final List<User> activeUsers = new ArrayList<>();
    private final List<Message> commonMessages = new ArrayList<>();

    public RMIServerImpl() {
        try {
            UnicastRemoteObject.exportObject(this, 0);
            registry = LocateRegistry.createRegistry(1099);
            registry.bind("RMIServer", this);
        } catch (RemoteException | AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
        initUsers();
    }

    private void initUsers() {
        allUsers.add(new User(1, "Igor", "123"));
        allUsers.add(new User(2, "Vasya", "123"));
        allUsers.add(new User(3, "Pete", "123"));
    }
    @Override
    public void sendMessageToServer(Message msg) {
        if(isPermit(msg.getSender())) {
            msg.setId(msgIdCounter++);
            if (activeUsers.contains(msg.getRecipient())) {
                String remoteObjectName = "User" + msg.getRecipient().getId();
                try {
                    ClientRemote chatUser = (ClientRemote) registry.lookup(remoteObjectName);
                    chatUser.sendMessageToUser(msg);
                } catch (RemoteException | NotBoundException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Message from " +  msg.getSender().getId() + " to " + msg.getRecipient().getId());
            }
        }
    }

    @Override
    public void sendCommonMessageToServer(Message msg) {
        if(isPermit(msg.getSender())) {
            msg.setId(msgIdCounter++);
            for (User active : activeUsers) {
                String remoteObjectName = "User" + active.getId();
                try {
                    ClientRemote chatUser = (ClientRemote) registry.lookup(remoteObjectName);
                    chatUser.sendMessageToUser(msg);
                } catch (RemoteException | NotBoundException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("Common message from " + msg.getSender().getId());
            commonMessages.add(msg);
        }
    }

    @Override
    public User connect(String username, String password) {
        User user = allUsers.stream()
                .filter(usr -> username.equals(usr.getUsername()))
                .findAny()
                .orElse(null);
        if(user != null && user.getPassword().equals(password)) {
            activeUsers.add(user);
            return user;
        }
        return null;
    }

    @Override
    public List<Message> getCommonDialog(User client) {
        if(isPermit(client)) {
            List<Message> res = new ArrayList<>();
            for(Message msg : commonMessages) {
                User sender = msg.getSender();
                res.add(new Message(
                        new User(sender.getId(), sender.getUsername(),
                                null),
                        null, msg.getContent()));
            }
            return res;
        }
        return null;
    }

    @Override
    public List<User> getActiveUsers(User client) {
        if(isPermit(client)) {
            List<User> res = new ArrayList<>();
            for (User usr : activeUsers) {
                res.add(new User(usr.getId(), usr.getUsername(), null));
            }
            return res;
        } return null;
    }

    @Override
    public boolean disconnect(User client) {
        if(isPermit(client)) {
            activeUsers.remove(client);
            return true;
        }
        return false;
    }

    private boolean isPermit(User client) {
        User actual = activeUsers.stream()
                .filter(client::equals)
                .findAny()
                .orElse(null);
        if(actual != null)
            return actual.getPassword().equals(client.getPassword());
        return false;
    }
}