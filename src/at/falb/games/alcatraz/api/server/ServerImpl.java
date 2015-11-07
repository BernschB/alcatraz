/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.server;

import at.falb.games.alcatraz.api.common.Lobby;
import at.falb.games.alcatraz.api.common.Player;
import at.falb.games.alcatraz.api.common.Server;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import spread.AdvancedMessageListener;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

/**
 *
 * @author stefanprinz
 */
public class ServerImpl extends UnicastRemoteObject implements Server, AdvancedMessageListener, Remote, Serializable {

    private ArrayList<Lobby> lobby = new ArrayList<Lobby>();
    Properties props = new Properties();
    String spreadGroupName = null;
    SpreadConnection c;

    public ServerImpl() throws RemoteException, FileNotFoundException, IOException {

    }

    public ServerImpl(SpreadConnection con) throws RemoteException, FileNotFoundException, IOException {
        super();
        c = con;
        FileReader reader = new FileReader("src/at/falb/games/alcatraz/api/common/server.properties");
        props.load(reader);
        spreadGroupName = props.getProperty("group.name");

        System.out.println("Joining the Spread Group \"" + spreadGroupName + "\"");

        //Verbindet sich zu dem Spread Deamon, der auf jedem Server lokal laufen muss (sonst single point of failure)
        /*try {
         con.add(this);
         con.connect(InetAddress.getByName(host), Port, privateName, false, true); //der 4. Parameter ist für priority connections da, der vierte für GroupMembership.
         } catch (SpreadException e) {
         System.err.println("Spread Connection couldn't be established: " + e.getMessage().toString());
         } catch (UnknownHostException ex) {
         Logger.getLogger("Unknown Host is unknown..." + ServerStart.class.getName()).log(Level.SEVERE, null, ex);
         }

         SpreadGroup group = new SpreadGroup();

         try {
         group.join(con, spreadGroupName);
         } catch (SpreadException e) {
         LOG.info("Could not join Spread Group: " + e.getMessage().toString());
         }*/
    }

    @Override
    public int loginClient(Player player) {

        try {
            this.sendLoginMessage(player);
        } catch (UnknownHostException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SpreadException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 1;
    }

    //Wird aufgerufen, nachdem ein Server eine Join nachricht erhalten hat (auch von sich selbst).
    protected int realLogin(Player player) {
        System.out.println("The real login Process will be done here!");
        player.setUsername(player.getUsername().substring(5));
                //TODO: Insert Multicast for active replication
        /*SpreadMessage message = new SpreadMessage();
         try {
         message.setObject(player);
         } catch (SpreadException ex) {
         Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
         }
         message.addGroup(spreadGroupName);
         message.setReliable();
        
         try {
         con.multicast(message);
         } catch (SpreadException e) {
         LOG.info("Could not join Spread Group: " + e.getMessage().toString());
         }
         */

        boolean newLobby = true;
        for (int i = 0; i < this.lobby.size(); i++) {
            if (this.lobby.get(i).getMaxPlayers() == player.getMaxPlayers()) {
                this.lobby.get(i).addPlayer(player);
                newLobby = false;
                System.out.println("User " + player.getUsername() + " wurde zur Lobby " + i + " hinzugefügt");
                if (this.lobby.get(i).isFull()) {
                    System.out.println("Lobby ist voll");
                    startGame(this.lobby.get(i));
                }
                break;
            }
        }
        if (newLobby == true) {
            System.out.println("User " + player.getUsername() + " hat eine neune Lobby angelegt");
            this.lobby.add(new Lobby(player));
        }

        return 1;

    }

    /**
     *
     * @param player
     */
    @Override
    public void logoutClient(Player player) {
        for (int i = 0; i < this.lobby.size(); i++) {
            for (int j = 0; j < this.lobby.get(i).getCurrentPlayers(); j++) {
                if (this.lobby.get(i).getPlayer().equals(player.getUsername())) {
                    this.lobby.get(i).removePlayer(player);
                    System.out.println("User " + player.getUsername() + " wurde aus der Lobby " + i + " gelöscht");
                    break;
                }
            }

        }
        //       throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void startGame(Lobby lobby) {
        this.lobby.remove(lobby);
        System.out.println("Jetzt würde das Spiel starten!");
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void regularMessageReceived(SpreadMessage sm) {
        try {
            System.out.println("New message from " + sm.getSender());
            System.out.println(sm.getObject().toString());

        } catch (SpreadException ex) {
            Logger.getLogger(ServerStart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage sm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void sendLoginMessage(Player player) throws UnknownHostException, SpreadException {

        //System.out.println("SendLoginMessage noch nicht supported");
        SpreadMessage message = new SpreadMessage();
        player.setUsername("Login".concat(player.getUsername()));
        message.setObject(player);
        message.addGroup(spreadGroupName);
        message.setReliable();
        try {
            System.out.println("Message wird gesendet an Gruppe :" + spreadGroupName);
            c.multicast(message);
        } catch (SpreadException e) {
            System.err.println("Could not Send Message: " + e.getMessage().toString());
        }

    }

}
