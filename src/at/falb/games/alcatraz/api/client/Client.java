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
import java.util.ArrayList;
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
        
        ArrayList<Server> s = new ArrayList<Server>();
        
        Player player1 = new Player("Prinzi", 3);
        Player player2 = new Player("Bernsch", 3);
        Player player3 = new Player("Swagger", 3);
                       
        s = player1.regPlayer();
        //player2.regPlayer();
        //player3.regPlayer();
        //System.out.print(s.loginClient(player1));
        
        //ie Clients melden sich bei jedem Server an.
        for (Server serv : s){
            try {
                serv.loginClient(player1);
                serv.loginClient(player2);
                serv.loginClient(player3);
            } catch (RemoteException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public void init(Player player){
        
    }
}