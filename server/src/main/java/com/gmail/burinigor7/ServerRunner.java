package com.gmail.burinigor7;

import com.gmail.burinigor7.remote.RMIServerImpl;

import java.util.Scanner;

public class ServerRunner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        new RMIServerImpl(scanner.nextLine());
    }
}
