/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.falb.games.alcatraz.api.server;

import at.falb.games.alcatraz.api.common.*;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
    public static void main(String[] args) throws SpreadException, RemoteException {

        String host = "localhost";
        String spreadGroupName = "alcatraz-server";
        
        System.out.println("Starting up Server...");

        //Lobbies werden erstellt. Gehört dann in die Impl rein, darum jetzt auskommentiert.
        
        /*
        Lobby lobbyTwo = createLobby(2);
        Lobby lobbyThree = createLobby(3);
        Lobby lobbyFour = createLobby(4);
        
        LOG.info("Following Lobbies Created:");
        LOG.info(lobbyTwo.toString());
        LOG.info(lobbyThree.toString());
        LOG.info(lobbyFour.toString());
        */
        
        //Erstellt einen RMI-Registry für RMI Binds auf dem Well known RMI Registry Port (1099). RMI Adressen müssen dann hier Angemeldet werden.
        try{
            System.out.println("Create RMI-Registry...");
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT); 
        } catch  (RemoteException e){
            LOG.info("Could not register Service. " +e.getMessage());
        }
        
        
        ServerStart impl1 = new ServerStart("Prinzi", host, 0, spreadGroupName);
        ServerStart impl2 = new ServerStart("Bernschibu", host, 0, spreadGroupName);
        ServerStart impl3 = new ServerStart("MasterOverlordSlayerOfDarkness", host, 0, spreadGroupName);
        
        System.out.println("done");
               
    }
    
}
