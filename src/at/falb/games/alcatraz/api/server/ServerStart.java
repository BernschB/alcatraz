/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.server;

import at.falb.games.alcatraz.api.common.Lobby;
import at.falb.games.alcatraz.api.common.LobbyList;
import at.falb.games.alcatraz.api.common.Player;
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
public final class ServerStart implements AdvancedMessageListener, Remote, Serializable {

    int numberServers = 0;
    private LobbyList lobby = new LobbyList();
    ArrayList<String> serverIPs = new ArrayList<>();
    Properties props = new Properties();
    String[] rmis;
    SpreadConnection c = new SpreadConnection();
    String privName = null;
    String sprGroupName = null;

    //Constructor for Servers
    public ServerStart(String privateName, String host, int Port, String spreadGroupName) throws SpreadException, RemoteException, IOException {
        privName = privateName;
        sprGroupName = spreadGroupName;

        //Lade Server Daten aus den server.properties file
        FileReader reader = new FileReader("src/at/falb/games/alcatraz/api/common/server.properties");
        props.load(reader);

        setServerProperties();

        //Create Spread Connection
        SpreadConnection con = new SpreadConnection();
        System.out.println("Joining the Spread Group \"" + spreadGroupName + "\"");

        //Connects to Spread Deamon which is running locally
        try {
            con.add(this);
            con.connect(InetAddress.getByName(host), Port, privateName, false, true); //der 4. Parameter ist für priority connections da, der vierte für GroupMembership.
        } catch (SpreadException ex) {
            Logger.getLogger("Spread Connection couldn''t be established: {0}", ex.getMessage());
        } catch (UnknownHostException ex) {
            Logger.getLogger("Unknown Host is unknown..." + ServerStart.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Make the connection global
        c = con;
        SpreadGroup group = new SpreadGroup();

        //Joins spread Group
        try {
            group.join(con, spreadGroupName);
        } catch (SpreadException e) {
            Logger.getLogger("Could not join Spread Group: " + e.getMessage().toString());
        }

        ServerImpl si = new ServerImpl(con);

        try {
            Naming.rebind("rmi://" + host + ":1099/".concat(privateName), si);
            System.out.println("Bound to " + host);
        } catch (RemoteException ex) {
            System.err.println("Coudldn't bind to " + host);
        } catch (MalformedURLException ex) {
            Logger.getLogger("MalformedURL means serious shit" + ServerStart.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Join to \"" + group.toString() + "\" successful. Startup complete.");

        //To synch with Spread Group
        si.sendHello("hello");
    }

    //Read infos from server.properties
    public void setServerProperties() {
        numberServers = Integer.parseInt(props.getProperty("server.number"));
        System.out.println("Anzahl der Server = " + numberServers);

        for (int i = 1; i <= numberServers; i++) {
            serverIPs.add(props.getProperty("server" + i + ".host"));
            System.out.println("IP vom" + i + ". Server = " + serverIPs.get(i - 1));

        }
    }

  
    //--------------AdvancedMessageListenerMethoden--------------------
    @Override
    public void regularMessageReceived(SpreadMessage sm) {
        try {
            String whichClass = sm.getObject().getClass().toString();
            System.out.println("New message from " + sm.getSender());

            ServerImpl si = new ServerImpl();

            //Receive Messages from class Player to login or logout
            if (whichClass.contains("Player")) {
                Player player = (Player) sm.getObject();
                if (player.getUsername().startsWith("Login")) {
                    player.setUsername(player.getUsername().substring(5));
                    lobby = si.realLogin(player, lobby);
                } else if (player.getUsername().startsWith("Logout")) {
                    player.setUsername(player.getUsername().substring(6));
                    System.out.println("Outlog Name: " + player.getUsername());
                    lobby = si.realLogout(player, lobby);
                }
            } 
            //Receive Hello Messages. So send your lobby to other servers
            else if (sm.getObject() instanceof String) {
                System.out.println(this.privName + " hat ein hello empfangen. Lobby wird gesendet");
                SpreadMessage message = new SpreadMessage();
                message.setObject(lobby);
                message.addGroup(sprGroupName);
                message.setReliable();
                try {
                    c.multicast(message);
                } catch (SpreadException e) {
                    System.err.println("Could not Send Message: " + e.getMessage().toString());
                }
            } 
            //Receive Lobbies to Synch 
            else if (whichClass.contains("LobbyList")) {
                System.out.println("Lobby empfangen. Muss ich mich synchen?");
                LobbyList lob = new LobbyList();
                lob = (LobbyList) sm.getObject();
                if (lobby.getSeqNr() < lob.getSeqNr()) {
                    System.out.println(this.privName + " hat sich gesyncht!");
                    lobby = lob;
                } else {
                    System.out.println("Kein Synch notwendig!");
                }
            }

        } catch (SpreadException | NotBoundException ex) {
            Logger.getLogger(ServerStart.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerStart.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerStart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage sm) {
        System.out.println("New membership message from " + sm.getMembershipInfo().getGroup());
    }

}
