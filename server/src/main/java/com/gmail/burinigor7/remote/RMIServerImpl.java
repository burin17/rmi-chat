package com.gmail.burinigor7.remote;

import com.gmail.burinigor7.domain.CommonMessage;
import com.gmail.burinigor7.domain.Dialog;
import com.gmail.burinigor7.domain.Message;
import com.gmail.burinigor7.domain.PrivateMessage;
import com.gmail.burinigor7.remote.client.ClientRemote;
import com.gmail.burinigor7.remote.server.RMIServer;
import com.gmail.burinigor7.util.SendMessageTask;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class RMIServerImpl implements RMIServer {
    private Registry registry = null;
    private final String serverName;

    private final Map<String, ClientRemote> activeUsers = new ConcurrentHashMap<>();

    private final ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors
                    .newFixedThreadPool(
                            Runtime.getRuntime().availableProcessors()
                    );

    public RMIServerImpl(String serverName) {
        this.serverName = serverName;
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
        threadPool.submit(new SendMessageTask(msg, this));
    }

    public void sendCommonMessageToUser(CommonMessage commonMessage) {
        for(String username : activeUsers.keySet()) {
            try {
                activeUsers.get(username).sendMessageToUser(commonMessage);
            } catch (RemoteException e) {
                activeUsers.remove(username);
                refreshUserListForAll(null);
            }
        }

    }

    public void sendPrivateMessageToUser(PrivateMessage privateMessage) {
        ClientRemote remote = activeUsers.get(privateMessage.getRecipient());
        try {
            remote.sendMessageToUser(privateMessage);
        } catch (RemoteException e) {
            activeUsers.remove(privateMessage.getRecipient());
            refreshUserListForAll(null);
        }
    }

    @Override
    public void connect(String username) {
        ClientRemote remote;
        String clientRemoteName = "User" + serverName + username;
        try {
            remote = (ClientRemote) registry.lookup(clientRemoteName);
            activeUsers.put(username, remote);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
        refreshUserListForAll(remote);
    }

    private void refreshUserListForAll(ClientRemote except) {
        Collection<ClientRemote> remotes = new ArrayList<>(activeUsers.values());
        remotes.remove(except);
        for (ClientRemote remote : remotes) {
            try {
                remote.refreshAvailableDialogsList();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Set<String> getActiveUsers(String username) {
        if(isPermit(username)) {
            Set<String> res = new HashSet<>(activeUsers.keySet());
            res.add("Common dialog");
            return res;
        } return null;
    }

    @Override
    public boolean disconnect(String username) {
        if(isPermit(username)) {
            activeUsers.remove(username);
            refreshUserListForAll(null);
            return true;
        }
        return false;
    }

    public boolean isPermit(String senderUsername) {
        return activeUsers.get(senderUsername) != null;
    }
}