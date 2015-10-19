/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.common;

import java.io.Serializable;

/**
 *
 * @author stefanprinz
 */
public class Player implements Serializable {
    
    private int ID;
    private String username;
    private String RMI;

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
    
}
