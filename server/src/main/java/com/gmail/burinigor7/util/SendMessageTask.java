package com.gmail.burinigor7.util;

import com.gmail.burinigor7.domain.CommonMessage;
import com.gmail.burinigor7.domain.Message;
import com.gmail.burinigor7.domain.PrivateMessage;
import com.gmail.burinigor7.remote.RMIServerImpl;

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
                server.sendPrivateMessageToUser((PrivateMessage) msg);
            }
            if(msg instanceof CommonMessage) {
                server.sendCommonMessageToUser((CommonMessage) msg);
            }
        }
    }
}
