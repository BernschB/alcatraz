/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.server;

import at.falb.games.alcatraz.api.common.*;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import spread.SpreadException;

/**
 *
 * @author stefanprinz
 */
//Todo: Write Configuration File for Servers. Example located in docs/. Finally located in ???
public class Main {

    private final static Logger LOG = Logger.getLogger(Main.class.getName());

    /**
     * @param args the command line arguments
     * @throws spread.SpreadException
     * @throws java.rmi.RemoteException
     */
    public static void main(String[] args) throws SpreadException, RemoteException, IOException {

        //Change dependin on your address
        String host = "127.0.0.1";
        String spreadGroupName = "alcatraz-SpreadGroup";

        System.out.println("Starting up Server...");

        //Erstellt einen RMI-Registry für RMI Binds auf dem Well known RMI Registry Port (1099). RMI Adressen müssen dann hier Angemeldet werden.
        try {
            System.out.println("Create RMI-Registry...");
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        } catch (RemoteException e) {
            LOG.log(Level.INFO, "Could not register Service. {0}", e.getMessage());
        }

        //hardcoded private spread name
        ServerStart impl = new ServerStart("Prinzi", host, 0, spreadGroupName);
        //ServerStart impl1 = new ServerStart("Prinzi", host, 0, spreadGroupName);
        
    }

}
