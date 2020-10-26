package com.gmail.burinigor7;

import com.gmail.burinigor7.domain.User;
import com.gmail.burinigor7.remote.client.impl.ClientRemoteImpl;

public class User3Runner {
    public static void main(String[] args) {
        User pete = User.connectToServer("Pete", "123");
        new ClientRemoteImpl(pete.getId());
        pete.sendCommonMessage("Hello Vasya");
        System.out.println(pete.getCommonDialog());
    }
}
