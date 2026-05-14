package Inheritance.BangunRuang;

public class Kubus extends BangunRuang {
    float sisi;
    
    @Override
    float volume(){
        float volume = sisi * sisi * sisi;
        System.out.println("Volume Kubus: "+ volume);
        return volume;
    }
    
    @Override
    float LPermukaan(){
        float LPermukaan = 6 * (sisi*sisi);
        System.out.println("Luas Permukaan Kubus: "+ LPermukaan);
        return LPermukaan;
    }
}
