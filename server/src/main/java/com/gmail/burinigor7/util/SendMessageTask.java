package com.gmail.burinigor7.util;

import com.gmail.burinigor7.domain.CommonMessage;
import com.gmail.burinigor7.domain.Message;
import com.gmail.burinigor7.domain.PrivateMessage;
import com.gmail.burinigor7.remote.RMIServerImpl;
import com.gmail.burinigor7.remote.client.ClientRemote;

import java.rmi.RemoteException;
import java.util.Map;

public class SendMessageTask implements Runnable {
    private final Message msg;
    private final RMIServerImpl server;

    public SendMessageTask(Message msg, RMIServerImpl server) {
        this.msg = msg;
        this.server = server;
    }

    @Override
    public void run() {
        if(server.isPermit(msg.getSender())) {
            if(msg instanceof PrivateMessage) {
                sendPrivateMessageToUser((PrivateMessage) msg);
            }
            if(msg instanceof CommonMessage) {
                sendCommonMessageToUser((CommonMessage) msg);
            }
        }
    }

    private void sendCommonMessageToUser(CommonMessage commonMessage) {
        Map<String, ClientRemote> activeUsers = server.getActiveUsers();
        for(Map.Entry<String, ClientRemote> entry : activeUsers.entrySet()) {
            try {
                activeUsers.get(entry.getKey()).sendMessageToUser(commonMessage);
            } catch (RemoteException e) {
                activeUsers.remove(entry.getKey());
                server.refreshUserListForAll(null);
            }
        }

    }

    private void sendPrivateMessageToUser(PrivateMessage privateMessage) {
        Map<String, ClientRemote> activeUsers = server.getActiveUsers();
        ClientRemote remote = activeUsers.get(privateMessage.getRecipient());
        try {
            remote.sendMessageToUser(privateMessage);
        } catch (RemoteException e) {
            activeUsers.remove(privateMessage.getRecipient());
            server.refreshUserListForAll(null);
        }
    }

}
