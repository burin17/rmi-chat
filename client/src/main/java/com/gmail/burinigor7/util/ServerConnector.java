package com.gmail.burinigor7.util;

import com.gmail.burinigor7.api.User;
import com.gmail.burinigor7.remote.ClientRemoteImpl;
import com.gmail.burinigor7.remote.server.RMIServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ServerConnector {
    private static final Registry registry;
    static {
        try {
            registry = LocateRegistry.getRegistry(1099);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<String> availableServers() {
        try {
            Registry registry = LocateRegistry.getRegistry(1099);
            return Arrays.stream(registry.list())
                    .filter(remote -> remote.contains("RMIServer"))
                    .map(name -> name.replace("RMIServer", ""))
                    .collect(Collectors.toList());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public static User connectToServer(String username, String serverName) {
        try {
            String remoteObjectName = "RMIServer" + serverName;
            RMIServer server = (RMIServer) registry.lookup(remoteObjectName);
            long sessionId = server.connect(username);
            return new User(username, sessionId, serverName);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }
}
