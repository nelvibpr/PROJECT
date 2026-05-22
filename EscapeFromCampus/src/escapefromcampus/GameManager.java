package escapefromcampus;

public class GameManager {
    public static final int KUNCI_TARGET = 0;

    public static String playerName = "";
    public static int playerGender = 0; // 0 = Laki-laki, 1 = Perempuan
    // -------------------------------

    public static int nyawa = 3;
    public static int kunci = 0;
    public static int kunciPalsu = 0;
    public static boolean catatanKelasDitemukan = false;
    public static boolean kelasSelesai = false;

    public static void resetGame() {
        nyawa = 3;
        kunci = 0;
        kunciPalsu = 0;
        catatanKelasDitemukan = false;
        kelasSelesai = false;
    }
}