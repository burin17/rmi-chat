package com.gmail.burinigor7;

import com.gmail.burinigor7.entity.User;
import com.gmail.burinigor7.remote.ClientRemoteImpl;

public class UserRunner {
    public static void main(String[] args) {
        User user10 = new User(10, "Lena", "123" , 10);
        new ClientRemoteImpl(10);
        user10.sendMessage("Hello", new User(102, "Igor", "123" , 20));
    }
}
