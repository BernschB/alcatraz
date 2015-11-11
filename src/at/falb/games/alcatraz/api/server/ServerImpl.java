/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.server;

import at.falb.games.alcatraz.api.common.ClientInterface;
import at.falb.games.alcatraz.api.common.Lobby;
import at.falb.games.alcatraz.api.common.LobbyList;
import at.falb.games.alcatraz.api.common.Player;
import at.falb.games.alcatraz.api.common.ServerInterface;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
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
public class ServerImpl extends UnicastRemoteObject implements ServerInterface, AdvancedMessageListener, Remote, Serializable {

    private LobbyList lobby = new LobbyList();
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
    protected LobbyList realLogin(Player player, LobbyList lob) throws NotBoundException, MalformedURLException {
        lobby = lob;
        lobby.seqNrPlus();

        System.out.println("SeqNr = " + lobby.getSeqNr());
        
        System.out.println("Logge spieler ein: " +player.getUsername() +" " +player.getMaxPlayers());
        
        for (Lobby l : lobby.getArrayList()){
            System.out.println("Lobby existiert für " +l.getMaxPlayers() +" Spieler");
        }

        boolean newLobby = true;

        for (Lobby l : lobby.getArrayList()) {
            if (l.getMaxPlayers() == player.getMaxPlayers()) {
                //Überprüft ob es in der Lobby schon einen Spieler mit dem Namen gibt
                if (l.getSpecificPlayer(player) == -1) {
                    System.out.println("Der Spielername existiert schon!");
                    return lobby;
                }
                
                player.setID(l.getCurrentPlayers());
                for (Player pla : l.getListOfPlayers()){
                    if (pla.getID() == player.getID()){
                        player.setID(player.getID()-1);
                    }
                }
                l.addPlayer(player);
                newLobby = false;
                System.out.println("User " + player.getUsername() + " wurde zur Lobby hinzugefügt");
                if (l.isFull()) {
                    System.out.println("Lobby ist jetzt voll");
                    try {
                        startGame(l);
                    } catch (RemoteException ex) {
                        Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            }
        }

        if (newLobby == true) {
            System.out.println("User " + player.getUsername() + " hat eine neune Lobby angelegt");
            player.setID(0);
            Lobby l = new Lobby();
            l.addPlayer(player);
            l.setMaxPlayers(player.getMaxPlayers());
            lobby.addLobby(l);
        }
        
        for (Lobby l : lobby.getArrayList()) {
            System.out.println("Aktuelle Spieler: " + l.getCurrentPlayers());
        }

        return lobby;
    }

    /**
     *
     * @param player
     */
    @Override
    public void logoutClient(Player player) {
        try {
            this.sendLogoutMessage(player);
        } catch (UnknownHostException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SpreadException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected LobbyList realLogout(Player player, LobbyList lob) {

        lobby = lob;
        lobby.seqNrPlus();

        System.out.println("SeqNr = " + lobby.getSeqNr());

        for (Lobby l : lobby.getArrayList()) {
            if (l.getMaxPlayers() == player.getMaxPlayers()) {
                l.removePlayer(player);
                System.out.println("User " + player.getUsername() + " wurde aus der Lobby gelöscht");
                return lobby;
            }
        }
        return lobby;
    }

    @Override
    public void regularMessageReceived(SpreadMessage sm) {
        System.out.println("RegularMessageReceived in ServerImpl... which is weird");
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

    public void sendLogoutMessage(Player player) throws UnknownHostException, SpreadException {

        //System.out.println("SendLoginMessage noch nicht supported");
        SpreadMessage message = new SpreadMessage();
        player.setUsername("Logout".concat(player.getUsername()));
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

    public void startGame(Lobby lob) throws RemoteException, NotBoundException, MalformedURLException {
        ArrayList<Player> pl = new ArrayList(lob.getListOfPlayers());

        ArrayList<ClientInterface> ci = new ArrayList<ClientInterface>();

        String rmi = null;

        for (Player p : pl) {
            rmi = p.getRMI();
            System.out.println(rmi);
            ci.add((ClientInterface) Naming.lookup(rmi));
        }

        for (ClientInterface c : ci) {
            c.gameStart(lob);
        }
    }

    @Override
    public void sendHello(String hello) throws SpreadException {
        SpreadMessage message = new SpreadMessage();
        message.setObject(hello);
        message.addGroup(spreadGroupName);
        message.setReliable();
        try {
            c.multicast(message);
        } catch (SpreadException e) {
            System.err.println("Could not Send Message: " + e.getMessage().toString());
        }
    }

    @Override
    public void sendLobby(LobbyList lob) throws SpreadException {
        System.out.println("Die Lobby die gesendet werden soll: " + lob);
        SpreadMessage message = new SpreadMessage();
        message.setObject(lob);
        message.addGroup(spreadGroupName);
        message.setReliable();
        try {
            System.out.println("Sende jetzt Lobby! :");
            c.multicast(message);
        } catch (SpreadException e) {
            System.err.println("Could not Send Message: " + e.getMessage().toString());
        }
    }

}
