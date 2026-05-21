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

public class VictoryPanel extends JPanel {
    public VictoryPanel(MainFrame frame) {
        setLayout(new GridBagLayout());
        setBackground(new Color(25, 110, 50)); // Warna hijau tema kemenangan

        JPanel contentBox = new JPanel();
        contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.Y_AXIS));
        contentBox.setOpaque(false);

        JLabel titleLabel = new JLabel("BERHASIL KABUR!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 54));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("Selamat! Kamu berhasil kabur dari Kampus, tapi tetap dapat Ilmu!");
        subLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subLabel.setForeground(Color.WHITE);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton menuButton = new JButton("Kembali ke Menu Utama");
        menuButton.setFont(new Font("Arial", Font.BOLD, 18));
        menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuButton.addActionListener(e -> frame.showPanel("Menu"));

        contentBox.add(titleLabel);
        contentBox.add(Box.createRigidArea(new Dimension(0, 15)));
        contentBox.add(subLabel);
        contentBox.add(Box.createRigidArea(new Dimension(0, 45)));
        contentBox.add(menuButton);

        add(contentBox);
    }
}