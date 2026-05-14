package nelvi.uts;
        
public class Kendaraan {
    // Enkapsulasi (private)
    private String nama;
    private int posisi;
    private int nitro;
    private boolean overheat;
    private boolean nitroTerakhir;
    
    // Constructor (this)
    public Kendaraan(String nama, int nitro){
        this.nama = nama;
        this.posisi = 0;
        this.nitro = nitro;
        this.overheat = false;
        this.nitroTerakhir = false;
    }
    
    // Getter dan Setter
    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getPosisi() {
        return posisi;
    }

    public void setPosisi(int posisi) {
        this.posisi = posisi;
    }

    public int getNitro() {
        return nitro;
    }

    public void setNitro(int nitro) {
        this.nitro = nitro;
    }

    public boolean isOverheat() {
        return overheat;
    }

    public void setOverheat(boolean overheat) {
        this.overheat = overheat;
    }

    public boolean isNitroTerakhir() {
        return nitroTerakhir;
    }

    public void setNitroTerakhir(boolean nitroTerakhir) {
        this.nitroTerakhir = nitroTerakhir;
    }
    
}
