/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author stefanprinz
 */
public interface ClientInterface extends Remote {
    
    public int gameStart(Lobby lobby) throws RemoteException;

}