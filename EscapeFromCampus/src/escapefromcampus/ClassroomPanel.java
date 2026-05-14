package escapefromcampus;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
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

public class ClassroomPanel extends JPanel {
    private static final int ROOM_WIDTH = 1100;
    private static final int ROOM_HEIGHT = 760;
    private static final int PLAYER_SIZE = 32;
    private static final int PLAYER_SPEED = 4;

    private final MainFrame frame;
    private final Timer gameLoop;
    private final Rectangle player;
    private final Rectangle fakeKey;
    private final List<RoomObject> obstacles;
    private final List<RoomSpot> spots;

    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean fakeKeyCollected;
    private int cameraX;
    private int cameraY;
    private String missionText;
    private RoomSpot nearbySpot;

    public ClassroomPanel(MainFrame frame) {
        this.frame = frame;
        this.player = new Rectangle(520, 690, PLAYER_SIZE, PLAYER_SIZE);
        this.fakeKey = new Rectangle(720, 620, 44, 32);
        this.obstacles = new ArrayList<>();
        this.spots = new ArrayList<>();
        this.missionText = "Hint: kunci asli ada di Laci Meja Dosen. Baca petunjuk, lalu jawab kuis OOP.";

        setBackground(new Color(42, 43, 48));
        setFocusable(true);

        buildRoom();
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

        drawScene(g);
        drawHud(g);
        drawMiniMap(g);

        g.dispose();
    }

    public void restartRoom() {
        player.setLocation(520, 690);
        stopMovement();
        fakeKeyCollected = false;
        nearbySpot = null;
        missionText = "Hint: kunci asli ada di Laci Meja Dosen. Baca petunjuk, lalu jawab kuis OOP.";
        updateCamera();
        repaint();
    }

    private void buildRoom() {
        obstacles.clear();
        spots.clear();

        obstacles.add(new RoomObject("Meja Dosen", 405, 166, 290, 58, new Color(118, 78, 50)));
        obstacles.add(new RoomObject("Rak Buku Kiri", 70, 205, 78, 360, new Color(92, 66, 52)));
        obstacles.add(new RoomObject("Rak Buku Kanan", 950, 205, 78, 360, new Color(92, 66, 52)));

        int[] deskXs = {210, 395, 580, 765};
        int[] deskYs = {330, 440, 550};
        for (int y : deskYs) {
            for (int x : deskXs) {
                obstacles.add(new RoomObject("Meja Mahasiswa", x, y, 105, 56, new Color(132, 91, 59)));
            }
        }

        spots.add(new RoomSpot("Pintu Keluar", 480, 685, 150, 62));
        spots.add(new RoomSpot("Papan Tulis", 420, 120, 260, 76));
        spots.add(new RoomSpot("Laci Meja Dosen", 412, 172, 130, 62));
        spots.add(new RoomSpot("Dosen", 505, 220, 90, 76));
        spots.add(new RoomSpot("Rak Buku", 72, 310, 92, 150));
        spots.add(new RoomSpot("Tas Misterius", 865, 590, 105, 72));
    }

    private void installControls() {
        bindAction("pressed W", "moveUpOn", () -> upPressed = true);
        bindAction("released W", "moveUpOff", () -> upPressed = false);
        bindAction("pressed UP", "moveUpArrowOn", () -> upPressed = true);
        bindAction("released UP", "moveUpArrowOff", () -> upPressed = false);

        bindAction("pressed S", "moveDownOn", () -> downPressed = true);
        bindAction("released S", "moveDownOff", () -> downPressed = false);
        bindAction("pressed DOWN", "moveDownArrowOn", () -> downPressed = true);
        bindAction("released DOWN", "moveDownArrowOff", () -> downPressed = false);

        bindAction("pressed A", "moveLeftOn", () -> leftPressed = true);
        bindAction("released A", "moveLeftOff", () -> leftPressed = false);
        bindAction("pressed LEFT", "moveLeftArrowOn", () -> leftPressed = true);
        bindAction("released LEFT", "moveLeftArrowOff", () -> leftPressed = false);

        bindAction("pressed D", "moveRightOn", () -> rightPressed = true);
        bindAction("released D", "moveRightOff", () -> rightPressed = false);
        bindAction("pressed RIGHT", "moveRightArrowOn", () -> rightPressed = true);
        bindAction("released RIGHT", "moveRightArrowOff", () -> rightPressed = false);

        bindAction("pressed E", "interact", this::interact);
        bindAction("pressed ESCAPE", "exitClassroom", () -> frame.showPanel("Level1"));
    }

