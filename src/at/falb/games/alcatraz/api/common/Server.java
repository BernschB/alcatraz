/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author stefanprinz
 */
public interface Server extends Remote {
    
    public void loginClientClient(Player player);

    public void logoutClient(Player player);
        
    public void startGame(Lobby lobby);
    
}
