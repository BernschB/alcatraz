/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.falb.games.alcatraz.api.server;

import at.falb.games.alcatraz.api.common.*;
import java.net.UnknownHostException;
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
     */
    public static void main(String[] args) throws SpreadException {

        String host = "localhost";
        String spreadGroupName = "alcatraz-server";
        String mySpreadName ="privatename";
        
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
        
        //Erstellt einen Registry für RMI Binds auf dem Well known RMI Registry Port (1099)
        try{
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        } catch  (RemoteException e){
            LOG.info("Could not register Service. " +e.getMessage());
        }
        
        try {
            ServerImpl impl = new ServerImpl(mySpreadName, host, 0, spreadGroupName); //wenn der Port 0 ist, wird der Default Port (4803) verwendet
        } catch (UnknownHostException e) {
            LOG.info("Could not create Server Implementation. " +e.getMessage());
        }
        
        
       
    }
    

    
}
