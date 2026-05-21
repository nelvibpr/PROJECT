package escapefromcampus;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.net.URL;

public class StoryPanel extends JPanel {
    private final JTextArea textArea;
    private final JButton startBtn;
    private Timer typewriterTimer;
    private String fullStoryText;
    private int charIndex;
    
    private Image bgImage; 
    
    public StoryPanel(MainFrame frame) {
    try {
        // Sesuaikan path "/escapefromcampus/assets/..." dengan lokasi file gambar Anda
        URL bgPath = getClass().getResource("/escapefromcampus/assets/scene/scene_bg.png");
        if (bgPath != null) {
            bgImage = new ImageIcon(bgPath).getImage();
        }
    } catch (Exception e) {
        System.out.println("Gagal memuat background story: " + e.getMessage());
    }
    
    setLayout(new GridBagLayout());
        setLayout(new GridBagLayout());
        
        // 1. Text Area untuk Cerita
        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.ITALIC, 16));
        textArea.setForeground(Color.WHITE);
        textArea.setOpaque(false);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setPreferredSize(new Dimension(700, 350));
        textArea.setMargin(new Insets(20, 20, 20, 20));
        textArea.setFocusable(false); // Agar klik tembus ke panel utama

        // 2. Tombol Mulai (Awalnya disembunyikan)
        startBtn = new JButton("MULAI MENCARI");
        startBtn.setFont(new Font("Arial", Font.BOLD, 18));
        startBtn.setBackground(new Color(50, 150, 50));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startBtn.setVisible(false); // Disembunyikan dulu
        startBtn.addActionListener(e -> frame.startNewGame());

        // 3. Container Kotak Info Transparan
        JPanel dialogBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setStroke(new BasicStroke(3));
                g2.setColor(new Color(255, 255, 255, 100));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            }
        };
        dialogBox.setOpaque(false);
        dialogBox.setLayout(new BoxLayout(dialogBox, BoxLayout.Y_AXIS));
        dialogBox.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        dialogBox.add(textArea);
        dialogBox.add(Box.createRigidArea(new Dimension(0, 20)));
        dialogBox.add(startBtn);
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(dialogBox);

        // 4. Timer untuk Animasi Ketik (Typewriter effect)
        typewriterTimer = new Timer(20, e -> { // Angka 35 adalah kecepatan (ms). Semakin kecil = semakin cepat.
            if (charIndex < fullStoryText.length()) {
                textArea.setText(fullStoryText.substring(0, charIndex + 1));
                charIndex++;
            } else {
                typewriterTimer.stop();
                startBtn.setVisible(true); // Munculkan tombol saat teks selesai
            }
        });

        // 5. Fitur Skip Animasi jika layar diklik
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (typewriterTimer.isRunning()) {
                    typewriterTimer.stop();
                    textArea.setText(fullStoryText); // Langsung tampilkan semua teks
                    startBtn.setVisible(true); // Langsung munculkan tombol
                }
            }
        });

        // Update teks dan mulai animasi saat panel ditampilkan
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                startStoryAnimation();
            }
        });
    }

    private void startStoryAnimation() {
        String name = GameManager.playerName != null ? GameManager.playerName : "Mahasiswa";
        
        fullStoryText = "Kelas terakhir hari ini sangat membosankan. Matahari sore bersinar terik, dan kamu berniat untuk bolos dari kelas itu.\n\n" +
                "Namun, setibanya di depan gerbang utama, pintu besi tiba-tiba menutup rapat dengan bunyi alarm keras. Layar digital gerbang berkedip merah:\n\n 'CRITICAL ERROR - MASTER KEY CORRUPTED'.\n\n" +
                "Halo " + name + ", sepertinya beberapa anomali kode terlarang telah lepas dari server pusat dan merusak sistem enkripsi gerbang utama.\n\n" +
                name + ", satu-satunya cara untuk kabur adalah dengan mengumpulkan 5 kunci cadangan yang tersebar di 5 gedung dalam kampus.\n" +
                "Di setiap gedung, kamu harus menyelesaikan 3 pertanyaan logika untuk merekonstruksi kunci. \n\n" + 
                "Hati-hati dengan anomali patroli yang berjaga dan jebakan kunci palsu yang rusak!";
        
        // Reset state sebelum mulai
        textArea.setText("");
        charIndex = 0;
        startBtn.setVisible(false); 
        
        // Mulai animasi
        typewriterTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Gambar background kustom Anda
        if (bgImage != null) {
            g2.drawImage(bgImage, 0, 0, w, h, this);
        } else {
            // Fallback jika gambar tidak ditemukan
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, w, h);
        }

        
    }
}