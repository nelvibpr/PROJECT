package escapefromcampus;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame game = new MainFrame();
            game.setVisible(true);
        });
    }
}