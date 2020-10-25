package com.gmail.burinigor7;

import com.gmail.burinigor7.entity.User;
import com.gmail.burinigor7.remote.ClientRemoteImpl;

public class User3Runner {
    public static void main(String[] args) {
        User user101 = new User(101, "Vasya", 19);
        new ClientRemoteImpl(101);
        user101.sendMessage("What's up?", new User(102, "Igor", 20));
    }
}
