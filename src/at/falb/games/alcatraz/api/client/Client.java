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
 * @author HTM_Campus
 */
public class Client {

    private static void setUserName(Player pl) {
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
        //int loginFlag = 0;

        //Fixer dummy player für die Registry
        Player dummy = new Player();
        
        Player player1 = new Player("Prinzi", 3);
        Player player2 = new Player("Bernsch", 3);
        Player player3 = new Player("Swagger", 3);

        s = dummy.regPlayer();

        System.out.println("Clients logged in!");

        
        //TODO: Client redet normal fix mit einem der Server. Falls der ausfällt (check durch periodic ping) wechelt er auf den nächsten. All Server sind in der Liste s.
            try {
                s.get(0).loginClient(player1);
                s.get(0).loginClient(player2);
                s.get(0).loginClient(player3);
            } catch (RemoteException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        

    }

    
}
