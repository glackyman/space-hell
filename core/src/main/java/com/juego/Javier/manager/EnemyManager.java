package com.juego.Javier.manager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.juego.Javier.entities.EnemyShip;
import com.juego.Javier.entities.KamikazeEnemy;
import com.juego.Javier.entities.PlayerShip;
import com.juego.Javier.entities.RotationEnemy;
import com.juego.Javier.entities.ShooterEnemy;

import java.util.Iterator;

public class EnemyManager {
    private Array<EnemyShip> enemies;
    private World world;
    private PlayerShip playerShip;
    private BulletManager bulletManager;
    private DropManager dropManager;
    private int wave = 1;
    private int enemiesToSpawn = 5;
    private int enemiesSpawned = 0;
    private int enemiesDefeated = 0;

    public EnemyManager(World world, PlayerShip playerShip, BulletManager bulletManager, DropManager dropManager) {
        this.world = world;
        this.enemies = new Array<>();
        this.playerShip = playerShip;
        this.bulletManager = bulletManager;
        this.dropManager = dropManager;
    }

    public void update(float delta, Vector2 playerPosition) {
        for (EnemyShip enemy : enemies) {
            enemy.update(delta, playerPosition);
        }

        // Eliminar enemigos marcados para eliminación
        Iterator<EnemyShip> iter = enemies.iterator();
        while (iter.hasNext()) {
            EnemyShip enemy = iter.next();
            if (enemy.isDestroyed()) {
                if (enemy instanceof RotationEnemy) {
                    ((RotationEnemy) enemy).removeDebuffEffect(playerShip);
                }
                world.destroyBody(enemy.getBody());
                enemy.dispose();
                iter.remove();
                enemiesDefeated++;
            }
        }

        // Si todos los enemigos han sido eliminados, iniciar una nueva oleada
        if (enemiesDefeated >= enemiesToSpawn) {
            startNewWave();
        }

        avoidOverlapping();
    }

    public void render(SpriteBatch batch) {
        for (EnemyShip enemy : enemies) {
            enemy.render(batch);
        }
    }

    public void spawnEnemy(Vector2 position) {
        if (enemiesSpawned >= enemiesToSpawn) return;

        EnemyShip enemy;
        float spawnChance = MathUtils.random();
        Vector2 spawnPosition = getSpawnPosition(position, 200); // Nueva posición con distancia mínima

        if (wave < 3) {
            enemy = new ShooterEnemy(world, bulletManager, dropManager);
        } else if (spawnChance < 0.5f) {
            enemy = new ShooterEnemy(world, bulletManager, dropManager);
        } else if (spawnChance < 0.8f) {
            enemy = new KamikazeEnemy(world, dropManager);
        } else {
            if (RotationEnemy.getOrbitingEnemies() < 3) {
                enemy = new RotationEnemy(world, playerShip, dropManager);
            } else {
                enemy = new ShooterEnemy(world, bulletManager, dropManager);
            }
        }

        enemy.getBody().setTransform(spawnPosition, 0);
        enemies.add(enemy);
        enemiesSpawned++;
    }

    private Vector2 getSpawnPosition(Vector2 playerPosition, float minDistance) {
        Vector2 spawnPosition;
        do {
            float angle = MathUtils.random(0, 360) * MathUtils.degreesToRadians;
            float distance = MathUtils.random(minDistance, minDistance + 50); // Distancia aleatoria para variedad
            spawnPosition = new Vector2(
                playerPosition.x + MathUtils.cos(angle) * distance,
                playerPosition.y + MathUtils.sin(angle) * distance
            );
        } while (spawnPosition.dst2(playerPosition) < minDistance * minDistance); // Asegurar distancia mínima
        return spawnPosition;
    }

    private void startNewWave() {
        wave++;
        enemiesToSpawn += 3; // Aumentar enemigos por oleada
        enemiesSpawned = 0;
        enemiesDefeated = 0;
    }

