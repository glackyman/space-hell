package com.juego.Javier.entities;

public class Wave {
    private int shooterCount;
    private int kamikazeCount;
    private int rotationEnemyCount;

    public Wave(int shooterCount, int kamikazeCount, int rotationEnemyCount) {
        this.shooterCount = shooterCount;
        this.kamikazeCount = kamikazeCount;
        this.rotationEnemyCount = rotationEnemyCount;
    }

    public int getShooterCount() { return shooterCount; }
    public int getKamikazeCount() { return kamikazeCount; }
    public int getRotationEnemyCount() { return rotationEnemyCount; }
}
