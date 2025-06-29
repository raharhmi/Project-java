import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class StartPanel extends JPanel {
    private JTextField idField;
    private JButton startButton,
            fullScreenButton, helpButton,
            exitButton;
    private GameLauncher launcher;
    private Image startBackground;

    public StartPanel(GameLauncher launcher) {
        this.launcher = launcher;
        startBackground = new ImageIcon("start_background.png").getImage();
        setLayout(null);
        setFocusable(true);
        setBackground(Color.BLACK);

        JLabel label = new JLabel("Enter Your ID: ");
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        label.setBounds(180, 130, 200, 30);
        add(label);

        idField = new JTextField();

        idField.setFont(new Font("Arial", Font.PLAIN, 20));
        idField.setHorizontalAlignment(JTextField.CENTER);
        idField.setBounds(150, 170, 200, 40);
        idField.addActionListener(e ->
            startGameIfValid());
        add(idField);

        startButton = new JButton("START GAME");
        startButton.setBounds(170, 230, 160, 40);
        startButton.addActionListener(e -> {
            SoundPlayer.play("resources/mouse_click.wav");
            startGameIfValid();
        });
        add(startButton);

        fullScreenButton = new JButton("FULLSCREEN");
        fullScreenButton.setBounds(170, 280, 160, 40);
        fullScreenButton.addActionListener(e -> {
            SoundPlayer.play("resources/mouse_click.wav");
            launcher.toggleFullScreen();
        });
        add(fullScreenButton);

        helpButton = new JButton("HELP");
        helpButton.setBounds(170, 330, 160, 40);
        helpButton.addActionListener(e -> {
            SoundPlayer.play("resources/mouse_click.wav");
            showHelpDialog();
        });
        add(helpButton);

        exitButton = new JButton("EXIT");
        exitButton.setBounds(170, 380, 160, 40);
        exitButton.addActionListener(e -> {
            SoundPlayer.play("resources/mouse_click.wav");
            System.exit(0);
        });
        add(exitButton);

        JButton[] buttons = {
                startButton, fullScreenButton, helpButton, exitButton};
        for (JButton btn : buttons) {
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.setForeground(Color.BLACK);

            btn.setBackground(UIManager.getColor("Button.background"));
            btn.setFont(new Font("Tahoma", Font.PLAIN, 16));
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setForeground(Color.BLACK);
                    btn.setBackground(Color.YELLOW);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setForeground(Color.BLACK);
                    btn.setBackground(UIManager.getColor("Button.background"));
                }
            });
        }
    }

    private void showHelpDialog(){
        String message =
                "Controls:\n\n" +
                        "Move Left:     A or <-- \n" +
                        "Move Right:    D or --> \n" +
                        "Shoot:         S or SPACE \n" +
                        "Toggle Fullscreen: F11 \n" +
                        "Resize Window: Use mouse (if enabled)\n" +
                        "Game Ends:     When enemy reaches the bottom\n\n" +
                        "Good luck, pilot! ";
        JOptionPane.showMessageDialog(this, message, "Game Help",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(startBackground, 0, 0, getWidth(), getHeight(), this);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String title = "SPACE GAME";
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(title)) / 2;
        g.drawString(title, x, 100);
    }

    private void startGameIfValid(){
        String id = idField.getText().trim();
        if (!id.isEmpty()){
                launcher.startGame(id);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please enter your ID first.");
        }
    }

    @Override
    public void doLayout(){
        super.doLayout();
        if (launcher.isFullScreenMode()) {
            idField.setBounds(getWidth() / 2 - 100, getHeight() / 2 - 120, 200, 40);
            int btnWidth = 160, btnHeight = 40, spacing = 10;
            int startY = getHeight() / 2 - 60;

            JButton[] buttons = {
                    startButton, fullScreenButton, helpButton, exitButton};
            for (int i = 0; i < buttons.length; i++) {
                buttons[i].setBounds(getWidth() / 2 - btnWidth / 2, startY + i * (btnHeight + spacing), btnWidth, btnHeight);
            }
        }
            else{
                idField.setBounds(150, 170, 200, 40);
                startButton.setBounds(170, 230, 160, 40);
                fullScreenButton.setBounds(170, 280, 160, 40);
                helpButton.setBounds(170, 330, 160, 40);
                exitButton.setBounds(170, 380, 160, 40);
        }
    }
}