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
    
    private ArrayList<Player> lobby = new ArrayList<Player>();
    private int numberPlayer;
    
    public Lobby(){}
    public Lobby(int numberPlayer){
        this.numberPlayer = numberPlayer;
    }

    /**
     * @return the lobby
     */
    public ArrayList<Player> getLobby() {
        return lobby;
    }

    /**
     * @param lobby the lobby to set
     */
    public void setLobby(ArrayList<Player> lobby) {
        this.lobby = lobby;
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
