package nelvi.gameflappy;

import java.awt.Image;

public class Pipa {
    
    int x;
    int y;
    int width;
    int height;
    Image img;
    boolean passed = false;

    public Pipa(int x, int y, int width, int height, Image img) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.img = img;
    }
}