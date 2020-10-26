package com.gmail.burinigor7;

import com.gmail.burinigor7.domain.User;
import com.gmail.burinigor7.remote.client.impl.ClientRemoteImpl;

public class User2Runner {
    public static void main(String[] args) {
        User vasya = User.connectToServer("Vasya", "123");
        new ClientRemoteImpl(vasya.getId());
        System.out.println(vasya.getActiveUsers());
        vasya.sendCommonMessage("Hello everyone");
        vasya.getCommonDialog();
    }
}