package nelvi.game;

public class Player {
    String name;
    int speed;
    int healthPoin;
    int height;

    void run(){
        System.out.println(name +" is running...");
        System.out.println("Speed: "+ speed);
        System.out.println("Jump Height: "+ height);
    }
    
    boolean jump(){
        if(height > 0) return true;
        return false;
    }

    boolean isDead(){
        if(healthPoin <= 0) return true;
        return false;
    }
}
