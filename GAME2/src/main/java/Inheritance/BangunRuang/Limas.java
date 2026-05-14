package Inheritance.BangunRuang;

public class Limas extends BangunRuang {
    float luasAlas;
    float tinggiLimas;
    
    @Override
    float volume(){
        float volume = (float) 1/3 * (luasAlas * tinggiLimas);
        System.out.println("Volume Limas: "+ volume);
        return volume;
    }
        
}
