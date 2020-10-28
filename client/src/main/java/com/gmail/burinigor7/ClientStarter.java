package com.gmail.burinigor7;

import com.gmail.burinigor7.gui.ConnectionForm;

import javax.swing.*;

public class ClientStarter {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Connect to server");
        frame.setContentPane(new ConnectionForm().getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}