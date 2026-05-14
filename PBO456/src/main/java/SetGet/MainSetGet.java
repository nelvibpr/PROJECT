package SetGet;

import java.util.Scanner;
        
public class MainSetGet {

    public static void main(String[] args) {
        UserSetGet nelvi = new UserSetGet();
        Scanner input = new Scanner (System.in);
        
        System.out.print("Nama: ");
        nelvi.setNama(input.nextLine());
        
        System.out.print("NIM: ");
        nelvi.setNIM(input.nextLine());
        
        System.out.print("Kelas: ");
        nelvi.setKelas(input.nextLine());
        
        System.out.println("========================");
        
        System.out.print("🏍️ Nama saya " + nelvi.getNama());
        System.out.print(" dengan NIM " + nelvi.getNIM());
        System.out.println(" dari kelas " + nelvi.getKelas());
        
    }
    
}

