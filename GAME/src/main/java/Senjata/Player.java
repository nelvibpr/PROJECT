package Senjata;

public class Player {
    String nama;
    int jumlahPeluru;
    
    void tembak(){
        System.out.println("Jumlah Peluru: "+ jumlahPeluru);
        if(jumlahPeluru > 0){
            jumlahPeluru--;
            System.out.println("TERTEMBAK!!");
        }else{
            System.out.println("Peluru anda kosong");
        }
        System.out.println("Sisa Peluru: "+ jumlahPeluru);
    }
    
    void reload(){
        jumlahPeluru += 5;
        System.out.println("Isi ulang peluru");
        System.out.println("Jumlah Peluru: "+ jumlahPeluru);
    }
    
}
