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

    private final Map<User, List<Message>> msgStorage = new HashMap<>();

    private final List<Message> commonMessages = new ArrayList<>();

    public RMIServerImpl() {
        try {
            UnicastRemoteObject.exportObject(this, 0);
            registry = LocateRegistry.createRegistry(1099);
            registry.bind("RMIServer", this);
        } catch (RemoteException | AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
        allUsers.add(new User(102, "Igor", "123" , 20));
        allUsers.add(new User(10, "Lena", "123" , 10));
        allUsers.add(new User(101, "Vasya", "123" , 19));
        allUsers.add(new User(103, "Pete", "123" , 29));
        for(User usr : RMIServerImpl.this.allUsers)
            msgStorage.put(usr, new ArrayList<>());
    }

    @Override
    public void sendMessageToServer(Message msg) {
        if(isPermit(msg.getSender())) {
            msg.setId(msgIdCounter++);
            if (activeUsers.contains(msg.getRecipient())) {
                long sessionId = 0;
                for(User usr : activeUsers) {
                    if(usr.getId() == msg.getRecipient().getId()) {
                        sessionId = usr.getSessionId();
                    }
                }
                String remoteObjectName = "User" + sessionId;
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
    }

    @Override
    public void sendCommonMessageToServer(Message msg) {
        if(isPermit(msg.getSender())) {
            msg.setId(msgIdCounter++);
            for (User active : activeUsers) {
                String remoteObjectName = "User" + active.getSessionId();
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
    }

    @Override
    public User connect(String username, String password) {
        for(User usr : allUsers) {
            if(usr.getUsername().equals(username)) {
                if(usr.getPassword().equals(password)) {
                    activeUsers.add(usr.setSessionId(generateSessionId()));
                    return usr;
                } else return null;
            }
        }
        return null;
    }

    @Override
    public List<Message> getDialog(User usr1, User usr2) {
        if(isPermit(usr1)) {
            List<Message> messages = msgStorage.get(usr1);
            messages.addAll(msgStorage.get(usr2));
            return messages;
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
                                null, sender.getAge()),
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
                res.add(new User(usr.getId(), usr.getUsername(), null, usr.getAge()));
            }
            return res;
        } return null;
    }

    @Override
    public boolean disconnect(User client) {
        if(isPermit(client)) {
            for (User usr : activeUsers) {
                if (usr.getUsername().equals(client.getUsername())) {
                    if (usr.getPassword().equals(client.getPassword())) {
                        activeUsers.remove(usr);
                        return true;
                    } else return false;
                }
            }
            return false;
        }
        return false;
    }

    private long generateSessionId() {
        return new Random().nextLong();
    }

    private boolean isPermit(User client) {
        for(User usr : activeUsers) {
            if(client.equals(usr)) {
                return client.getSessionId() == usr.getSessionId();
            }
        }
        return false;
    }
}