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

public class CampusBuildingPanel extends JPanel {
    private static final int ROOM_WIDTH = 1050;
    private static final int ROOM_HEIGHT = 740;
    private static final int PLAYER_SIZE = 32;
    private static final int PLAYER_SPEED = 4;

    private final MainFrame frame;
    private final Timer gameLoop;
    private final Rectangle player;
    private final List<RoomObject> obstacles;
    private final List<RoomSpot> spots;
    private final List<InteriorKey> keys;
    private final String roomName;
    private final String clueSpotName;
    private final String clueText;
    private final String infoSpotName;
    private final String infoText;
    private final String storageSpotName;
    private final String realKeyName;
    private final String quizQuestion;
    private final String[] quizOptions;
    private final int quizCorrectIndex;
    private final Color floorColor;
    private final Color wallColor;
    private final Color furnitureColor;
    private final Color accentColor;

    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean clueFound;
    private boolean realKeyCollected;
    private int cameraX;
    private int cameraY;
    private String missionText;
    private RoomSpot nearbySpot;

    public static CampusBuildingPanel createLibrary(MainFrame frame) {
        return new CampusBuildingPanel(
                frame,
                "Perpustakaan",
                "Meja Referensi",
                "Katalog lama menandai rak 7 sebagai tempat kunci asli.",
                "Rak Buku",
                "Banyak buku terkunci, tapi satu catatan menyebut kunci palsu sering disebar di lorong.",
                "Laci Meja Referensi",
                "Kunci Arsip Perpustakaan",
                "Dalam OOP Java, class paling tepat diartikan sebagai apa?",
                new String[]{
                    "Blueprint/rancangan untuk membuat objek",
                    "Objek yang sudah pasti berjalan",
                    "Perintah untuk mencetak teks"
                },
                0,
                new Color(174, 165, 140),
                new Color(77, 69, 89),
                new Color(96, 66, 50),
                new Color(126, 132, 190)
        );
    }

    public static CampusBuildingPanel createLab(MainFrame frame) {
        return new CampusBuildingPanel(
                frame,
                "Lab Komputer",
                "Terminal Admin",
                "Terminal menampilkan pesan: kunci asli ada di meja server kanan.",
                "Rak Perangkat",
                "Kabel dan komponen berserakan. Tidak semua benda berkilau bisa dipercaya.",
                "Laci Meja Server",
                "Kunci Server Lab",
                "Kata kunci Java untuk membuat class anak dari class induk adalah...",
                new String[]{
                    "extends",
                    "return",
                    "new"
                },
                0,
                new Color(143, 162, 169),
                new Color(55, 76, 87),
                new Color(68, 84, 96),
                new Color(89, 164, 197)
        );
    }

    public static CampusBuildingPanel createCanteen(MainFrame frame) {
        return new CampusBuildingPanel(
                frame,
                "Kantin",
                "Kasir",
                "Kasir berbisik: kunci asli disimpan di dekat dapur, bukan di meja makan.",
                "Meja Makan",
                "Ada kunci mengilap di beberapa meja. Rasanya terlalu mudah.",
                "Laci Kasir",
                "Kunci Dapur Kantin",
                "Constructor di Java akan dieksekusi kapan?",
                new String[]{
                    "Saat objek dibuat",
                    "Saat objek dihapus manual",
                    "Saat program selesai dikompilasi"
                },
                0,
                new Color(182, 158, 118),
                new Color(82, 91, 61),
                new Color(123, 77, 45),
                new Color(115, 174, 97)
        );
    }

    public static CampusBuildingPanel createRectorate(MainFrame frame) {
        return new CampusBuildingPanel(
                frame,
                "Gedung Rektorat",
                "Lemari Arsip",
                "Arsip izin keluar kampus menyebut kunci asli berada di brankas kecil.",
                "Meja Resepsionis",
                "Resepsionis kosong, tapi ada peringatan: kunci tanpa label biasanya jebakan.",
                "Laci Arsip",
                "Kunci Rektorat",
                "Modifier private membuat member class...",
                new String[]{
                    "Hanya bisa diakses dari dalam class itu sendiri",
                    "Bisa diakses dari semua package",
                    "Wajib diwariskan ke semua class anak"
                },
                0,
                new Color(171, 151, 135),
                new Color(88, 61, 52),
                new Color(116, 78, 57),
                new Color(171, 103, 83)
        );
    }

