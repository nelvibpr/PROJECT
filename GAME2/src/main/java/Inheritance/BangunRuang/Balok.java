package Inheritance.BangunRuang;

public class Balok extends BangunRuang{
    float panjang;
    float lebar;
    float tinggi;
    
    @Override
    float volume(){
        float volume = panjang * lebar * tinggi;
        System.out.println("Volume Balok: "+ volume);
        return volume;
    }
    
    @Override
    float LPermukaan(){
        float LPermukaan = 2 * ((panjang*lebar)+(panjang*tinggi)+(lebar*tinggi));
        System.out.println("Luas Permukaan Balok: "+ LPermukaan);
        return LPermukaan;
    }
   
}
