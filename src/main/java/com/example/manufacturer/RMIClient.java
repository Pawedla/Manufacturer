package com.example.manufacturer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.github.Pawedla.Server;

public class RMIClient {

    private Server server;

    public void startClient() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        server = (Server) registry.lookup("Server");
    }

    //Carries out the whole ordering process
    public void bookOrder(int number, String[] order, String[] offer) {
        try {
            server.bookOrder(number, order, offer);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
