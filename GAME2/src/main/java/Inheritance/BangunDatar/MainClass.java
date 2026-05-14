package Inheritance.BangunDatar;

public class MainClass {

    public static void main(String[] args) {
        
        BangunDatar bangunDatar = new BangunDatar();
        
        Persegi persegi = new Persegi();
        persegi.sisi = 2;
        
        Lingkaran lingkaran = new Lingkaran();
        lingkaran.r = 22;
        
        PersegiPanjang persegiPanjang = new PersegiPanjang();
        persegiPanjang.panjang = 8;
        persegiPanjang.lebar = 4;
        
        Segitiga segitiga = new Segitiga();
        segitiga.alas = 12;
        segitiga.tinggi = 8;
        
        
        bangunDatar.luas();
        bangunDatar.keliling();
        
        persegi.luas();
        persegi.keliling();
        
        lingkaran.luas();
        lingkaran.keliling();
        
        persegiPanjang.luas();
        persegiPanjang.keliling();
        
        segitiga.luas();
    }
    
}
