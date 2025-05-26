package io.github.space_wars.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
    private Music backgroundMusic;
    private Sound gameOverSound;

    public SoundManager() {
        try {
            if (Gdx.files.internal("music.mp3").exists()) {
                backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
                backgroundMusic.setLooping(true);
                backgroundMusic.setVolume(0.5f);
            } else {
                Gdx.app.error("SoundManager", "music.mp3 file not found");
            }

            if (Gdx.files.internal("gameover.mp3").exists()) {
                gameOverSound = Gdx.audio.newSound(Gdx.files.internal("gameover.mp3"));
            } else {
                Gdx.app.error("SoundManager", "gameover.mp3 file not found");
            }
        } catch (Exception e) {
            Gdx.app.error("SoundManager", "Error loading audio: " + e.getMessage(), e);
        }
    }

    public void playBackgroundMusic() {
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            try {
                backgroundMusic.play();
            } catch (Exception e) {
                Gdx.app.error("SoundManager", "Error playing background music: " + e.getMessage(), e);
            }
        }
    }

    public void playGameOverSound() {
        if (gameOverSound != null) {
            try {
                if (backgroundMusic != null && backgroundMusic.isPlaying()) {
                    backgroundMusic.pause();
                }
                gameOverSound.play(0.7f);
            } catch (Exception e) {
                Gdx.app.error("SoundManager", "Error playing game over sound: " + e.getMessage(), e);
            }
        }
    }

    public void pauseBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }

    public void setVolume(float volume) {
        if (backgroundMusic != null) {
            float clampedVolume = Math.max(0, Math.min(1, volume));
            backgroundMusic.setVolume(clampedVolume);
        }
    }

    public void dispose() {
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
        if (gameOverSound != null) {
            gameOverSound.dispose();
        }
    }
}
