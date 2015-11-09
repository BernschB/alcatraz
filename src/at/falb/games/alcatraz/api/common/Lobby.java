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
            string = string.concat(pl.getUsername() + " mit ID: " + pl.getID() + "    ");
        }
        System.out.println(string);
        return string;
    }

    public void addPlayer(Player pl) {
        player.add(pl);
        currentPlayers++;
        for (Player p : player) {
            System.out.println(p.getUsername() + " mit ID: " + p.getID() + "    ");
        }
    }

    public void removePlayer(Player pl) {
        
        ArrayList<Player> copy = new ArrayList<Player>(player);

        for (Player p : player) {
            if (p.getUsername().equals(pl.getUsername())) {
                copy.remove(p);
                break;
            }
        }
        player = copy;

        currentPlayers--;
        for (Player p : player) {
            System.out.println(p.getUsername() + " mit ID: " + p.getID() + "    ");
        }
    }

    public int getMaxPlayers() {
        return maxPlayers;
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

    public int getSpecificPlayer(Player pl) {
        for (Player p : player) {
            if (p.getUsername().equals(pl.getUsername())) {
                return -1;
            }
        }
        return 1;
    }
    
    public ArrayList<Player> getListOfPlayers(){
        return player;
    }

}
