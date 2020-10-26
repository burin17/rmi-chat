package com.gmail.burinigor7;

import com.gmail.burinigor7.domain.User;
import com.gmail.burinigor7.remote.client.impl.ClientRemoteImpl;

import java.util.List;

public class User1Runner {
    public static void main(String[] args) throws Exception {
        User igor = User.connectToServer("Igor", "123");
        new ClientRemoteImpl(igor.getId());
        List<User> active = igor.getActiveUsers();
        System.out.println(active);
        igor.sendMessage("Hello", new User().setId(2));
        igor.disconnectFromServer();
    }
}
