package com.gmail.burinigor7.gui;

import com.gmail.burinigor7.api.User;
import com.gmail.burinigor7.domain.Message;
import com.gmail.burinigor7.exception.SpecifiedServerUnavailableException;
import com.gmail.burinigor7.util.MessageSenderThread;

import javax.swing.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

public class UserForm extends JFrame {
    private final User user;
    private JPopupMenu popupMenu;
    private JList<String> dialogsList;
    private JTextArea chat;
    private JButton leaveServerButton;
    private JTextField messageTextField;
    private JButton sendButton;
    private JPanel mainPanel;
    private JPanel availableDialogsPanel;
    private JPanel chatPanel;
    private JPanel leaveServerButtonPanel;
    private JPanel sendMessagePanel;
    private JPanel messageTextFieldPanel;
    private JPanel sendButtonPanel;
    private JPanel messageLabelPanel;
    private JLabel messageLabel;
    private JPanel dialogsListPanel;
    private JLabel availableDialogsLabel;
    private JLabel chatLabel;
    private JLabel serverInfoLabel;
    private JLabel userInfoLabel;
    private JScrollPane chatContentPanel;
    private final String serverName;

    public UserForm(User user, String serverName) {
        this.serverName = serverName;
        this.user = user;
        init();
        leaveServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                user.disconnectFromServer();
                dispose();
                new AvailableServers();
            }
        });
        dialogsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                refreshChat();
            }
        });
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                new MessageSenderThread(UserForm.this,
                        messageTextField.getText(), dialogsList.getSelectedValue()).start();
            }
        });
    }

    private void init() {
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        try {
            Set<String> participants = user.getActiveUsers();
            DefaultListModel<String> dialogsModel = new DefaultListModel<>();
            for (String participant : participants) {
                if (!participant.equals(user.getUsername()))
                    dialogsModel.addElement(participant);
            }
            dialogsList.setModel(dialogsModel);
            setTitle("Chat");
            String serverInfo = "Server: " + serverName;
            String userInfo = "Username: " + user.getUsername();
            serverInfoLabel.setText(serverInfo);
            userInfoLabel.setText(userInfo);
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent event) {
                    try {
                        user.getServer().disconnect(user.getUsername(), user.getSessionId());
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            chat.setEditable(false);
        } catch (SpecifiedServerUnavailableException e) {
            serverUnavailable();
        }
    }

    public User getUser() {
        return user;
    }

    public JList<String> getDialogsList() {
        return dialogsList;
    }

    public void refreshChat() {
        try {
            List<Message> messagesForDialogArea = user.getDialog(dialogsList.getSelectedValue());
            StringBuilder dialog = new StringBuilder();
            for (Message message : messagesForDialogArea) {
                if (message.getSenderUsername().equals(user.getUsername()))
                    dialog.append("You: ").append(message.getContent())
                            .append('\n');
                else dialog.append(message.getSenderUsername()).append(": ")
                        .append(message.getContent()).append('\n');
            }
            synchronized (this) {
                chat.setText(dialog.toString());
            }
        } catch (SpecifiedServerUnavailableException e) {
            serverUnavailable();
        }
    }

    public void serverUnavailable() {
        JOptionPane.showMessageDialog(null, "Current server unavailable");
        dispose();
        new AvailableServers();
    }
}