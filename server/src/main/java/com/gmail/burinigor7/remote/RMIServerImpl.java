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
    private final Map<String, ClientRemote> activeUsers = new ConcurrentHashMap<>();
    private final List<Dialog> messageStorage
            = Collections.synchronizedList(new ArrayList<>());
    private final String serverName;
    private final ThreadPoolExecutor threadPool =
            (ThreadPoolExecutor) Executors.newCachedThreadPool();

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
        saveCommonMessage(commonMessage);
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
            savePrivateMessage(privateMessage);
        } catch (RemoteException e) {
            activeUsers.remove(privateMessage.getRecipient());
            refreshUserListForAll(null);
        }
    }

    private void savePrivateMessage(PrivateMessage msg) {
        Dialog dialog = dialog(msg.getSender(), msg.getRecipient());
        if(dialog.isNew()) {
            messageStorage.add(dialog);
        }
        dialog.addMessage(msg);
    }

    private void saveCommonMessage(CommonMessage msg) {
        Dialog dialog = dialog("Common dialog", null);
        if(dialog.isNew()) {
            messageStorage.add(dialog);
        }
        dialog.addMessage(msg);
    }

    private Dialog dialog(String username1, String username2) {
        Dialog newDialog = new Dialog(username1, username2);
        int idx = messageStorage.indexOf(newDialog);
        if(idx != -1) {
            return messageStorage.get(idx);
        }
        return newDialog;
    }

    @Override
    public void connect(String username) {
        ClientRemote remote;
        synchronized (activeUsers) {
            String clientRemoteName = "User" + serverName + username;
            try {
                remote = (ClientRemote) registry.lookup(clientRemoteName);
                activeUsers.put(username, remote);
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        }
        refreshUserListForAll(remote);
    }

    private void refreshUserListForAll(ClientRemote except) {
        Collection<ClientRemote> remotes = activeUsers.values();
        for (ClientRemote remote : remotes) {
            try {
                if(except != remote)
                    remote.refreshAvailableDialogsList();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<String> getDialog(String requesting, String otherUser) {
        if(isPermit(requesting)) {
            List<String> res = new ArrayList<>();
            for(Object o : dialog(otherUser.equals("Common dialog") ? null : requesting,
                    otherUser).getMessages()) {
                Message msg = (Message) o;
                if(requesting.equals(msg.getSender()))
                    res.add("You : " + msg.getContent() + "\n");
                else res.add(msg.getSender() + " : " + msg.getContent() + "\n");
            }
            return res;
        }
        return null;
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

// все сообщения хранятся на сервере (ok)
// отправка сообщений пользователям с сервера (пул потоков) распараллелена
// Мапа активных пользователей - <String, ClientRemote> (ok)
// клиент не хранит сообщения (ok)
// иерархия Message, у сервера один метод для общих и личных сообщений. (ok)
// написать свою Map
// работа ConcurrencyHashMap