package Inheritance.BangunRuang;

import java.util.Scanner;

public class MainClass {

    public static void main(String[] args) {
        
        BangunRuang bangunRuang = new BangunRuang();
        
        Kubus kubus = new Kubus();
        kubus.sisi = 3;
        
        Bola bola = new Bola();
        bola.r = 8;
        
        Balok balok = new Balok();
        balok.lebar = 2;
        balok.panjang = 10;
        balok.tinggi = 5;
        
        Limas limas = new Limas();
        limas.luasAlas = 8;
        limas.tinggiLimas = 18;
        
        bangunRuang.volume();
        bangunRuang.LPermukaan();
        
        kubus.volume();
        kubus.LPermukaan();
        
        bola.volume();
        bola.LPermukaan();
        
        balok.volume();
        balok.LPermukaan();
        
        limas.volume();
        
    }
    
}
