package escapefromcampus;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

public class Level1Panel extends JPanel {
    private static final int WORLD_WIDTH = 1800;
    private static final int WORLD_HEIGHT = 1300;
    private static final int PLAYER_SIZE = 34;
    private static final int PLAYER_SPEED = 5;

    private final MainFrame frame;
    private final Timer gameLoop;
    private final Rectangle player;
    private final List<WorldArea> obstacles;
    private final List<WorldArea> places;
    private final List<Collectible> collectibles;

    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private int cameraX;
    private int cameraY;
    
    // Teks Pengumuman
    private String infoText = "Pengumuman: Sistem gerbang utama rusak oleh anomali. Cari total 5 kunci asli di dalam gedung. Tiap gedung memiliki 3 pertanyaan keamanan.";
    private WorldArea nearbyPlace;

    // --- VARIABEL ANIMASI SPRITE ---
    private Image heartIcon;
    private Image keyIcon;
    private Image bgImage;
    private Image up1, up2, down1, down2, left1, left2, right1, right2;
    private String direction = "down"; 
    private int spriteCounter = 0;     
    private int spriteNum = 1;
    
    private Image imgPerpus, imgLab, imgKantin, imgRektorat, imgKelas;

    public Level1Panel(MainFrame frame) {
                try {
            // Sesuaikan nama file image_df9803.jpg dengan nama file aset Anda
            URL bgPath = getClass().getResource("/escapefromcampus/assets/map/map .png");
            if (bgPath != null) {
                bgImage = new ImageIcon(bgPath).getImage();
            }
        } catch (Exception e) {
            System.out.println("Gagal memuat background map: " + e.getMessage());
        }
        this.frame = frame;
        this.player = new Rectangle(150, 1080, PLAYER_SIZE, PLAYER_SIZE);
        this.obstacles = new ArrayList<>();
        this.places = new ArrayList<>();
        this.collectibles = new ArrayList<>();
        
        imgKelas = loadImg("/escapefromcampus/assets/gedung/gedung kelas.png");
        imgKantin = loadImg("/escapefromcampus/assets/gedung/kantin.png");
        imgLab = loadImg("/escapefromcampus/assets/gedung/lab komputer.png");
        imgPerpus = loadImg("/escapefromcampus/assets/gedung/perpustakaan.png");
        imgRektorat = loadImg("/escapefromcampus/assets/gedung/rektorat.png");
        
        heartIcon = loadImg("/escapefromcampus/assets/asset dalam ruangan/NYAWA.png");
        keyIcon = loadImg("/escapefromcampus/assets/asset dalam ruangan/KUNCI.png");
        setBackground(new Color(105, 157, 95));
        setFocusable(true);

        buildWorld();
        installControls();

        gameLoop = new Timer(16, e -> {
            updateGame();
            repaint();
        });
        gameLoop.setCoalesce(true);
        
        getPlayerImage(); // Muat sprite saat panel dibuat
    }

    // --- FUNGSI MENGAMBIL SPRITE DARI FOLDER ---
    private void getPlayerImage() {
        try {
            String folder = (GameManager.playerGender == 1) ? "asset player GIRL/" : "asset player BOY/";
            String prefix = (GameManager.playerGender == 1) ? "girl " : "boy ";
            String basePath = "/escapefromcampus/assets/" + folder + prefix;

            up1 = loadImg(basePath + "atas 1.png");
            up2 = loadImg(basePath + "atas 2.png");
            down1 = loadImg(basePath + "bawah 1.png");
            down2 = loadImg(basePath + "bawah 2.png");
            left1 = loadImg(basePath + "kiri 1.png");
            left2 = loadImg(basePath + "kiri 2.png");
            right1 = loadImg(basePath + "kanan 1.png");
            right2 = loadImg(basePath + "kanan 2.png");
        } catch (Exception e) {
            System.out.println("Gagal memuat sprite: " + e.getMessage());
        }
    }

