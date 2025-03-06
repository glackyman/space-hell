package com.juego.Javier.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameRecords {
    private static final String RECORDS_FILE = "game_records.prefs";

    private int maxKills;
    private int maxDeaths;
    private int highestWave;

    public GameRecords() {
        load();
    }

    public void save() {
        Preferences prefs = Gdx.app.getPreferences(RECORDS_FILE);
        prefs.putInteger("maxKills", maxKills);
        prefs.putInteger("maxDeaths", maxDeaths);
        prefs.putInteger("highestWave", highestWave);
        prefs.flush();
    }

    public void load() {
        Preferences prefs = Gdx.app.getPreferences(RECORDS_FILE);
        maxKills = prefs.getInteger("maxKills", 0);
        maxDeaths = prefs.getInteger("maxDeaths", 0);
        highestWave = prefs.getInteger("highestWave", 0);
    }

    // Getters y Setters
    public int getMaxKills() { return maxKills; }
    public int getMaxDeaths() { return maxDeaths; }
    public int getHighestWave() { return highestWave; }

    public void updateKills(int kills) {
        if(kills > maxKills) {
            maxKills = kills;
            save();
        }
    }

    public void updateDeaths(int deaths) {
        if(deaths > maxDeaths) {
            maxDeaths = deaths;
            save();
        }
    }

    public void updateWave(int wave) {
        if(wave > highestWave) {
            highestWave = wave;
            save();
        }
    }

    public void reset() {
        maxKills = 0;
        maxDeaths = 0;
        highestWave = 0;
        save();
    }
}
