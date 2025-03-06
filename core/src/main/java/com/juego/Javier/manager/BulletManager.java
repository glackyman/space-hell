package com.juego.Javier.manager;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.juego.Javier.entities.Bullet;

import java.util.Iterator;

public class BulletManager {
    private Array<Bullet> bullets;
    private World world;
    private OrthographicCamera gameCam;

    public BulletManager(World world, OrthographicCamera gameCam) {
        this.world = world;
        this.bullets = new Array<>();
        this.gameCam = gameCam;
    }

    public void update(float delta) {
        // Actualizar todas las balas
        for (Bullet bullet : bullets) {
            bullet.update(delta);
        }

        // Eliminar balas que ya no son necesarias
        Iterator<Bullet> iter = bullets.iterator();
        while (iter.hasNext()) {
            Bullet bullet = iter.next();
            if (bullet.isOutOfBounds() || bullet.isMarkedForRemoval()) {
                bullet.dispose();
                iter.remove();
            }
        }
    }

    public void render(SpriteBatch batch) {
        // Renderizar todas las balas
        for (Bullet bullet : bullets) {
            bullet.render(batch);
        }
    }


    public void dispose() {
        // Liberar recursos de todas las balas
        for (Bullet bullet : bullets) {
            bullet.dispose();
        }
        bullets.clear();
    }

    private int shootLevel = 1; // 1 = simple, 2 = doble, 3 = triple

    public void upgradeShoot() {
        if (shootLevel < 3) {
            shootLevel++;
        }
    }

    public void addBullet(float x, float y, Vector2 direction) {
        switch (shootLevel) {
            case 1:
                bullets.add(new Bullet(world, x, y, direction, gameCam));
                break;
            case 2:
                // Doble disparo
                bullets.add(new Bullet(world, x, y, direction.cpy().rotateDeg(10), gameCam));
                bullets.add(new Bullet(world, x, y, direction.cpy().rotateDeg(-10), gameCam));
                break;
            case 3:
                // Triple disparo
                bullets.add(new Bullet(world, x, y, direction, gameCam));
                bullets.add(new Bullet(world, x, y, direction.cpy().rotateDeg(15), gameCam));
                bullets.add(new Bullet(world, x, y, direction.cpy().rotateDeg(-15), gameCam));
                break;
        }
    }

    public void addBullet(float x, float y, Vector2 direction, boolean isEnemy) {
        switch (shootLevel) {
            case 1:
                bullets.add(new Bullet(world, x, y, direction, gameCam, isEnemy));
                break;
            case 2:
                // Doble disparo
                bullets.add(new Bullet(world, x, y, direction.cpy().rotateDeg(10), gameCam, isEnemy));
                bullets.add(new Bullet(world, x, y, direction.cpy().rotateDeg(-10), gameCam, isEnemy));
                break;
            case 3:
                // Triple disparo
                bullets.add(new Bullet(world, x, y, direction, gameCam));
                bullets.add(new Bullet(world, x, y, direction.cpy().rotateDeg(15), gameCam, isEnemy));
                bullets.add(new Bullet(world, x, y, direction.cpy().rotateDeg(-15), gameCam, isEnemy));
                break;
        }
    }
    public Array<Bullet> getBullets() {
        return bullets;
    }
}
