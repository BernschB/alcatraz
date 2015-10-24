/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.server;

import at.falb.games.alcatraz.api.common.Lobby;
import at.falb.games.alcatraz.api.common.Player;
import at.falb.games.alcatraz.api.common.Server;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 *
 * @author stefanprinz
 */
public class ServerImpl extends UnicastRemoteObject implements Server {

    private ArrayList<Lobby> lobby = new ArrayList<Lobby>();

    public ServerImpl() throws RemoteException {
        super();
    }

    @Override
    public int loginClient(Player player) {

        
        //Insert Multicast here for active replication?

        boolean newLobby = true;
        for(int i = 0; i < this.lobby.size(); i++)
        {
            if(this.lobby.get(i).getMaxPlayers() == player.getMaxPlayers())
            {
                this.lobby.get(i).addPlayer(player);
                newLobby = false;
                System.out.println("User " + player.getUsername() + " wurde zur Lobby " + i + " hinzugefügt");
                if(this.lobby.get(i).isFull())
                {
                    System.out.println("Lobby ist voll");
                    startGame(this.lobby.get(i));
                }
                break;
            }
        }
        if(newLobby == true)
        {
            System.out.println("User " + player.getUsername() + " hat eine neune Lobby angelegt");
            this.lobby.add(new Lobby(player));
        }
        
        return 1;
        
    }

    @Override
    public void logoutClient(Player player) {
        for (int i = 0; i < this.lobby.size(); i++) {
            for (int j = 0; j < this.lobby.get(i).getCurrentPlayers(); j++) {
                if (this.lobby.get(i).getPlayer().get(j).getUsername().equals(player.getUsername())) {
                    this.lobby.get(i).removePlayer(player);
                    System.out.println("User " + player.getUsername() + " wurde aus der Lobby " + i + " gelöscht");
                    break;
                }
            }

        }
        //       throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void startGame(Lobby lobby) {
        this.lobby.remove(lobby);
        System.out.println("Jetzt würde das Spiel starten!");
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