    public static CampusBuildingPanel createDormitory(MainFrame frame) {
        return new CampusBuildingPanel(
                frame,
                "Asrama",
                "Papan Pengumuman",
                "Pengumuman malam: kunci asli ditaruh di loker penjaga asrama.",
                "Loker Lama",
                "Loker-loker lain sengaja diisi kunci palsu untuk mengecoh mahasiswa.",
                "Loker Penjaga",
                "Kunci Asrama",
                "Setter dan getter biasanya dipakai untuk...",
                new String[]{
                    "Mengisi dan mengambil data yang dibungkus private",
                    "Menghapus objek dari memori",
                    "Mengubah class menjadi package"
                },
                0,
                new Color(159, 142, 164),
                new Color(74, 60, 84),
                new Color(91, 70, 112),
                new Color(157, 119, 177)
        );
    }

    private CampusBuildingPanel(
            MainFrame frame,
            String roomName,
            String clueSpotName,
            String clueText,
            String infoSpotName,
            String infoText,
            String storageSpotName,
            String realKeyName,
            String quizQuestion,
            String[] quizOptions,
            int quizCorrectIndex,
            Color floorColor,
            Color wallColor,
            Color furnitureColor,
            Color accentColor
    ) {
        this.frame = frame;
        this.roomName = roomName;
        this.clueSpotName = clueSpotName;
        this.clueText = clueText;
        this.infoSpotName = infoSpotName;
        this.infoText = infoText;
        this.storageSpotName = storageSpotName;
        this.realKeyName = realKeyName;
        this.quizQuestion = quizQuestion;
        this.quizOptions = quizOptions;
        this.quizCorrectIndex = quizCorrectIndex;
        this.floorColor = floorColor;
        this.wallColor = wallColor;
        this.furnitureColor = furnitureColor;
        this.accentColor = accentColor;
        this.player = new Rectangle(510, 670, PLAYER_SIZE, PLAYER_SIZE);
        this.obstacles = new ArrayList<>();
        this.spots = new ArrayList<>();
        this.keys = new ArrayList<>();
        this.missionText = getRoomHint();

        setBackground(wallColor);
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
        player.setLocation(510, 670);
        stopMovement();
        clueFound = false;
        realKeyCollected = false;
        nearbySpot = null;
        missionText = getRoomHint();

        for (InteriorKey key : keys) {
            key.collected = false;
        }

        updateCamera();
        repaint();
    }

    private void buildRoom() {
        obstacles.clear();
        spots.clear();
        keys.clear();

        obstacles.add(new RoomObject("Meja Utama", 365, 150, 320, 60, furnitureColor));
        obstacles.add(new RoomObject("Rak Kiri", 75, 210, 82, 350, furnitureColor.darker()));
        obstacles.add(new RoomObject("Rak Kanan", 895, 210, 82, 350, furnitureColor.darker()));

        int[] deskXs = {230, 410, 590, 770};
        int[] deskYs = {320, 440, 560};
        for (int y : deskYs) {
            for (int x : deskXs) {
                obstacles.add(new RoomObject("Furnitur", x, y, 100, 54, furnitureColor));
            }
        }

        spots.add(new RoomSpot("Pintu Keluar", 450, 675, 150, 54));
        spots.add(new RoomSpot(clueSpotName, 405, 116, 240, 90));
        spots.add(new RoomSpot(infoSpotName, 78, 315, 95, 120));
        spots.add(new RoomSpot(storageSpotName, 835, 585, 120, 90));

        keys.add(new InteriorKey("Kunci Palsu", 245, 390, true));
        keys.add(new InteriorKey("Kunci Palsu", 690, 510, true));
        keys.add(new InteriorKey("Kunci Palsu", 185, 615, true));
    }

