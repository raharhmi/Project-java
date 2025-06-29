import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Board board;
    private Timer timer;
    private Timer enemySpawner;

    private boolean buttonsAdded = false;
    private JButton playAgainButton;
    private JButton startMenuButton;
    private JButton exitButton;

    private Image gameBackground;

    private GameLauncher launcher;

    private int highScore = 0;
    private boolean scoreSaved = false;

    private Image gameOverBackground;

    public GamePanel(Board board, GameLauncher launcher) {
        this.board = board;
        this.launcher = launcher;
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.requestFocusInWindow();
        this.addKeyListener(this);

        loadHighScore();

        timer = new Timer(50, this); // بروزرسانی هر 100 میلی‌ثانیه
        timer.start();

        enemySpawner = new Timer(1000, e -> board.spawnEnemyRandom());
        enemySpawner.start();

        gameBackground = new ImageIcon("game_background.png").getImage();
        gameOverBackground = new ImageIcon("gameOver_background1.png").getImage();

//        this.setFocusable(true);
//        this.requestFocusInWindow();
//        this.requestFocus();
//        this.addKeyListener(this);

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(board.isGameOver()) {

//            g.drawImage(gameOverBackground, 0, 0, getWidth(), getHeight(), this);

            saveHighScore();
            if(!buttonsAdded){
                addGameOverButtons();
                buttonsAdded = true;
            }

            g.setColor(Color.RED);
            Font titleFont = new Font("Arial", Font.BOLD, 50);
            g.setFont(titleFont);

            String title = "GAME OVER!";
            FontMetrics fmTitle = g.getFontMetrics(titleFont);
            int titleWidth = fmTitle.stringWidth(title);
            int titleX = (getWidth() - titleWidth) / 2;
            int titleY = getHeight() / 3;
            g.drawString(title, titleX, titleY);

            Font infoFont = new Font("Arial", Font.BOLD, 24);
            g.setFont(infoFont);
            g.setColor(Color.RED);

            String playerId = "ID: " + board.getPlayer().getId();
            String scoreStr = "Score: " + board.getPlayer().getScore();
            String highScoreStr = "High Score: " + highScore;

            FontMetrics fmInfo = g.getFontMetrics(infoFont);
            int idWidth = fmInfo.stringWidth(playerId);
            int scoreWidth = fmInfo.stringWidth(scoreStr);
            int highScoreWidth = fmInfo.stringWidth(highScoreStr);

            int infoXId = (getWidth() - idWidth) / 2;
            int infoXScore = (getWidth() - scoreWidth) / 2;
            int infoXHighScore = (getWidth() - highScoreWidth) / 2;

            int lineHeight = fmInfo.getHeight();
            g.drawString(playerId, infoXId, titleY + lineHeight + 20);
            g.drawString(scoreStr, infoXScore, titleY +2 * (lineHeight + 20));
            g.drawString(highScoreStr, infoXHighScore, titleY + 3 * (lineHeight + 20));


            return;
        }

        g.drawImage(gameBackground,0,0,getWidth(),getHeight(), this);
        drawGame(g);
    }

    private void drawGame(Graphics g) {

        if(board.isGameOver()) {

            saveHighScore();

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("GAME OVER!", 120, 200);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("ID: " + board.getPlayer().getId(), 180, 260);
            g.drawString("Score: " + board.getPlayer().getScore(), 180, 290);
            g.drawString("High Score: " + highScore, 180, 320);

            return;
        }

        int cellSize = 50;
        for (Explosion explosion : board.getExplosions()){
            explosion.draw(g, cellSize);
        }
//        int gridWidth = 10 * cellSize;
//        int gridHeight = 10 * cellSize;
//
//        int gridStartX = (getWidth() - gridWidth) / 2;
//        int gridStartY = (getHeight() - gridHeight) / 2;

        for (int row = 0; row < board.getRowCount(); row++) {
            for (int col = 0; col < board.getColCount(); col++) {

                Cell cell = board.getCell(row, col);

                int x = col * cellSize;
                int y = row * cellSize;

                switch (cell.getType()) {
                    case PLAYER_SHIP -> {
                        g.setColor(Color.BLUE);
                        g.fillRect(x, y+10, cellSize, cellSize -20);
                    }
                    case ENEMY_SHIP -> {
                        g.setColor(Color.RED);
                        g.fillRect(x, y +10, cellSize, cellSize -20);
                    }
                    case BULLET -> {
                        g.setColor(Color.YELLOW);
                        g.fillOval(x + 20, y + 10, 10, 10);
                    }
                    case EXPLOSION -> {
                        g.drawImage(new ImageIcon("Sprite Effects2.gif").getImage(),
                                x, y, cellSize, cellSize, null);
                    }
                }
            }
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
//        g.drawString("Score: " + board.getPlayer().getScore(), getWidth() - 200, 30);
        String scoreText = "Score: " + board.getPlayer().getScore();
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(scoreText)) / 2;
        g.drawString(scoreText, x, 30);
    }

    private void loadHighScore() {

        String playerId = board.getPlayer().getId();
        highScore = 0;

        try (Scanner sc = new Scanner(new File("highscore.txt"))) {
            while (sc.hasNext()) {
                if (!sc.hasNext()) {
                    break;
                }
                String id = sc.next();
                if(!sc.hasNextInt()){
                    continue;
                }
                int score = sc.nextInt();
                if (id.equals(playerId)) {
                    highScore = score;
                    return;
                }
            }
        } catch (IOException e) {
            highScore = 0;
        }
    }

    private void saveHighScore(){

        String playerId = board.getPlayer().getId();
        int currentScore = board.getPlayer().getScore();
        File file = new File("highscore.txt");
        ArrayList<String> lines = new ArrayList<>();
        boolean found = false;

        try(Scanner sc = new Scanner(file)){
            while (sc.hasNext()) {
                String id = sc.next();
                int score = sc.nextInt();
                if (id.equals(playerId)) {
                    if (currentScore >= score) {
                        lines.add(id + " " + currentScore);
                    } else {
                        lines.add(id + " " + score);
                    }
                    found = true;
                } else {
                    lines.add(id + " " + score);
                }
            }
        } catch (IOException e){

        }
        if(!found){
            lines.add(playerId + " " + currentScore);
        }
        try (PrintWriter pw = new PrintWriter(file)){
            for(String line : lines){
                pw.println(line);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(board.isGameOver()){
            if(!scoreSaved){
                saveHighScore();
                scoreSaved = true;
                addGameOverButtons();
            }
            ((Timer)e.getSource()).stop();
            return;
        }
        board.updateBullets();
        board.updateEnemies();

        board.uppdateExplosions();

        int currentScore = board.getPlayer().getScore();
        if (currentScore >= highScore){
            highScore = currentScore;
        }

        repaint();

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_A  -> board.movePlayerLeft();
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D  -> board.movePlayerRight();
            case KeyEvent.VK_SPACE, KeyEvent.VK_S -> board.playerShoot();
            case KeyEvent.VK_F11 -> launcher.toggleFullScreen();
        }
    }

    private void addGameOverButtons(){
        setLayout(null);

        int buttonWidth = 200;
        int buttonHeight = 40;
        int x = getWidth() / 2 - buttonWidth / 2;
        int yStart = 350;

        playAgainButton = new JButton("Play Again");
        playAgainButton.setBounds(x, yStart, buttonWidth, buttonHeight);
        playAgainButton.addActionListener(e -> {
            SoundPlayer.play("resources/mouse_click.wav");
            launcher.startGame(board.getPlayer().getId());
        });

        startMenuButton = new JButton("Start Menu");
        startMenuButton.setBounds(x, yStart +50, buttonWidth, buttonHeight);
        startMenuButton.addActionListener(e -> {
            SoundPlayer.play("resources/mouse_click.wav");
            launcher.showStartMenu();
        });

        exitButton = new JButton("Exit");
        exitButton.setBounds(x, yStart +100, buttonWidth, buttonHeight);
        exitButton.addActionListener(e -> {
            SoundPlayer.play("resources/mouse_click.wav");
            System.exit(0);
        });

        JButton[] buttons = {
                playAgainButton, startMenuButton, exitButton};
        for (JButton btn : buttons) {
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.setForeground(Color.BLACK);

            btn.setBackground(UIManager.getColor("Button.background"));
            btn.setFont(new Font("Tahoma", Font.PLAIN, 16));

            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(Color.YELLOW);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setForeground(Color.BLACK);
                    btn.setBackground(UIManager.getColor("Button.background"));
                }
            });

            add(btn);
        }

        repaint();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}