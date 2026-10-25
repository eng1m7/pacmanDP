import javax.swing.JFrame;

public class GameFacade {

    private JFrame frame;
    private PacMan pacmanGame;

    public GameFacade() {
        frame = new JFrame("PacMan");
        pacmanGame = PacMan.getInstance();
    }

    public void startGame() {
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(pacmanGame);
        frame.pack();
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
        pacmanGame.requestFocusInWindow();
    }
}
