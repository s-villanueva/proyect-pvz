package org.example.ui;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.example.model.Audio.Audio;
import org.example.model.Audio.AudioName;
import org.example.model.Audio.AudioState;
import org.example.model.Audio.DurationType;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioManager {

    private List<Audio> audios;
    private static final ExecutorService soundPool = Executors.newCachedThreadPool();

    public AudioManager() {
        audios = new ArrayList<>();
        audios.add(new Audio("sounds/Soundtrack.mp3", DurationType.ETERNAL));
        startThread();
    }

    public void addAudio(AudioName audioName) {
        switch (audioName) {
            case PEA_IMPACT -> audios.add(new Audio("sounds/Splat.mp3", DurationType.UNIQUE));
            case THROW -> audios.add(new Audio("sounds/Throw.mp3", DurationType.UNIQUE));
            case SNOW_PEA_IMPACT -> audios.add(new Audio("sounds/SnowPeaImpact.mp3", DurationType.UNIQUE));
            case PLANT_ATE -> audios.add(new Audio("sounds/Plant_ate.mp3", DurationType.UNIQUE));
            case PLANT_EATING -> audios.add(new Audio("sounds/Eating_plant.mp3", DurationType.UNIQUE));
            case PLANTING -> audios.add(new Audio("sounds/Planting.mp3", DurationType.UNIQUE));
            case SUN_PICKING -> audios.add(new Audio("sounds/Sun_points.mp3", DurationType.UNIQUE));
            case ZOMBIES_INCOMING -> audios.add(new Audio("sounds/Voice_zombies_are_coming.mp3", DurationType.UNIQUE));
            case ZOMBIES_WIN -> audios.add(new Audio("sounds/Zombies_win.mp3", DurationType.UNIQUE));
            case CHERRYBOMBING -> audios.add(new Audio("sounds/Cherry_bomb.mp3", DurationType.UNIQUE));
            case LAWN_MOWER -> audios.add(new Audio("sounds/Lawnmower.mp3", DurationType.UNIQUE));
            default -> System.out.println("audio aun no implementado");
        }
    }

    private void startThread() {
        new Thread(() -> {
            while (true) {
                for (int i = 0; i < audios.size(); i++) {
                    if (audios.get(i).getState() == AudioState.NEW) {
                        playSound(audios.get(i));
                    } else if (audios.get(i).getState() == AudioState.FINISHED) {
                        if (audios.get(i).getDurationType() == DurationType.ETERNAL) {
                            playSound(audios.get(i));
                        } else { // una sola reproduccion
                            audios.remove(i);
                            i--;
                        }
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void playSound(Audio audio) {
        //System.out.println("playSound inicio.");
        soundPool.execute(() -> { // funciona de forma asincrona
            Player player = null;
            try {
                audio.setState(AudioState.IN_PROGRESS);
                InputStream isSound = this.getClass().getClassLoader().getResourceAsStream(audio.getName());
                //System.out.println("inputStream: " + isSound);
                player = new Player(isSound); // no reproduce ogg
                player.play();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            } finally {
                if (player != null)
                    player.close();
                audio.setState(AudioState.FINISHED);
            }
        });
        //System.out.println("playSound fin.");
    }

}