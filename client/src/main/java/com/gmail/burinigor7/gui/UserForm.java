package com.gmail.burinigor7.gui;

import com.gmail.burinigor7.api.User;
import com.gmail.burinigor7.exception.SpecifiedServerUnavailableException;
import com.gmail.burinigor7.util.MessageSenderRunner;

import javax.swing.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
    private final ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors
            .newFixedThreadPool(
                    Runtime.getRuntime().availableProcessors()
            );

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
                threadPool.submit(new MessageSenderRunner(UserForm.this,
                        messageTextField.getText(), dialogsList.getSelectedValue()));
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
                        user.getServer().disconnect(user.getUsername());
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
        List<String> messagesForDialogArea =
            user.getDialog(dialogsList.getSelectedValue());
        if(messagesForDialogArea != null) {
            StringBuilder dialog = new StringBuilder();
            for (String message : messagesForDialogArea) {
                dialog.append(message);
            }
            chat.setText(dialog.toString());
        } else chat.setText("Nothing here yet.");
    }

    public JTextArea getChat() {
        return chat;
    }

    public void serverUnavailable() {
        JOptionPane.showMessageDialog(null, "Current server unavailable");
        dispose();
        new AvailableServers();
    }
}