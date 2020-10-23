package com.gmail.burinigor7;

import com.gmail.burinigor7.remote.ChatUserImpl;

public class UserRunner {
    public static void main(String[] args) {
        ChatUserImpl user10 = new ChatUserImpl(10);
        user10.sendMessage("Hello", 102);
    }
}
