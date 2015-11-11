/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.common;

import java.util.ArrayList;

/**
 *
 * @author stefanprinz
 */
public class LobbyList extends ArrayList {
    
    private ArrayList<Lobby> lobby = new ArrayList<Lobby>();
    private int seqNr;
    
    /**
     * @return the seqNr
     */
    
    public LobbyList(){
        super();
        this.seqNr = 0;
    }
    
    public ArrayList<Lobby> getArrayList(){
        return lobby;
    }
    
    public LobbyList addLobby(Lobby lob){
        lobby.add(lob);
        return this;
    }
    
    public Lobby getLobby(int maxPlayers){
        for (Lobby l : lobby){
            if (l.getMaxPlayers() == maxPlayers){
                return l;
            }
        }
        return null;
    }
    
    public int getSeqNr() {
        return seqNr;
    }

    /**
     * @param seqNr the seqNr to set
     */
    public void setSeqNr(int seqNr){
        this.seqNr = seqNr;
    }
    
    public void seqNrPlus() {
        this.seqNr++;
    }

}
