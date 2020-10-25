package com.gmail.burinigor7;

import com.gmail.burinigor7.entity.User;
import com.gmail.burinigor7.remote.ClientRemoteImpl;

public class User2Runner {
    public static void main(String[] args) {
        User user102 = new User(102, "Igor", "123" , 20);
        new ClientRemoteImpl(102);
        System.out.println(user102.getDialog(null));
        user102.sendCommonMessage("Igor's common message");
    }
}
