package escapefromcampus;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.Timer;

public class CampusBuildingPanel extends JPanel {
    private static final int ROOM_WIDTH = 1050;
    private static final int ROOM_HEIGHT = 740;
    private static final int PLAYER_SIZE = 32;
    private static final int PLAYER_SPEED = 4;
    private static final int PUZZLE_TARGET = 3; 

    private final MainFrame frame;
    private final Timer gameLoop;
    private final Rectangle player;
    private final List<RoomObject> obstacles;
    private final List<RoomSpot> spots;
    private final List<InteriorKey> keys;
    private final List<PatrolEnemy> enemies;
    
    private final List<PuzzleTask> puzzleTasks;
    private final Map<String, PuzzleTask> activePuzzles; 

    private final String roomName;
    private final String clueSpotName;
    private final String clueText;
    private final String infoSpotName;
    private final String infoText;
    private final String storageSpotName;
    private final String realKeyName;
    
    private final Color floorColor;
    private final Color wallColor;
    private final Color furnitureColor;
    private final Color accentColor;
    private final int roomStyle;

    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean clueFound;
    private boolean realKeyCollected;
    private int puzzlesSolved;
    private int enemyHitCooldown;
    private int cameraX;
    private int cameraY;
    private RoomSpot nearbySpot;

    // --- VARIABEL ANIMASI SPRITE ---
    private Image up1, up2, down1, down2, left1, left2, right1, right2;
    private String direction = "down"; 
    private int spriteCounter = 0;     
    private int spriteNum = 1;

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
                new PuzzleTask[]{
                    new PuzzleTask(
                        "Konteks: Anda menemukan sebuah buku tebal yang diselipkan sebuah microchip tipis di antara dua halaman. Untuk mengambilnya tanpa memicu alarm, Anda harus menekan nomor kedua halaman tersebut pada panel.",
                        "Buku tersebut terbuka lebar, dan bagian tengahnya sengaja dicoret. Petunjuk dari pustakawan: 'Jumlah dari nomor halaman kiri dan halaman kanan yang saling berhadapan ini adalah 333.'\nHalaman berapakah yang ada di sisi kiri?",
                        new String[]{"165", "166", "167", "333"}, 1,
                        "Nomor halaman yang berhadapan selalu berurutan (x dan x+1). Maka x + (x + 1) = 333 -> 2x = 332 -> x = 166. Halaman sebelah kiri selalu bernomor genap, yaitu 166."
                    ),
                    new PuzzleTask(
                        "Konteks: Pintu rahasia hanya akan terbuka jika rak-rak di Lorong Barat berada di urutan klasifikasi yang benar dari rak 1 hingga 4. Anda harus menempatkan rak Komik ke posisi yang tepat.",
                        "Rak Komik harus tepat di antara Novel dan Sejarah.\nRak Sains terpaku mati di posisi nomor 4.\nRak Sejarah dilarang di ujung paling kiri (posisi 1).\nDi posisi nomor berapakah Anda menempatkan rak Komik?",
                        new String[]{"Posisi 1", "Posisi 2", "Posisi 3", "Posisi 4"}, 1,
                        "Sains di posisi 4. Tersisa 1, 2, 3. Komik di tengah Novel dan Sejarah (mengisi 1, 2, 3). Karena Sejarah tidak boleh di nomor 1, maka Sejarah di 3, Novel di 1. Komik berada di tengahnya, yaitu Posisi 2."
                    ),
                    new PuzzleTask(
                        "Konteks: Anda tiba di gerbang turnstile keluar. Sistem tidak meminta kartu, melainkan jumlah tagihan denda terakhir (tanpa angka nol terakhir).",
                        "Denda adalah Rp 500/hari. Buku dipinjam tgl 1 Maret (maksimal 1 minggu / kembali tgl 8 Maret). Tapi dikembalikan tgl 14 Maret.\nBerapakah denda totalnya?",
                        new String[]{"2500", "3000", "3500", "4000"}, 1,
                        "Batas waktu adalah 8 Maret. Dikembalikan 14 Maret. Keterlambatan = 14 - 8 = 6 hari. Denda = 6 x Rp 500 = Rp 3000."
                    )
                },
                0, new Color(174, 165, 140), new Color(77, 69, 89), new Color(96, 66, 50), new Color(126, 132, 190)
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
                new PuzzleTask[]{
                    new PuzzleTask(
                        "Konteks: Sistem komputer utama sedang diretas malware. Anda harus menekan tombol abort di menit yang tepat saat file virus memenuhi 'setengah' dari kapasitas server.",
                        "Virus menggandakan dirinya (2 kali lipat) setiap 1 menit. Jika server akan penuh (100%) dalam waktu 10 menit, pada menit ke berapakah server tersebut terisi tepat setengah (50%)?",
                        new String[]{"Menit ke-5", "Menit ke-9", "Menit ke-10", "Menit ke-20"}, 1,
                        "Karena virus berlipat ganda setiap menitnya, maka tepat satu menit sebelum penuh (menit ke-9), kapasitasnya pasti baru setengah."
                    ),
                    new PuzzleTask(
                        "Konteks: Pintu lab hanya terbuka jika lampu hijau menyala. Ada 3 sakelar (A, B, C). Pintu terbuka jika hanya tepat DUA sakelar ON.",
                        "Buku manual:\n1. Butuh 2 sakelar ON.\n2. Jika A = ON, maka B otomatis OFF.\nSaat ini, sakelar C rusak dan permanen tertahan di OFF. Apa yang harus Anda lakukan pada sakelar A dan B?",
                        new String[]{"A ON, B ON", "A OFF, B OFF", "A OFF, B ON", "Sistem Gagal"}, 3,
                        "C sudah OFF. Agar 2 sakelar ON, maka A dan B harus sama-sama ON. Namun aturan 2 melarang A dan B ON bersamaan. Jadi mustahil pintu bisa terbuka lewat sakelar (Sistem Gagal)."
                    ),
                    new PuzzleTask(
                        "Konteks: Kabel jaringan terpotong dan ditandai angka biner. Untuk menyambungnya, tebak urutan kabel kelima.",
                        "Kabel 1: 0010\nKabel 2: 0100\nKabel 3: 0110\nKabel 4: 1000\nKabel 5: ????",
                        new String[]{"0111", "1001", "1010", "1100"}, 2,
                        "Pola biner ini mewakili bilangan genap yang terus bertambah dua: 2, 4, 6, 8. Angka genap selanjutnya adalah 10, yang ditulis dalam biner sebagai 1010."
                    )
                },
                1, new Color(143, 162, 169), new Color(55, 76, 87), new Color(68, 84, 96), new Color(89, 164, 197)
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
                new PuzzleTask[]{
                    new PuzzleTask(
                        "Konteks: Ibu Kantin mau memberi token jika Anda bisa menakar tepat 400 ml air sirop ke dalam mangkuk besar. Namun ia hanya punya dua gelas takar berukuran 500 ml dan 300 ml.",
                        "Berapa kali Anda harus mengisi penuh gelas 500 ml dari keran untuk bisa mendapatkan tepat 400 ml air di akhir proses?",
                        new String[]{"1 kali", "2 kali", "3 kali", "4 kali"}, 1,
                        "Isi gelas 500 (Isi 1). Tuang ke gelas 300. Sisa di gelas 500 = 200 ml. Buang isi 300, tuang 200 ml tadi ke 300. Isi penuh lagi gelas 500 (Isi 2). Tuang ke 300 sampai penuh (butuh 100 ml). Sisa di gelas besar tepat 400 ml. Butuh 2 kali pengisian penuh."
                    ),
                    new PuzzleTask(
                        "Konteks: Anda harus menyusup ke ruang penyimpanan, kulkas digembok dengan sandi harga.",
                        "Kemarin: 2 Nasi Goreng + 1 Es Teh = Rp 25.000.\nHari ini: 1 Nasi Goreng + 2 Es Teh = Rp 20.000.\nBerapakah harga 1 porsi Nasi Goreng? (Dalam ribuan)",
                        new String[]{"5", "10", "15", "20"}, 1,
                        "Total keduanya: 3 Nasi + 3 Es = Rp 45.000 -> 1 Nasi + 1 Es = Rp 15.000. Dari pesanan hari ini: (1 Nasi + 1 Es) + 1 Es = 20.000. Berarti 1 Es Teh = Rp 5.000. Maka 1 Nasi Goreng = Rp 10.000."
                    ),
                    new PuzzleTask(
                        "Konteks: Salah satu dari 4 orang di meja bundar (Rina, Toni, Siska, Dimas) membawa sekering. Target adalah yang makan Bakso.",
                        "Rina berhadapan dengan orang yg makan Bakso.\nToni duduk di kanan Rina.\nSiska makan Soto & tidak berhadapan dengan Toni.\nSiapa yang membawa sekering tersebut?",
                        new String[]{"Rina", "Toni", "Siska", "Dimas"}, 3,
                        "Siska tidak di depan Toni, Rina di depan si Target, berarti Siska harus di depan Rina. Tapi Siska makan Soto, bukan Bakso. Berarti Siska duduk di kiri Rina. Tersisa posisi di depan Rina untuk Dimas. Dimas-lah yang makan bakso."
                    )
                },
                2, new Color(182, 158, 118), new Color(82, 91, 61), new Color(123, 77, 45), new Color(115, 174, 97)
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
                new PuzzleTask[]{
                    new PuzzleTask(
                        "Konteks: Anda harus memindahkan 3 brankas ke lantai bawah dengan Lift VIP. Kapasitas angkut lift maksimal 150 kg (lift tak bisa turun tanpa Anda).",
                        "Berat Anda 60kg. Brankas Emas (60kg), Perak (50kg), dan Perunggu (40kg). Berapa kali minimum lift harus bergerak TURUN agar semua brankas sampai ke bawah?",
                        new String[]{"1 kali", "2 kali", "3 kali", "4 kali"}, 1,
                        "Turun 1: Anda (60) + Emas (60) = 120 kg. (Anda naik lagi). Turun 2: Anda (60) + Perak (50) + Perunggu (40) = 150 kg. Total perjalanan turun minimum adalah 2 kali."
                    ),
                    new PuzzleTask(
                        "Konteks: Rektor menahan Anda dengan Surat Tugas yang ia tunjukkan. Anda harus membuktikan bahwa surat itu palsu.",
                        "Di bagian bawah surat tertulis:\n'Ditetapkan di Surabaya, pada tanggal 29 Februari 2023.'\nMengapa surat ini terbukti palsu?",
                        new String[]{"Surabaya bukan ibukota", "Bulan Februari libur panjang", "Tahun 2023 bukan tahun kabisat", "Rektor tidak punya wewenang"}, 2,
                        "Bulan Februari pada tahun 2023 hanya memiliki 28 hari, bukan tahun kabisat. Sehingga tanggal 29 Februari 2023 tidak pernah ada."
                    ),
                    new PuzzleTask(
                        "Konteks: Asisten Rektor butuh kode brankas. Kodenya adalah 'Jam dimulainya Rapat Senat'.",
                        "Rektor pergi jam 12:00 setelah rapat beruntun. Agendanya: Rapat Keuangan (1 jam), Mahasiswa (30 mnt), Senat (45 mnt). Urutannya: Senat lalu Mahasiswa lalu Keuangan. Pukul berapa Rapat Senat dimulai?",
                        new String[]{"0830", "0900", "0945", "1015"}, 2,
                        "Total waktu = 60 + 30 + 45 = 135 menit (2 jam 15 menit). Jam 12:00 mundur 2 jam 15 menit = 09:45. Senat adalah acara pertama, jadi dimulai pukul 09:45."
                    )
                },
                3, new Color(171, 151, 135), new Color(88, 61, 52), new Color(116, 78, 57), new Color(171, 103, 83)
        );
    }

    public static CampusBuildingPanel createDormitory(MainFrame frame) {
        return new CampusBuildingPanel(
                frame,
                "Gedung Kelas",
                "Papan Pengumuman",
                "Pengumuman malam: kunci asli ditaruh di loker penjaga asrama.",
                "Loker Lama",
                "Loker-loker lain sengaja diisi kunci palsu untuk mengecoh mahasiswa.",
                "Loker Penjaga",
                "Kunci Gedung Kelas",
                new PuzzleTask[]{
                    new PuzzleTask(
                        "Konteks: Anda perlu mengambil ID Card dari loker ketua kelas. Loker digembok PIN 2 digit. Petunjuk: Kodenya adalah nomor urut presensi anak yang bolos hari ini.",
                        "Total 30 mahasiswa. Nomor urutnya adalah angka genap, habis dibagi 3, dan jika angka-angka penyusunnya dijumlahkan, hasilnya adalah 6. Berapakah PIN loker tersebut?",
                        new String[]{"12", "18", "24", "30"}, 2,
                        "Angka genap di bawah 30 yang habis dibagi 3: 6, 12, 18, 24. Dari angka tersebut, hanya 2 + 4 yang jumlahnya 6. Jawabannya 24."
                    ),
                    new PuzzleTask(
                        "Konteks: Dosen menyembunyikan kunci di salah satu dari 4 meja ujian yang tersusun sebaris ke belakang (Posisi 1, 2, 3, 4).",
                        "Meja Budi tepat di depan Cici. Meja Deni paling belakang (4). Meja Anton TIDAK bersebelahan dengan Deni.\nJika kunci ada di meja Cici, di posisi berapakah kuncinya?",
                        new String[]{"Posisi 1", "Posisi 2", "Posisi 3", "Posisi 4"}, 2,
                        "Deni di 4. Anton tidak boleh di 3, maka Anton di 1. Sisa 2 dan 3 untuk Budi dan Cici. Karena Budi di depan Cici, Budi di 2 dan Cici di 3."
                    ),
                    new PuzzleTask(
                        "Konteks: Keypad pintu meminta hasil dari pola aneh di papan tulis.",
                        "Jika:\n5 -> 3 = 18\n4 -> 2 = 10\n7 -> 4 = 32\nMaka berapakah hasil dari 6 -> 5 = ?",
                        new String[]{"30", "35", "40", "42"}, 1,
                        "Polanya adalah (A x B) + B. Maka (6 x 5) + 5 = 30 + 5 = 35."
                    )
                },
                4, new Color(159, 142, 164), new Color(74, 60, 84), new Color(91, 70, 112), new Color(157, 119, 177)
        );
    }

    private CampusBuildingPanel(
            MainFrame frame, String roomName, String clueSpotName, String clueText,
            String infoSpotName, String infoText, String storageSpotName, String realKeyName,
            PuzzleTask[] puzzleTasks, int roomStyle, Color floorColor, Color wallColor,
            Color furnitureColor, Color accentColor
    ) {
        this.frame = frame;
        this.roomName = roomName;
        this.clueSpotName = clueSpotName;
        this.clueText = clueText;
        this.infoSpotName = infoSpotName;
        this.infoText = infoText;
        this.storageSpotName = storageSpotName;
        this.realKeyName = realKeyName;
        
        this.puzzleTasks = new ArrayList<>();
        Collections.addAll(this.puzzleTasks, puzzleTasks);
        this.activePuzzles = new HashMap<>();

        this.roomStyle = roomStyle;
        this.floorColor = floorColor;
        this.wallColor = wallColor;
        this.furnitureColor = furnitureColor;
        this.accentColor = accentColor;
        
        this.player = new Rectangle(510, 670, PLAYER_SIZE, PLAYER_SIZE);
        this.obstacles = new ArrayList<>();
        this.spots = new ArrayList<>();
        this.keys = new ArrayList<>();
        this.enemies = new ArrayList<>();

        setBackground(wallColor);
        setFocusable(true);

        buildRoom();
        installControls();

        gameLoop = new Timer(16, e -> {
            updateGame();
            repaint();
        });
        gameLoop.setCoalesce(true);
        
        getPlayerImage(); // Muat sprite karakter
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
            javax.swing.SwingUtilities.invokeLater(this::requestFocusInWindow);
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
        puzzlesSolved = 0;
        enemyHitCooldown = 0;
        nearbySpot = null;

        for (InteriorKey key : keys) {
            key.collected = false;
        }
        for (PuzzleTask puzzleTask : puzzleTasks) {
            puzzleTask.solved = false;
        }
        for (PatrolEnemy enemy : enemies) {
            enemy.reset();
        }
        
        getPlayerImage();
        assignRandomPuzzles();
        updateCamera();
        repaint();
    }

    private void buildRoom() {
        obstacles.clear();
        spots.clear();
        keys.clear();
        enemies.clear();

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
        spots.add(new RoomSpot("Akses 1", 215, 265, 110, 52));
        spots.add(new RoomSpot("Akses 2", 760, 265, 110, 52));
        spots.add(new RoomSpot("Akses 3", 215, 500, 110, 52));
        spots.add(new RoomSpot("Akses 4", 760, 500, 110, 52));
        spots.add(new RoomSpot("Akses 5", 470, 610, 110, 52));
        
        assignRandomPuzzles();

        keys.add(new InteriorKey("Kunci Palsu", 245, 390, true));
        keys.add(new InteriorKey("Kunci Palsu", 690, 510, true));
        keys.add(new InteriorKey("Kunci Palsu", 185, 615, true));

        enemies.add(new PatrolEnemy(185, 248, 34, 34, 185, 830, 248, 248, 2, 0));
        enemies.add(new PatrolEnemy(830, 408, 34, 34, 185, 830, 408, 408, -3, 0));
        enemies.add(new PatrolEnemy(505, 240, 34, 34, 505, 505, 240, 630, 0, 2));
    }

    private void assignRandomPuzzles() {
        activePuzzles.clear();
        List<String> aksesNames = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            aksesNames.add("Akses " + i);
        }
        Collections.shuffle(aksesNames);
        
        for (int i = 0; i < puzzleTasks.size(); i++) {
            activePuzzles.put(aksesNames.get(i), puzzleTasks.get(i));
        }
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
        updateEnemies();
        collectKeys();
        updateNearbySpot();
        updateCamera();
    }

    private void updateEnemies() {
        for (PatrolEnemy enemy : enemies) {
            enemy.update();

            if (enemyHitCooldown == 0 && player.intersects(enemy.bounds)) {
                GameManager.nyawa--;
                enemyHitCooldown = 75;
                player.setLocation(510, 670);
                stopMovement();
                showCustomDialog("AWAS!\n\nAnda tertangkap penjaga anomali!\nHindari jalur patrolinya.\n\nNyawa tersisa: " + GameManager.nyawa, null);
                checkGameOver();
            }
        }
        if (enemyHitCooldown > 0) {
            enemyHitCooldown--;
        }
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
            if (spriteCounter > 10) { 
                if (spriteNum == 1) {
                    spriteNum = 2;
                } else if (spriteNum == 2) {
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }
        } else {
            spriteNum = 1;
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

        if (nextPosition.x < 40 || nextPosition.y < 80
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

    // --- MENGGAMBAR SPRITE KARAKTER PADA LAYER ---
    private void drawPlayer(Graphics2D g) {
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
            g.fillOval(player.x + 3, player.y + player.height - 8, player.width - 4, 12);
            
            // Gambar sprite
            g.drawImage(image, player.x - 6, player.y - 14, player.width + 12, player.height + 18, null);
        } else {
            // Fallback cadangan
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
        showCustomDialog("JEBAKAN!\n\nItu kunci jebakan berbentuk wajik merah!\nNyawa berkurang.\nKunci palsu terkumpul: " + GameManager.kunciPalsu, null);
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

    // --- METODE CUSTOM DIALOG (Pop Up Abu Transparan) ---
    private int showCustomDialog(String message, String[] options) {
        JDialog dialog = new JDialog(frame, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0)); 
        dialog.setSize(800, 500); 
        dialog.setLocationRelativeTo(frame);

        JPanel bgPanel = new JPanel(new BorderLayout(20, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 210)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setStroke(new BasicStroke(3));
                g2.setColor(new Color(255, 255, 255, 100));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
            }
        };
        bgPanel.setOpaque(false);
        bgPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40)); 

        JTextArea textArea = new JTextArea(message);
        textArea.setFont(new Font("Arial", Font.BOLD, 18));
        textArea.setForeground(Color.WHITE);
        textArea.setOpaque(false);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFocusable(false);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setOpaque(false);

        bgPanel.add(scrollPane, BorderLayout.CENTER);

        final int[] selectedAnswer = {-1};

        if (options != null && options.length > 0) {
            JPanel btnPanel = new JPanel(new GridLayout(2, 2, 20, 20));
            btnPanel.setOpaque(false);
            for (int i = 0; i < options.length; i++) {
                int index = i;
                JButton btn = new JButton("<html><center>" + options[i] + "</center></html>");
                btn.setFont(new Font("Arial", Font.BOLD, 18));
                btn.setBackground(new Color(60, 90, 140));
                btn.setForeground(Color.WHITE);
                btn.setFocusPainted(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.addActionListener(e -> {
                    selectedAnswer[0] = index;
                    dialog.dispose();
                });
                btnPanel.add(btn);
            }
            bgPanel.add(btnPanel, BorderLayout.SOUTH);
        } else {
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            btnPanel.setOpaque(false);
            JButton btn = new JButton("LANJUT");
            btn.setFont(new Font("Arial", Font.BOLD, 18));
            btn.setBackground(new Color(50, 150, 80));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> dialog.dispose());
            btnPanel.add(btn);
            bgPanel.add(btnPanel, BorderLayout.SOUTH);
        }

        dialog.add(bgPanel);
        dialog.setVisible(true);

        return selectedAnswer[0];
    }
    // ---------------------------------------------------

    private void interact() {
        updateNearbySpot();

        if (nearbySpot == null) {
            return;
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            if ("Pintu Keluar".equals(nearbySpot.name)) {
                frame.showPanel("Level1");
            } else if (clueSpotName.equals(nearbySpot.name)) {
                clueFound = true;
                showCustomDialog("PETUNJUK UTAMA\n\n" + clueText + "\n\nKunci asli disembunyikan di " + storageSpotName + ".", null);
            } else if (infoSpotName.equals(nearbySpot.name)) {
                showCustomDialog("INFORMASI\n\n" + infoText + "\n\nKunci palsu berbentuk wajik merah tersebar di antara meja dan rak.", null);
            } else if (nearbySpot.name.startsWith("Akses ")) {
                PuzzleTask task = activePuzzles.get(nearbySpot.name);
                if (task != null) {
                    openRoomPuzzle(task);
                } else {
                    showCustomDialog("KOSONG\n\n Fun Fact: UNESA dulunya bernama IKIP Surabaya sebelum berubah menjadi Universitas", null);
                }
            } else if (storageSpotName.equals(nearbySpot.name)) {
                if (clueFound && puzzlesSolved >= PUZZLE_TARGET) {
                    openStoragePuzzle();
                } else if (!clueFound) {
                    showCustomDialog("TERKUNCI\n\n" + storageSpotName + " Terkunci!\nFun Fact: UNESA memiliki dua kampus utama, yaitu Kampus Lidah Wetan dan Kampus Ketintang. " + clueSpotName + ".", null);
                } else {
                    showCustomDialog("TERKUNCI\n\n" + storageSpotName + " terkunci.\nAnda harus menyelesaikan ke-" + PUZZLE_TARGET + " pertanyaan terlebih dahulu.\nProgres saat ini: " + puzzlesSolved + "/" + PUZZLE_TARGET, null);
                }
            }
        });
    }

    private void openRoomPuzzle(PuzzleTask puzzleTask) {
        if (puzzleTask.solved) {
            showCustomDialog("AKSES SELESAI\n\nAkses ini sudah berhasil Anda perbaiki.", null);
            return;
        }

        // Gabungkan Konteks dan Pertanyaan dalam satu Pop Up
        String puzzleText = puzzleTask.konteks + "\n\nPERTANYAAN:\n" + puzzleTask.soal;
        
        int answer = showCustomDialog(puzzleText, puzzleTask.options);

        // Tampilkan Jawaban dan Logika
        if (answer == puzzleTask.correctIndex) {
            puzzleTask.solved = true;
            puzzlesSolved++;
            
            String successMsg = "JAWABAN BENAR!\n\nPenjelasan:\n" + puzzleTask.logika;
            if (puzzlesSolved >= PUZZLE_TARGET) {
                successMsg += "\n\nSemua teka-teki logika selesai!\nSistem keamanan terbuka, segera periksa " + storageSpotName + "!";
            }
            showCustomDialog(successMsg, null);
            
        } else if (answer >= 0) { 
            GameManager.nyawa--;
            String failMsg = "JAWABAN SALAH!\n\nJawaban yang benar:\n" + puzzleTask.options[puzzleTask.correctIndex] + "\n\nPenjelasan:\n" + puzzleTask.logika + "\n\nNyawa berkurang! Sisa nyawa: " + GameManager.nyawa;
            showCustomDialog(failMsg, null);
            checkGameOver();
        }
    }

    private void openStoragePuzzle() {
        if (realKeyCollected) {
            showCustomDialog("KOSONG\n\n" + storageSpotName + " sudah kosong. Kunci asli sudah diambil.", null);
            return;
        }

        realKeyCollected = true;
        GameManager.kunci++;
        showCustomDialog("BERHASIL!\n\nAnda berhasil menemukan " + realKeyName + " di dalam " + storageSpotName + "!\n\nKunci Asli Terkumpul: " + GameManager.kunci + "/" + GameManager.KUNCI_TARGET, null);
    }

    private void checkGameOver() {
        if (GameManager.nyawa <= 0) {
            showCustomDialog("GAME OVER\n\nNyawa Anda habis! Anda gagal kabur.", null);
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
        drawRoomIdentityDecor(world);
        drawDoor(world);
        drawFurniture(world);
        drawStorage(world);
        drawInteractionZones(world);
        drawKeys(world);
        drawEnemies(world);
        drawPlayer(world);

        world.dispose();
    }

    private void drawFloor(Graphics2D g) {
        g.setColor(floorColor);
        g.fillRect(0, 0, ROOM_WIDTH, ROOM_HEIGHT);

        g.setColor(new Color(0, 0, 0, 28));
        for (int x = 40; x < ROOM_WIDTH; x += 80) g.drawLine(x, 80, x, ROOM_HEIGHT - 28);
        for (int y = 80; y < ROOM_HEIGHT; y += 80) g.drawLine(40, y, ROOM_WIDTH - 40, y);

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

    private void drawRoomIdentityDecor(Graphics2D g) {
        g.setStroke(new BasicStroke(2));

        if (roomStyle == 0) {
            for (int y = 230; y <= 520; y += 58) {
                g.setColor(new Color(73, 49, 38));
                g.fillRect(86, y, 58, 18);
                g.fillRect(908, y, 58, 18);
                g.setColor(new Color(218, 184, 96));
                g.fillRect(91, y + 3, 48, 4);
                g.fillRect(913, y + 3, 48, 4);
            }
            drawCenteredText(g, "PERPUSTAKAAN", 365, 96, 320, Color.WHITE, Font.BOLD, 16);
        } else if (roomStyle == 1) {
            for (int x = 260; x <= 740; x += 160) {
                g.setColor(new Color(33, 45, 54));
                g.fillRoundRect(x, 330, 58, 36, 6, 6);
                g.setColor(new Color(92, 210, 235));
                g.fillRect(x + 8, 337, 42, 18);
                g.setColor(new Color(24, 30, 36));
                g.fillRect(x + 22, 366, 14, 12);
            }
            drawCenteredText(g, "LAB KOMPUTER", 365, 96, 320, Color.WHITE, Font.BOLD, 16);
        } else if (roomStyle == 2) {
            for (int x = 230; x <= 770; x += 180) {
                g.setColor(new Color(228, 217, 190));
                g.fillOval(x + 20, 332, 36, 18);
                g.setColor(new Color(198, 80, 52));
                g.fillOval(x + 30, 336, 12, 8);
            }
            g.setColor(new Color(58, 54, 45));
            g.fillRoundRect(828, 154, 96, 46, 8, 8);
            drawCenteredText(g, "DAPUR", 828, 160, 96, Color.WHITE, Font.BOLD, 13);
        } else if (roomStyle == 3) {
            for (int x = 230; x <= 770; x += 180) {
                g.setColor(new Color(95, 65, 52));
                g.fillRect(x + 12, 322, 34, 42);
                g.setColor(new Color(224, 207, 148));
                g.drawLine(x + 18, 334, x + 40, 334);
                g.drawLine(x + 18, 348, x + 40, 348);
            }
            drawCenteredText(g, "ARSIP REKTORAT", 365, 96, 320, Color.WHITE, Font.BOLD, 16);
        } else {
            for (int x = 205; x <= 805; x += 200) {
                g.setColor(new Color(76, 66, 94));
                g.fillRoundRect(x, 322, 42, 62, 6, 6);
                g.setColor(new Color(205, 196, 225));
                g.fillOval(x + 18, 350, 6, 6);
            }
            drawCenteredText(g, "GEDUNG KELAS", 365, 96, 320, Color.WHITE, Font.BOLD, 16);
        }
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
            boolean isSpotAkses = spot.name.startsWith("Akses ");
            
            boolean hasPuzzle = activePuzzles.containsKey(spot.name);
            boolean solvedPuzzle = hasPuzzle && activePuzzles.get(spot.name).solved;

            if (isSpotAkses && solvedPuzzle) {
                g.setColor(active ? new Color(102, 210, 124, 145) : new Color(75, 178, 98, 88));
            } else if (isSpotAkses) {
                g.setColor(active ? new Color(92, 195, 236, 150) : new Color(92, 195, 236, 70));
            } else {
                g.setColor(active ? new Color(250, 218, 92, 130) : new Color(255, 255, 255, 48));
            }
            g.fillRoundRect(b.x, b.y, b.width, b.height, 12, 12);
            g.setColor(active ? new Color(119, 82, 20) : new Color(80, 70, 60, 115));
            g.setStroke(new BasicStroke(active ? 3 : 1));
            g.drawRoundRect(b.x, b.y, b.width, b.height, 12, 12);

            if (isSpotAkses) {
                String label = solvedPuzzle ? "OK" : spot.name;
                drawCenteredText(g, label, b.x, b.y + 8, b.width,
                        solvedPuzzle ? new Color(20, 80, 35) : new Color(25, 62, 82), Font.BOLD, 13);
            }
        }
    }

    private void drawStorage(Graphics2D g) {
        RoomSpot storageSpot = null;
        for (RoomSpot spot : spots) {
            if (spot.name.equals(storageSpotName)) {
                storageSpot = spot;
                break;
            }
        }
        if (storageSpot == null) return;

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

        drawCenteredText(g, realKeyCollected ? "Kosong" : "Kunci", b.x, b.y + 4, b.width, Color.WHITE, Font.BOLD, 12);
    }

    private void drawKeys(Graphics2D g) {
        for (InteriorKey key : keys) {
            if (key.collected) continue;
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

    private void drawEnemies(Graphics2D g) {
        for (PatrolEnemy enemy : enemies) {
            Rectangle b = enemy.bounds;
            g.setColor(new Color(0, 0, 0, 75));
            g.fillOval(b.x + 2, b.y + 26, b.width, 12);
            g.setColor(new Color(173, 48, 54));
            g.fillRoundRect(b.x + 5, b.y + 12, 24, 23, 10, 10);
            g.setColor(new Color(232, 184, 132));
            g.fillOval(b.x + 6, b.y, 22, 22);
            g.setColor(new Color(42, 42, 42));
            g.fillArc(b.x + 5, b.y - 3, 24, 15, 0, 180);
            g.setColor(new Color(255, 229, 102));
            g.fillRect(b.x + 9, b.y + 18, 16, 5);
        }
    }

    private void drawHud(Graphics2D g) {
        // Kotak Abu Transparan Kiri Atas
        g.setColor(new Color(20, 24, 28, 210));
        g.fillRoundRect(18, 16, 680, 80, 12, 12);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        String pName = (GameManager.playerName == null || GameManager.playerName.isEmpty()) ? "Mahasiswa" : GameManager.playerName;
        g.drawString(roomName + "  |  " + pName, 34, 45);

        g.setFont(new Font("Arial", Font.PLAIN, 15));
        String stats = String.format("Nyawa: %d    Kunci Asli: %d/%d    Kunci Palsu: %d    Akses: %d/%d", 
                GameManager.nyawa, GameManager.kunci, GameManager.KUNCI_TARGET, GameManager.kunciPalsu, puzzlesSolved, PUZZLE_TARGET);
        g.drawString(stats, 34, 75);

        // Box Bantuan Bawah
        String helpText = "WASD/Arrow: Jalan    E: Interaksi    Hindari Penjaga    Esc: Keluar";
        int helpWidth = g.getFontMetrics().stringWidth(helpText);
        g.setColor(new Color(20, 24, 28, 190));
        g.fillRoundRect(18, getHeight() - 54, helpWidth + 32, 36, 12, 12);
        g.setColor(Color.WHITE);
        g.drawString(helpText, 34, getHeight() - 31);
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

        for (PatrolEnemy enemy : enemies) {
            int x = mapX + 10 + (int) (enemy.bounds.x * scaleX);
            int y = mapY + 10 + (int) (enemy.bounds.y * scaleY);
            g.setColor(new Color(210, 48, 54));
            g.fillRect(x - 2, y - 2, 7, 7);
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
            Graphics2D g, String text, int x, int y, int width, Color color, int style, int size
    ) {
        g.setFont(new Font("Arial", style, size));
        FontMetrics metrics = g.getFontMetrics();
        int textX = x + (width - metrics.stringWidth(text)) / 2;
        g.setColor(color);
        g.drawString(text, textX, y + metrics.getAscent());
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

    private static class PuzzleTask {
        private final String konteks;
        private final String soal;
        private final String[] options;
        private final int correctIndex;
        private final String logika;
        private boolean solved;

        private PuzzleTask(String konteks, String soal, String[] options, int correctIndex, String logika) {
            this.konteks = konteks;
            this.soal = soal;
            this.options = options;
            this.correctIndex = correctIndex;
            this.logika = logika;
        }
    }

    private static class PatrolEnemy {
        private final Rectangle bounds;
        private final int startX;
        private final int startY;
        private final int minX;
        private final int maxX;
        private final int minY;
        private final int maxY;
        private int dx;
        private int dy;

        private PatrolEnemy(int x, int y, int width, int height, int minX, int maxX, int minY, int maxY, int dx, int dy) {
            this.bounds = new Rectangle(x, y, width, height);
            this.startX = x;
            this.startY = y;
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
            this.dx = dx;
            this.dy = dy;
        }

        private void update() {
            bounds.translate(dx, dy);

            if (bounds.x < minX || bounds.x > maxX) {
                dx = -dx;
                bounds.x = Math.max(minX, Math.min(maxX, bounds.x));
            }
            if (bounds.y < minY || bounds.y > maxY) {
                dy = -dy;
                bounds.y = Math.max(minY, Math.min(maxY, bounds.y));
            }
        }

        private void reset() {
            bounds.setLocation(startX, startY);
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