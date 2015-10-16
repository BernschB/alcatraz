/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.server;

import at.falb.games.alcatraz.api.common.Lobby;
import at.falb.games.alcatraz.api.common.Player;
import at.falb.games.alcatraz.api.common.Server;
import spread.*;

/**
 *
 * @author stefanprinz
 */
public class ServerImpl implements Server {

    public ServerImpl(String spreadName, String host, int Port){
        
        SpreadConnection con = new SpreadConnection();
        
        
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
    
}
