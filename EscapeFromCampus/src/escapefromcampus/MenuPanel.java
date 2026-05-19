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

public class MenuPanel extends JPanel {
    public MenuPanel(MainFrame frame) {
        setLayout(new GridBagLayout());
        setBackground(Color.DARK_GRAY);

        JPanel contentBox = new JPanel();
        contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.Y_AXIS));
        contentBox.setOpaque(false);

        JLabel titleLabel = new JLabel("ESCAPE FROM CAMPUS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startButton = new JButton("Mulai Game");
        startButton.setFont(new Font("Arial", Font.PLAIN, 20));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> frame.showPanel("CharacterSelect"));

        contentBox.add(titleLabel);
        contentBox.add(Box.createRigidArea(new Dimension(0, 30)));
        contentBox.add(startButton);

        add(contentBox);
    }
}