package com.gmail.burinigor7;

import com.gmail.burinigor7.domain.User;
import com.gmail.burinigor7.remote.ClientRemoteImpl;

public class User3Runner {
    public static void main(String[] args) {
        User user101 = User.connectToServer("Vasya", "123");
        new ClientRemoteImpl(user101.getSessionId());
        user101.sendMessage("What's up?", new User(102, "Igor", "123" , 20));
        System.out.println("common dialog --> " + user101.getCommonDialog());
    }
}
