package com.gmail.burinigor7.util;

import com.gmail.burinigor7.api.User;
import com.gmail.burinigor7.exception.ServersUnavailableException;
import com.gmail.burinigor7.exception.SpecifiedServerUnavailableException;
import com.gmail.burinigor7.exception.UsernameInUseException;
import com.gmail.burinigor7.remote.server.RMIServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ServerConnector {
    private static Registry registry;

    private static void initRegistry() throws ServersUnavailableException {
        try {
            if(registry == null)
                registry = LocateRegistry.getRegistry(1099);
        } catch (RemoteException e) {
            throw new ServersUnavailableException(e);
        }
    }

    public static List<String> availableServers() throws ServersUnavailableException {
        initRegistry();
        try {
            return Arrays.stream(registry.list())
                    .filter(remote -> remote.contains("RMIServer"))
                    .map(name -> name.replace("RMIServer", ""))
                    .collect(Collectors.toList());
        } catch (RemoteException e) {
            throw new ServersUnavailableException(e);
        }
//        throw new ServersUnavailableException(null);
    }

    public static User connectToServer(String username, String serverName)
            throws ServersUnavailableException, SpecifiedServerUnavailableException {
        initRegistry();
        try {
            String remoteObjectName = "RMIServer" + serverName;
            RMIServer server = (RMIServer) registry.lookup(remoteObjectName);
            try {
                long sessionId = server.connect(username);
                return new User(username, sessionId, serverName);
            } catch (UsernameInUseException e) {
                return null;
            }
        } catch (RemoteException | NotBoundException e) {
            throw new SpecifiedServerUnavailableException(e);
        }
//        throw new ServersUnavailableException(null);
    }
}
