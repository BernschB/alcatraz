/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.common;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author stefanprinz
 */
public class Lobby implements Serializable {
    
    private ArrayList<Player> player = new ArrayList<Player>();
    private int currentPlayers;
    private int maxPlayers;
    
    public Lobby(){}

    public Lobby(Player player)
    {
        this.addPlayer(player);
        this.maxPlayers = player.getMaxPlayers();
        this.currentPlayers = 1;
    }
    public Lobby(int numberPlayer){
        this.currentPlayers = numberPlayer;
    }
    
    public boolean isFull()
    {
        return this.currentPlayers == this.maxPlayers;
    }
    
    public ArrayList<Player> getPlayer() {
        return this.player;
    }

    public void setPlayer(ArrayList<Player> player) {
        this.player = player;
    }
    
    public void addPlayer(Player player)
    {
        this.player.add(player);
        this.currentPlayers++;
    }
    
    public void removePlayer(Player player)
    {
        this.player.remove(player);
        this.currentPlayers--;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void getCurrentPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    


    /**
     * @return the numberPlayer
     */
    public int getCurrentPlayers() {
        return currentPlayers;
    }

    /**
     * @param currentPlayers the numberPlayer to set
     */
    public void setCurrentPlayers(int currentPlayers) {
        this.currentPlayers = currentPlayers;
    }
    
    public String toString(){
        return "Diese Lobbie hat " +this.currentPlayers +" Spieler";
    }
    
}
