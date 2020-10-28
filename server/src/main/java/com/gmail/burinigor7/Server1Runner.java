package com.gmail.burinigor7;

import com.gmail.burinigor7.remote.RMIServerImpl;

public class Server1Runner {
    public static void main(String[] args) {
        new RMIServerImpl("MyServer");
    }
}