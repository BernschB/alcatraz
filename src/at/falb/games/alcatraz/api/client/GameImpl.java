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
import java.io.IOException;
import java.io.Serializable;
import static java.lang.Thread.sleep;
import java.net.InetAddress;
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
        otherMoveDone(player, prisoner, rowOrCol, row, col);
        System.out.println("Before myMoveDone");
        myMoveDone(player, prisoner, rowOrCol, row, col);
        System.out.println("after myMoveDone");
    }

    public void myMoveDone(at.falb.games.alcatraz.api.Player player, Prisoner prisoner, int rowOrCol, int row, int col) {
        System.out.println("moving " + prisoner + " to " + (rowOrCol == Alcatraz.ROW ? "row" : "col") + " " + (rowOrCol == Alcatraz.ROW ? row : col));
    }

    public void otherMoveDone(at.falb.games.alcatraz.api.Player player, Prisoner prisoner, int rowOrCol, int row, int col) {// throws InterruptedException {
        System.out.println("otherMoveDone");
        System.out.println(this.otherPlayers.get(0).getRMI());
        ArrayList<Integer> notDone = new ArrayList<Integer>();

        for (int i = 0; i < numPlayer - 1; i++) {

            System.out.println("this.otherPlayers.get(" + i + ").getRMI()" + this.otherPlayers.get(i).getRMI());
            System.out.println("Versuche Zug auf (" + this.otherPlayers.get(i).getUsername() + " auszuführen");
            try {
                GameInterface game = (GameInterface) Naming.lookup(this.otherPlayers.get(i).getRMI());
                System.out.println("pre remoteMoveDone" + this.otherPlayers.get(i).getRMI());
                game.remoteMoveDone(player, prisoner, rowOrCol, row, col);
                System.out.println("remoteMoveDone");
            } catch (NotBoundException | MalformedURLException | RemoteException ex) {
                System.out.println("notDone.add(i)");
                //               Logger.getLogger(GameImpl.class.getName()).log(Level.SEVERE, null, ex);
                notDone.add(i);
            }
//            } catch (UnknownHostException e) {
//                Logger.getLogger(GameImpl.class.getName()).log(Level.SEVERE, null, e);
//            }

        }
        System.out.println("Before notDone");
        for (int i : notDone) {
            int k = 0;
            int j = 0;
            System.out.println("in notDone j = " + j + " k = " + k);
            while (j == 0) {
                k++;
                System.out.println("in notDoneWhile" + j);
                while (true) {
                    try {
                        if (InetAddress.getByName(this.otherPlayers.get(i).getIP()).isReachable(2000) == true) {
                            System.out.println("Ping to " + this.otherPlayers.get(i).getIP() + " OK");
                            break;
                        }
                    } catch (java.net.UnknownHostException ex) {
                        Logger.getLogger(GameImpl.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(GameImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("Ping to " + this.otherPlayers.get(i).getIP() + " not OK");
                }

                try {
                    GameInterface game = (GameInterface) Naming.lookup(this.otherPlayers.get(i).getRMI());
                    System.out.println("pre remoteMoveDone" + this.otherPlayers.get(i).getRMI());
                    game.remoteMoveDone(player, prisoner, rowOrCol, row, col);
                    System.out.println("remoteMoveDone");
                    j = 1;
                } catch (NotBoundException | MalformedURLException | RemoteException ex) {
                    System.out.println("remoteMoveDoneCatch");
//                    Logger.getLogger(GameImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public void remoteMoveDone(at.falb.games.alcatraz.api.Player player, Prisoner prisoner, int rowOrCol, int row, int col) throws RemoteException {

//        System.out.println(numPlayer);
        for (int i = 0; i < numPlayer; i++) {
//            if((player.getName().equals(this.lobby.getListOfPlayers().get(i).getUsername())))
//            {
//                System.out.println("in for " + i);
//                System.out.println("Player Name" + player.getName());
//                System.out.println("Player " + player);
//                System.out.println("Player other" + other[i].getPlayer(i));
//                System.out.println("Player ID" + player.getId());
//                System.out.println("Prisoner ID" + prisoner.getId());
//                System.out.println("Player " + other[i].getPlayer(player.getId()).getName());
//                System.out.println("Prisoner " + other[i].getPrisoner(prisoner.getId()));             
//                System.out.println("Start !player.equals(this.lobby.getListOfPlayers().get(i)) == true");
//                other[i].doMove(other[i].getPlayer(player.getId()), other[i].getPrisoner(prisoner.getId()), rowOrCol, row, col);   
            other[i].doMove(player, prisoner, rowOrCol, row, col);
//                System.out.println(" End !player.equals(this.lobby.getListOfPlayers().get(i)) == true");
//            }
//            System.out.println("!player.equals(this.lobby.getListOfPlayers().get(i)) == true i = " + i);
        }
//        System.out.println("remote Move Done moving " + prisoner + " to " + (rowOrCol == Alcatraz.ROW ? "row" : "col") + " " + (rowOrCol == Alcatraz.ROW ? row : col));
    }

    public void undoMove() {
        System.out.println("Undoing move");
    }

    @Override
    public void gameWon(at.falb.games.alcatraz.api.Player player) {
        System.out.println("Player " + player.getId() + " wins.");
    }

    @Override
    public void startGame(Lobby lobby, Player player) throws RemoteException {
        ArrayList<Alcatraz> alcatraz = new ArrayList<>();
        this.lobby = lobby;
        this.player = player;

        this.numPlayer = this.lobby.getCurrentPlayers();
        System.out.println("numPlayer = " + numPlayer);

        //Create alcatraz Interface for each player
        for (int i = 0; i < numPlayer; i++) {
            alcatraz.add(new Alcatraz());
            alcatraz.get(i).init(numPlayer, this.lobby.getListOfPlayers().get(i).getID());
            System.out.println("alcatraz Player = " + this.lobby.getListOfPlayers().get(i).getUsername());
            System.out.println("alcatraz PlayerID = " + this.lobby.getListOfPlayers().get(i).getID());
        }

        for (int i = 0; i < numPlayer; i++) {
            for (int j = 0; j < numPlayer; j++) {
                alcatraz.get(i).getPlayer(j).setName(this.lobby.getListOfPlayers().get(j).getUsername());
            }
        }

        for (int i = 0; i < numPlayer; i++) {
            System.out.println("setOther i = " + i);
            this.setOther(i, alcatraz.get((i + 1) % numPlayer));
        }

        for (int i = 0; i < numPlayer; i++) {
            //Fill "otherplayer Array with other players
            if (!(player.equals(this.lobby.getListOfPlayers().get(i)))) {
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

//    private static class nested {
//
//        public nested() {
//        }
//    }
}
