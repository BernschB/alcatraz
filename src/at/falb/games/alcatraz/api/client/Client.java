/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.client;

import at.falb.games.alcatraz.api.common.Player;
import at.falb.games.alcatraz.api.common.Server;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HTM_Campus
 */
public class Client {

    Properties props = new Properties();
    static int numberServers = 0;
    static ArrayList<String> serverIPs = new ArrayList<String>();

    // CLI für die erstellung eines Users
    @SuppressWarnings("empty-statement")
    private static Player setUserName() {
        Player player;
        InputStreamReader isr;
        BufferedReader br;
        player = new Player();

        String username = null;
        int maxPlayers = 0;
        while (username == null);
        {
            isr = new InputStreamReader(System.in);
            br = new BufferedReader(isr);
            System.out.print("Bitte geben Sie ihren Usernamen ein: ");

            try {
                username = br.readLine();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        while (maxPlayers < 2 && maxPlayers > 4) {
            isr = new InputStreamReader(System.in);
            br = new BufferedReader(isr);
            System.out.print("Bitte geben Sie die gewünschte Spieleranzahl an 2-4:");

            try {
                maxPlayers = Integer.parseInt(br.readLine());
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        player.setUsername(username);
        player.setMaxPlayers(maxPlayers);

        System.out.println("Ihr Uername lautet " + username + " und Sie spielen mit " + maxPlayers + " Spielern.");
        return player;

    }

    public static void main(String[] args) throws IOException {

        ArrayList<Server> s = new ArrayList<Server>();
        InputStreamReader isr;
        BufferedReader br;
        boolean end = false;

        Client client = new Client();

        //Lade Server Daten aus den server.properties file
        FileReader reader = new FileReader("src/at/falb/games/alcatraz/api/common/server.properties");
        client.props.load(reader);

        client.setServerProperties();

        //Fixer dummy player für die Registry
        Player dummy;
        dummy = new Player();

        Player player = null;

        s = dummy.regPlayer(numberServers, serverIPs);

        int doAction = 0;

        while (end == false) {
            System.out.print("Was möchten Sie tun?\n");
            System.out.print("[1] Neuen User erstellen\n");
            System.out.print("[2] Aktuellen User Abmelden\n");
            System.out.print("[3] Programm beenden\n");
            isr = new InputStreamReader(System.in);
            br = new BufferedReader(isr);
            try {
                doAction = Integer.parseInt(br.readLine());
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }

            switch (doAction) {
                case 1:
                    player = setUserName();

                    try {
                        s.get(0).loginClient(player);
                        System.out.println("Client logged in!");
                    } catch (RemoteException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case 2:
                    try {
                        s.get(0).logoutClient(player);
                    } catch (RemoteException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case 3:
                    end = true;
                    break;
                default:
                    System.out.println("Bitte geben Sie einen gültigen Wert ein");

            }
        }
        //int loginFlag = 0;

        /*
         Player player1 = new Player("Prinzi", 3);
         Player player2 = new Player("Bernsch", 3);
         Player player3 = new Player("Swagger", 3);
        
        
        
        


         System.out.println("Clients logged in!");

        
         //TODO: Client redet normal fix mit einem der Server. Falls der ausfällt (check durch periodic ping) wechelt er auf den nächsten. All Server sind in der Liste s.
         try {
         s.get(0).loginClient(player);
         s.get(0).loginClient(player1);
         s.get(0).loginClient(player2);
         s.get(0).loginClient(player3);
         } catch (RemoteException ex) {
         Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
         }
        
         */
    }

    public void setServerProperties() {
        numberServers = Integer.parseInt(props.getProperty("server.number"));
        System.out.println("Anzahl der Server = " + numberServers);

        for (int i = 1; i <= numberServers; i++) {
            serverIPs.add(props.getProperty("server" + i + ".host"));
            System.out.println("IP vom" + i + ". Server = " + serverIPs.get(i - 1));

        }
    }

}
