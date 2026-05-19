package escapefromcampus;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class CharacterSelectPanel extends JPanel {
    private JTextField nameField;
    private int selectedGender = -1; // -1: belum pilih, 0: Laki, 1: Perempuan
    private JButton maleBtn, femaleBtn;

    public CharacterSelectPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 35));
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // 1. JUDUL ATAS
        JLabel titleLabel = new JLabel("PILIH KARAKTERMU", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        // 2. AREA TENGAH (Input Nama & Pilihan Karakter)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Input Nama
        JLabel nameLabel = new JLabel("Masukkan Namamu:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(300, 40));
        nameField.setFont(new Font("Arial", Font.BOLD, 18));
        nameField.setHorizontalAlignment(JTextField.CENTER);

        // Panel Tombol Karakter
        JPanel charPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        charPanel.setOpaque(false);
        charPanel.setMaximumSize(new Dimension(500, 250));
        charPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        // Tombol Laki-laki
        maleBtn = createCharButton("Laki-laki", 0);
        maleBtn.addActionListener(e -> selectGender(0));

        // Tombol Perempuan
        femaleBtn = createCharButton("Perempuan", 1);
        femaleBtn.addActionListener(e -> selectGender(1));

        charPanel.add(maleBtn);
        charPanel.add(femaleBtn);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        centerPanel.add(nameLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(nameField);
        centerPanel.add(charPanel);
        add(centerPanel, BorderLayout.CENTER);

        // 3. TOMBOL NEXT
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
                JOptionPane.showMessageDialog(this, "Pilih karaktermu (Laki-laki/Perempuan)!");
            } else {
                GameManager.playerName = name;
                GameManager.playerGender = selectedGender;
                frame.showPanel("Story"); // Lanjut ke cerita
            }
        });
        
        JPanel southPanel = new JPanel();
        southPanel.setOpaque(false);
        southPanel.add(nextBtn);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void selectGender(int g) {
        selectedGender = g;
        if (g == 0) {
            maleBtn.setBorder(new LineBorder(Color.CYAN, 5));
            femaleBtn.setBorder(new LineBorder(Color.GRAY, 2));
        } else {
            femaleBtn.setBorder(new LineBorder(new Color(255, 105, 180), 5));
            maleBtn.setBorder(new LineBorder(Color.GRAY, 2));
        }
    }

    private JButton createCharButton(String label, int type) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();

                // Gambar Wajah (Lingkaran)
                g2.setColor(new Color(255, 220, 180));
                g2.fillOval(w/2 - 40, h/2 - 60, 80, 80);

                if (type == 0) { // Rambut Laki-laki
                    g2.setColor(new Color(50, 50, 50));
                    g2.fillArc(w/2 - 42, h/2 - 65, 84, 50, 0, 180);
                } else { // Rambut Perempuan
                    g2.setColor(new Color(100, 60, 40));
                    g2.fillOval(w/2 - 45, h/2 - 65, 90, 40); // Rambut atas
                    g2.fillRect(w/2 - 45, h/2 - 40, 20, 60); // Samping kiri
                    g2.fillRect(w/2 + 25, h/2 - 40, 20, 60); // Samping kanan
                }

                // Mata
                g2.setColor(Color.BLACK);
                g2.fillOval(w/2 - 20, h/2 - 30, 8, 8);
                g2.fillOval(w/2 + 12, h/2 - 30, 8, 8);
                
                // Senyum
                g2.drawArc(w/2 - 15, h/2 - 15, 30, 20, 0, -180);
            }
        };
        btn.setLayout(new BorderLayout());
        JLabel l = new JLabel(label, SwingConstants.CENTER);
        l.setFont(new Font("Arial", Font.BOLD, 16));
        l.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        btn.add(l, BorderLayout.SOUTH);
        btn.setBackground(new Color(60, 60, 65));
        btn.setBorder(new LineBorder(Color.GRAY, 2));
        btn.setFocusPainted(false);
        return btn;
    }
}