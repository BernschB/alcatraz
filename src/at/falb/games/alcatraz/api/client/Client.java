/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.falb.games.alcatraz.api.client;


import at.falb.games.alcatraz.api.common.ClientInterface;
import at.falb.games.alcatraz.api.common.Lobby;
import at.falb.games.alcatraz.api.common.Player;
import at.falb.games.alcatraz.api.common.ServerInterface;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HTM_Campus
 */
public class Client extends UnicastRemoteObject implements ClientInterface, Remote, Serializable {

    private static int globalCounter = 0;

    public Client() throws RemoteException {
    }

    Properties props = new Properties();
    static int numberServers = 0;
    static ArrayList<String> serverIPs = new ArrayList<String>();
    private Lobby lobby = new Lobby();

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

        ArrayList<ServerInterface> s = new ArrayList<ServerInterface>();
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

        Player player = new Player();

        int doAction = 0;

        while (end == false) {

            System.out.println("Was möchten Sie tun?");
            System.out.println("[1] Neuen User erstellen");
            System.out.println("[2] Aktuellen User Abmelden");
            System.out.println("[3] Programm beenden");
            isr = new InputStreamReader(System.in);
            br = new BufferedReader(isr);
            try {
                doAction = Integer.parseInt(br.readLine());
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }

            //Next line for debugging
            //doAction = 1;
            switch (doAction) {
                case 1:
                    System.out.println("Geben Sie ihren Usernamen ein!");
                    isr = new InputStreamReader(System.in);
                    br = new BufferedReader(isr);

                    player.setUsername(br.readLine());

                    System.out.println("Geben Sie ihre maximale Spielerzahl ein!");
                    isr = new InputStreamReader(System.in);
                    br = new BufferedReader(isr);

                    player.setMaxPlayers(Integer.parseInt(br.readLine()));

                    player.setRMI("rmi://192.168.5.1:10099/".concat(player.getUsername()));
                    ClientInterface c = new Client();

                    try {
                        LocateRegistry.createRegistry(10099);
                    } catch (RemoteException ex) {

                    }

                    Naming.rebind(player.getRMI(), c);
                    player.regMyRMI();

//                    ClientInterface c = new Client();
                    try {
                        s = dummy.regPlayer(numberServers, serverIPs);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NotBoundException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    for (ServerInterface server : s) {
                        try {
                            System.out.println("Serveradresse: " + server);
                            System.out.println(server.loginClient(player));
//                        System.out.println(s.get(0).loginClient(player5));
//                        System.out.println(s.get(0).loginClient(player2));
//                        System.out.println(s.get(0).loginClient(player3));
//                        System.out.println(s.get(0).loginClient(player4));

                        } catch (RemoteException ex) {
                            //System.out.println("Höppala, server ned da");
                            //continue;
                        }
                    }
                    break;
                case 2:
                    System.out.println("Geben Sie ihren Usernamen ein!");
                    isr = new InputStreamReader(System.in);
                    br = new BufferedReader(isr);

                    player.setMaxPlayers(4);
                    player.setUsername(br.readLine());
                    player.setRMI("rmi://192.168.5.1:10099".concat(player.getUsername()));
                    //player.setMaxPlayers(4);

                    try {
                        s.get(0).logoutClient(player);
                        System.out.println("Client logged out");
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

    }

    public void setServerProperties() {
        numberServers = Integer.parseInt(props.getProperty("server.number"));
        System.out.println("Anzahl der Server = " + numberServers);

        for (int i = 1; i <= numberServers; i++) {
            serverIPs.add(props.getProperty("server" + i + ".host"));
            System.out.println("IP vom" + i + ". Server = " + serverIPs.get(i - 1));
        }
    }

    @Override
    public int gameStart(Lobby lob, Player me) throws RemoteException {
        lobby = lob;
        System.out.println("Jetzt startet das Spiel!!! whuuuhh");
        //Player me = new Player();
        Registry reg;

        //Folgende Zeilen ersetzen die Main-Routine in PlayerOne usw.
        //Erstellt einen RMI-Registry für RMI Binds auf dem Well known RMI Registry Port (10099). RMI Adressen müssen dann hier Angemeldet werden.
        System.out.println("Create RMI-Registry...");
        try {
            LocateRegistry.createRegistry(10099);
        } catch (RemoteException ex) {
        }

        try {
            GameImpl game = new GameImpl();
            game.startGame(lobby, me);
            Naming.rebind(me.getRMI(), game);
            System.out.println("Bind with: " + me.getRMI() + "           ok");

        } catch (RemoteException | MalformedURLException ex) {
            System.out.println("shiet");
        }

        return 0;
    }

}
