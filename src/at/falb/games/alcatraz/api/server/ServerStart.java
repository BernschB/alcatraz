/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.server;

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
import java.rmi.registry.LocateRegistry;
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
public class ServerStart implements AdvancedMessageListener, Remote, Serializable {

    private final static Logger LOG = Logger.getLogger(ServerStart.class.getName());
    int numberServers = 0;
    private LobbyList lobby = new LobbyList();
    ArrayList<String> serverIPs = new ArrayList<String>();
    Properties props = new Properties();
    String[] rmis;
    SpreadConnection c = new SpreadConnection();
    String privName = null;
    String sprGroupName = null;

    //Konstruktor für den Server. 
    //Er bekommt den individuellen Namen jedes Spread-Gruppenmitglieds (also der Server)
    //Den Host auf dem der Spread Deamon läuft wurden. In unserem Fall Localhost (könnte da auch "null" übergeben, dann wird auch der Localhost verwendet)
    //Und die Portnummer --> Default Port wenn 0 (4803)
    public ServerStart(String privateName, String host, int Port, String spreadGroupName) throws SpreadException, RemoteException, IOException {
        privName = privateName;
        sprGroupName = spreadGroupName;

        //Lade Server Daten aus den server.properties file
        FileReader reader = new FileReader("src/at/falb/games/alcatraz/api/common/server.properties");
        props.load(reader);

        setServerProperties();

        SpreadConnection con = new SpreadConnection();
        System.out.println("Joining the Spread Group \"" + spreadGroupName + "\"");

        //Verbindet sich zu dem Spread Deamon, der auf jedem Server lokal laufen muss (sonst single point of failure)
        try {
            con.add(this);
            con.connect(InetAddress.getByName(host), Port, privateName, false, true); //der 4. Parameter ist für priority connections da, der vierte für GroupMembership.
        } catch (SpreadException e) {
            LOG.info("Spread Connection couldn't be established: " + e.getMessage().toString());
        } catch (UnknownHostException ex) {
            Logger.getLogger("Unknown Host is unknown..." + ServerStart.class.getName()).log(Level.SEVERE, null, ex);
        }

        c = con;
        SpreadGroup group = new SpreadGroup();

        try {
            group.join(con, spreadGroupName);
        } catch (SpreadException e) {
            LOG.info("Could not join Spread Group: " + e.getMessage().toString());
        }

        ServerImpl si = new ServerImpl(con);

        //Naming.rebind erfolgt hier auf jedem Server. 
        for (String ip : serverIPs) {
            try {
                Naming.rebind("rmi://" + ip + ":1099/".concat(privateName), si);
                System.out.println("Bound to " + ip);
            } catch (RemoteException ex) {
                System.err.println("Coudldn't bind to " + ip);
            } catch (MalformedURLException ex) {
                Logger.getLogger("MalformedURL means serious shit" + ServerStart.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        System.out.println("Join to \"" + group.toString() + "\" successful. Startup complete.");

        //Syncht sich über Hello mit den anderen Servern
        si.sendHello("hello");
    }

    //Erlernt dynamisch, wieviele Serveres gibt und welche IP diese haben.
    public void setServerProperties() {
        numberServers = Integer.parseInt(props.getProperty("server.number"));
        System.out.println("Anzahl der Server = " + numberServers);

        for (int i = 1; i <= numberServers; i++) {
            serverIPs.add(props.getProperty("server" + i + ".host"));
            System.out.println("IP vom" + i + ". Server = " + serverIPs.get(i - 1));

        }
    }

    public void startGame(Lobby lobby) {
        this.lobby.remove(lobby);
        System.out.println("Jetzt würde das Spiel starten!");

    }

    //--------------AdvancedMessageListenerMethoden--------------------
    @Override
    public void regularMessageReceived(SpreadMessage sm) {
        try {
            String whichClass = sm.getObject().getClass().toString();            
            System.out.println("New message from " + sm.getSender());


            ServerImpl si = new ServerImpl();

            //Empfange Player zum ein- und ausloggen.
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
            
            //Empfange Hello Nachrichten. Fordert die anderen Server auf ihre Lobby zu senden, um sich zu synchronisieren.
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
            
            //Empfange Die Lobbies. Falls meine Lobby einen älteren Status hat als die empfangene, date ich ab.
            else if (whichClass.contains("LobbyList")) {
                System.out.println("Lobby zum synchen empfangen.");
                LobbyList lob = new LobbyList();
                lob = (LobbyList) sm.getObject();
                if (lobby.getSeqNr() < lob.getSeqNr()) {
                    System.out.println(this.privName + " Updated its lobby.");
                    lobby = lob;
                } 
            }

        } catch (SpreadException ex) {
            Logger.getLogger(ServerStart.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerStart.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerStart.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            Logger.getLogger(ServerStart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage sm) {
        //throw new UnsupportedOperationException("membershipMessageReceived: Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.println("New membership message from " + sm.getMembershipInfo().getGroup());
    }

}
