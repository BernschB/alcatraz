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
import static java.lang.Thread.sleep;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnknownHostException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HTM_Campus
 */

public class GameImpl extends UnicastRemoteObject implements GameInterface, MoveListener, Remote, Serializable { 
    
    private final Alcatraz other[] = new Alcatraz[4];
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
        System.out.println("Before Sleep");
        myMoveDone(player, prisoner, rowOrCol, row, col);
        System.out.println("after Sleep");
        otherMoveDone(player, prisoner, rowOrCol, row, col);
    }
    
    public void myMoveDone(at.falb.games.alcatraz.api.Player player, Prisoner prisoner, int rowOrCol, int row, int col) {
        System.out.println("moving " + prisoner + " to " + (rowOrCol == Alcatraz.ROW ? "row" : "col") + " " + (rowOrCol == Alcatraz.ROW ? row : col));
    }
    
     public void otherMoveDone(at.falb.games.alcatraz.api.Player player, Prisoner prisoner, int rowOrCol, int row, int col) {
        System.out.println("otherMoveDone");
        System.out.println(this.otherPlayers.get(0).getRMI());
        ArrayList<Integer> notDone = new ArrayList<Integer>();

        for(int i = 0; i < numPlayer - 1; i++)
        {
            try {     
                GameInterface game = (GameInterface) Naming.lookup(this.otherPlayers.get(i).getRMI());
                System.out.println("pre remoteMoveDone" + this.otherPlayers.get(i).getRMI());
                game.remoteMoveDone(player, prisoner, rowOrCol, row, col);
                System.out.println("remoteMoveDone");
            } catch (NotBoundException | MalformedURLException | RemoteException ex) {
                Logger.getLogger(GameImpl.class.getName()).log(Level.SEVERE, null, ex);
                notDone.add(i);
            }
//            } catch (UnknownHostException e) {
//                Logger.getLogger(GameImpl.class.getName()).log(Level.SEVERE, null, e);
//            }
            
        }
       
        for(int i : notDone)
        {
            int j = 0;
            while(j == 0)
            {
                try {
                    sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(GameImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {     
                    GameInterface game = (GameInterface) Naming.lookup(this.otherPlayers.get(i).getRMI());
                    System.out.println("pre remoteMoveDone" + this.otherPlayers.get(i).getRMI());
                    game.remoteMoveDone(player, prisoner, rowOrCol, row, col);
                    System.out.println("remoteMoveDone");
                    j = 1;
                } catch (NotBoundException | MalformedURLException | RemoteException ex) {
                    Logger.getLogger(GameImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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
        ArrayList<Alcatraz> alcatraz = new ArrayList<>();
        this.lobby = lobby;
        this.player = player;
        

        this.numPlayer = this.lobby.getCurrentPlayers();
        System.out.println("numPlayer = " + numPlayer);
        
        //Create alcatraz Interface for each player
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
            //Fill "otherplayer Array with other players
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

    private static class nested {

        public nested() {
        }
    }
}