    private void avoidOverlapping() {
        for (int i = 0; i < enemies.size; i++) {
            EnemyShip enemyA = enemies.get(i);
            for (int j = i + 1; j < enemies.size; j++) {
                EnemyShip enemyB = enemies.get(j);

                Vector2 diff = new Vector2(enemyA.getBody().getPosition()).sub(enemyB.getBody().getPosition());
                float distance = diff.len();

                if (distance < 3f) { // Distancia mínima entre enemigos
                    diff.nor().scl(20f);
                    enemyA.getBody().applyForceToCenter(diff, true);
                    enemyB.getBody().applyForceToCenter(diff.scl(-1), true); // Enemigos se repelen mutuamente
                }
            }
        }
    }

    public void dispose() {
        for (EnemyShip enemy : enemies) {
            enemy.dispose();
        }
        enemies.clear();
    }

    public Array<EnemyShip> getEnemies() {
        return enemies;
    }

    public int getActiveEnemyCount() {
        int count = 0;
        for (EnemyShip enemy : enemies) {
            if (!enemy.isDestroyed()) {
                count++;
            }
        }
        return count;
    }
}



//package com.juego.Javier.manager;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.math.MathUtils;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.physics.box2d.World;
//import com.badlogic.gdx.utils.Array;
//import com.juego.Javier.entities.EnemyShip;
//import com.juego.Javier.entities.KamikazeEnemy;
//import com.juego.Javier.entities.PlayerShip;
//import com.juego.Javier.entities.RotationEnemy;
//import com.juego.Javier.entities.ShooterEnemy;
//
//import java.util.Iterator;
//public class EnemyManager {
//    private Array<EnemyShip> enemies;
//    private World world;
//    private PlayerShip playerShip;
//    private  BulletManager bulletManager;
//    public EnemyManager(World world, PlayerShip playerShip, BulletManager bulletManager) {
//        this.world = world;
//        this.enemies = new Array<>();
//        this.playerShip = playerShip;
//        this.bulletManager = bulletManager;
//    }
//
//    public void update(float delta, Vector2 playerPosition) {
//        for (EnemyShip enemy : enemies) {
//            enemy.update(delta, playerPosition);
//        }
//
//        // Eliminar enemigos marcados para eliminación
//        Iterator<EnemyShip> iter = enemies.iterator();
//        while (iter.hasNext()) {
//            EnemyShip enemy = iter.next();
//            if (enemy.isDestroyed()) {
//                if (enemy instanceof RotationEnemy) {
//                    ((RotationEnemy) enemy).removeDebuffEffect(playerShip);
//                }
//                world.destroyBody(enemy.getBody());
//                enemy.dispose();
//                iter.remove();
//            }
//        }
//    }
//
//    public void render(SpriteBatch batch) {
//        for (EnemyShip enemy : enemies) {
//            enemy.render(batch);
//        }
//    }
//
//    public void spawnEnemy(Vector2 position) {
//        EnemyShip enemy;
//
//        float spawnChance = MathUtils.random(); // Número aleatorio entre 0 y 1
//
//        if (spawnChance < 0.6f) { // 60% de probabilidad de ser ShootingEnemy
//            enemy = new ShooterEnemy(world, bulletManager);
//        } else if (spawnChance < 0.3f) { // 30% de probabilidad de ser KamikazeEnemy
//            enemy = new KamikazeEnemy(world);
//        } else { // 10% de probabilidad de ser RotationEnemy (si no hay más de 3)
//            if (RotationEnemy.getOrbitingEnemies() < 3) {
//                enemy = new RotationEnemy(world, playerShip);
//            } else {
//                enemy = new ShooterEnemy(world, bulletManager); // Si ya hay 3, spawnear un ShootingEnemy
//            }
//        }
//
//        enemy.getBody().setTransform(position, 0); // Posicionar al enemigo
//        enemies.add(enemy);
//    }
//
//    public void dispose() {
//        for (EnemyShip enemy : enemies) {
//            enemy.dispose();
//        }
//        enemies.clear();
//    }
//
//    public Array<EnemyShip> getEnemies() {
//        return enemies;
//    }
//}
