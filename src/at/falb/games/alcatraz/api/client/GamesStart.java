package at.falb.games.alcatraz.api.client;


import at.falb.games.alcatraz.api.Alcatraz;
import at.falb.games.alcatraz.api.MoveListener;
import at.falb.games.alcatraz.api.Player;
import at.falb.games.alcatraz.api.Prisoner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
//HAllo
/**
 * A test class initializing a local Alcatraz game -- illustrating how
 * to use the Alcatraz API.
 */
public class GamesStart implements MoveListener {

    private Alcatraz other[] = new Alcatraz[4];
    private int numPlayer = 4;

    public GamesStart() {
    }   
    
    public GamesStart(int numPlayer) {
        this.numPlayer = numPlayer; 
    }
    
    //Sets the Position of the Other Players
    public void setOther(int i, Alcatraz t) {
        this.other[i] = t;
    }
    
    public int getNumPlayer() {
        return numPlayer;
    }

    public void setNumPlayer(int numPlayer) {
        this.numPlayer = numPlayer;
    }

    public void moveDone(Player player, Prisoner prisoner, int rowOrCol, int row, int col) {
        System.out.println("moving " + prisoner + " to " + (rowOrCol == Alcatraz.ROW ? "row" : "col") + " " + (rowOrCol == Alcatraz.ROW ? row : col));
        for (int i = 0; i < getNumPlayer() - 1; i++) {
            other[i].doMove(other[i].getPlayer(player.getId()), other[i].getPrisoner(prisoner.getId()), rowOrCol, row, col);
        }
    }

    public void undoMove() {
         System.out.println("Undoing move");
    }
    
    public void gameWon(Player player) {
        System.out.println("Player " + player.getId() + " wins.");
    }

    /**
     * @param args Command line args
     */
    public static void main(String[] args) throws IOException {
        BufferedReader br;        
        InputStreamReader isr;
        int numPlayer;
     
        ArrayList<GamesStart> t = new ArrayList<GamesStart>();
        ArrayList<Alcatraz> a = new ArrayList<Alcatraz>();
        
        System.out.print("Wieviel Spieler drüfens denn sein? 2,3 oder 4?\n");
        isr = new InputStreamReader(System.in);
        br = new BufferedReader(isr);
        
        numPlayer = Integer.parseInt(br.readLine());
        
        System.out.print("Es spielen " + numPlayer + " mit!");
        
        
        // Zuerst müsse  alle Games und Acatrac Objekte erstellt werden
        for(int i = 0; i < numPlayer; i++)
        {
//            System.out.print("i = " + i);

            t.add(new GamesStart());
            t.get(i).setNumPlayer(numPlayer);
            a.add(new Alcatraz());       
            a.get(i).init(numPlayer, i);
        }
        
        // In diesen 2 For-SChleifen werden die Namen und Positionen der Gegener gesetzt
        for(int i = 0; i < numPlayer; i++)
        {   
            int k = 0;
            for(int j = 0; j < numPlayer; j++)
            {
//                System.out.print("j = " + j);
                a.get(i).getPlayer(j).setName("Player " + j);
                if(j != i)
                {
                    t.get(i).setOther(k, a.get(j));  
                    k++;
                }
            }

        }
        
        
        //In dieser For-SChleife werden die Fenster aufgerufen 
        for(int i = 0; i < numPlayer; i++)
        {
            a.get(i).showWindow();
            a.get(i).addMoveListener(t.get(i));
            a.get(i).start();
        }
      
    }

}
