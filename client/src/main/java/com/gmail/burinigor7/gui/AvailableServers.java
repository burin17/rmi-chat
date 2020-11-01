package com.gmail.burinigor7.gui;

import com.gmail.burinigor7.remote.ClientRemoteImpl;
import com.gmail.burinigor7.util.ServerConnector;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AvailableServers extends JFrame {
    private JPanel mainPanel;
    private JList<String> serversList;
    private JPanel serversLabelPanel;
    private JLabel serversLabel;
    private JPanel serversListPanel;
    private JPanel refreshButtonPanel;
    private JButton refreshButton;
    private JPanel usernamePanel;
    private JPanel textFieldPanel;
    private JTextField usernameField;
    private JButton connectButton;
    private JPanel connectButtonPanel;
    private DefaultListModel<String> serversModel = new DefaultListModel<>();

    public AvailableServers() {
        init();
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> serversNames = ServerConnector.availableServers();
                serversModel = new DefaultListModel<>();
                for(String name : serversNames) {
                    serversModel.addElement(name);
                }
                serversList.setModel(serversModel);
            }
        });
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String serverName = serversList.getSelectedValue();
                if(serverName == null) {
                    JOptionPane.showMessageDialog(null, "Choose the server!");
                } else if(username.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Type the username!");
                } else {
                    dispose();
                    new ClientRemoteImpl(new UserForm(ServerConnector.connectToServer(username, serverName), serverName));
                }
            }
        });
    }

    private void init() {
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        List<String> serversNames = ServerConnector.availableServers();
        for(String name : serversNames) {
            serversModel.addElement(name);
        }
        serversList.setModel(serversModel);
        setTitle("Connection to server");
    }
}