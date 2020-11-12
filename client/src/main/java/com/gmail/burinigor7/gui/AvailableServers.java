package com.gmail.burinigor7.gui;

import com.gmail.burinigor7.api.User;
import com.gmail.burinigor7.exception.ServersUnavailableException;
import com.gmail.burinigor7.exception.SpecifiedServerUnavailableException;
import com.gmail.burinigor7.exception.UsernameInUseException;

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
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    System.out.println("here");
                    List<String> serversNames = User.availableServers();
                    serversModel = new DefaultListModel<>();
                    for (String name : serversNames) {
                        serversModel.addElement(name);
                    }
                    serversList.setModel(serversModel);
                } catch (ServersUnavailableException e) {
                    serversList.setModel(new DefaultListModel<>());
                    JOptionPane.showMessageDialog(null, "No available servers for now");
                }
            }
        });
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String username = usernameField.getText();
                String serverName = serversList.getSelectedValue();
                if(serverName == null) {
                    JOptionPane.showMessageDialog(null, "Choose the server!");
                } else if(username.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Type the username!");
                } else {
                    try {
                        try {
                            new User(username, serverName);
                            dispose();
                        } catch (UsernameInUseException e) {
                            JOptionPane.showMessageDialog(null, "Typed username already in use!");
                        }
                    } catch (SpecifiedServerUnavailableException | ServersUnavailableException e2) {
                        JOptionPane.showMessageDialog(null, "Specified server unavailable");
                        refreshButton.doClick();
                    }
                }
            }
        });
        init();
    }

    private void init() {
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        refreshButton.doClick();
        setTitle("Connection to server");
    }
}