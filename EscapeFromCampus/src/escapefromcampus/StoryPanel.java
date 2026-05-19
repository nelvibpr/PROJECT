package escapefromcampus;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class StoryPanel extends JPanel {
    private final JTextArea textArea;
    private final JButton startBtn;
    private Timer typewriterTimer;
    private String fullStoryText;
    private int charIndex;

    public StoryPanel(MainFrame frame) {
        setLayout(new GridBagLayout());
        
        // 1. Text Area untuk Cerita
        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.ITALIC, 22));
        textArea.setForeground(Color.WHITE);
        textArea.setOpaque(false);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setPreferredSize(new Dimension(700, 350));
        textArea.setMargin(new Insets(20, 20, 20, 20));
        textArea.setFocusable(false); // Agar klik tembus ke panel utama

        // 2. Tombol Mulai (Awalnya disembunyikan)
        startBtn = new JButton("MULAI PETUALANGAN");
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
        typewriterTimer = new Timer(35, e -> { // Angka 35 adalah kecepatan (ms). Semakin kecil = semakin cepat.
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
        
        fullStoryText = "Malam semakin larut di kampus UNESA.\n\n" +
                "Kamu terbangun seorang diri di kelas, namun suasana terasa berbeda. " +
                "Gerbang utama terkunci.\n\n" +
                "Halo " + name + ", sepertinya sistem keamanan AI telah mengambil alih gedung. " +
                "Hanya mahasiswa dengan logika OOP yang boleh keluar.\n\n" +
                name + ", kamu harus menemukan 5 kunci asli untuk membuka gerbang.\n" +
                "Hati-hati dengan kunci palsu dan penjaga yang tersebar di berbagai tempat!";
        
        // Reset state sebelum mulai
        textArea.setText("");
        charIndex = 0;
        startBtn.setVisible(false); // Pastikan tombol sembunyi lagi
        
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

        // Lantai
        g2.setColor(new Color(60, 50, 40)); 
        g2.fillRect(0, 0, w, h);

        // Papan Tulis (Background)
        g2.setColor(new Color(20, 50, 30));
        g2.fillRoundRect(w/2 - 300, 50, 600, 200, 10, 10);
        g2.setColor(new Color(100, 80, 50));
        g2.setStroke(new BasicStroke(10));
        g2.drawRoundRect(w/2 - 300, 50, 600, 200, 10, 10);

        // Dekorasi Kapur/Tulisan OOP
        g2.setColor(new Color(255, 255, 255, 50));
        g2.setFont(new Font("Monospaced", Font.BOLD, 40));
        g2.drawString("SYSTEM_LOCKED = TRUE", w/2 - 220, 140);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 20));
        g2.drawString("while(!hasLogikaOOP) { stayInside(); }", w/2 - 230, 180);

        // Meja-meja (Siluet/Bayangan)
        g2.setColor(new Color(40, 30, 20));
        for (int i = 0; i < 4; i++) {
            g2.fillRect(50 + (i * 220), h - 150, 150, 80);
        }
    }
}