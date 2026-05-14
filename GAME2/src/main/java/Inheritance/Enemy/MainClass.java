package Inheritance.Enemy;

public class MainClass {
    public static void main (String[] args){
        
        enemy monster = new enemy();
        monster.name = "Adudu";
        monster.hp = 20;
        monster.attackPoin = 50;
        
        Zombie zumbi = new Zombie();
        zumbi.name = "Nino";
        zumbi.hp = 30;
        zumbi.attackPoin = 60;
        
        Pocong poci = new Pocong();
        poci.name = "Yardan";
        poci.hp = 10;
        poci.attackPoin = 70;
        
        Burung garuda = new Burung();
        garuda.name = "Indo";
        garuda.hp = 40;
        garuda.attackPoin = 90;
        
        monster.attack();
        
        zumbi.attack();
        zumbi.walk();
        
        poci.attack();
        poci.jump();
        
        garuda.attack();
        garuda.fly();
        garuda.walk();
        garuda.jump();
                               
    }
    
}