    private void installControls() {
        bindAction("pressed W", "moveUpOn" + roomName, () -> upPressed = true);
        bindAction("released W", "moveUpOff" + roomName, () -> upPressed = false);
        bindAction("pressed UP", "moveUpArrowOn" + roomName, () -> upPressed = true);
        bindAction("released UP", "moveUpArrowOff" + roomName, () -> upPressed = false);

        bindAction("pressed S", "moveDownOn" + roomName, () -> downPressed = true);
        bindAction("released S", "moveDownOff" + roomName, () -> downPressed = false);
        bindAction("pressed DOWN", "moveDownArrowOn" + roomName, () -> downPressed = true);
        bindAction("released DOWN", "moveDownArrowOff" + roomName, () -> downPressed = false);

        bindAction("pressed A", "moveLeftOn" + roomName, () -> leftPressed = true);
        bindAction("released A", "moveLeftOff" + roomName, () -> leftPressed = false);
        bindAction("pressed LEFT", "moveLeftArrowOn" + roomName, () -> leftPressed = true);
        bindAction("released LEFT", "moveLeftArrowOff" + roomName, () -> leftPressed = false);

        bindAction("pressed D", "moveRightOn" + roomName, () -> rightPressed = true);
        bindAction("released D", "moveRightOff" + roomName, () -> rightPressed = false);
        bindAction("pressed RIGHT", "moveRightArrowOn" + roomName, () -> rightPressed = true);
        bindAction("released RIGHT", "moveRightArrowOff" + roomName, () -> rightPressed = false);

        bindAction("pressed E", "interact" + roomName, this::interact);
        bindAction("pressed ESCAPE", "exit" + roomName, () -> frame.showPanel("Level1"));
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
        collectKeys();
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

    private void collectKeys() {
        for (InteriorKey key : keys) {
            if (key.collected || !player.intersects(key.bounds)) {
                continue;
            }

            collectFakeKey(key);
        }
    }

    private void collectFakeKey(InteriorKey key) {
        key.collected = true;
        GameManager.kunciPalsu++;
        GameManager.nyawa--;
        missionText = "Itu kunci palsu berbentuk wajik merah. Nyawa berkurang. Palsu: " + GameManager.kunciPalsu + ".";
        checkGameOver();
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

        if ("Pintu Keluar".equals(nearbySpot.name)) {
            frame.showPanel("Level1");
        } else if (clueSpotName.equals(nearbySpot.name)) {
            clueFound = true;
            missionText = clueText + " Kunci asli disembunyikan di " + storageSpotName + ".";
        } else if (infoSpotName.equals(nearbySpot.name)) {
            missionText = infoText + " Kunci palsu berbentuk wajik merah tersebar di antara meja dan rak.";
        } else if (storageSpotName.equals(nearbySpot.name)) {
            if (clueFound) {
                openStoragePuzzle();
            } else {
                missionText = storageSpotName + " terkunci. Cari petunjuk: " + clueSpotName + ".";
            }
        }
    }

    private String getRoomHint() {
        return "Hint: kunci asli disembunyikan di " + storageSpotName + ". Baca petunjuk, lalu jawab kuis OOP untuk membukanya.";
    }

    private void openStoragePuzzle() {
        if (realKeyCollected) {
            missionText = storageSpotName + " sudah kosong. Kunci asli ruangan ini sudah kamu ambil.";
            return;
        }

        int answer = JOptionPane.showOptionDialog(
                this,
                quizQuestion,
                "Puzzle OOP - " + roomName,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                quizOptions,
                quizOptions[quizCorrectIndex]
        );

        if (answer == quizCorrectIndex) {
            realKeyCollected = true;
            GameManager.kunci++;
            missionText = "Puzzle benar. Kamu menemukan " + realKeyName + " di " + storageSpotName + ".";
            JOptionPane.showMessageDialog(this, realKeyName + " didapat!");
        } else if (answer >= 0) {
            GameManager.nyawa--;
            missionText = "Puzzle salah. " + storageSpotName + " tetap terkunci. Nyawa tersisa: " + GameManager.nyawa + ".";
            checkGameOver();
        }
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
        drawDoor(world);
        drawFurniture(world);
        drawStorage(world);
        drawInteractionZones(world);
        drawKeys(world);
        drawPlayer(world);

        world.dispose();
    }

    private void drawFloor(Graphics2D g) {
        g.setColor(floorColor);
        g.fillRect(0, 0, ROOM_WIDTH, ROOM_HEIGHT);

        g.setColor(new Color(0, 0, 0, 28));
        for (int x = 40; x < ROOM_WIDTH; x += 80) {
            g.drawLine(x, 80, x, ROOM_HEIGHT - 28);
        }
        for (int y = 80; y < ROOM_HEIGHT; y += 80) {
            g.drawLine(40, y, ROOM_WIDTH - 40, y);
        }

        g.setColor(wallColor);
        g.fillRect(0, 0, ROOM_WIDTH, 82);
        g.fillRect(0, 0, 40, ROOM_HEIGHT);
        g.fillRect(ROOM_WIDTH - 40, 0, 40, ROOM_HEIGHT);
        g.fillRect(0, ROOM_HEIGHT - 28, ROOM_WIDTH, 28);

        g.setColor(new Color(230, 230, 218));
        g.fillRect(125, 28, 175, 34);
        g.fillRect(750, 28, 175, 34);
        g.setColor(accentColor.darker());
        g.drawLine(212, 28, 212, 62);
        g.drawLine(837, 28, 837, 62);

        g.setColor(new Color(255, 255, 255, 50));
        g.fillRect(120, 82, 190, 460);
        g.fillRect(745, 82, 190, 460);
    }

    private void drawDoor(Graphics2D g) {
        g.setColor(new Color(91, 58, 36));
        g.fillRoundRect(460, ROOM_HEIGHT - 72, 130, 54, 8, 8);
        g.setColor(new Color(44, 35, 28));
        g.drawRoundRect(460, ROOM_HEIGHT - 72, 130, 54, 8, 8);
        g.setColor(new Color(239, 199, 76));
        g.fillOval(565, ROOM_HEIGHT - 44, 8, 8);
    }

    private void drawFurniture(Graphics2D g) {
        for (RoomObject obstacle : obstacles) {
            Rectangle b = obstacle.bounds;

            g.setColor(new Color(0, 0, 0, 55));
            g.fillRoundRect(b.x + 5, b.y + 6, b.width, b.height, 8, 8);
            g.setColor(obstacle.color);
            g.fillRoundRect(b.x, b.y, b.width, b.height, 8, 8);
            g.setColor(new Color(49, 38, 32));
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(b.x, b.y, b.width, b.height, 8, 8);

            if ("Furnitur".equals(obstacle.name)) {
                g.setColor(accentColor);
                g.fillRect(b.x + 12, b.y + 10, 32, 20);
                g.setColor(new Color(236, 232, 205));
                g.fillRect(b.x + b.width - 32, b.y + 12, 18, 24);
            }
        }

        g.setColor(new Color(235, 235, 220));
        g.setFont(new Font("Arial", Font.BOLD, 18));
        drawCenteredText(g, roomName, 365, 168, 320, Color.WHITE, Font.BOLD, 18);
    }

    private void drawInteractionZones(Graphics2D g) {
        for (RoomSpot spot : spots) {
            Rectangle b = spot.bounds;
            boolean active = spot == nearbySpot;

            g.setColor(active ? new Color(250, 218, 92, 130) : new Color(255, 255, 255, 48));
            g.fillRoundRect(b.x, b.y, b.width, b.height, 12, 12);
            g.setColor(active ? new Color(119, 82, 20) : new Color(80, 70, 60, 115));
            g.setStroke(new BasicStroke(active ? 3 : 1));
            g.drawRoundRect(b.x, b.y, b.width, b.height, 12, 12);

            if (active) {
                drawCenteredText(g, "Tekan E", b.x, b.y + 24, b.width, new Color(48, 38, 20), Font.BOLD, 14);
            }
        }
    }

    private void drawStorage(Graphics2D g) {
        RoomSpot storageSpot = findSpot(storageSpotName);
        if (storageSpot == null) {
            return;
        }

        Rectangle b = storageSpot.bounds;
        boolean active = storageSpot == nearbySpot;

        g.setColor(active ? accentColor.brighter() : accentColor);
        if (storageSpotName.contains("Loker")) {
            g.fillRoundRect(b.x + 20, b.y + 8, b.width - 40, b.height - 12, 8, 8);
            g.setColor(new Color(40, 40, 45));
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(b.x + 20, b.y + 8, b.width - 40, b.height - 12, 8, 8);
            g.drawLine(b.x + b.width / 2, b.y + 12, b.x + b.width / 2, b.y + b.height - 8);
            g.fillOval(b.x + b.width / 2 - 10, b.y + 42, 6, 6);
            g.fillOval(b.x + b.width / 2 + 6, b.y + 42, 6, 6);
        } else {
            g.fillRoundRect(b.x + 10, b.y + 28, b.width - 20, b.height - 35, 8, 8);
            g.setColor(new Color(48, 36, 28));
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(b.x + 10, b.y + 28, b.width - 20, b.height - 35, 8, 8);
            g.drawLine(b.x + 16, b.y + 52, b.x + b.width - 16, b.y + 52);
            g.setColor(new Color(230, 204, 102));
            g.fillOval(b.x + b.width / 2 - 4, b.y + 43, 8, 8);
            g.fillOval(b.x + b.width / 2 - 4, b.y + 66, 8, 8);
        }

        if (realKeyCollected) {
            drawCenteredText(g, "Kosong", b.x, b.y + 4, b.width, Color.WHITE, Font.BOLD, 12);
        } else {
            drawCenteredText(g, "Kunci", b.x, b.y + 4, b.width, Color.WHITE, Font.BOLD, 12);
        }
    }

    private RoomSpot findSpot(String name) {
        for (RoomSpot spot : spots) {
            if (spot.name.equals(name)) {
                return spot;
            }
        }

        return null;
    }

    private void drawKeys(Graphics2D g) {
        for (InteriorKey key : keys) {
            if (key.collected) {
                continue;
            }

            if (key.fake) {
                drawFakeKey(g, key.bounds);
            } else {
                drawRealKey(g, key.bounds);
            }
        }
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
        g.fillRoundRect(18, 16, 552, 116, 12, 12);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Interior - " + roomName, 34, 42);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString(
                "Nyawa: " + GameManager.nyawa
                + "    Kunci asli: " + GameManager.kunci + "/" + GameManager.KUNCI_TARGET
                + "    Palsu: " + GameManager.kunciPalsu,
                34,
                68
        );
        drawWrappedText(g, missionText, 34, 90, 500, 2, 18);

        String helpText = "WASD/Arrow: jalan    E: interaksi    Esc: keluar";
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
        g.setColor(floorColor);
        g.fillRect(mapX + 10, mapY + 10, mapWidth - 20, mapHeight - 20);

        g.setColor(furnitureColor.darker());
        for (RoomObject obstacle : obstacles) {
            Rectangle b = obstacle.bounds;
            int x = mapX + 10 + (int) (b.x * scaleX);
            int y = mapY + 10 + (int) (b.y * scaleY);
            int w = Math.max(3, (int) (b.width * scaleX));
            int h = Math.max(3, (int) (b.height * scaleY));
            g.fillRect(x, y, w, h);
        }

        for (InteriorKey key : keys) {
            if (!key.collected) {
                int x = mapX + 10 + (int) (key.bounds.x * scaleX);
                int y = mapY + 10 + (int) (key.bounds.y * scaleY);
                g.setColor(key.fake ? new Color(206, 70, 62) : new Color(82, 190, 111));
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
        g.drawString("Denah", mapX + 12, mapY + 25);
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

    private static class InteriorKey {
        private final String name;
        private final Rectangle bounds;
        private final boolean fake;
        private boolean collected;

        private InteriorKey(String name, int x, int y, boolean fake) {
            this.name = name;
            this.bounds = new Rectangle(x, y, 44, 32);
            this.fake = fake;
        }
    }
}
