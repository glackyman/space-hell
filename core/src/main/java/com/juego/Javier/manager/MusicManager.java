package com.juego.Javier.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.juego.Javier.GameClass;

public class MusicManager {
    private static MusicManager instance;
    private Music currentMusic;
    private float volume = 0.5f; // Volumen por defecto

    private MusicManager() {} // Constructor privado para Singleton

    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    public void play(String filePath) {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
        }
        currentMusic = Gdx.audio.newMusic(Gdx.files.internal(filePath));
        currentMusic.setLooping(true);
        currentMusic.setVolume(volume);
        currentMusic.play();
    }

    public void setVolume(float volume) {
        if(currentMusic != null) {
            currentMusic.setVolume(volume);
        }
        // Guardar la preferencia
        GameClass.prefs.putFloat("musicVolume", volume);
        GameClass.prefs.flush();
    }

    public void pause() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }

    public void resume() {
        if (currentMusic != null && !currentMusic.isPlaying()) {
            currentMusic.play();
        }
    }

    public void dispose() {
        if (currentMusic != null) {
            currentMusic.dispose();
            currentMusic = null;
        }
    }
}
