package com.example.manufacturer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import com.github.Pawedla.Order;
import com.github.Pawedla.Server;

public class RMIClient {

    private Server server;

    public void startClient() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        server = (Server) registry.lookup("Server");
    }

    //Carries out the whole ordering process
    public void postBookOrder(String order) {
        try {
            server.bookOrder(order);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
