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

public class GameOverPanel extends JPanel {
    public GameOverPanel(MainFrame frame) {
        setLayout(new GridBagLayout());
        setBackground(new Color(130, 20, 20));

        JPanel contentBox = new JPanel();
        contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.Y_AXIS));
        contentBox.setOpaque(false);

        JLabel titleLabel = new JLabel("GAME OVER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 64));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("Nyawa kamu telah habis tertangkap penjaga atau jebakan kunci.");
        subLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        subLabel.setForeground(Color.WHITE);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton menuButton = new JButton("Kembali ke Menu Utama");
        menuButton.setFont(new Font("Arial", Font.BOLD, 20));
        menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuButton.addActionListener(e -> frame.showPanel("Menu"));

        contentBox.add(titleLabel);
        contentBox.add(Box.createRigidArea(new Dimension(0, 10)));
        contentBox.add(subLabel);
        contentBox.add(Box.createRigidArea(new Dimension(0, 50)));
        contentBox.add(menuButton);

        add(contentBox);
    }
}