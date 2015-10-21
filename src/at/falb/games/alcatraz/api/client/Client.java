/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.client;
import at.falb.games.alcatraz.api.common.Player;
import at.falb.games.alcatraz.api.common.Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Thread.sleep;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author HTM_Campus
 */
public class Client {
    private static void setUserName(Player pl)
    {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        System.out.print("Bitte geben Sie ihren Usernamen ein: ");
        String username = null;
        try {
            username = br.readLine();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        pl.setUsername(username);
        System.out.println("Ihr Uername lautet " + username);
    }
    public static void main(String[] args) {
        Player player1 = new Player();
        Player player2 = new Player();
        player1.setID(1);
        player1.setUsername("Bernsch");
        player1.setMaxPlayers(3);
        
        player2.setID(2);
        player2.setUsername("Prinzi");
        player2.setMaxPlayers(3);
        try {
           // Player pl = (ServerImpl)Naming.lookup("rmi://localhost:1099/Prinzi");
            Server s = (Server) Naming.lookup("rmi://localhost:1099/Prinzi");
            //setUserName(pl);
            //System.out.print(pl.getUsername());
            s.loginClient(player1);
            s.loginClient(player2);
            s.logoutClient(player2);
            //System.out.print(s.loginClient(player1));
            
        } catch (NotBoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}