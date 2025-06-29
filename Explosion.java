import java.awt.*;

public class Explosion {
    private int x, y;
    private Image explosionGif;
    private long startTime;
    private long duration = 500;

    public Explosion(int x, int y, Image explosionGif){
        this.x = x;
        this.y = y;
        this.explosionGif = explosionGif;
        this.startTime = System.currentTimeMillis();
    }

    public boolean isFinished(){
        return
                System.currentTimeMillis() - startTime > duration;
    }

    public void draw(Graphics g, int cellSize){
        g.drawImage(explosionGif, x * cellSize, y * cellSize, cellSize, cellSize, null);
    }
}
