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
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Musik akan berulang terus
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