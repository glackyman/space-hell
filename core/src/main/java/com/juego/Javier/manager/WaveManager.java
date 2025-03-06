package com.juego.Javier.manager;

import static com.juego.Javier.GameClass.bundle;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.juego.Javier.entities.Wave;

import java.util.ArrayList;
import java.util.List;

public class WaveManager {
    private EnemyManager enemyManager;
    private int currentWave;
    private boolean waveInProgress;
    private boolean waveCompleted; // Nuevo estado
    private Vector2 lastPlayerPosition;

    public WaveManager(EnemyManager enemyManager) {
        this.enemyManager = enemyManager;
        this.currentWave = 0;
        this.waveInProgress = false;
        this.waveCompleted = false;
    }

    public void update(float delta) {
        // Verificar si todos los enemigos han sido derrotados
        if (waveInProgress && enemyManager.getActiveEnemyCount() == 0) {
            waveInProgress = false;
            waveCompleted = true; // Marcar la oleada como completada
        }
    }

    public void startNextWave(Vector2 playerPosition) {
        if (waveInProgress) return;

        this.lastPlayerPosition = playerPosition;
        this.waveInProgress = true;
        this.waveCompleted = false; // Reiniciar estado
        currentWave++;
        spawnWave(generateWave(currentWave), lastPlayerPosition);
    }

    public boolean isWaveComplete() {
        return waveCompleted;
    }

    private void startWave() {
        currentWave++;
        Wave wave = generateWave(currentWave);
        spawnWave(wave, lastPlayerPosition);
    }

    public boolean isWaveInProgress() {
        return waveInProgress;
    }

    // Método vacío porque ya no hay cuenta regresiva
    public String getCountdownText() {
        return "";
    }

    private Wave generateWave(int waveNumber) {
        // Cálculo exponencial para cada tipo de enemigo
        int shooterCount = (int) Math.round(5 * Math.pow(1.3, waveNumber));  // Base 5, crecimiento 30% por oleada
        int kamikazeCount = (int) Math.round(2 * Math.pow(1.25, waveNumber)); // Base 2, crecimiento 25% por oleada
        int rotationEnemyCount = (waveNumber >= 5) ? (int) Math.round(Math.pow(1.2, waveNumber - 4)) : 0; // Aparece desde oleada 5

        return new Wave(shooterCount, kamikazeCount, rotationEnemyCount);
    }

    private void spawnWave(Wave wave, Vector2 playerPosition) {
        for (int i = 0; i < wave.getShooterCount(); i++) {
            enemyManager.spawnEnemy(getSpawnPosition(playerPosition));
        }
        for (int i = 0; i < wave.getKamikazeCount(); i++) {
            enemyManager.spawnEnemy(getSpawnPosition(playerPosition));
        }
        for (int i = 0; i < wave.getRotationEnemyCount(); i++) {
            enemyManager.spawnEnemy(getSpawnPosition(playerPosition));
        }
    }

    private Vector2 getSpawnPosition(Vector2 playerPosition) {
        float angle = MathUtils.random(0f, 360f) * MathUtils.degreesToRadians;
        float distance = 10f; // Distancia de spawn

        return new Vector2(
            playerPosition.x + distance * MathUtils.cos(angle),
            playerPosition.y + distance * MathUtils.sin(angle)
        );
    }

    public int getCurrentWave() {
        return currentWave;
    }

}
