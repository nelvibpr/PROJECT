package escapefromcampus;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class MenuPanel extends JPanel {
    private Image bgImage;

    public MenuPanel(MainFrame frame) {
        // 1. Memuat gambar background
        try {
            URL bgPath = getClass().getResource("/escapefromcampus/assets/AWAL/BG PLAY.png");
            if (bgPath != null) {
                bgImage = new ImageIcon(bgPath).getImage();
            }
        } catch (Exception e) {
            System.out.println("Background tidak ditemukan.");
        }

        setLayout(new GridBagLayout());
        
        // Menggunakan GridBagConstraints untuk mengatur posisi agar bisa didorong ke bawah
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        // Mendorong tombol 140 pixel ke bawah dari titik tengah layar
        // Jika masih kurang ke bawah, besarkan angka 140 ini.
        gbc.insets = new Insets(140, 0, 0, 0); 

        // 2. Membuat Tombol PLAY 
        JButton startButton = new JButton();
        try {
            URL btnPath = getClass().getResource("/escapefromcampus/assets/AWAL/PLAY.png");
            if (btnPath != null) {
                ImageIcon icon = new ImageIcon(btnPath);
                
                // Memaksa ukuran gambar menjadi 90x90 piksel agar proporsional dan tidak raksasa
                Image scaledImg = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
                startButton.setIcon(new ImageIcon(scaledImg));
            }
        } catch (Exception e) {
            startButton.setText("MULAI");
        }

        // 3. Styling Tombol Transparan
        startButton.setContentAreaFilled(false);
        startButton.setFocusPainted(false);
        startButton.setOpaque(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Border kosong awal
        startButton.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        startButton.setBorderPainted(true);

        // 4. Efek Hover Outline 
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Outline kuning
                startButton.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 3, true));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Hilang saat kursor keluar
                startButton.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            }
        });

        // Aksi perpindahan panel
        startButton.addActionListener(e -> frame.showPanel("CharacterSelect"));

        // 5. Memasukkan tombol ke panel dengan aturan posisi (gbc)
        add(startButton, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}