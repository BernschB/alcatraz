/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.common;

import at.falb.games.alcatraz.api.Prisoner;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author HTM_Campus
 */
public interface GameInterface extends Remote {
    
    public void startGame(Lobby lobby, Player myPlayer) throws RemoteException;
    
    public void remoteMoveDone(at.falb.games.alcatraz.api.Player player, Prisoner prisoner, int rowOrCol, int row, int col) throws RemoteException;
    
//    public void otherMoveDone(at.falb.games.alcatraz.api.Player player, Prisoner prisoner, int rowOrCol, int row, int col);    

//    public void myMoveDone(at.falb.games.alcatraz.api.Player player, Prisoner prisoner, int rowOrCol, int row, int col);

}
