/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.common;

import at.falb.games.alcatraz.api.server.ServerImpl;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
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
    ArrayList<ServerInterface> s = new ArrayList<>();

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

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    
    @Override
    public String toString(){
        return "Spielername: " +this.getUsername();
    }

    public ArrayList<ServerInterface> regPlayer(int numberServers, ArrayList<String> serverIPs) throws FileNotFoundException, IOException, NotBoundException {
        String[] rmis = null;
        String regIP = null;

        //Locate all server Registrys to not have SPOF
        for (String ip : serverIPs) {
            try {
                rmis = LocateRegistry.getRegistry(ip, 1099).list();
                System.out.println("Alle gefundenen RMI-Hosts auf der Registry: " + Arrays.toString(rmis));
                regIP =  ip;
            } catch (RemoteException ex) {}
        }
        
        //Bind to all Servers
        for (int i = 0; i < Array.getLength(rmis); i++){
            System.out.println("Bind to host:" +rmis[i]);
            try {
            s.add((ServerInterface) Naming.lookup("rmi://" + regIP + ":1099/".concat(rmis[i])));
            } catch (RemoteException ex){
                System.out.println("Whoppalaaa, namin.lookup shit. " +ex);
            }
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
                Naming.rebind(rmi, sInt);
            } catch (RemoteException | MalformedURLException ex) {
                System.out.println("Jez happats");
            }
        
        }
                
    }

    //Erlernt dynamisch, wieviele Serveres gibt und welche IP diese haben.
}
