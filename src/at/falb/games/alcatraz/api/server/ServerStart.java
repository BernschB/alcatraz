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
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
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
    private ArrayList<Lobby> lobby = new ArrayList<Lobby>();
    ArrayList<String> serverIPs = new ArrayList<String>();
    Properties props = new Properties();
    String[] rmis;
    SpreadConnection c = new SpreadConnection();

    //Konstruktor für den Server. 
    //Er bekommt den individuellen Namen jedes Spread-Gruppenmitglieds (also der Server)
    //Den Host auf dem der Spread Deamon läuft wurden. In unserem Fall Localhost (könnte da auch "null" übergeben, dann wird auch der Localhost verwendet)
    //Und die Portnummer --> Default Port wenn 0 (4803)
    public ServerStart(String privateName, String host, int Port, String spreadGroupName) throws SpreadException, RemoteException, IOException {

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
                continue;
            } catch (MalformedURLException ex) {
                Logger.getLogger("MalformedURL means serious shit" + ServerStart.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        System.out.println("Join to \"" + group.toString() + "\" successful. Startup complete.");

        //********************** TEST LOBBY UND PLAYER FÜR MULTICATING **************************
        //Test Lobby zum Synchen
        //Player die Lobby Beitreten
        Player phteven = new Player();
        phteven.setID(0);
        phteven.setRMI("rmi://shiiieeet");
        phteven.setUsername("Phteven");

        System.out.println(phteven.toString());

        SpreadMessage message = new SpreadMessage();
        message.setObject(phteven);
        message.addGroup(spreadGroupName);
        message.setReliable();

        try {
            con.multicast(message);
        } catch (SpreadException e) {
            LOG.info("Could not join Spread Group: " + e.getMessage().toString());
        }

        //******************************** ENDE DES TESTBLOCKS ************************************
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

    //--------------AdvancedMessageListenerMethoden--------------------
    @Override
    public void regularMessageReceived(SpreadMessage sm) {
        try {
            System.out.println("New message from " + sm.getSender());
            System.out.println(sm.getObject().toString());
            String whichClass = sm.getObject().getClass().toString();
            ServerImpl si = new ServerImpl();

            if (whichClass.contains("Player")) {
                Player player = (Player) sm.getObject();
                if (player.getUsername().startsWith("Login")) {
                    lobby = si.realLogin(player, lobby);
                } else if (player.getUsername().startsWith("Logout")) {
                    lobby = si.realLogout(player, lobby);

                }
            }

        } catch (SpreadException ex) {
            Logger.getLogger(ServerStart.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerStart.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerStart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage sm) {
        //throw new UnsupportedOperationException("membershipMessageReceived: Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.println("New membership message from " + sm.getMembershipInfo().getGroup());
    }

}
