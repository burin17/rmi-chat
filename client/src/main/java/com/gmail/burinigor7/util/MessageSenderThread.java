package com.gmail.burinigor7.util;

import com.gmail.burinigor7.exception.SpecifiedServerUnavailableException;
import com.gmail.burinigor7.gui.UserForm;

public class MessageSenderThread extends Thread {
    private final UserForm userForm;
    private final String msgContent;
    private final String dialog;

    public MessageSenderThread(UserForm userForm, String msgContent, String dialog) {
        this.userForm = userForm;
        this.msgContent = msgContent;
        this.dialog = dialog;
    }

    @Override
    public void run() {
        try {
            userForm.getUser().sendMessage(msgContent, dialog);
            userForm.refreshChat();
        } catch (SpecifiedServerUnavailableException e) {
            userForm.serverUnavailable();
        }
    }
}
