package nelvi.uts;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MainStreetRace {
    static int posisiTerdepan = 0;
    
    public static void  main(String[] args){
        Scanner sc = new Scanner(System.in);
        Random rndm = new Random();
        
        // Membuat 3 objek
        Mobil mobil1 = new Mobil("MOBIL GTR", 2);
        Mobil mobil2 = new Mobil("MOBIL Supra", 2);
        Motor motor1 = new Motor("MOTOR Ninja", 2);
        
        // Memisahkan ArrayList agar tanpa Polymorphism
        ArrayList<Mobil> daftarMobil = new ArrayList<>();
        daftarMobil.add(mobil1);
        daftarMobil.add(mobil2);
        
        ArrayList<Motor> daftarMotor = new ArrayList<>();
        daftarMotor.add(motor1);
        
        System.out.println("=============================================");
        System.out.println("           >>> GAME STREET RACE <<<          ");
        System.out.println("=============================================");
        System.out.println("            Garis Finish: 100 meter          ");
        System.out.println("=============================================\n");
        
        boolean adaPemenang = false;
        
        while (!adaPemenang){
            for(Mobil mobil : daftarMobil){
                adaPemenang = prosesGiliranMobil(mobil, sc, rndm);
                if(adaPemenang)break;
            }
            if(adaPemenang)break;
            
            for(Motor motor : daftarMotor){
                adaPemenang = prosesGiliranMotor(motor, sc, rndm);
            if(adaPemenang)break;
            }
        }
        sc.close();
    }
    
    // --- LOGIKA GAME ---
    
    // Method khusus mobil
    public static boolean prosesGiliranMobil(Mobil k, Scanner sc, Random rndm){
        System.out.println("\n=============================================");
        System.out.println("             Giliran " + k.getNama() +"      ");
        System.out.println("       | Posisi: " + k.getPosisi() + "m | Sisa Nitro: " + k.getNitro() + " |");
        System.out.println("---------------------------------------------");
        
        // Cek Overheat (skip turn)
        if(k.isOverheat()){
            System.out.println(k.getNama() + " sedang OVERHEAT! Tidak bisa jalan (skip turn).");
            k.setOverheat(false);
            return false;
        }
        
        System.out.println("Menu:\n1. Gas Biasa (+random)\n2. Nitro (+30)");
        System.out.print("Pilih aksi (1/2): ");
        int aksi = sc.nextInt();
        
        if(aksi == 2){
            if(k.getNitro() > 0){
                if(k.isNitroTerakhir()){
                    System.out.println(k.getNama() + " OVERHEAT! Tidak bisa jalan");
                    k.setOverheat(true);
                    k.setNitroTerakhir(false);
                } else {
                    System.out.println(k.getNama() + " pakai Nitro!\n+30 meter");
                    k.setPosisi(k.getPosisi() + 30);
                    k.setNitro(k.getNitro() - 1);
                    k.setNitroTerakhir(true);
                }
                
            } else {
                System.out.println("Nitro habis! Terpaksa gas biasa.");
                int maju = rndm.nextInt(15) + 5;
                System.out.println(k.getNama() + " gas biasa! \n+" + maju + " meter");
                k.setPosisi(k.getPosisi() + maju);
                k.setNitroTerakhir(false);
            }
        } else {
            if(aksi == 1){
            int maju = rndm.nextInt(15)+ 5;
            System.out.println(k.getNama() + " gas biasa! \n+" + maju + " meter");
            k.setPosisi(k.getPosisi() + maju);
            k.setNitroTerakhir(false);
            }
        }
        
        return cekKondisiMenang(k.getNama(), k.getPosisi());  
    }
        
    // Method khusus Motor
    public static boolean prosesGiliranMotor(Motor k, Scanner sc, Random rndm){
        System.out.println("\n=============================================");
        System.out.println("             Giliran " + k.getNama() +"      ");
        System.out.println("       | Posisi: " + k.getPosisi() + "m | Sisa Nitro: " + k.getNitro() + " |");
        System.out.println("---------------------------------------------");
        
        // Cek Overheat (skip turn)
        if(k.isOverheat()){
            System.out.println(k.getNama() + " sedang OVERHEAT! Tidak bisa jalan (skip turn).");
            k.setOverheat(false);
            return false;
        }
        
        System.out.println("Menu:\n1. Gas Biasa (+random)\n2. Nitro (+30)");
        System.out.print("Pilih aksi (1/2): ");
        int aksi = sc.nextInt();
        
        if(aksi == 2){
            if(k.getNitro() > 0){
                if(k.isNitroTerakhir()){
                    System.out.println(k.getNama() + " OVERHEAT! Tidak bisa jalan");
                    k.setOverheat(true);
                    k.setNitroTerakhir(false);
                } else {
                    System.out.println(k.getNama() + " pakai Nitro!\n+30 meter");
                    k.setPosisi(k.getPosisi() + 30);
                    k.setNitro(k.getNitro() - 1);
                    k.setNitroTerakhir(true);
                }
                
            } else {
                System.out.println("Nitro habis! Terpaksa gas biasa.");
                int maju = rndm.nextInt(15) + 5;
                System.out.println(k.getNama() + " gas biasa! \n+" + maju + " meter");
                k.setPosisi(k.getPosisi() + maju);
                k.setNitroTerakhir(false);
            }
        } else {
            if(aksi == 1){
            int maju = rndm.nextInt(15) + 5;
            System.out.println(k.getNama() + " gas biasa! \n+" + maju + " meter");
            k.setPosisi(k.getPosisi() + maju);
            k.setNitroTerakhir(false);
            }
        }   
        
        return cekKondisiMenang(k.getNama(), k.getPosisi());  
    }
    
    // Mengecek siapa yg menyusul dan melewati garis finish
    public static boolean cekKondisiMenang(String nama, int posisiSekarang){
        
        //Logika Menyusul
        if (posisiSekarang > posisiTerdepan){
            if (posisiTerdepan > 0 && (posisiSekarang - posisiTerdepan > 0)) {
                System.out.println(nama + " menyusul!");
            }
            posisiTerdepan = posisiSekarang;
        }

        // Logika Finish
        if (posisiSekarang >= 100) {
            System.out.println("\n================================");
            System.out.println(nama + " MENANG!");
            return true; 
        }
        return false; 
    
    }
}


