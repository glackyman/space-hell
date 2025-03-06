package com.juego.Javier.manager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.juego.Javier.entities.Bullet;
import com.juego.Javier.entities.HealthDrop;
import com.juego.Javier.entities.PlayerShip;

import java.util.Iterator;

public class DropManager {

    private Array<HealthDrop> activeDrops;

    private World world;
    public DropManager(World world) {
        this.world = world;

        activeDrops = new Array<>();

    }

    public void addDrop(HealthDrop drop) {
        activeDrops.add(drop);
    }

    public void update(float delta, PlayerShip player) {
        for (HealthDrop drop : activeDrops) {
            drop.update(delta);
        }

        Iterator<HealthDrop> iter = activeDrops.iterator();
        while (iter.hasNext()) {
            HealthDrop drop = iter.next();
            if (drop.isCollected()) {
                world.destroyBody(drop.getBody());
                drop.dispose();
                iter.remove();
            }
        }
         //Eliminar drops recolectados
        //activeDrops.removeAll(activeDrops -> drop.isCollected(), true);
    }

    public void render(SpriteBatch batch) {
        for (HealthDrop drop : activeDrops) {
            drop.render(batch);
        }
    }

    public void dispose() {
        for (HealthDrop drop : activeDrops) {
            drop.dispose();
        }
        activeDrops.clear();
    }

    public Array<HealthDrop> getActiveDrops() {
        return activeDrops;
    }
}
