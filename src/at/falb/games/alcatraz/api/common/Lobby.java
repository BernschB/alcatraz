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
    private int numberPlayer;
    private int maxPlayers;

    public Lobby(Player player)
    {
        this.maxPlayers = player.getMaxPlayers();
        this.numberPlayer = 1;
    }
    public Lobby(int numberPlayer){
        this.numberPlayer = numberPlayer;
    }
    
    public boolean isFull()
    {
        return this.numberPlayer == this.maxPlayers;
    }
    
    public ArrayList<Player> getPlayer() {
        return player;
    }

    public void setPlayer(ArrayList<Player> player) {
        this.player = player;
    }
    
    public void addPlayer(Player player)
    {
        this.player.add(player);
        this.numberPlayer++;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    


    /**
     * @return the numberPlayer
     */
    public int getNumberPlayer() {
        return numberPlayer;
    }

    /**
     * @param numberPlayer the numberPlayer to set
     */
    public void setNumberPlayer(int numberPlayer) {
        this.numberPlayer = numberPlayer;
    }
    
    public String toString(){
        return "Diese Lobbie hat " +this.numberPlayer +" Spieler";
    }
    
}