    private void bindAction(String keyStroke, String actionName, Runnable action) {
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
        collectFakeKey();
        updateNearbySpot();
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

        if (nextPosition.x < 40
                || nextPosition.y < 80
                || nextPosition.x + nextPosition.width > ROOM_WIDTH - 40
                || nextPosition.y + nextPosition.height > ROOM_HEIGHT - 28) {
            return;
        }

        for (RoomObject obstacle : obstacles) {
            if (nextPosition.intersects(obstacle.bounds)) {
                return;
            }
        }

        player.setBounds(nextPosition);
    }

    private void updateNearbySpot() {
        nearbySpot = null;

        Rectangle interactionRange = new Rectangle(player);
        interactionRange.grow(24, 24);

        for (RoomSpot spot : spots) {
            if (interactionRange.intersects(spot.bounds)) {
                nearbySpot = spot;
                return;
            }
        }
    }

    private void updateCamera() {
        int viewWidth = getWidth() > 0 ? getWidth() : 800;
        int viewHeight = getHeight() > 0 ? getHeight() : 600;

        cameraX = player.x + player.width / 2 - viewWidth / 2;
        cameraY = player.y + player.height / 2 - viewHeight / 2;
        cameraX = clamp(cameraX, 0, Math.max(0, ROOM_WIDTH - viewWidth));
        cameraY = clamp(cameraY, 0, Math.max(0, ROOM_HEIGHT - viewHeight));
    }

    private void interact() {
        updateNearbySpot();

        if (nearbySpot == null) {
            missionText = "Tidak ada objek penting di dekat sini.";
            return;
        }

        switch (nearbySpot.name) {
            case "Pintu Keluar":
                frame.showPanel("Level1");
                break;
            case "Papan Tulis":
                interactBoard();
                break;
            case "Laci Meja Dosen":
                interactTeacherDrawer();
                break;
            case "Dosen":
                interactTeacher();
                break;
            case "Rak Buku":
                missionText = "Rak buku: kunci palsu di lantai biasanya berbentuk wajik dengan tanda silang merah.";
                break;
            case "Tas Misterius":
                interactBag();
                break;
            default:
                missionText = "Objek ini belum bisa dipakai.";
                break;
        }
    }

    private void interactBoard() {
        GameManager.catatanKelasDitemukan = true;
        missionText = "Papan tulis: dalam OOP, fungsi di dalam class disebut method. Coba buka Laci Meja Dosen.";
    }

    private void interactBag() {
        if (GameManager.catatanKelasDitemukan) {
            missionText = "Tas itu sudah kosong. Petunjuknya sudah kamu catat.";
            return;
        }

        GameManager.catatanKelasDitemukan = true;
        missionText = "Catatan: atribut adalah data/ciri objek, method adalah operasi/tingkah laku objek.";
    }

    private void interactTeacher() {
        if (GameManager.kelasSelesai) {
            missionText = "Dosen: Kunci kelas sudah kamu ambil dari laci. Lanjut cari kunci lain di kampus.";
            return;
        }

        if (!GameManager.catatanKelasDitemukan) {
            missionText = "Dosen: baca papan tulis atau catatan dulu sebelum membuka laci meja.";
            return;
        }

        missionText = "Dosen: kunci asli ada di Laci Meja Dosen. Jawab kuis OOP untuk membukanya.";
    }

