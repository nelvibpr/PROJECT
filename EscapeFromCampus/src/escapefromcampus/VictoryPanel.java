package escapefromcampus;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import javax.swing.*;
import java.net.URL;

public class VictoryPanel extends JPanel {
    private Image bgImage;
    
    public VictoryPanel(MainFrame frame) {
        // Memuat gambar background
        try {
            URL bgPath = getClass().getResource("/escapefromcampus/assets/victory/victory_bg.png");
            if (bgPath != null) {
                bgImage = new ImageIcon(bgPath).getImage();
            }
        } catch (Exception e) {
            System.out.println("Gagal memuat background Victory: " + e.getMessage());
        }
   
        // Menggunakan GridBagLayout untuk kontrol posisi yang presisi
        setLayout(new GridBagLayout());
        setBackground(new Color(25, 110, 50)); 

        GridBagConstraints gbc = new GridBagConstraints();
        
        // Pengaturan agar tombol berada di bagian bawah
        gbc.gridx = 0;
        gbc.gridy = 1; // Baris kedua
        gbc.weighty = 1.0; // Mengambil sisa ruang vertikal
        gbc.anchor = GridBagConstraints.SOUTH; // Mendorong ke sisi bawah (SOUTH)
        gbc.insets = new Insets(0, 0, 100, 0); // Jarak 100 piksel dari bawah layar

        // Tombol Kembali ke Menu Utama
        JButton menuButton = new JButton("Kembali ke Menu Utama");
        menuButton.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Styling tombol
        menuButton.setBackground(new Color(0, 102, 204)); 
        menuButton.setForeground(Color.WHITE);
        menuButton.setFocusPainted(false); 
        menuButton.setPreferredSize(new Dimension(250, 50)); // Ukuran tombol agar rapi
        
        menuButton.addActionListener(e -> frame.showPanel("Menu"));

        add(menuButton, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}