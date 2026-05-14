package nelvi.game;

public class Petani {
     public static void main(String[] args){

        Player petani = new Player();

        petani.name = "Petani Nelvi";
        petani.speed = 100;
        petani.healthPoin = 20;
        petani.height = 5;

        petani.run();
        
        if(petani.jump()){
            System.out.println("Petani Nelvi melompat!");
        }else
             System.out.println("Petani Nelvi Diam");

        if(petani.isDead()){
            System.out.println("Game Over!");
        }

    }

}
