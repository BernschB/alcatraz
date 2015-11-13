/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import spread.SpreadException;

/**
 *
 * @author stefanprinz
 */

//Interface used for Clients to call Servers and Servers between Servers
public interface ServerInterface extends Remote {
    
    public int loginClient(Player player) throws RemoteException;

    public void logoutClient(Player player) throws RemoteException;
    
    public void sendHello(String hello) throws SpreadException, RemoteException;
    
    public void sendLobby(LobbyList lob) throws SpreadException, RemoteException;


}
