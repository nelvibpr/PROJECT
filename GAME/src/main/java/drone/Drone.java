package drone;

public class Drone {
    public static void main(String[] args){
        
        Player Drone = new Player();
        
        Drone.energi = 80;
        Drone.ketinggian = 30;
        Drone.kecepatan = 50;
        Drone.merek = "Mini";
        
        Drone.terbang();
        Drone.matikanMesin();
        Drone.turun();
        Drone.belok();
        Drone.maju();
        Drone.mundur();
        
        
                
    }
    
}
