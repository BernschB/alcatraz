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

    //reads prop-file
    public ServerImpl(SpreadConnection con) throws RemoteException, FileNotFoundException, IOException {
        super();
        c = con;
        FileReader reader = new FileReader("src/at/falb/games/alcatraz/api/common/server.properties");
        props.load(reader);
        spreadGroupName = props.getProperty("group.name");

    }

    //Sends Reliable Multicast to group --> It's time to login.
    //RegularMessageReceived gets called in "ServerStart", because "c" is created in "ServerStart"
    @Override
    public int loginClient(Player player) {

        try {
            this.sendLoginMessage(player);
        } catch (UnknownHostException | SpreadException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 1;
    }

    //Gets called after all servers got the Login Request from Client
    protected LobbyList realLogin(Player player, LobbyList lob) throws NotBoundException, MalformedURLException {

        lobby = lob;
        lobby.seqNrPlus();

        System.out.println("SeqNr = " + lobby.getSeqNr());

        System.out.println("Logge spieler ein: " + player.getUsername() + " " + player.getMaxPlayers());

        for (Lobby l : lobby.getArrayList()) {
            System.out.println("Lobby existiert für " + l.getMaxPlayers() + " Spieler");
        }

        boolean newLobby = true;

        for (Lobby l : lobby.getArrayList()) {
            if (l.getMaxPlayers() == player.getMaxPlayers()) {
                //Checks if there is a player with that name in any Lobby --> unique
                if (l.getSpecificPlayer(player) == -1) {
                    System.out.println("Der Spielername existiert schon!");
                    return lobby;
                }

                //Sets ID between 0 and maxplayers-1 on the fly
                ArrayList<Integer> ids = new ArrayList<>();

                for(Player pla : l.getListOfPlayers()){
                    ids.add(pla.getID());
                }
                
                for (int i = 0; i < 4; i++){
                    if (!ids.contains(i)){
                        player.setID(i);
                        break;
                    }
                }
 

                l.addPlayer(player);

                newLobby = false;
                System.out.println("User " + player.getUsername() + " wurde zur Lobby hinzugefügt");

                //If lobby is full, startGame gets called, which initiates the start procedure on the clients
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

        //If there is no Lobby yet with the required amount of players, create it
        if (newLobby == true) {
            System.out.println("User " + player.getUsername() + " hat eine neune Lobby angelegt");
            player.setID(0);
            Lobby l = new Lobby();
            l.addPlayer(player);
            l.setMaxPlayers(player.getMaxPlayers());
            lobby.addLobby(l);
        }

        //Prints the current players in the lobby
        for (Lobby l : lobby.getArrayList()) {
            System.out.println("Aktuelle Spieler: " + l.getCurrentPlayers());
        }

        return lobby;
    }

    /**
     *
     * @param player
     */
    //Same as with login. Sends message to all Servers an then "realLogout" gest called
    @Override
    public void logoutClient(Player player) {
        try {
            this.sendLogoutMessage(player);
        } catch (UnknownHostException | SpreadException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Real Logout, all servers do this
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

    //Will not be used here
    //Spread connection is of ServerSTart
    @Override
    public void regularMessageReceived(SpreadMessage sm) {
        System.out.println("RegularMessageReceived in ServerImpl... which is weird");
    }

    @Override
    public void membershipMessageReceived(SpreadMessage sm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //Sends the LoginMessage
    public void sendLoginMessage(Player player) throws UnknownHostException, SpreadException {

        //Servers must know what todo with the received message
        //So we just concatenated "Login" at the beginning of the username
        //Servers see that, know what todo and remove "Login" from name again
        SpreadMessage message = new SpreadMessage();
        player.setUsername("Login".concat(player.getUsername()));
        message.setObject(player);
        message.addGroup(spreadGroupName);
        message.setReliable();
        try {
            System.out.println("Message wird gesendet an Gruppe :" + spreadGroupName);
            c.multicast(message);
        } catch (SpreadException e) {
            System.err.println("Could not Send Message: " + e.getMessage());
        }

    }

    public void sendLogoutMessage(Player player) throws UnknownHostException, SpreadException {

        //Servers must know what todo with the received message
        //So we just concatenated "Logou" at the beginning of the username
        //Servers see that, know what todo and remove "Logout" from name again        
        SpreadMessage message = new SpreadMessage();
        player.setUsername("Logout".concat(player.getUsername()));
        message.setObject(player);
        message.addGroup(spreadGroupName);
        message.setReliable();
        try {
            System.out.println("Message wird gesendet an Gruppe :" + spreadGroupName);
            c.multicast(message);
        } catch (SpreadException e) {
            System.err.println("Could not Send Message: " + e.getMessage());
        }

    }

    //This Method calls the Client via RMI to initiate the game to start
    public void startGame(Lobby lob) throws RemoteException, NotBoundException, MalformedURLException {

        ArrayList<Player> pl = new ArrayList(lob.getListOfPlayers());
        ArrayList<ClientInterface> ci = new ArrayList<>();
        String rmi;

        for (Player p : pl) {
            rmi = p.getRMI();
            System.out.println(rmi);
            ci.add((ClientInterface) Naming.lookup(rmi));
        }

        System.out.println("NumberPlayer: " + lob.getMaxPlayers());

        //Calls "gameStart" on every Client participating in the game
        int i = 0;
        for (ClientInterface cli : ci) {
            cli.gameStart(lob, pl.get(i));
            i++;
        }

    }

    //Method to synchronize with other servers after a reboot
    //If servers receive a hello message, they multicast their current LobbyList (including sequence number)
    //Every server then gets the most current lobby
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

    //Send the Lobby here after receiving a Hello Message
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
            System.err.println("Could not Send Message: " + e.getMessage());
        }
    }

}
