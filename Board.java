import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Board {

    private int width;
    private int height;
    private Cell[][] board;
    private ArrayList<EnemyShip> enemies = new ArrayList<>();;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private PlayerShip playerShip;
    private Player player;
    private boolean gameOver = false;
    private ArrayList<Explosion> explosions = new ArrayList<>();
    private Image explosionGif;

    public Board(int width, int height, PlayerShip playerShip, Player player) {
        this.width = width;
        this.height = height;
        this.playerShip = playerShip;
        this.player = player;
        explosionGif = new ImageIcon("Sprite Effects1.gif").getImage();

        board = new Cell[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                board[row][col] = new Cell(row, col, CellType.EMPTY);
            }
        }
        placePlayerShip();
    }

    public Cell getCell(int row, int col){
        return board[row][col];
    }

    public boolean isGameOver(){
        return gameOver;
    }

    public Player getPlayer() {
        return player;
    }

    private void placePlayerShip() {
        board[playerShip.getY()][playerShip.getX()].setType(CellType.PLAYER_SHIP);
    }

    public ArrayList<Explosion> getExplosions(){
        return explosions;
    }

    public void spawnEnemyRandom(){
        if(enemies.size() >= 5) {
            return;
        }

        Random rand = new Random();
        int x = rand.nextInt(width);

        if(board[0][x].getType() == CellType.EMPTY) {
            EnemyShip enemy = new EnemyShip(x, 0, 1, 1);
            enemies.add(enemy);
            board[enemy.getY()][enemy.getX()].setType(CellType.ENEMY_SHIP);
        }
    }

    public void movePlayerLeft() {
        if (playerShip.getX() > 0) {
            board[playerShip.getY()][playerShip.getX()].setType(CellType.EMPTY);
            playerShip.moveLeft();
            board[playerShip.getY()][playerShip.getX()].setType(CellType.PLAYER_SHIP);
        }
    }

    public void movePlayerRight() {
        if (playerShip.getX() < width - 1) {
            board[playerShip.getY()][playerShip.getX()].setType(CellType.EMPTY);
            playerShip.moveRight();
            board[playerShip.getY()][playerShip.getX()].setType(CellType.PLAYER_SHIP);
        }
    }

    public void playerShoot() {
        Bullet b = playerShip.shoot();
        bullets.add(b);
        SoundPlayer.play("resources/Shoot2.wav");
    }

    public void uppdateExplosions(){
        for (int i = explosions.size() -1; i >= 0 ; i--) {
            if(explosions.get(i).isFinished()){
                explosions.remove(i);
            }
        }
    }

    public void updateBullets() {
        ArrayList<Bullet> toRemove = new ArrayList<>();
        for (Bullet b : bullets) {
            if (b.getY() >= 0 && b.getY() < height && b.getX() >= 0 && b.getX() < width) {
                board[b.getY()][b.getX()].setType(CellType.EMPTY);
            }
            b.move();

            if (b.getY() < 0) {
                toRemove.add(b);
                continue;
            }

            boolean hit = false;
            for (EnemyShip e : new ArrayList<>(enemies)) {
                if (b.getX() == e.getX() && b.getY() == e.getY()) {
                    e.takeDamage(1);
                    toRemove.add(b);
                    hit = true;

                    if (e.isDestroyed()) {

                        SoundPlayer.play("resources/Sprite1.wav");
                        board[b.getY()][b.getX()].setType(CellType.EXPLOSION);
                        enemies.remove(e);
                        player.addScore(10);

                        new javax.swing.Timer(300, evt -> {
                            board[b.getY()][b.getX()].setType(CellType.EMPTY);
                        }).start();
//                        enemies.remove(e);
//                        player.addScore(10);
                        explosions.add(new Explosion(e.getX(), e.getY(), explosionGif));
                    }
                    break;
                }
            }
            if (!hit && b.getY() >= 0 && b.getY() < height) {
                board[b.getY()][b.getX()].setType(CellType.BULLET);
            }
        }
        bullets.removeAll(toRemove);
    }

    private int enemyMoveCounter = 0;

    public void updateEnemies() {
        enemyMoveCounter++;
        if(enemyMoveCounter < 10){
            return;
        }
        enemyMoveCounter = 0;

        for (EnemyShip e : enemies) {
            board[e.getY()][e.getX()].setType(CellType.EMPTY);
            e.moveDown();
            if (e.getY() >= height -1) {
                gameOver = true;
                return;
            }
            board[e.getY()][e.getX()].setType(CellType.ENEMY_SHIP);
        }
    }

    public int getRowCount() {
        return board.length;
    }

    public int getColCount() {
        return board[0].length;
    }
}

////////////
enum CellType{
    EMPTY,
    ENEMY_SHIP,
    PLAYER_SHIP,
    BULLET,
    EXPLOSION
}


class Cell{

    private int row;
    private int col;
    private CellType type;

    public Cell(int row, int col, CellType type){
        this.row = row;
        this.col = col;
        this.type = type;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }
}

abstract class Ship{
    protected int x;
    protected int y;
    protected int speed;

    public Ship(int x, int y, int speed){
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getSpeed(){
        return speed;
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }
}


class PlayerShip extends Ship{
    public PlayerShip(int x, int y, int speed){
        super(x, y, speed);
    }

    public void moveLeft(){
        this.x -= speed;
    }
    public void moveRight(){
        this.x += speed;
    }

    public Bullet shoot(){
        return new Bullet(x, y-1, 1);
    }
}


class EnemyShip extends Ship{
    private int hp;
    private Color color;

    public EnemyShip(int x, int y, int speed, int hp){
        super(x, y, speed);
        this.hp = hp;

        Random r = new Random();
        this.color = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
    }

    public Color getColor(){
        return color;
    }

    public void moveDown(){
        this.y += speed;
    }

    public void takeDamage(int damage) {
        this.hp -= damage;
    }

    public boolean isDestroyed(){
        return this.hp <= 0;
    }

    public int getHp(){
        return hp;
    }
}


class Bullet{

    private int x;
    private int y;
    private int speed;

    public Bullet(int x, int y, int speed){
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public void move(){
        this.y -= speed;
    }

    public boolean isOutOfBounds(int height){
        return y < 0 || y >= height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
