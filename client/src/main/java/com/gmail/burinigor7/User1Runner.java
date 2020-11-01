package com.gmail.burinigor7;

import com.gmail.burinigor7.api.User;
import com.gmail.burinigor7.util.ServerConnector;

public class User1Runner {
    public static void main(String[] args) {
        System.out.println(ServerConnector.availableServers());
        User igor123 = ServerConnector.connectToServer("igor123", "Sun");
        igor123.sendMessage("Hello, Pete!", "pete");
        igor123.sendMessage("How are you?", "pete");
        igor123.sendCommonMessage("Hello everyone!");
        System.out.println("active users ---> " + igor123.getActiveUsers());
        System.out.println("common dialog ---> " + igor123.getCommonDialog());
    }
}