/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.server;

import at.falb.games.alcatraz.api.common.Lobby;
import at.falb.games.alcatraz.api.common.Player;
import at.falb.games.alcatraz.api.common.Server;
import java.io.InterruptedIOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import spread.*;
import spread.AdvancedMessageListener;

/**
 *
 * @author stefanprinz
 */
public class ServerImpl implements Server {

    private final static Logger LOG = Logger.getLogger(ServerImpl.class.getName());

    //Konstruktor für den Server. 
    //Er bekommt den individuellen Namen jedes Spread-Gruppenmitglieds (also der Server)
    //Den Host auf dem der Spread Deamon läuft wurden. In unserem Fall Localhost (könnte da auch "null" übergeben, dann wird auch der Localhost verwendet)
    //Und die Portnummer --> Default Port wenn 0 (4803)
    public ServerImpl(String privateName, String host, int Port, String spreadGroupName) throws UnknownHostException, SpreadException {

        SpreadConnection con = new SpreadConnection();
        
        try {
            con.connect(InetAddress.getByName(host), Port, privateName, false, true); //der 4. Parameter ist für priority connections da, der vierte für GroupMembership.
        } catch (SpreadException e) {
            LOG.info("Spread Connection couldn't be established: " + e.getMessage().toString());
        }

        SpreadGroup group = new SpreadGroup();

        try {
            group.join(con, spreadGroupName);
        } catch (SpreadException e) {
            LOG.info("Could not join Spread Group: " + e.getMessage().toString());
        }

        System.out.println(group.toString());
        
        //Erstelle einen Player zum Testen der Übertragung über Spread
        Player player = new Player();
        player.setID(5);
        player.setRMI("rmi://shiiieeet");
        player.setUsername("Phteven");
        

        SpreadMessage message = new SpreadMessage();
        message.setObject(player);
        message.addGroup(spreadGroupName);
        message.setReliable();

        try {
            con.multicast(message);
        } catch (SpreadException e) {
            LOG.info("Could not join Spread Group: " + e.getMessage().toString());
        }
        

        try {
            SpreadMessage msg = con.receive();
            //Player pl = new Player();
            //System.out.println("getObjekt: " + msg.getObject());
            //Player pl = (Player) msg.getObject();
            
            if (msg.isRegular()) {
                System.out.println("New message from " + msg.getSender() +"Message is: " +msg.toString());
            } else {
                System.out.println("New membership message from " + msg.getMembershipInfo().getGroup() +" Message is: "+ msg.getData());
            }
        } catch (SpreadException e) {
            LOG.info("SpreadException: " + e.getMessage().toString());
        } catch (InterruptedIOException e) {
            LOG.info("InterruptedIOException: " + e.getMessage().toString());
        }

    }

    @Override
    public void loginClientClient(Player player) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void logoutClient(Player player) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void startGame(Lobby lobby) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //Erstellt eine Lobby mit der gewünschten Anzahl der Spieler
    private static Lobby createLobby(int numbPlayer) {
        Lobby lobby = new Lobby(numbPlayer);
        return lobby;
    }


}
