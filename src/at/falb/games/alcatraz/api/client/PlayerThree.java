/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.client;

import static at.falb.games.alcatraz.api.client.PlayerFour.player1;
import at.falb.games.alcatraz.api.common.Lobby;
import at.falb.games.alcatraz.api.common.Player;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HTM_Campus
 */
public class PlayerThree {
    static Player player1 = new Player();
    static Player player2 = new Player();
    static Player player3 = new Player();
    static Player player4 = new Player();

    public static void main(String[] args) {
        Lobby lobby = new Lobby();
        player1.setID(0);
        player1.setMaxPlayers(4);
        player1.setRMI("rmi://localhost:1099/Player1Service");
        player1.setUsername("BernschB");

        player2.setID(1);
        player2.setMaxPlayers(4);
        player2.setRMI("rmi://localhost:1099/Player2Service");
        player2.setUsername("Prinzi");

        player3.setID(2);
        player3.setMaxPlayers(4);
        player3.setRMI("rmi://localhost:1099/Player3Service");
        player3.setUsername("Karl");

        player4.setID(3);
        player4.setMaxPlayers(4);
        player4.setRMI("rmi://localhost:1099/Player4Service");
        player4.setUsername("Karl");

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);
        lobby.addPlayer(player3);
        lobby.addPlayer(player4);
        lobby.setCurrentPlayers(4);
        lobby.setMaxPlayers(4);
        
        System.out.println("Before Bind " + player3.getRMI() + " ok");
        
        //Erstellt einen RMI-Registry für RMI Binds auf dem Well known RMI Registry Port (1099). RMI Adressen müssen dann hier Angemeldet werden.

        System.out.println("Create RMI-Registry...");
//        try {
//            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
//        } catch (RemoteException ex) {
//            Logger.getLogger(PlayerOne.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        try {
            GameImpl game = new GameImpl();
            game.startGame(lobby, player3);
            
            Naming.rebind(player3.getRMI(), game);
            System.out.println("Bind with: " + player3.getRMI() + "ok");
        } catch (RemoteException | MalformedURLException ex) {
            Logger.getLogger(PlayerTwo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }      
}
