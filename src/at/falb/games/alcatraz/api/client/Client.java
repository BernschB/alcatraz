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
import java.net.InetAddress;
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
    private boolean started = false;

    public Client() throws RemoteException {
    }

    Properties props = new Properties();
    static int numberServers = 0;
    static ArrayList<String> serverIPs = new ArrayList<String>();
    private Lobby lobby = new Lobby();

    public static void main(String[] args) throws IOException {
        String host = InetAddress.getLocalHost().getHostAddress();
//        String host = "192.168.5.1";

        ArrayList<ServerInterface> s = new ArrayList<>();
        InputStreamReader isr;
        BufferedReader br;
        boolean checkInput = false;
        boolean end = false;
        int maxPlayer = 2;

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

            do {
                System.out.println("Was möchten Sie tun?");
                System.out.println("[1] Neuen User erstellen");
                System.out.println("[2] Aktuellen User Abmelden");
                System.out.println("[3] Programm beenden");
                isr = new InputStreamReader(System.in);
                br = new BufferedReader(isr);
                try {
                    doAction = Integer.parseInt(br.readLine());
                    if (doAction > 0 && doAction < 4) {
                        checkInput = true;
                    } else {
                        System.out.println("Bitte geben Sie 1,2 oder 3 ein!");
                    }
                } catch (IOException | NumberFormatException ex) {
                    System.out.println("Bitte geben Sie 1,2 oder 3 ein!");
//                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            } while (!checkInput);
            checkInput = false;

            switch (doAction) {
                case 1:
                    System.out.println("Geben Sie ihren Usernamen ein!");
                    isr = new InputStreamReader(System.in);
                    br = new BufferedReader(isr);

                    player.setUsername(br.readLine());

                    do {
                        System.out.println("Geben Sie ihre maximale Spielerzahl ein! 2 bis 4");
                        isr = new InputStreamReader(System.in);
                        br = new BufferedReader(isr);
                        try {
                            maxPlayer = Integer.parseInt(br.readLine());
                            if (maxPlayer > 1 && maxPlayer < 5) {
                                checkInput = true;
                            } else {
                                System.out.println("Bitte geben Sie 2,3 oder 4 ein!");
                            }
                        } catch (IOException | NumberFormatException ex) {
                            System.out.println("Bitte geben Sie 2,3 oder 4 ein!");
                            //                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } while (!checkInput);

                    player.setMaxPlayers(maxPlayer);

                    //RMI Address for Client
                    player.setRMI("rmi://" + host + ":10099/".concat(player.getUsername()));
                    player.setIP(host);
//                    player.setRMI("rmi://localhost:10099/".concat(player.getUsername()));

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

                    player.setRMI("rmi://" + host + ":10099/".concat(player.getUsername()));
                    player.setIP(host);
//                    player.setRMI("rmi://localhost:10099/".concat(player.getUsername()));

                    //Actual Logout process
                    boolean logoutWorks = false;
                    while (!logoutWorks) {
                        System.out.println("!logoutWorks");
                        try {
                            s.get(0).logoutClient(player);
                            logoutWorks = true;
                            System.out.println("Client logged out");
                        } catch (RemoteException ex) {
                            for (ServerInterface server : s) {
                                System.out.println("alle Server" + server);
                            }
                            System.out.println("alle Server" + s);
                            for (ServerInterface server : s) {
                                System.out.println("ServerInterface server : s" + server);
                                try {
                                    server.logoutClient(player);
                                    logoutWorks = true;
                                    System.out.println("server.logoutClient(player); logoutWorks");
                                } catch (RemoteException e) {
                                    System.out.println("server.logoutClient(player);");
                                }
                            }
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("User konnte nicht gelöscht werden");
                            break;
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
        System.out.println("bevor if started " + started);
        if(started == false)
        {
            started = true;
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
            System.out.println("Setzt started auf true " + started);
        }
        else
        {
            System.out.println("started" + started);
        }

        return 0;
    }

    @Override
    public void playerExists() throws RemoteException {
        System.out.println("Der von Ihnen gewählte Spielername ist schon vergeben!");
    }

}