    private Image loadImg(String path) {
        URL imgUrl = getClass().getResource(path);
        if (imgUrl != null) {
            return new ImageIcon(imgUrl).getImage();
        }
        return null;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (visible) {
            stopMovement();
            updateCamera();
            if (!gameLoop.isRunning()) {
                gameLoop.start();
            }
            requestFocusInWindow();
        } else {
            stopMovement();
            gameLoop.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawWorld(g);
        drawCollectibles(g);
        drawPlaces(g);
        drawPlayer(g); 
        drawUI(g); 

        g.dispose();
    }

    private void buildWorld() {
        obstacles.clear();
        places.clear();
        collectibles.clear();

        obstacles.add(new WorldArea("Gedung Rektorat", 150, 240, 320, 180, new Color(142, 77, 57)));
        obstacles.add(new WorldArea("Lab Komputer", 1250, 220, 330, 190, new Color(69, 105, 126)));
        obstacles.add(new WorldArea("Perpustakaan", 290, 560, 310, 190, new Color(99, 101, 137)));
        obstacles.add(new WorldArea("Kantin", 940, 620, 280, 180, new Color(78, 129, 89)));
        obstacles.add(new WorldArea("Billboard Developer", 1320, 760, 280, 190, new Color(40, 40, 40)));
        obstacles.add(new WorldArea("Gedung Kelas", 310, 900, 280, 180, new Color(119, 82, 123)));

        places.add(new WorldArea("Gerbang Utama", 780, 1130, 200, 160, new Color(80, 80, 80)));
        places.add(new WorldArea("Papan Info", 220, 1120, 135, 75, new Color(226, 204, 122)));
        places.add(new WorldArea("Gedung Rektorat", 235, 420, 150, 75, new Color(196, 130, 100))); 
        places.add(new WorldArea("Perpustakaan", 350, 750, 195, 80, new Color(153, 159, 204)));
        places.add(new WorldArea("Lab Komputer", 1340, 410, 170, 75, new Color(126, 176, 196)));
        places.add(new WorldArea("Kantin", 1000, 800, 160, 75, new Color(129, 184, 125)));
        places.add(new WorldArea("Gedung Kelas", 380, 1080, 150, 75, new Color(175, 135, 178)));
        places.add(new WorldArea("Billboard Developer", 1310, 750, 300, 220, new Color(200, 200, 200)));

        collectibles.add(new Collectible("Kunci Mengilap", 890, 450, true));
        collectibles.add(new Collectible("Kunci Tua", 1120, 890, true));
        collectibles.add(new Collectible("Kunci Rumput", 1500, 560, true));
        collectibles.add(new Collectible("Kunci Bangku", 680, 1010, true));
    }

    public void restartWorld() {
        player.setLocation(150, 1080);
        stopMovement();
        nearbyPlace = null;

        for (Collectible collectible : collectibles) {
            collectible.collected = false;
        }

        getPlayerImage(); // Muat sprite lagi agar karakter terupdate
        updateCamera();
        repaint();
    }

    private void installControls() {
        bindMove("pressed W", "moveUpOn", () -> upPressed = true);
        bindMove("released W", "moveUpOff", () -> upPressed = false);
        bindMove("pressed UP", "moveUpArrowOn", () -> upPressed = true);
        bindMove("released UP", "moveUpArrowOff", () -> upPressed = false);

        bindMove("pressed S", "moveDownOn", () -> downPressed = true);
        bindMove("released S", "moveDownOff", () -> downPressed = false);
        bindMove("pressed DOWN", "moveDownArrowOn", () -> downPressed = true);
        bindMove("released DOWN", "moveDownArrowOff", () -> downPressed = false);

        bindMove("pressed A", "moveLeftOn", () -> leftPressed = true);
        bindMove("released A", "moveLeftOff", () -> leftPressed = false);
        bindMove("pressed LEFT", "moveLeftArrowOn", () -> leftPressed = true);
        bindMove("released LEFT", "moveLeftArrowOff", () -> leftPressed = false);

        bindMove("pressed D", "moveRightOn", () -> rightPressed = true);
        bindMove("released D", "moveRightOff", () -> rightPressed = false);
        bindMove("pressed RIGHT", "moveRightArrowOn", () -> rightPressed = true);
        bindMove("released RIGHT", "moveRightArrowOff", () -> rightPressed = false);

        bindMove("pressed E", "interact", this::interact);
        bindMove("pressed ESCAPE", "backToMenu", () -> frame.showPanel("Menu"));
    }

    private void bindMove(String keyStroke, String actionName, Runnable action) {
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(keyStroke), actionName);
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isShowing()) {
                    action.run();
                }
            }
        });
    }

    private void updateGame() {
        movePlayer();
        collectNearbyKeys();
        updateNearbyPlace();
        updateCamera();
    }

    private void movePlayer() {
        int dx = 0;
        int dy = 0;
        boolean isMoving = false;

        if (upPressed) {
            dy -= PLAYER_SPEED;
            direction = "up";
            isMoving = true;
        }
        if (downPressed) {
            dy += PLAYER_SPEED;
            direction = "down";
            isMoving = true;
        }
        if (leftPressed) {
            dx -= PLAYER_SPEED;
            direction = "left";
            isMoving = true;
        }
        if (rightPressed) {
            dx += PLAYER_SPEED;
            direction = "right";
            isMoving = true;
        }

        // LOGIKA ANIMASI SPRITE
        if (isMoving) {
            spriteCounter++;
            if (spriteCounter > 10) { // Kecepatan pergantian frame
                if (spriteNum == 1) {
                    spriteNum = 2;
                } else if (spriteNum == 2) {
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }
        } else {
            spriteNum = 1; // Kembali diam jika tidak berjalan
        }

        if (dx != 0 && dy != 0) {
            dx = (int) Math.round(dx * 0.72);
            dy = (int) Math.round(dy * 0.72);
        }

        tryMove(dx, 0);
        tryMove(0, dy);
    }

    private void tryMove(int dx, int dy) {
        if (dx == 0 && dy == 0) return;

        Rectangle nextPosition = new Rectangle(player);
        nextPosition.translate(dx, dy);

        if (nextPosition.x < 0 || nextPosition.y < 0
                || nextPosition.x + nextPosition.width > WORLD_WIDTH
                || nextPosition.y + nextPosition.height > WORLD_HEIGHT) {
            return;
        }

        Rectangle gateBlock = new Rectangle(790, 1160, 180, 40); 
        if (nextPosition.intersects(gateBlock)) {
            return;
        }

        for (WorldArea obstacle : obstacles) {
            if (nextPosition.intersects(obstacle.bounds)) {
                return;
            }
        }

        player.setBounds(nextPosition);
    }

    // --- MENGGAMBAR SPRITE KARAKTER PADA LAYER ---
    private void drawPlayer(Graphics2D g) {
        int screenX = player.x - cameraX;
        int screenY = player.y - cameraY;

        Image image = null;

        switch (direction) {
            case "up": image = (spriteNum == 1) ? up1 : up2; break;
            case "down": image = (spriteNum == 1) ? down1 : down2; break;
            case "left": image = (spriteNum == 1) ? left1 : left2; break;
            case "right": image = (spriteNum == 1) ? right1 : right2; break;
        }

        if (image != null) {
            // Gambar bayangan di bawah kaki
            g.setColor(new Color(0, 0, 0, 70));
            g.fillOval(screenX + 3, screenY + player.height - 8, player.width - 4, 12);
            
            // Gambar sprite
            g.drawImage(image, screenX - 6, screenY - 14, player.width + 12, player.height + 18, null);
        } else {
            // Fallback cadangan
            g.setColor(new Color(48, 76, 171));
            g.fillOval(screenX, screenY, player.width, player.height);
            g.setColor(Color.WHITE);
            g.drawOval(screenX, screenY, player.width, player.height);
        }
    }

    private void collectNearbyKeys() {
        for (Collectible collectible : collectibles) {
            if (!collectible.collected && player.intersects(collectible.bounds)) {
                collectible.collected = true;
                if (collectible.fake) {
                    GameManager.kunciPalsu++;
                    GameManager.nyawa--;
                    JOptionPane.showMessageDialog(this, collectible.name + " ternyata kunci palsu!\nNyawa berkurang.", "Jebakan", JOptionPane.WARNING_MESSAGE);
                    checkGameOver();
                } else {
                    GameManager.kunci++;
                }
            }
        }
    }

    private void updateNearbyPlace() {
        nearbyPlace = null;
        Rectangle interactionRange = new Rectangle(player);
        interactionRange.grow(24, 24);

        for (WorldArea place : places) {
            if (interactionRange.intersects(place.bounds)) {
                nearbyPlace = place;
                return;
            }
        }
    }

    private void updateCamera() {
        int viewWidth = getWidth() > 0 ? getWidth() : 800;
        int viewHeight = getHeight() > 0 ? getHeight() : 600;

        cameraX = player.x + player.width / 2 - viewWidth / 2;
        cameraY = player.y + player.height / 2 - viewHeight / 2;
        cameraX = clamp(cameraX, 0, Math.max(0, WORLD_WIDTH - viewWidth));
        cameraY = clamp(cameraY, 0, Math.max(0, WORLD_HEIGHT - viewHeight));
    }

    private void interact() {
        updateNearbyPlace();
        if (nearbyPlace == null) {
            return;
        }

        switch (nearbyPlace.name) {
            case "Gerbang Utama": interactGate(); break;
            case "Papan Info": break; 
            case "Billboard Developer": break;
            case "Gedung Rektorat": frame.showPanel("Rectorate"); break;
            case "Perpustakaan": frame.showPanel("Library"); break;
            case "Lab Komputer": frame.showPanel("Lab"); break;
            case "Kantin": frame.showPanel("Canteen"); break;
            case "Gedung Kelas": frame.showPanel("Dormitory"); break;
        }
    }

    private void interactGate() {
        if (GameManager.kunci >= GameManager.KUNCI_TARGET) {
            stopMovement();
            frame.showPanel("Victory"); 
        } else {
            JOptionPane.showMessageDialog(this, "Gerbang terkunci rapat!\nKamu harus menemukan " + GameManager.KUNCI_TARGET 
                    + " kunci asli untuk bisa membukanya.\nKunci terkumpul: " + GameManager.kunci, "Gerbang Terkunci", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void stopMovement() {
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
    }

    private void checkGameOver() {
        if (GameManager.nyawa <= 0) {
            JOptionPane.showMessageDialog(this, "Nyawa habis. GAME OVER.");
            frame.showPanel("Menu");
        }
    }

   private void drawWorld(Graphics2D g) {
    
    g.translate(-cameraX, -cameraY);
    
    // GAMBAR ASET BACKGROUND ANDA DI SINI
    if (bgImage != null) {
        g.drawImage(bgImage, 0, 0, WORLD_WIDTH, WORLD_HEIGHT, null);
    } else {
        // Fallback jika gambar gagal dimuat
        GradientPaint grass = new GradientPaint(0, 0, new Color(121, 170, 103), WORLD_WIDTH, WORLD_HEIGHT, new Color(77, 139, 92));
        g.setPaint(grass);
        g.fillRect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
    }

    // Tetap panggil drawPaths dan drawTrees jika ingin elemen tersebut tetap ada di atas background
    drawPaths(g);
    drawTrees(g);
    drawGate(g); 

    g.setColor(new Color(67, 107, 65));
    g.setStroke(new BasicStroke(8));
    g.drawRect(4, 4, WORLD_WIDTH - 8, WORLD_HEIGHT - 8);

    g.translate(cameraX, cameraY);
}
    
    private void drawGate(Graphics2D g) {
        int gx = 790;
        int gy = 1140;
        int gw = 180;
        int gh = 140;
        boolean isOpen = (GameManager.kunci >= GameManager.KUNCI_TARGET);

        g.setColor(new Color(60, 60, 60));
        g.fillRect(gx, gy, 25, gh);
        g.setColor(new Color(30, 30, 30));
        g.setStroke(new BasicStroke(2));
        g.drawRect(gx, gy, 25, gh);

        g.setColor(new Color(60, 60, 60));
        g.fillRect(gx + gw - 25, gy, 25, gh);
        g.setColor(new Color(30, 30, 30));
        g.drawRect(gx + gw - 25, gy, 25, gh);

        g.setColor(new Color(45, 45, 45));
        g.fillRect(gx - 10, gy - 20, gw + 20, 35);
        g.setColor(new Color(20, 20, 20));
        g.drawRect(gx - 10, gy - 20, gw + 20, 35);

        drawCenteredText(g, isOpen ? "GERBANG TERBUKA" : "GERBANG KELUAR", gx, gy - 15, gw, 
                         isOpen ? new Color(100, 255, 100) : Color.WHITE, Font.BOLD, 14);

        if (!isOpen) {
            g.setColor(new Color(70, 70, 70));
            for (int i = 1; i <= 7; i++) {
                int barX = gx + 12 + (i * 19);
                g.fillRect(barX, gy + 15, 12, gh - 15);
            }
            
            int padX = gx + gw / 2 - 14;
            int padY = gy + gh / 2 - 10;
            g.setColor(new Color(218, 165, 32));
            g.fillOval(padX, padY, 28, 28);
            g.setColor(new Color(30, 30, 30));
            g.drawOval(padX, padY, 28, 28);
            
            g.fillArc(padX + 8, padY + 6, 12, 12, 0, -180);
            g.fillRect(padX + 12, padY + 12, 4, 8);
        } else {
            g.setColor(new Color(70, 70, 70));
            g.fillRect(gx + 25, gy + 15, 20, gh - 15);
            g.setColor(new Color(40, 40, 40));
            g.drawLine(gx + 35, gy + 15, gx + 35, gy + gh); 
            
            g.setColor(new Color(70, 70, 70));
            g.fillRect(gx + gw - 45, gy + 15, 20, gh - 15);
            g.setColor(new Color(40, 40, 40));
            g.drawLine(gx + gw - 35, gy + 15, gx + gw - 35, gy + gh); 
        }
    }

    private void drawPaths(Graphics2D g) {
        g.setColor(new Color(207, 194, 157));
        g.fillRect(0, 1085, WORLD_WIDTH, 90);
        g.fillRect(820, 0, 120, WORLD_HEIGHT);
        g.fillRect(0, 475, WORLD_WIDTH, 80);
        g.fillRect(620, 220, 90, 900);
        g.fillOval(720, 450, 350, 220);
    }

    private void drawTrees(Graphics2D g) {
        for (int x = 110; x < WORLD_WIDTH; x += 210) {
            drawTree(g, x, 80 + (x % 4) * 26);
            drawTree(g, x + 60, 1210 - (x % 5) * 18);
        }
        for (int y = 210; y < WORLD_HEIGHT - 160; y += 190) {
            drawTree(g, 95 + (y % 3) * 30, y);
            drawTree(g, 1650 - (y % 4) * 36, y + 30);
        }
    }

    private void drawTree(Graphics2D g, int x, int y) {
        g.setColor(new Color(89, 69, 48));
        g.fillRect(x + 13, y + 28, 10, 24);
        g.setColor(new Color(47, 119, 67));
        g.fillOval(x, y, 38, 38);
        g.setColor(new Color(35, 92, 54));
        g.drawOval(x, y, 38, 38);
    }

    private void drawPlaces(Graphics2D g) {
    g.translate(-cameraX, -cameraY);
    
    // Panggil metode drawBuilding untuk setiap obstacle
    for (WorldArea obstacle : obstacles) {
        drawBuilding(g, obstacle); // <--- INI KUNCI NYA
    }
    
    // Tetap gambar zona interaksi (kotak kuning di bawah pintu gedung)
    for (WorldArea place : places) {
        drawInteractionZone(g, place);
    }
    g.translate(cameraX, cameraY);
}

    private void drawBuilding(Graphics2D g, WorldArea area) {
        Rectangle b = area.bounds;
        Image imgToDraw = null;
        
        // 1. Tentukan gambar berdasarkan nama
        switch (area.name) {
            case "Gedung Rektorat": imgToDraw = imgRektorat; break;
            case "Lab Komputer":    imgToDraw = imgLab; break;
            case "Perpustakaan":    imgToDraw = imgPerpus; break;
            case "Kantin":          imgToDraw = imgKantin; break;
            case "Gedung Kelas":    imgToDraw = imgKelas; break;
        }
        
        // 2. Logika penggambaran
        if (imgToDraw != null) {
            // Jika gambar ditemukan, gambar gambarnya saja
            g.drawImage(imgToDraw, b.x, b.y, b.width, b.height, null);
        } else {
            // Jika gambar TIDAK ditemukan, baru gunakan kode geometri yang Anda sebutkan tadi
            g.setColor(area.color);
            g.fillRoundRect(b.x, b.y, b.width, b.height, 8, 8);
            g.setColor(new Color(45, 45, 45));
            g.setStroke(new BasicStroke(3));
            g.drawRoundRect(b.x, b.y, b.width, b.height, 8, 8);
            g.setColor(new Color(230, 232, 215));
            for (int wx = b.x + 34; wx < b.x + b.width - 25; wx += 58) {
                g.fillRect(wx, b.y + 38, 30, 28);
                g.fillRect(wx, b.y + 92, 30, 28);
            }
            g.setColor(new Color(54, 43, 37));
            g.fillRect(b.x + b.width / 2 - 24, b.y + b.height - 54, 48, 54);
        }
        
        // 3. Gambar teks nama gedung (sekarang di posisi bawah gedung)
        drawCenteredText(g, area.name, b.x, b.y + b.height + 10, b.width, Color.BLACK, Font.BOLD, 16);
    }

    private void drawBillboard(Graphics2D g, WorldArea area) {
        Rectangle b = area.bounds;
        g.setColor(new Color(0, 0, 0, 45));
        g.fillRoundRect(b.x + 10, b.y + 12, b.width, b.height, 10, 10);
        g.setColor(new Color(70, 70, 70));
        g.fillRect(b.x + 40, b.y + 130, 20, 60);
        g.fillRect(b.x + b.width - 60, b.y + 130, 20, 60);
        g.setColor(new Color(40, 40, 40));
        g.fillRoundRect(b.x, b.y, b.width, 140, 10, 10);
        g.setColor(new Color(245, 245, 245));
        g.fillRoundRect(b.x + 10, b.y + 10, b.width - 20, 120, 5, 5);
        drawCenteredText(g, "TIM DEVELOPER", b.x, b.y + 15, b.width, new Color(50, 50, 50), Font.BOLD, 16);
    }

    private void drawInteractionZone(Graphics2D g, WorldArea place) {
        Rectangle b = place.bounds;
        boolean active = place == nearbyPlace;
        g.setColor(active ? new Color(255, 226, 92, 130) : new Color(255, 255, 255, 70));
        g.fillRoundRect(b.x, b.y, b.width, b.height, 14, 14);
        g.setColor(active ? new Color(130, 94, 22) : new Color(90, 90, 90, 120));
        g.setStroke(new BasicStroke(active ? 3 : 1));
        g.drawRoundRect(b.x, b.y, b.width, b.height, 14, 14);
    }

    private void drawCollectibles(Graphics2D g) {
        g.translate(-cameraX, -cameraY);
        for (Collectible collectible : collectibles) {
            if (collectible.bounds != null){
                if (keyIcon != null){
                  g.drawImage(keyIcon, collectible.bounds.x, collectible.bounds.y, collectible.bounds.width, collectible.bounds.height, null);
                }
            }
            if (collectible.fake) {
                drawFakeKey(g, collectible.bounds);
            } else {
                drawRealKey(g, collectible.bounds);
            }
        }
        g.translate(cameraX, cameraY);
    }

    private void drawRealKey(Graphics2D g, Rectangle b) {
        g.setColor(new Color(244, 199, 50));
        g.fillOval(b.x, b.y, 28, 28);
    }

    private void drawFakeKey(Graphics2D g, Rectangle b) {
        g.setColor(new Color(191, 45, 45));
        g.fillRect(b.x, b.y, 20, 20);
    }

    private void drawUI(Graphics2D g) {
        int width = getWidth(); 

        g.setColor(new Color(20, 24, 28, 180)); 
        g.fillRoundRect(15, 15, 550, 85, 12, 12); 

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Escape from Campus", 30, 40);

        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(30, 55, 25, 25, 5, 5); 
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("P", 38, 73); 

        g.setFont(new Font("Arial", Font.PLAIN, 15));
        String pName = (GameManager.playerName == null || GameManager.playerName.isEmpty()) ? "Mahasiswa" : GameManager.playerName;
        g.drawString(pName, 65, 74);

        int nyawa = GameManager.nyawa;
        g.drawString("Nyawa: ", 170, 74);
        g.setColor(new Color(220, 40, 40)); 
        int textY = 74;
        int heartSize = 20; // Ukuran hati (sesuaikan dengan tinggi kotak merah lama)
        int startX = 220;   // Posisi awal X (sesuaikan agar sejajar dengan teks "Nyawa:")
        int startY = 10;    // Posisi Y (sesuaikan agar sejajar vertikal)
        int spacing = 35;   // Jarak antar hati
        for (int i = 0; i < nyawa; i++) {
            if (heartIcon != null) {
        // Menggunakan textY - 15 agar hati naik sedikit sejajar dengan tengah teks
               g.drawImage(heartIcon, startX + (i * 25), textY - 18, heartSize, heartSize, null);
            } else {
        // Fallback kotak merah tetap di posisi yang sama
               g.setColor(Color.RED);
               g.fillRect(startX + (i * 25), textY - 18, heartSize, heartSize);
            }
        }

        int kunci = GameManager.kunci;
        g.setColor(Color.WHITE);
        g.drawString("Kunci: ", 310, 74);
        g.setColor(new Color(240, 200, 40)); 
        int keySize = 25;
        int textKunciY = 74;
        int startKunciX = 360;
        for (int i = 0; i < kunci; i++) {
            if (keyIcon != null) {
                g.drawImage(keyIcon, startKunciX + (i * 30), textKunciY - 26, keySize, keySize, null);
            } else {
        // Fallback jika icon hilang
                g.setColor(new Color(240, 200, 40));
                g.fillRect(startKunciX + (i * 30), textKunciY - 22, 25, 25);
            } 
        }

        int infoX = 580; 
        int infoWidth = width - infoX - 15; 
        
        g.setColor(new Color(20, 24, 28, 180));
        g.fillRoundRect(infoX, 15, infoWidth, 85, 12, 12); 

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 13));
        drawWrappedText(g, infoText, infoX + 15, 35, infoWidth - 30); 
    }

    private void drawCenteredText(Graphics2D g, String text, int x, int y, int width, Color color, int style, int size) {
        g.setFont(new Font("Arial", style, size));
        FontMetrics metrics = g.getFontMetrics();
        int textX = x + (width - metrics.stringWidth(text)) / 2;
        g.setColor(color);
        g.drawString(text, textX, y + metrics.getAscent());
    }

    private void drawWrappedText(Graphics2D g, String text, int x, int y, int width) {
        FontMetrics metrics = g.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        int currentY = y;

        for (String word : words) {
            if (metrics.stringWidth(currentLine + word) < width) {
                currentLine.append(word).append(" ");
            } else {
                g.drawString(currentLine.toString(), x, currentY);
                currentLine = new StringBuilder(word).append(" ");
                currentY += metrics.getHeight();
            }
        }
        g.drawString(currentLine.toString(), x, currentY);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static class WorldArea {
        private final String name;
        private final Rectangle bounds;
        private final Color color;

        private WorldArea(String name, int x, int y, int width, int height, Color color) {
            this.name = name;
            this.bounds = new Rectangle(x, y, width, height);
            this.color = color;
        }
    }

    private static class Collectible {
        private final String name;
        private final Rectangle bounds;
        private final boolean fake;
        private boolean collected;

        private Collectible(String name, int x, int y, boolean fake) {
            this.name = name;
            this.bounds = new Rectangle(x, y, 44, 32);
            this.fake = fake;
        }
    }
}