package escapefromcampus;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final Level1Panel level1Panel;
    private final ClassroomPanel classroomPanel;
    private final CampusBuildingPanel libraryPanel;
    private final CampusBuildingPanel labPanel;
    private final CampusBuildingPanel canteenPanel;
    private final CampusBuildingPanel rectoratePanel;
    private final CampusBuildingPanel dormitoryPanel;

    public MainFrame() {
        setTitle("Escape from Campus");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        level1Panel = new Level1Panel(this);
        classroomPanel = new ClassroomPanel(this);
        libraryPanel = CampusBuildingPanel.createLibrary(this);
        labPanel = CampusBuildingPanel.createLab(this);
        canteenPanel = CampusBuildingPanel.createCanteen(this);
        rectoratePanel = CampusBuildingPanel.createRectorate(this);
        dormitoryPanel = CampusBuildingPanel.createDormitory(this);

        mainPanel.add(new MenuPanel(this), "Menu");
        mainPanel.add(level1Panel, "Level1");
        mainPanel.add(classroomPanel, "Classroom");
        mainPanel.add(libraryPanel, "Library");
        mainPanel.add(labPanel, "Lab");
        mainPanel.add(canteenPanel, "Canteen");
        mainPanel.add(rectoratePanel, "Rectorate");
        mainPanel.add(dormitoryPanel, "Dormitory");

        add(mainPanel);
    }

    public void startNewGame() {
        GameManager.resetGame();
        level1Panel.restartWorld();
        classroomPanel.restartRoom();
        libraryPanel.restartRoom();
        labPanel.restartRoom();
        canteenPanel.restartRoom();
        rectoratePanel.restartRoom();
        dormitoryPanel.restartRoom();
        showPanel("Level1");
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }
}
