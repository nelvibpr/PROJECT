package nelvi.gameflappy;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    // Aset Gambar
    Image bg;
    Image nelvibird;
    Image pipaatas;
    Image pipabawah;
            

    // Default Koordinat dan Ukuran Burung
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    // Default Koordinat dan Ukuran Pipa
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;  
    int pipeHeight = 512;

    // Objek Game
    Nelvi bird;
    ArrayList<Pipa> pipes;
    Random random = new Random();

    // Logika Fisika & Game
    int velocityX = -4; 
    int velocityY = 0; 
    int gravity = 1;

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;

    public FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // Memuat Gambar
        bg = new ImageIcon(getClass().getResource("/Resource/bg.png")).getImage();
        nelvibird = new ImageIcon(getClass().getResource("/Resource/nelvibird.png")).getImage();
        pipaatas = new ImageIcon(getClass().getResource("/Resource/pipaatas.png")).getImage();
        pipabawah= new ImageIcon(getClass().getResource("/Resource/pipabawah.png")).getImage();

        // Inisialisasi Burung dari class terpisah
        bird = new Nelvi(birdX, birdY, birdWidth, birdHeight, nelvibird);
        pipes = new ArrayList<Pipa>();

        // Timer Pipa
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        // Game Loop
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    public void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;

        // Buat Pipa Atas menggunakan class Pipe
        Pipa topPipe = new Pipa(pipeX, randomPipeY, pipeWidth, pipeHeight, pipaatas);
        pipes.add(topPipe);

        // Buat Pipa Bawah menggunakan class Pipe
        Pipa bottomPipe = new Pipa(pipeX, topPipe.y + pipeHeight + openingSpace, pipeWidth, pipeHeight, pipabawah);
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(bg, 0, 0, boardWidth, boardHeight, null);
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        for (int i = 0; i < pipes.size(); i++) {
            Pipa pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        for (int i = 0; i < pipes.size(); i++) {
            Pipa pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5;
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Nelvi a, Pipa b) {
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height &&
               a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            } else {
                velocityY = -9;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
