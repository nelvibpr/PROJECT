package escapefromcampus;

import javax.sound.sampled.*;
import java.net.URL;

public class SoundManager {
    private static Clip clip;

    public static void playMusic(String path) {
        try {
            URL url = SoundManager.class.getResource(path);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            // --- TAMBAHKAN KODE INI UNTUK MENGECILKAN VOLUME ---
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-30.0f); 
            }
            // ---------------------------------------------------

            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            System.out.println("Gagal memuat musik: " + e.getMessage());
        }
    }

    public static void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}