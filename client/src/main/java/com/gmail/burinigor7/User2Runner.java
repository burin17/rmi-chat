package com.gmail.burinigor7;

import com.gmail.burinigor7.api.User;
import com.gmail.burinigor7.util.ServerConnector;

public class User2Runner {
    public static void main(String[] args) {
        User pete = ServerConnector.connectToServer("pete", "Sun");
        pete.sendCommonMessage("I'm new here!");
    }
}