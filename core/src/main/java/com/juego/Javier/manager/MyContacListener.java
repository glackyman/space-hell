package com.juego.Javier.manager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class MyContacListener implements ContactListener {
    private final Array<Body> bodiesToRemove = new Array<>();

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        // Comprobar colisión entre bala y enemigo
        if (isBullet(fixtureA) && isEnemy(fixtureB)) {
            addBodyToRemove(fixtureB.getBody());
        } else if (isBullet(fixtureB) && isEnemy(fixtureA)) {
            addBodyToRemove(fixtureA.getBody());
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
//        Fixture fixtureA = contact.getFixtureA();
//        Fixture fixtureB = contact.getFixtureB();
//
//        // Evitar colisiones entre balas y el jugador
//        if ((isBullet(fixtureA) && isPlayer(fixtureB)) || (isBullet(fixtureB) && isPlayer(fixtureA))) {
//            contact.setEnabled(false);
//        }
//
//        // Evitar colisiones entre balas
//        if (isBullet(fixtureA) && isBullet(fixtureB)) {
//            contact.setEnabled(false);
//        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    public void removeBodies(World world) {
        for (Body body : bodiesToRemove) {
            if (body != null && body.getWorld() != null) {
                world.destroyBody(body);
            }
        }
        bodiesToRemove.clear();
    }

    private void addBodyToRemove(Body body) {
        if (body != null && body.getWorld() != null && !bodiesToRemove.contains(body, true)) {
            bodiesToRemove.add(body);
        }
    }

    private boolean isPlayer(Fixture fixture) {
        return "player".equals(fixture.getUserData());
    }

    private boolean isEnemy(Fixture fixture) {
        return "body".equals(fixture.getUserData()); // Asegúrate de marcar los enemigos con "enemy"
    }

    private boolean isBullet(Fixture fixture) {
        return "bullet".equals(fixture.getUserData());
    }
}
