/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.client;

import at.falb.games.alcatraz.api.Alcatraz;
import at.falb.games.alcatraz.api.MoveListener;
import at.falb.games.alcatraz.api.Prisoner;
import at.falb.games.alcatraz.api.common.GameInterface;
import at.falb.games.alcatraz.api.common.Lobby;
import at.falb.games.alcatraz.api.common.Player;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HTM_Campus
 */

public class GameImpl extends UnicastRemoteObject implements GameInterface, MoveListener, Remote, Serializable { //
    private final Alcatraz other[] = new Alcatraz[2];
    private int numPlayer = 2;
    private Player player = new Player();
    private Lobby lobby = new Lobby();
    
    private final ArrayList<Player> otherPlayers = new ArrayList<Player>();
    
    /**
     *
     * @throws java.rmi.RemoteException
     */
    public GameImpl() throws RemoteException {
        super();
    }
      
    public void setOther(int i, Alcatraz t) {
        this.other[i] = t;
    }
    
    public int getNumPlayer() {
        return numPlayer;
    }
        
    public void setNumPlayer(int numPlayer) {
        this.numPlayer = numPlayer;
    }
    
    @Override
    public void moveDone(at.falb.games.alcatraz.api.Player player, Prisoner prisoner, int rowOrCol, int row, int col) {
        myMoveDone(player, prisoner, rowOrCol, row, col);
        otherMoveDone(player, prisoner, rowOrCol, row, col);
    }
    
    public void myMoveDone(at.falb.games.alcatraz.api.Player player, Prisoner prisoner, int rowOrCol, int row, int col) {
        System.out.println("moving " + prisoner + " to " + (rowOrCol == Alcatraz.ROW ? "row" : "col") + " " + (rowOrCol == Alcatraz.ROW ? row : col));
    }
    
    public void otherMoveDone(at.falb.games.alcatraz.api.Player player, Prisoner prisoner, int rowOrCol, int row, int col) {
        System.out.println("otherMoveDone");
        System.out.println(this.otherPlayers.get(0).getRMI());
//        System.out.println(this.lobby.getListOfPlayers().get(this.otherPlayers.get(0).getID()).getRMI());
        try {     
            GameInterface game = (GameInterface) Naming.lookup(this.otherPlayers.get(0).getRMI());
            System.out.println("pre remoteMoveDone" + this.otherPlayers.get(0).getRMI());
            game.remoteMoveDone(player, prisoner, rowOrCol, row, col);
            System.out.println("remoteMoveDone");
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            Logger.getLogger(GameImpl.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
    
    
    @Override
    public void remoteMoveDone(at.falb.games.alcatraz.api.Player player, Prisoner prisoner, int rowOrCol, int row, int col) throws RemoteException  {

        System.out.println("remote Move Done moving " + prisoner + " to " + (rowOrCol == Alcatraz.ROW ? "row" : "col") + " " + (rowOrCol == Alcatraz.ROW ? row : col));

        System.out.println(numPlayer);
        for (int i = 0; i < numPlayer; i++) {
            if((!player.equals(this.lobby.getListOfPlayers().get(i))))
            {
//                System.out.println("in for " + i);
//                System.out.println("Player Name" + player.getName());
//                System.out.println("Player ID" + player.getId());
//                System.out.println("Prisoner ID" + prisoner.getId());
//                System.out.println("Player " + other[i].getPlayer(player.getId()).getName());
//                System.out.println("Prisoner " + other[i].getPrisoner(prisoner.getId()));
                other[i].doMove(other[i].getPlayer(player.getId()), other[i].getPrisoner(prisoner.getId()), rowOrCol, row, col);   
                other[i].doMove(player, prisoner, rowOrCol, row, col);
//                System.out.println("doMovedone");
            }
        }
    }


    public void undoMove() {
         System.out.println("Undoing move");
    }
    
    @Override
    public void gameWon(at.falb.games.alcatraz.api.Player player) {
        System.out.println("Player " + player.getId() + " wins.");
    }
    
    @Override
    public void startGame(Lobby lobby, Player player) throws RemoteException
    {
        ArrayList<Alcatraz> alcatraz = new ArrayList<Alcatraz>();
        this.lobby = lobby;
        this.player = player;
        

        this.numPlayer = this.lobby.getCurrentPlayers();
        System.out.println("numPlayer = " + numPlayer);
        
        // Zuerst müsse  alle Games und Alcatrac Objekte erstellt werden
        for(int i = 0; i < numPlayer; i++)
        {
            alcatraz.add(new Alcatraz());       
            alcatraz.get(i).init(numPlayer, this.lobby.getListOfPlayers().get(i).getID());
            System.out.println("alcatraz Player = " + this.lobby.getListOfPlayers().get(i).getUsername());
            System.out.println("alcatraz PlayerID = " + this.lobby.getListOfPlayers().get(i).getID());
        }     
        
        for(int i = 0; i < numPlayer; i++)
        {
            for(int j = 0; j < numPlayer; j++)
            {
                alcatraz.get(i).getPlayer(j).setName(this.lobby.getListOfPlayers().get(j).getUsername());
            }
        } 
       
        
        for(int i = 0; i < numPlayer; i++)
        {
                System.out.println("setOther i = " + i);
                this.setOther(i, alcatraz.get((i+1) % numPlayer));
        } 
        
        for(int i = 0; i < numPlayer; i++)
        {
            // Der Array mit den anderen Spieler wird befüllt
            if(!(player.equals(this.lobby.getListOfPlayers().get(i))))
            {
                this.otherPlayers.add(this.lobby.getListOfPlayers().get(i));
                System.out.println("Other PlayerID = " + this.lobby.getListOfPlayers().get(i).getID());
                System.out.println("Other Player = " + this.lobby.getListOfPlayers().get(i).getUsername());
            }
        }   

        System.out.println("player.getID() = " + player.getID());
        alcatraz.get(player.getID()).showWindow();
        alcatraz.get(player.getID()).addMoveListener(this);
        alcatraz.get(player.getID()).start();
    } 
}
