package com.gmail.burinigor7;

import com.gmail.burinigor7.domain.User;
import com.gmail.burinigor7.remote.ClientRemoteImpl;

public class UserRunner {
    public static void main(String[] args) {
        User user10 = User.connectToServer("Lena", "123");
        new ClientRemoteImpl(user10.getSessionId());
        user10.sendMessage("Hello", new User(102, "Igor", null, 0));
        user10.sendCommonMessage("Hello everyone!");
        user10.sendCommonMessage("I'm new here!");
        System.out.println(user10.getSessionId());
    }
}
