/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.falb.games.alcatraz.api.server;

import at.falb.games.alcatraz.api.common.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

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
    public static void main(String[] args) {

        String host = "server";
        String groupName = "alcatraz-server";
        String myNameInGroup ="";
        
        //Lobbies werden erstellt
        Lobby lobbyTwo = createLobby(2);
        Lobby lobbyThree = createLobby(3);
        Lobby lobbyFour = createLobby(4);
        
        LOG.info("Following Lobbies Created:");
        LOG.info(lobbyTwo.toString());
        LOG.info(lobbyThree.toString());
        LOG.info(lobbyFour.toString());
        
        //Erstellt einen Registry für RMI Binds auf dem Well known RMI Registry Port (1099)
        try{
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        } catch  (RemoteException e){
            LOG.info("Could not register Service. " +e.getMessage().toString());
        }
        
        
       
    }
    
    //Erstellt eine Lobby mit der gewünschten Anzahl der Spieler
    private static Lobby createLobby(int numbPlayer){
        Lobby lobby = new Lobby(numbPlayer);
        return lobby;
    }
    
}
