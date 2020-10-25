package com.gmail.burinigor7;

import com.gmail.burinigor7.domain.User;
import com.gmail.burinigor7.remote.ClientRemoteImpl;

public class User2Runner {
    public static void main(String[] args) {
        User user102 = User.connectToServer("Igor", "123");
        new ClientRemoteImpl(user102.getSessionId());
        System.out.println(user102.getCommonDialog());
        user102.sendCommonMessage("Igor's common message");
        user102.sendMessage("Hello Lena!", new User().setId(10).setUsername("Lena"));
        System.out.println(user102.getDialog(new User().setId(10)));
    }
}