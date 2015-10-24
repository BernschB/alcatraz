/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.common;

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
    ArrayList<Server> s = new ArrayList<Server>();

    public Player() {
    }

    public Player(String username, int maxPlayers) {
        this.username = username;
        this.maxPlayers = maxPlayers;
        
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
    
    public ArrayList<Server> regPlayer(){
        String[] rmis;
        

        //Get all Server RMIs from RMI-Registry 
        //Then bind to all Servers in ArrayList<Server>
        try {
            rmis = LocateRegistry.getRegistry("localhost").list();
            System.out.println("Alle gefundenen RMI-Hosts auf der Registry: " + Arrays.toString(rmis));

            for (int i = 0; i < Array.getLength(rmis); i++) {
                System.out.println("Bind to host: " + rmis[i]);
                s.add((Server) Naming.lookup("rmi://localhost:1099/".concat(rmis[i])));
            }
            
        } catch (RemoteException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return s;
      
    }

}
