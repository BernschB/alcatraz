/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.server;

import at.falb.games.alcatraz.api.common.Lobby;
import at.falb.games.alcatraz.api.common.Player;
import at.falb.games.alcatraz.api.common.Server;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
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
public class ServerStart implements AdvancedMessageListener, Remote {

    private final static Logger LOG = Logger.getLogger(ServerStart.class.getName());

    //Konstruktor für den Server. 
    //Er bekommt den individuellen Namen jedes Spread-Gruppenmitglieds (also der Server)
    //Den Host auf dem der Spread Deamon läuft wurden. In unserem Fall Localhost (könnte da auch "null" übergeben, dann wird auch der Localhost verwendet)
    //Und die Portnummer --> Default Port wenn 0 (4803)
    public ServerStart(String privateName, String host, int Port, String spreadGroupName) throws SpreadException {

        
        SpreadConnection con = new SpreadConnection();

        System.out.println("Joining the Spread Group \"" + spreadGroupName + "\"");

        try {
            con.add(this);
            con.connect(InetAddress.getByName(host), Port, privateName, false, true); //der 4. Parameter ist für priority connections da, der vierte für GroupMembership.
        } catch (SpreadException e) {
            LOG.info("Spread Connection couldn't be established: " + e.getMessage().toString());
        } catch (UnknownHostException ex) {
            Logger.getLogger("Unknown Host is unknown..." +ServerStart.class.getName()).log(Level.SEVERE, null, ex);
        }

        SpreadGroup group = new SpreadGroup();

        try {
            group.join(con, spreadGroupName);
        } catch (SpreadException e) {
            LOG.info("Could not join Spread Group: " + e.getMessage().toString());
        }
        
        /*
        try {
            Naming.rebind("rmi://localhost:1099/".concat(privateName), this);
        } catch (RemoteException ex) {
            Logger.getLogger("Remote Exception is stupid..." +ServerStart.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger("MalformedURL means serious shit" +ServerStart.class.getName()).log(Level.SEVERE, null, ex);
        }
        */

        System.out.println("Join to \"" + group.toString() + "\" successful. Startup complete.");

        //Spread Message
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
        
        //while(1 == 1){}
    }


    //--------------AdvancedMessageListenerMethoden--------------------
    @Override
    public void regularMessageReceived(SpreadMessage sm) {
        System.out.println("New message from " + sm.getSender());
    }

    @Override
    public void membershipMessageReceived(SpreadMessage sm) {
        //throw new UnsupportedOperationException("membershipMessageReceived: Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        System.out.println("New membership message from " + sm.getMembershipInfo().getGroup());
    }

}
