/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.common;

import at.falb.games.alcatraz.api.server.ServerImpl;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stefanprinz
 */
public class Player implements Serializable {

    private int ID;
    private String username;
    private int maxPlayers;
    private String RMI;     //Hätte mir gedacht, die RMI der Clients wird ihnen vom Server übergeben. Also die Clients binden sich nicht selbst, sondern bekommen zur Laufzeit von den Servern eine RMI Adresse zugewisesn.
    ArrayList<ServerInterface> s = new ArrayList<ServerInterface>();

    public Player() {
    }

    public Player(String username, int maxPlayers) {
        if (maxPlayers > 1 && maxPlayers < 5) {
            this.username = username;
            this.maxPlayers = maxPlayers;
        } else {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, "Wrong maxPlayers");
        }
    }

    /**
     * @return the ID
     */
    public int getID() {
        return ID;
    }

    /**
     * @param ID the ID to set
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the RMI
     */
    public String getRMI() {
        return RMI;
    }

    /**
     * @param RMI the RMI to set
     */
    public void setRMI(String RMI) {
        this.RMI = RMI;
    }

    /**
     * @return the ID
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * @param ID the ID to set
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    
    public String toString(){
        return "Spielername: " +this.getUsername();
    }

    public ArrayList<ServerInterface> regPlayer(int numberServers, ArrayList<String> serverIPs) throws FileNotFoundException, IOException, NotBoundException {
        String[] rmis = null;
        String regIP = "0.0.0.0";

        //Get all Server RMIs from RMI-Registry 
        //If first Registry is not available, go to next
        //Then bind to all Servers in ArrayList<Server>
        for (String ip : serverIPs) {
            try {
                rmis = LocateRegistry.getRegistry(ip).list();
                System.out.println("Alle gefundenen RMI-Hosts auf der Registry: " + Arrays.toString(rmis));
                regIP = ip;
                break;
            } catch (RemoteException ex) {
                continue;
            }
        }
        
        for (int i = 0; i < Array.getLength(rmis); i++){
            System.out.println("Bind to host:" +rmis[i]);
            s.add((ServerInterface) Naming.lookup("rmi://" + regIP + ":1099/".concat(rmis[i])));
        }

        return s;

    }
    
    public void regMyRMI(){
        String rmi = this.getRMI();
        ServerImpl si = null;
        try {
            si = new ServerImpl();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for (ServerInterface sInt : s){
            try {
                Naming.rebind(rmi, si);
            } catch (RemoteException ex) {
                System.out.println("Jez happats");
            } catch (MalformedURLException ex) {
                System.out.println("Jez happats");
            }
        
        }
    }

    //Erlernt dynamisch, wieviele Serveres gibt und welche IP diese haben.
}
