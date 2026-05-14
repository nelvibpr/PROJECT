package drone;

public class Player {
    int energi, ketinggian, kecepatan;
    String merek;
    
    void terbang(){
        energi-= 2; //energi = energi - 2
        if(energi > 10){
            ketinggian++;
            System.out.println("Drone terbang...");
        } else {
            System.out.println("Energi lemah: Drone tidak bisa terbang");
        }
    }
    
    void matikanMesin(){
        if(ketinggian > 0){
            System.out.println("Mesin tidak bisa dimatikan karena sedang terbang");
        } else {
            System.out.println("Mesi dimatikan...");
       }
    }
    
    void turun(){
        ketinggian--;
        energi--;
        System.out.println("Drone turun");
    }
    
    void belok(){
        energi--;
        System.out.println("Drone belok");
        // belok ke mana? perlu dicek :)
    }
    
     void maju(){
        energi--;
        System.out.println("Drone maju ke depan");
        kecepatan++;
    }
     
      void mundur(){
        energi--;
        System.out.println("Drone mundur");
        kecepatan++;
    }

}
