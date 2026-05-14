package nelvi.gameflappy;

import java.awt.Image;

public class Nelvi {

    int x;
    int y;
    int width;
    int height;
    Image img;

    public Nelvi(int x, int y, int width, int height, Image img) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.img = img;
    }
}
