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
    private int currentPlayers = 0;
    private int maxPlayers;

    public Lobby() {
    }

    public Lobby(Player player) {
        this.addPlayer(player);
        this.maxPlayers = player.getMaxPlayers();
    }

    public Lobby(int numberPlayer) {
        this.currentPlayers = numberPlayer;
    }

    public boolean isFull() {
        return this.currentPlayers == this.maxPlayers;
    }

    public String getPlayer() {
        String string = "";
        for (Player pl : player) {
            string = string.concat(pl.getUsername()) + " ";
        }
        System.out.println(string);
        return string;
    }


    public void addPlayer(Player player) {
        this.player.add(player);
        this.currentPlayers++;
    }

    public void removePlayer(Player player) {
        this.player.remove(player);
        this.currentPlayers--;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void getCurrentPlayers(int maxPlayers) {
        this.setMaxPlayers(maxPlayers);
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

    public String toString() {
        return "Diese Lobbie hat " + this.currentPlayers + " Spieler";
    }

    /**
     * @param maxPlayers the maxPlayers to set
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

}
