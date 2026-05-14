package Inheritance.BangunDatar;

public class Segitiga extends BangunDatar{
    float alas;
    float tinggi;
    
    @Override
    float luas(){
        float luas = (float) (0.5 * alas * tinggi);
        System.out.println("Luas Segitiga: "+ luas);
        return luas;
    }
    
}