    private void interactTeacherDrawer() {
        if (GameManager.kelasSelesai) {
            missionText = "Laci Meja Dosen sudah kosong. Kunci kelas sudah kamu ambil.";
            return;
        }

        if (!GameManager.catatanKelasDitemukan) {
            missionText = "Laci terkunci. Baca petunjuk di papan tulis atau tas misterius dulu.";
            return;
        }

        String[] options = {
            "Method",
            "Atribut",
            "Package"
        };

        int answer = JOptionPane.showOptionDialog(
                this,
                "Puzzle Laci Meja Dosen:\nDalam OOP Java, fungsi yang berada di dalam class disebut apa?",
                "Puzzle OOP - Kelas Algoritma",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (answer == 0) {
            GameManager.kelasSelesai = true;
            GameManager.kunci++;
            missionText = "Puzzle benar. Kamu menemukan Kunci Kelas di Laci Meja Dosen. Kunci asli: "
                    + GameManager.kunci + "/" + GameManager.KUNCI_TARGET + ".";
            JOptionPane.showMessageDialog(this, "Kunci Kelas didapat dari Laci Meja Dosen!");
        } else if (answer >= 0) {
            GameManager.nyawa--;
            missionText = "Puzzle salah. Laci tetap terkunci. Nyawa tersisa: " + GameManager.nyawa + ".";

            if (GameManager.nyawa <= 0) {
                JOptionPane.showMessageDialog(this, "Nyawa habis. GAME OVER.");
                frame.showPanel("Menu");
            }
        }
    }

    private void collectFakeKey() {
        if (fakeKeyCollected || !player.intersects(fakeKey)) {
            return;
        }

        fakeKeyCollected = true;
        GameManager.kunciPalsu++;
        GameManager.nyawa--;
        missionText = "Kunci wajik merah di lantai itu palsu. Nyawa berkurang.";
        checkGameOver();
    }

    private void checkGameOver() {
        if (GameManager.nyawa <= 0) {
            JOptionPane.showMessageDialog(this, "Nyawa habis. GAME OVER.");
            frame.showPanel("Menu");
        }
    }

    private void stopMovement() {
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
    }

    private void drawScene(Graphics2D g) {
        Graphics2D world = (Graphics2D) g.create();
        world.translate(-cameraX, -cameraY);

        drawFloor(world);
        drawBoard(world);
        drawDoor(world);
        drawObstacles(world);
        drawSpots(world);
        drawTeacher(world);
        drawTeacherDrawer(world);
        drawFakeKey(world);
        drawPlayer(world);

        world.dispose();
    }

    private void drawFloor(Graphics2D g) {
        g.setColor(new Color(178, 167, 144));
        g.fillRect(0, 0, ROOM_WIDTH, ROOM_HEIGHT);

        g.setColor(new Color(157, 146, 125));
        for (int x = 40; x < ROOM_WIDTH; x += 80) {
            g.drawLine(x, 80, x, ROOM_HEIGHT - 28);
        }
        for (int y = 80; y < ROOM_HEIGHT; y += 80) {
            g.drawLine(40, y, ROOM_WIDTH - 40, y);
        }

        g.setColor(new Color(92, 78, 68));
        g.fillRect(0, 0, ROOM_WIDTH, 82);
        g.fillRect(0, 0, 40, ROOM_HEIGHT);
        g.fillRect(ROOM_WIDTH - 40, 0, 40, ROOM_HEIGHT);
        g.fillRect(0, ROOM_HEIGHT - 28, ROOM_WIDTH, 28);

        g.setColor(new Color(230, 230, 218));
        g.fillRect(130, 28, 170, 34);
        g.fillRect(800, 28, 170, 34);
        g.setColor(new Color(113, 151, 171));
        g.drawLine(215, 28, 215, 62);
        g.drawLine(885, 28, 885, 62);
    }

    private void drawBoard(Graphics2D g) {
        g.setColor(new Color(28, 89, 61));
        g.fillRoundRect(300, 92, 500, 86, 8, 8);
        g.setColor(new Color(224, 224, 205));
        g.setStroke(new BasicStroke(4));
        g.drawRoundRect(300, 92, 500, 86, 8, 8);

        g.setColor(new Color(235, 235, 220));
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("ALGORITMA = LANGKAH LOGIS", 392, 128);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("1. Pahami masalah  2. Susun langkah  3. Uji solusi", 345, 154);
    }

    private void drawDoor(Graphics2D g) {
        g.setColor(new Color(93, 59, 37));
        g.fillRoundRect(485, ROOM_HEIGHT - 72, 130, 54, 8, 8);
        g.setColor(new Color(44, 35, 28));
        g.drawRoundRect(485, ROOM_HEIGHT - 72, 130, 54, 8, 8);
        g.setColor(new Color(239, 199, 76));
        g.fillOval(590, ROOM_HEIGHT - 44, 8, 8);
    }

    private void drawObstacles(Graphics2D g) {
        for (RoomObject obstacle : obstacles) {
            Rectangle b = obstacle.bounds;

            g.setColor(new Color(0, 0, 0, 55));
            g.fillRoundRect(b.x + 5, b.y + 6, b.width, b.height, 8, 8);
            g.setColor(obstacle.color);
            g.fillRoundRect(b.x, b.y, b.width, b.height, 8, 8);
            g.setColor(new Color(62, 42, 32));
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(b.x, b.y, b.width, b.height, 8, 8);

            if ("Meja Mahasiswa".equals(obstacle.name)) {
                g.setColor(new Color(236, 232, 205));
                g.fillRect(b.x + 12, b.y + 10, 30, 22);
                g.setColor(new Color(70, 93, 146));
                g.fillRect(b.x + b.width - 32, b.y + 12, 18, 26);
            }
        }
    }

    private void drawSpots(Graphics2D g) {
        for (RoomSpot spot : spots) {
            if ("Dosen".equals(spot.name)) {
                continue;
            }

            Rectangle b = spot.bounds;
            boolean active = spot == nearbySpot;
            g.setColor(active ? new Color(250, 218, 92, 135) : new Color(255, 255, 255, 55));
            g.fillRoundRect(b.x, b.y, b.width, b.height, 12, 12);
            g.setColor(active ? new Color(119, 82, 20) : new Color(90, 80, 70, 120));
            g.setStroke(new BasicStroke(active ? 3 : 1));
            g.drawRoundRect(b.x, b.y, b.width, b.height, 12, 12);

            if (active) {
                drawCenteredText(g, "Tekan E", b.x, b.y + 24, b.width, new Color(48, 38, 20), Font.BOLD, 14);
            }
        }

        drawBag(g);
    }

    private void drawBag(Graphics2D g) {
        g.setColor(new Color(55, 77, 118));
        g.fillRoundRect(892, 614, 42, 38, 12, 12);
        g.setColor(new Color(35, 47, 76));
        g.drawRoundRect(892, 614, 42, 38, 12, 12);
        g.drawArc(900, 604, 26, 22, 0, 180);
    }

    private void drawTeacher(Graphics2D g) {
        int x = 535;
        int y = 226;

        g.setColor(new Color(0, 0, 0, 65));
        g.fillOval(x - 8, y + 44, 52, 13);
        g.setColor(new Color(80, 62, 135));
        g.fillRoundRect(x, y + 22, 34, 34, 12, 12);
        g.setColor(new Color(232, 188, 140));
        g.fillOval(x + 4, y, 26, 26);
        g.setColor(new Color(55, 44, 34));
        g.fillArc(x + 4, y - 3, 26, 16, 0, 180);
        g.setColor(new Color(35, 35, 35));
        g.drawOval(x + 4, y, 26, 26);

        RoomSpot teacherSpot = findSpot("Dosen");
        if (teacherSpot == nearbySpot) {
            Rectangle b = teacherSpot.bounds;
            g.setColor(new Color(250, 218, 92, 120));
            g.fillRoundRect(b.x, b.y, b.width, b.height, 12, 12);
            g.setColor(new Color(119, 82, 20));
            g.setStroke(new BasicStroke(3));
            g.drawRoundRect(b.x, b.y, b.width, b.height, 12, 12);
        }
    }

    private void drawFakeKey(Graphics2D g) {
        if (fakeKeyCollected) {
            return;
        }

        drawFakeKeyShape(g, fakeKey);
    }

    private void drawTeacherDrawer(Graphics2D g) {
        RoomSpot drawerSpot = findSpot("Laci Meja Dosen");
        if (drawerSpot == null) {
            return;
        }

        Rectangle b = drawerSpot.bounds;
        boolean active = drawerSpot == nearbySpot;

        g.setColor(active ? new Color(172, 111, 72) : new Color(114, 74, 47));
        g.fillRoundRect(b.x + 8, b.y + 20, b.width - 16, b.height - 28, 8, 8);
        g.setColor(new Color(53, 37, 29));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(b.x + 8, b.y + 20, b.width - 16, b.height - 28, 8, 8);
        g.drawLine(b.x + 14, b.y + 40, b.x + b.width - 14, b.y + 40);
        g.setColor(new Color(228, 194, 93));
        g.fillOval(b.x + b.width / 2 - 4, b.y + 31, 8, 8);
        g.fillOval(b.x + b.width / 2 - 4, b.y + 51, 8, 8);

        drawCenteredText(
                g,
                GameManager.kelasSelesai ? "Kosong" : "Laci",
                b.x,
                b.y + 2,
                b.width,
                Color.WHITE,
                Font.BOLD,
                12
        );
    }

    private void drawRealKeyShape(Graphics2D g, Rectangle b) {
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

    private void drawFakeKeyShape(Graphics2D g, Rectangle b) {
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
        g.setColor(new Color(0, 0, 0, 70));
        g.fillOval(player.x + 3, player.y + 24, player.width - 4, 12);
        g.setColor(new Color(48, 76, 171));
        g.fillRoundRect(player.x + 5, player.y + 12, 22, 22, 10, 10);
        g.setColor(new Color(238, 195, 146));
        g.fillOval(player.x + 6, player.y, 20, 20);
        g.setColor(new Color(30, 35, 65));
        g.fillArc(player.x + 6, player.y - 2, 20, 14, 0, 180);
        g.setColor(new Color(20, 20, 20));
        g.drawOval(player.x + 6, player.y, 20, 20);
    }

    private void drawHud(Graphics2D g) {
        g.setColor(new Color(20, 24, 28, 210));
        g.fillRoundRect(18, 16, 520, 116, 12, 12);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Level 1 - Interior Kelas Algoritma", 34, 42);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString(
                "Nyawa: " + GameManager.nyawa
                + "    Kunci asli: " + GameManager.kunci + "/" + GameManager.KUNCI_TARGET
                + "    Palsu: " + GameManager.kunciPalsu,
                34,
                68
        );
        drawWrappedText(g, missionText, 34, 90, 470, 2, 18);

        String helpText = "WASD/Arrow: jalan    E: interaksi    Esc: keluar kelas";
        int helpWidth = g.getFontMetrics().stringWidth(helpText);
        g.setColor(new Color(20, 24, 28, 190));
        g.fillRoundRect(18, getHeight() - 54, helpWidth + 32, 36, 12, 12);
        g.setColor(Color.WHITE);
        g.drawString(helpText, 34, getHeight() - 31);

        if (nearbySpot != null) {
            String prompt = "Dekat: " + nearbySpot.name + " | Tekan E";
            int promptWidth = g.getFontMetrics().stringWidth(prompt);
            g.setColor(new Color(244, 210, 82, 230));
            g.fillRoundRect((getWidth() - promptWidth) / 2 - 18, 18, promptWidth + 36, 38, 12, 12);
            g.setColor(new Color(45, 36, 20));
            g.setFont(new Font("Arial", Font.BOLD, 15));
            g.drawString(prompt, (getWidth() - promptWidth) / 2, 43);
        }
    }

    private void drawMiniMap(Graphics2D g) {
        int mapWidth = 170;
        int mapHeight = 118;
        int mapX = getWidth() - mapWidth - 18;
        int mapY = 16;
        double scaleX = (mapWidth - 20) / (double) ROOM_WIDTH;
        double scaleY = (mapHeight - 20) / (double) ROOM_HEIGHT;

        g.setColor(new Color(18, 22, 27, 205));
        g.fillRoundRect(mapX, mapY, mapWidth, mapHeight, 10, 10);
        g.setColor(new Color(178, 167, 144));
        g.fillRect(mapX + 10, mapY + 10, mapWidth - 20, mapHeight - 20);

        g.setColor(new Color(98, 70, 48));
        for (RoomObject obstacle : obstacles) {
            Rectangle b = obstacle.bounds;
            int x = mapX + 10 + (int) (b.x * scaleX);
            int y = mapY + 10 + (int) (b.y * scaleY);
            int w = Math.max(3, (int) (b.width * scaleX));
            int h = Math.max(3, (int) (b.height * scaleY));
            g.fillRect(x, y, w, h);
        }

        if (!fakeKeyCollected) {
            int kx = mapX + 10 + (int) (fakeKey.x * scaleX);
            int ky = mapY + 10 + (int) (fakeKey.y * scaleY);
            g.setColor(new Color(206, 70, 62));
            g.fillOval(kx, ky, 5, 5);
        }

        int px = mapX + 10 + (int) (player.x * scaleX);
        int py = mapY + 10 + (int) (player.y * scaleY);
        g.setColor(new Color(38, 83, 225));
        g.fillOval(px - 3, py - 3, 8, 8);

        g.setColor(Color.WHITE);
        g.drawRoundRect(mapX, mapY, mapWidth, mapHeight, 10, 10);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Denah", mapX + 12, mapY + 25);
    }

    private RoomSpot findSpot(String name) {
        for (RoomSpot spot : spots) {
            if (spot.name.equals(name)) {
                return spot;
            }
        }

        return null;
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

    private static class RoomObject {
        private final String name;
        private final Rectangle bounds;
        private final Color color;

        private RoomObject(String name, int x, int y, int width, int height, Color color) {
            this.name = name;
            this.bounds = new Rectangle(x, y, width, height);
            this.color = color;
        }
    }

    private static class RoomSpot {
        private final String name;
        private final Rectangle bounds;

        private RoomSpot(String name, int x, int y, int width, int height) {
            this.name = name;
            this.bounds = new Rectangle(x, y, width, height);
        }
    }
}
