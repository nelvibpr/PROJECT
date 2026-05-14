package Inheritance.BangunRuang;

public class Bola extends BangunRuang {
    float r;
    
    @Override
    float volume(){
        float volume = (float) ((float) 4/3 * (Math.PI * r*r*r));
        System.out.println("Volume Bola: "+ volume);
        return volume;
    }
    
    @Override
    float LPermukaan(){
        float LPermukaan = (float) (4 * (Math.PI * r*r));
        System.out.println("Luas Permukaan Bola: "+ LPermukaan);
        return LPermukaan;
    }
    
}
