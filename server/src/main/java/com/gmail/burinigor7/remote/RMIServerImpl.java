package com.gmail.burinigor7.remote;

import com.gmail.burinigor7.domain.Message;
import com.gmail.burinigor7.exception.UsernameInUseException;
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
    private Registry registry = null;
    private final Map<String, Long> activeUsers = new HashMap<>();
    private final List<Message> commonMessages = new ArrayList<>();

    public RMIServerImpl(String serverName) {
        String serverRemoteObjectName = "RMIServer" + serverName;
        try {
            registry = LocateRegistry.createRegistry(1099);
        } catch (RemoteException ignore) {}
        if(registry == null) {
            try {
                registry = LocateRegistry.getRegistry(1099);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            UnicastRemoteObject.exportObject(this, 0);
            registry.bind(serverRemoteObjectName, this);
        } catch (RemoteException | AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessageToServer(Message msg) {
        if(isPermit(msg.getSenderUsername(), msg.getSenderSessionId())) {
            Long recipientSessionId;
            if ((recipientSessionId = activeUsers
                    .get(msg.getRecipientUsername())) != null) {
                String remoteObjectName = "User" + recipientSessionId;
                try {
                    ClientRemote chatUser = (ClientRemote) registry.lookup(remoteObjectName);
                    chatUser.sendMessageToUser(msg.setSenderSessionId(null));
                } catch (RemoteException | NotBoundException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
        }
    }

    @Override
    public void sendCommonMessageToServer(Message msg) {
        if(isPermit(msg.getSenderUsername(), msg.getSenderSessionId())) {
            commonMessages.add(msg);
            for (Long sessionId : activeUsers.values()) {
                String remoteObjectName = "User" + sessionId;
                try {
                    ClientRemote chatUser = (ClientRemote) registry.lookup(remoteObjectName);
                    chatUser.sendMessageToUser(msg.setSenderSessionId(null));
                } catch (RemoteException | NotBoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public long connect(String username) throws UsernameInUseException {
        if(activeUsers.containsKey(username))
            throw new UsernameInUseException();
        long sessionId = new Random().nextLong();
        activeUsers.put(username, sessionId);
        refreshUserListForAll(sessionId);
        return sessionId;
    }

    private void refreshUserListForAll(long sessionId) {
        for (Long session : activeUsers.values()) {
            if(sessionId != session) {
                String remoteObjectName = "User" + session;
                try {
                    ClientRemote chatUser = (ClientRemote) registry.lookup(remoteObjectName);
                    chatUser.refreshAvailableDialogsList();
                } catch (RemoteException | NotBoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public List<Message> getCommonDialog(String username, long sessionId) {
        if(isPermit(username, sessionId)) {
            return new ArrayList<>(commonMessages);
        }
        return null;
    }

    @Override
    public Set<String> getActiveUsers(String username, long sessionId) {
        if(isPermit(username, sessionId)) {
            Set<String> res = new HashSet<>(activeUsers.keySet());
            res.add("Common dialog");
            return res;
        } return null;
    }

    @Override
    public boolean disconnect(String username, long sessionId) {
        if(isPermit(username, sessionId)) {
            activeUsers.remove(username);
            refreshUserListForAll(sessionId);
            return true;
        }
        return false;
    }

    public boolean disconnect(String username) {
        Long value = activeUsers.remove(username);
        return value != null;
    }

    private boolean isPermit(String senderUsername, long senderSessionId) {
        Long session = activeUsers.get(senderUsername);
        if(session != null)
            return session.equals(senderSessionId);
        return false;
    }
}