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
public class ServerImpl extends UnicastRemoteObject implements Server  {
    
    private ArrayList<Lobby> lobby = new ArrayList<Lobby>();
    
    public ServerImpl() throws RemoteException{
        super();
    }
    
    @Override
    public void loginClient(Player player) {
        boolean newLobby = true;
        for(int i = 0; i < this.lobby.size(); i++)
        {
            if(this.lobby.get(i).getMaxPlayers() == player.getMaxPlayers())
            {
                this.lobby.get(i).addPlayer(player);
                newLobby = false;
                System.out.print("\nUser " + player.getUsername() + " wurde zur Lobby " + i + " hinzugefügt");
                if(this.lobby.get(i).isFull())
                {
                    System.out.print("\nLobby ist voll");
                    startGame(this.lobby.get(i));
                }
                break;
            }
        }
        if(newLobby == true)
        {
            System.out.print("\nUser " + player.getUsername() + " hat eine neune Lobby angelegt");
            this.lobby.add(new Lobby(player));
        }
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void logoutClient(Player player) {
        for(int i = 0; i < this.lobby.size(); i++)
        {
            for(int j = 0; j < this.lobby.get(i).getNumberPlayer(); j++)
            {
                if(this.lobby.get(i).getPlayer().get(j).getUsername().equals(player.getUsername()))
                {
                    this.lobby.get(i).removePlayer(player);
                    System.out.print("\nUser " + player.getUsername() + " wurde aus der Lobby " + i + " gelöscht");
                    break;
                }
            }

        }
 //       throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void startGame(Lobby lobby) {
        this.lobby.remove(lobby);
        System.out.print("\nLobby gelöscht");
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
