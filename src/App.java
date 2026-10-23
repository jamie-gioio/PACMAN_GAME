
import javax.swing.JFrame;


public class App {
    public static void main(String[] args) throws Exception {
        int rowAmount = 21;
        int columnAmount = 19;
        int tileSize = 32;
        int boardWidth = tileSize * columnAmount;
        int boardHeight = tileSize * rowAmount;

        JFrame frame = new JFrame("Pac Man");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PacMan pacmanGame = new PacMan();
        frame.add(pacmanGame);
        frame.pack();
        pacmanGame.requestFocus();
        frame.setVisible(true);

        
        

        

        
        
    }
}
