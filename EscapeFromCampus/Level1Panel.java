package escapefromcampus;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
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
    private String missionText;
    private WorldArea nearbyPlace;

    public Level1Panel(MainFrame frame) {
        this.frame = frame;
        this.player = new Rectangle(150, 1080, PLAYER_SIZE, PLAYER_SIZE);
        this.obstacles = new ArrayList<>();
        this.places = new ArrayList<>();
        this.collectibles = new ArrayList<>();
        this.missionText = "Hint: tiap gedung punya 5 puzzle OOP dan penjaga patroli. Hindari penjaga, baca petunjuk, lalu ambil kunci asli.";

        setBackground(new Color(105, 157, 95));
        setFocusable(true);

        buildWorld();
        installControls();

        gameLoop = new Timer(16, e -> {
            updateGame();
            repaint();
        });
        gameLoop.setCoalesce(true);
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
        drawHud(g);
        drawMiniMap(g);

        g.dispose();
    }

    private void buildWorld() {
        obstacles.clear();
        places.clear();
        collectibles.clear();

        obstacles.add(new WorldArea("Gedung Rektorat", 250, 170, 300, 180, new Color(142, 77, 57)));
        obstacles.add(new WorldArea("Perpustakaan", 760, 150, 340, 210, new Color(99, 101, 137)));
        obstacles.add(new WorldArea("Lab Komputer", 1250, 220, 330, 190, new Color(69, 105, 126)));
        obstacles.add(new WorldArea("Kelas Algoritma", 340, 560, 310, 190, new Color(173, 129, 67)));
        obstacles.add(new WorldArea("Kantin", 940, 620, 280, 180, new Color(78, 129, 89)));
        obstacles.add(new WorldArea("Kolam Kampus", 1320, 760, 280, 190, new Color(59, 133, 160)));
        obstacles.add(new WorldArea("Asrama", 310, 900, 280, 180, new Color(119, 82, 123)));

        places.add(new WorldArea("Gerbang Utama", 70, 1120, 130, 95, new Color(80, 80, 80)));
        places.add(new WorldArea("Papan Info", 220, 1120, 135, 75, new Color(226, 204, 122)));
        places.add(new WorldArea("Gedung Rektorat", 335, 350, 140, 75, new Color(196, 130, 100)));
        places.add(new WorldArea("Kelas Algoritma", 395, 750, 205, 80, new Color(214, 184, 109)));
        places.add(new WorldArea("Perpustakaan", 850, 360, 170, 75, new Color(153, 159, 204)));
        places.add(new WorldArea("Lab Komputer", 1340, 410, 170, 75, new Color(126, 176, 196)));
        places.add(new WorldArea("Kantin", 1000, 800, 160, 75, new Color(129, 184, 125)));
        places.add(new WorldArea("Asrama", 380, 1080, 150, 75, new Color(175, 135, 178)));

        collectibles.add(new Collectible("Kunci Mengilap", 890, 450, true));
        collectibles.add(new Collectible("Kunci Tua", 1120, 890, true));
        collectibles.add(new Collectible("Kunci Rumput", 1500, 560, true));
        collectibles.add(new Collectible("Kunci Bangku", 680, 1010, true));
    }

    public void restartWorld() {
        player.setLocation(150, 1080);
        stopMovement();
        nearbyPlace = null;
        missionText = "Hint: tiap gedung punya 5 puzzle OOP dan penjaga patroli. Hindari penjaga, baca petunjuk, lalu ambil kunci asli.";

        for (Collectible collectible : collectibles) {
            collectible.collected = false;
        }

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

        if (upPressed) {
            dy -= PLAYER_SPEED;
        }
        if (downPressed) {
            dy += PLAYER_SPEED;
        }
        if (leftPressed) {
            dx -= PLAYER_SPEED;
        }
        if (rightPressed) {
            dx += PLAYER_SPEED;
        }

        if (dx != 0 && dy != 0) {
            dx = (int) Math.round(dx * 0.72);
            dy = (int) Math.round(dy * 0.72);
        }

        tryMove(dx, 0);
        tryMove(0, dy);
    }

    private void tryMove(int dx, int dy) {
        if (dx == 0 && dy == 0) {
            return;
        }

        Rectangle nextPosition = new Rectangle(player);
        nextPosition.translate(dx, dy);

        if (nextPosition.x < 0
                || nextPosition.y < 0
                || nextPosition.x + nextPosition.width > WORLD_WIDTH
                || nextPosition.y + nextPosition.height > WORLD_HEIGHT) {
            return;
        }

        for (WorldArea obstacle : obstacles) {
            if (nextPosition.intersects(obstacle.bounds)) {
                return;
            }
        }

        player.setBounds(nextPosition);
    }

    private void collectNearbyKeys() {
        for (Collectible collectible : collectibles) {
            if (!collectible.collected && player.intersects(collectible.bounds)) {
                collectible.collected = true;

                if (collectible.fake) {
                    GameManager.kunciPalsu++;
                    GameManager.nyawa--;
                    missionText = collectible.name + " ternyata kunci palsu. Nyawa berkurang.";
                    checkGameOver();
                } else {
                    GameManager.kunci++;
                    missionText = "Kamu menemukan " + collectible.name + ". Kunci asli: "
                            + GameManager.kunci + "/" + GameManager.KUNCI_TARGET + ".";
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
            missionText = "Tidak ada yang bisa diperiksa di dekat sini.";
            return;
        }

        switch (nearbyPlace.name) {
            case "Gerbang Utama":
                interactGate();
                break;
            case "Papan Info":
                missionText = "Hint lokasi: masuk gedung, hindari penjaga merah, selesaikan 5 puzzle OOP, lalu buka laci/loker kunci asli. Kunci halaman itu palsu.";
                break;
            case "Gedung Rektorat":
                missionText = "Rektorat: cari Lemari Arsip, selesaikan 5 puzzle OOP, lalu buka Laci Arsip.";
                frame.showPanel("Rectorate");
                break;
            case "Kelas Algoritma":
                interactClassroom();
                break;
            case "Perpustakaan":
                missionText = "Perpustakaan: cari Meja Referensi, selesaikan 5 puzzle OOP, lalu buka Laci Meja Referensi.";
                frame.showPanel("Library");
                break;
            case "Lab Komputer":
                missionText = "Lab: baca Terminal Admin, selesaikan 5 puzzle OOP, lalu buka Laci Meja Server.";
                frame.showPanel("Lab");
                break;
            case "Kantin":
                missionText = "Kantin: tanya Kasir, selesaikan 5 puzzle OOP, lalu buka Laci Kasir.";
                frame.showPanel("Canteen");
                break;
            case "Asrama":
                missionText = "Asrama: baca Papan Pengumuman, selesaikan 5 puzzle OOP, lalu buka Loker Penjaga.";
                frame.showPanel("Dormitory");
                break;
            default:
                missionText = "Area ini belum punya interaksi.";
                break;
        }
    }

    private void interactGate() {
        if (GameManager.kunci >= GameManager.KUNCI_TARGET) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gerbang terbuka!\nKamu berhasil lolos dari area kampus pertama."
            );
            frame.showPanel("Menu");
        } else {
            missionText = "Gerbang masih terkunci. Butuh " + GameManager.KUNCI_TARGET
                    + " kunci asli. Saat ini: " + GameManager.kunci + "/" + GameManager.KUNCI_TARGET + ".";
        }
    }

    private void interactClassroom() {
        if (GameManager.kelasSelesai) {
            missionText = "Kelas Algoritma sudah selesai. Kamu masih bisa masuk untuk membaca petunjuk.";
        } else {
            missionText = "Masuk ke Kelas Algoritma untuk mencari petunjuk dan kunci kelas.";
        }

        frame.showPanel("Classroom");
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

        GradientPaint grass = new GradientPaint(
                0,
                0,
                new Color(121, 170, 103),
                WORLD_WIDTH,
                WORLD_HEIGHT,
                new Color(77, 139, 92)
        );
        g.setPaint(grass);
        g.fillRect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        drawPaths(g);
        drawTrees(g);

        g.setColor(new Color(67, 107, 65));
        g.setStroke(new BasicStroke(8));
        g.drawRect(4, 4, WORLD_WIDTH - 8, WORLD_HEIGHT - 8);

        g.translate(cameraX, cameraY);
    }

    private void drawPaths(Graphics2D g) {
        g.setColor(new Color(207, 194, 157));
        g.fillRect(0, 1085, WORLD_WIDTH, 90);
        g.fillRect(820, 0, 120, WORLD_HEIGHT);
        g.fillRect(0, 475, WORLD_WIDTH, 80);
        g.fillRect(620, 220, 90, 900);
        g.fillOval(720, 450, 350, 220);

        g.setColor(new Color(185, 171, 132));
        g.setStroke(new BasicStroke(3));
        g.drawLine(0, 1130, WORLD_WIDTH, 1130);
        g.drawLine(880, 0, 880, WORLD_HEIGHT);
        g.drawLine(0, 515, WORLD_WIDTH, 515);
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

        for (WorldArea obstacle : obstacles) {
            drawBuilding(g, obstacle);
        }

        for (WorldArea place : places) {
            drawInteractionZone(g, place);
        }

        g.translate(cameraX, cameraY);
    }

    private void drawBuilding(Graphics2D g, WorldArea area) {
        Rectangle b = area.bounds;

        g.setColor(new Color(0, 0, 0, 45));
        g.fillRoundRect(b.x + 10, b.y + 12, b.width, b.height, 10, 10);

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

        drawCenteredText(g, area.name, b.x, b.y + 12, b.width, new Color(255, 255, 255), Font.BOLD, 16);
    }

    private void drawInteractionZone(Graphics2D g, WorldArea place) {
        Rectangle b = place.bounds;
        boolean active = place == nearbyPlace;

        g.setColor(active ? new Color(255, 226, 92, 130) : new Color(255, 255, 255, 70));
        g.fillRoundRect(b.x, b.y, b.width, b.height, 14, 14);
        g.setColor(active ? new Color(130, 94, 22) : new Color(90, 90, 90, 120));
        g.setStroke(new BasicStroke(active ? 3 : 1));
        g.drawRoundRect(b.x, b.y, b.width, b.height, 14, 14);

        drawCenteredText(g, active ? "Tekan E" : place.name, b.x, b.y + 26, b.width, new Color(40, 40, 40), Font.BOLD, 14);
    }

    private void drawCollectibles(Graphics2D g) {
        g.translate(-cameraX, -cameraY);

        for (Collectible collectible : collectibles) {
            if (collectible.collected) {
                continue;
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
        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval(b.x + 4, b.y + 15, b.width, 12);
        g.setColor(new Color(244, 199, 50));
        g.fillOval(b.x, b.y, 28, 28);
        g.fillRect(b.x + 23, b.y + 11, 24, 7);
        g.fillRect(b.x + 40, b.y + 11, 6, 16);
        g.setColor(new Color(41, 137, 78));
        g.fillRoundRect(b.x + 8, b.y - 10, 28, 12, 6, 6);
        g.setColor(new Color(101, 72, 14));
        g.setStroke(new BasicStroke(2));
        g.drawOval(b.x, b.y, 28, 28);
        g.drawLine(b.x + 23, b.y + 14, b.x + 47, b.y + 14);
    }

    private void drawFakeKey(Graphics2D g, Rectangle b) {
        int[] xPoints = {b.x + 13, b.x + 26, b.x + 13, b.x};
        int[] yPoints = {b.y - 2, b.y + 11, b.y + 24, b.y + 11};

        g.setColor(new Color(0, 0, 0, 55));
        g.fillOval(b.x + 4, b.y + 16, b.width, 11);
        g.setColor(new Color(176, 139, 48));
        g.fillPolygon(xPoints, yPoints, 4);
        g.fillRect(b.x + 21, b.y + 8, 22, 7);
        g.fillRect(b.x + 35, b.y + 8, 7, 15);
        g.setColor(new Color(90, 63, 16));
        g.setStroke(new BasicStroke(2));
        g.drawPolygon(xPoints, yPoints, 4);
        g.drawLine(b.x + 21, b.y + 11, b.x + 43, b.y + 11);
        g.setColor(new Color(191, 45, 45));
        g.setStroke(new BasicStroke(3));
        g.drawLine(b.x + 3, b.y + 1, b.x + 24, b.y + 22);
        g.drawLine(b.x + 24, b.y + 1, b.x + 3, b.y + 22);
    }

    private void drawPlayer(Graphics2D g) {
        int screenX = player.x - cameraX;
        int screenY = player.y - cameraY;

        g.setColor(new Color(0, 0, 0, 70));
        g.fillOval(screenX + 3, screenY + 25, player.width - 6, 12);
        g.setColor(new Color(48, 76, 171));
        g.fillRoundRect(screenX + 5, screenY + 12, 24, 23, 10, 10);
        g.setColor(new Color(238, 195, 146));
        g.fillOval(screenX + 6, screenY, 22, 22);
        g.setColor(new Color(30, 35, 65));
        g.fillArc(screenX + 6, screenY - 2, 22, 16, 0, 180);
        g.setColor(new Color(20, 20, 20));
        g.drawOval(screenX + 6, screenY, 22, 22);
    }

    private void drawHud(Graphics2D g) {
        g.setColor(new Color(20, 24, 28, 205));
        g.fillRoundRect(18, 16, 552, 116, 12, 12);
        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Escape from Campus - Area Open World", 34, 42);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString(
                "Nyawa: " + GameManager.nyawa
                + "    Kunci asli: " + GameManager.kunci + "/" + GameManager.KUNCI_TARGET
                + "    Palsu: " + GameManager.kunciPalsu,
                34,
                68
        );
        drawWrappedText(g, missionText, 34, 90, 506, 2, 18);

        String helpText = "WASD/Arrow: jalan    E: interaksi    Esc: menu";
        int helpWidth = g.getFontMetrics().stringWidth(helpText);
        g.setColor(new Color(20, 24, 28, 190));
        g.fillRoundRect(18, getHeight() - 54, helpWidth + 32, 36, 12, 12);
        g.setColor(Color.WHITE);
        g.drawString(helpText, 34, getHeight() - 31);

        if (nearbyPlace != null) {
            String prompt = "Dekat: " + nearbyPlace.name + " | Tekan E";
            int promptWidth = g.getFontMetrics().stringWidth(prompt);
            g.setColor(new Color(244, 210, 82, 230));
            g.fillRoundRect((getWidth() - promptWidth) / 2 - 18, 18, promptWidth + 36, 38, 12, 12);
            g.setColor(new Color(45, 36, 20));
            g.setFont(new Font("Arial", Font.BOLD, 15));
            g.drawString(prompt, (getWidth() - promptWidth) / 2, 43);
        }
    }

    private void drawMiniMap(Graphics2D g) {
        int mapWidth = 180;
        int mapHeight = 130;
        int mapX = getWidth() - mapWidth - 18;
        int mapY = 16;
        double scaleX = mapWidth / (double) WORLD_WIDTH;
        double scaleY = mapHeight / (double) WORLD_HEIGHT;

        g.setColor(new Color(18, 22, 27, 205));
        g.fillRoundRect(mapX, mapY, mapWidth, mapHeight, 10, 10);
        g.setColor(new Color(115, 166, 103));
        g.fillRect(mapX + 10, mapY + 10, mapWidth - 20, mapHeight - 20);

        g.setColor(new Color(85, 73, 63));
        for (WorldArea obstacle : obstacles) {
            Rectangle b = obstacle.bounds;
            int x = mapX + 10 + (int) (b.x * scaleX);
            int y = mapY + 10 + (int) (b.y * scaleY);
            int w = Math.max(3, (int) (b.width * scaleX));
            int h = Math.max(3, (int) (b.height * scaleY));
            g.fillRect(x, y, w, h);
        }

        for (Collectible collectible : collectibles) {
            if (!collectible.collected) {
                int x = mapX + 10 + (int) (collectible.bounds.x * scaleX);
                int y = mapY + 10 + (int) (collectible.bounds.y * scaleY);
                g.setColor(collectible.fake ? new Color(206, 70, 62) : new Color(247, 219, 71));
                g.fillOval(x, y, 5, 5);
            }
        }

        int px = mapX + 10 + (int) (player.x * scaleX);
        int py = mapY + 10 + (int) (player.y * scaleY);
        g.setColor(new Color(38, 83, 225));
        g.fillOval(px - 3, py - 3, 8, 8);

        g.setColor(Color.WHITE);
        g.drawRoundRect(mapX, mapY, mapWidth, mapHeight, 10, 10);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Peta", mapX + 12, mapY + 25);
    }

    private void drawCenteredText(
            Graphics2D g,
            String text,
            int x,
            int y,
            int width,
            Color color,
            int style,
            int size
    ) {
        g.setFont(new Font("Arial", style, size));
        FontMetrics metrics = g.getFontMetrics();
        int textX = x + (width - metrics.stringWidth(text)) / 2;
        g.setColor(color);
        g.drawString(text, textX, y + metrics.getAscent());
    }

    private void drawWrappedText(Graphics2D g, String text, int x, int y, int maxWidth, int maxLines, int lineHeight) {
        FontMetrics metrics = g.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int drawnLines = 0;

        for (String word : words) {
            String nextLine = line.length() == 0 ? word : line + " " + word;

            if (metrics.stringWidth(nextLine) <= maxWidth) {
                line = new StringBuilder(nextLine);
                continue;
            }

            if (line.length() > 0) {
                g.drawString(line.toString(), x, y + drawnLines * lineHeight);
                drawnLines++;
            }

            if (drawnLines >= maxLines) {
                return;
            }

            line = new StringBuilder(word);
        }

        if (line.length() > 0 && drawnLines < maxLines) {
            g.drawString(line.toString(), x, y + drawnLines * lineHeight);
        }
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
