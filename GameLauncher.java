import javax.swing.*;
import java.awt.*;

class GameLauncher{
    private JFrame frame;

    public GameLauncher() {
//        GamePanel gamePanel = new GamePanel(board);

        frame = new JFrame("Space Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setContentPane(new StartPanel(this));
        frame.setVisible(true);

    }
    public void startGame(String id){
//        System.out.println("starting game for id" + id);
        try {

            Player player = new Player(id);
//            int boardWidth = 16;
//            int boardHeight = 20;
//
//            int playerX = boardWidth / 2;
//            int playerY = boardHeight - 1;
            PlayerShip playerShip = new PlayerShip(7, 9, 1);

            Board board = new Board(15, 10, playerShip, player);

            GamePanel gamePanel = new GamePanel(board, this);

            frame.setContentPane(gamePanel);
            frame.revalidate();
            frame.repaint();
            gamePanel.requestFocusInWindow();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showStartMenu(){
        frame.setContentPane(new StartPanel(this));
        frame.revalidate();
    }


    private boolean isFullScreen = false;
    private GraphicsDevice gd = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getDefaultScreenDevice();

    public void toggleFullScreen(){
        frame.dispose();

        if(isFullScreen){
            frame.setUndecorated(false);
            gd.setFullScreenWindow(null);
        }
        else {
            frame.setUndecorated(true);
            gd.setFullScreenWindow(frame);
            frame.setSize(800, 600);

            frame.setLocationRelativeTo(null);
        }

        isFullScreen = !isFullScreen;
        frame.setVisible(true);
    }

    public boolean isFullScreenMode(){
        return isFullScreen;
    }
}