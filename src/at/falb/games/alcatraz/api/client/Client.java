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

    public static void main(String[] args) throws IOException {

        ArrayList<ServerInterface> s = new ArrayList<>();
        InputStreamReader isr;
        BufferedReader br;
        boolean end = false;

        Client client = new Client();

        FileReader reader = new FileReader("src/at/falb/games/alcatraz/api/common/server.properties");
        client.props.load(reader);

        client.setServerProperties();

        //Fixed dummy player for registration
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

                    //RMI Address for Client
                    player.setRMI("rmi://localhost:10099/".concat(player.getUsername()));
                    ClientInterface c = new Client();

                    //Try to create a Registry. If there is already a registry, continue
                    try {
                        LocateRegistry.createRegistry(10099);
                    } catch (RemoteException ex) {

                    }

                    //Bind client to local registry
                    Naming.rebind(player.getRMI(), c);
                    player.regMyRMI();

                    //Client meet Servers here
                    try {
                        s = dummy.regPlayer(numberServers, serverIPs);
                    } catch (FileNotFoundException | NotBoundException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //Actual Login Process
                    boolean loginWorks = false;
                    while (!loginWorks) {
                        try {
                            System.out.println("Das sollt nur einmal da stehn: ");
                            System.out.println(s.get(0).loginClient(player));
                            loginWorks = true;
                        } catch (RemoteException ex) {
                            //Wenn der erste nicht erreichbar ist, probiere die Anderen Server
                            for (ServerInterface server : s) {
                                try {
                                    server.loginClient(player);
                                    loginWorks = true;
                                } catch (RemoteException e) {
                                }
                            }
                        }
                    }

                    break;

                case 2:
                    System.out.println("Geben Sie ihren Usernamen ein!");
                    isr = new InputStreamReader(System.in);
                    br = new BufferedReader(isr);
                    player.setUsername(br.readLine());

                    player.setRMI("rmi://localhost:10099".concat(player.getUsername()));

                    //Actual Logout process
                    boolean logoutWorks = false;
                    while (!logoutWorks) {
                        try {
                            s.get(0).logoutClient(player);
                            logoutWorks = true;
                            System.out.println("Client logged out");
                        } catch (RemoteException ex) {
                            for (ServerInterface server : s) {
                                try {
                                    server.logoutClient(player);
                                    logoutWorks = true;
                                } catch (RemoteException e) {
                                }
                            }
                        }
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
