package escapefromcampus;

import java.awt.*;
import java.net.URL;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class CharacterSelectPanel extends JPanel {
    private JTextField nameField;
    private int selectedGender = -1; // -1: belum pilih, 0: Laki, 1: Perempuan
    private JButton maleBtn, femaleBtn;
    private Image bgImage;

    // Warna-warna untuk Outline
    private final Color COLOR_MALE = Color.CYAN;
    private final Color COLOR_FEMALE = new Color(255, 105, 180); // Hot Pink
    private final Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);

    public CharacterSelectPanel(MainFrame frame) {
        // 1. Memuat Gambar Background Karakter
        try {
            URL bgPath = getClass().getResource("/escapefromcampus/assets/AWAL/BG KARAKTER.png");
            if (bgPath != null) {
                bgImage = new ImageIcon(bgPath).getImage();
            }
        } catch (Exception e) {
            System.out.println("Background Karakter tidak ditemukan.");
        }

        setLayout(new BorderLayout());
        // Memberi padding luar (Atas, Kiri, Bawah, Kanan)
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // JUDUL TEKS "PILIH KARAKTERMU" DIHAPUS (Karena sudah ada di gambar background)

        // 2. AREA TENGAH
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false); // Transparan agar background terlihat

        // --- PERBAIKAN 1: MENURUNKAN TEKS ---
        // Jarak dari atas ditingkatkan dari 110 menjadi 160 agar teks nama turun
        centerPanel.add(Box.createRigidArea(new Dimension(0, 160)));

        JLabel nameLabel = new JLabel("Masukkan Namamu:") {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(Color.BLACK);
                g.drawString(getText(), 1, getHeight() - 4);
                super.paintComponent(g);
            }
        };
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 22));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 3. MENGUBAH TEXT INPUT MENJADI GAMBAR (INPUT NAMA.png)
        JPanel inputPanel = new JPanel() {
            Image inputBg;
            {
                try {
                    URL path = getClass().getResource("/escapefromcampus/assets/AWAL/INPUT NAMA.png");
                    if(path != null) inputBg = new ImageIcon(path).getImage();
                } catch(Exception e){}
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if(inputBg != null) {
                    g.drawImage(inputBg, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.WHITE); // Warna cadangan
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        inputPanel.setOpaque(false);
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setMaximumSize(new Dimension(300, 50));
        
        nameField = new JTextField();
        nameField.setOpaque(false); // Dibuat transparan agar gambar dari panel di belakangnya terlihat
        nameField.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // Hilangkan garis bawaan
        nameField.setFont(new Font("Arial", Font.BOLD, 18));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        inputPanel.add(nameField, BorderLayout.CENTER);

        // 4. AREA KARTU KARAKTER
        // Gap horizontal antar kartu 40px
        JPanel charPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        charPanel.setOpaque(false);
        
        // --- PERBAIKAN 2: UBAH UKURAN KARTU MENJADI 230x250 ---
        // Total lebar untuk 2 kartu (230*2) + gap (40) = 500. Tinggi total = 250.
        // Sebelumnya: Dimension(500, 220).
        charPanel.setMaximumSize(new Dimension(500, 250));
        charPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        // Teks "Laki-laki" dan "Perempuan" sudah dihapus
        maleBtn = createCharButton("/escapefromcampus/assets/AWAL/boy.png", COLOR_MALE);
        maleBtn.addActionListener(e -> selectGender(0));

        femaleBtn = createCharButton("/escapefromcampus/assets/AWAL/girl.png", COLOR_FEMALE);
        femaleBtn.addActionListener(e -> selectGender(1));

        charPanel.add(maleBtn);
        charPanel.add(femaleBtn);

        centerPanel.add(nameLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(inputPanel);
        centerPanel.add(charPanel);
        add(centerPanel, BorderLayout.CENTER);

        // 5. TOMBOL NEXT
        JButton nextBtn = new JButton("SELANJUTNYA");
        nextBtn.setFont(new Font("Arial", Font.BOLD, 22));
        nextBtn.setBackground(new Color(70, 160, 70));
        nextBtn.setForeground(Color.WHITE);
        nextBtn.setFocusPainted(false);
        nextBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Isi namamu dulu ya!");
            } else if (selectedGender == -1) {
                JOptionPane.showMessageDialog(this, "Pilih karaktermu!");
            } else {
                GameManager.playerName = name;
                GameManager.playerGender = selectedGender;
                frame.showPanel("Story");
            }
        });
        
        JPanel southPanel = new JPanel();
        southPanel.setOpaque(false);
        // MENAIKKAN TOMBOL: Menambahkan jarak kosong (margin bawah) sebesar 70 pixel
        southPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 70, 0)); 
        southPanel.add(nextBtn);
        add(southPanel, BorderLayout.SOUTH);
    }

    // Mengganti warna bingkai saat dipilih (klik)
    private void selectGender(int g) {
        selectedGender = g;
        if (g == 0) {
            maleBtn.setBorder(new LineBorder(COLOR_MALE, 5));
            // Hapus border dari tombol cewek
            femaleBtn.setBorder(new LineBorder(COLOR_TRANSPARENT, 5)); 
        } else {
            femaleBtn.setBorder(new LineBorder(COLOR_FEMALE, 5));
            // Hapus border dari tombol cowok
            maleBtn.setBorder(new LineBorder(COLOR_TRANSPARENT, 5)); 
        }
    }

    // Metode bantuan untuk membuat tombol kartu karakter yang menggunakan background KARTU PILIHAN.png
    private JButton createCharButton(String imagePath, Color hoverColor) {
        JButton btn = new JButton() {
            Image cardBg;
            {
                try {
                    // Memuat gambar latar belakang kartu
                    URL path = getClass().getResource("/escapefromcampus/assets/AWAL/KARTU PILIHAN.png");
                    if(path != null) cardBg = new ImageIcon(path).getImage();
                } catch(Exception e){}
            }
            @Override
            protected void paintComponent(Graphics g) {
                // Gambar background kartu dulu
                if(cardBg != null) {
                    g.drawImage(cardBg, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(40, 40, 45, 200)); 
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                // Gambar karakter di atas background kartu
                super.paintComponent(g); 
            }
        };
        btn.setLayout(new BorderLayout());
        
        // Memuat gambar boy.png atau girl.png
        try {
            URL path = getClass().getResource(imagePath);
            if (path != null) {
                ImageIcon icon = new ImageIcon(path);
                // --- PERBAIKAN 3: KARAKTER MENYESUAIKAN ---
                // Gambar karakter diperbesar dari 120x120 menjadi 140x140 agar pas di kartu yang lebih tinggi (250)
                Image scaled = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
                JLabel imgLabel = new JLabel(new ImageIcon(scaled), SwingConstants.CENTER);
                btn.add(imgLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            System.out.println("Gagal memuat karakter ikon.");
        }

        btn.setContentAreaFilled(false); 
        // Menggunakan border tak terlihat secara default agar ukuran tidak lompat saat dipilih/dihover
        btn.setBorder(new LineBorder(COLOR_TRANSPARENT, 5)); 
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // --- Efek Hover Outline ---
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Jika tombol ini BELUM dipilih, tampilkan outline hover
                boolean isThisSelected = (btn == maleBtn && selectedGender == 0) || (btn == femaleBtn && selectedGender == 1);
                if (!isThisSelected) {
                    btn.setBorder(new LineBorder(hoverColor, 5));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Saat mouse keluar, kembalikan ke transparan JIKA tidak sedang dipilih
                boolean isThisSelected = (btn == maleBtn && selectedGender == 0) || (btn == femaleBtn && selectedGender == 1);
                if (!isThisSelected) {
                    btn.setBorder(new LineBorder(COLOR_TRANSPARENT, 5));
                }
            }
        });
        // ----------------------------------------

        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(30, 30, 35));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}