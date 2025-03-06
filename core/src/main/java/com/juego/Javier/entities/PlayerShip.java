package com.juego.Javier.entities;

import static com.juego.Javier.GameClass.sound;
import static com.juego.Javier.scenes.GameScene.PPM;
import static com.juego.Javier.scenes.GameScene.WORLD_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.juego.Javier.GameClass;
import com.juego.Javier.hud.Joystick;
import com.juego.Javier.scenes.GameScene;

public class PlayerShip extends Sprite {

    private Body body;
    private World world;
    private Joystick joystick;
    private GameClass gameClass;
    private GameScene gameScene;
    // Propiedades de progresión
    private float baseDamage = 10f;       // Daño base
    private float damageMultiplier = 1f;  // Multiplicador que se incrementa con upgrades
    private float fireRate = 0.3f;        // Tiempo mínimo entre disparos (en segundos)
    private float fireCooldown = 0f;
    private int extraShots = 0;           // Número de disparos adicionales laterales (0 = disparo único)
    private boolean frontShotEnabled = false; // Disparo extra frontal

    public float scaleFactor = 2f;
    private float currentAngle = 0f;
    private float velocity = 150f;
    private boolean isDead;
    private int life;
    private int maxLife;
    private boolean hasPenetratingShots = false;


    public PlayerShip(World world, Joystick joystick, GameClass gameClass, GameScene gameScene) {
        super(new Texture(Gdx.files.internal("ships/playerships/copper.png")));
        setSize(getTexture().getWidth() / PPM * scaleFactor, getTexture().getHeight() / PPM * scaleFactor);
        setOrigin(getWidth() / 2f, getHeight() / 2f);


        this.world = world;
        this.joystick = joystick;
        this.gameClass = gameClass;
        this.gameScene = gameScene;
        createBody();
        setMaxLife(100);
        setLife(100);
        // Inicialmente el daño base se iguala a baseDamage
    }

    public void update(float delta) {
        Vector2 direction = joystick.getJoystickDirection();
        if (direction.len() > 0) {
            Vector2 impulse = new Vector2(direction.x * velocity * delta, direction.y * velocity * delta);
            body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
            float angle = MathUtils.atan2(direction.y, direction.x);
            currentAngle = MathUtils.radiansToDegrees * angle;
            setRotation(currentAngle - 90);
        } else {
            setRotation(currentAngle - 90);
        }
        if (fireCooldown > 0) {
            fireCooldown -= delta;
        }
//        // Ejemplo de disparo: dispara cuando se toca la pantalla
//        if (Gdx.input.justTouched() && fireCooldown <= 0) {
//            fireWeapon();
//            fireCooldown = fireRate;
//        }
        if (direction.len() > 0 && fireCooldown <= 0) {
            fireWeapon();
            fireCooldown = fireRate;
        }
    }

    private void fireWeapon() {
        if(GameClass.prefs.getBoolean("sfxOn", true)){
            sound.play();
        }
        Vector2 position = body.getPosition();
        // Si no hay dirección (joystick en reposo), disparar hacia arriba
        Vector2 direction = joystick.getJoystickDirection();
        if (direction.len() == 0) {
            direction.set(0, 1);
        } else {
            direction.nor();
        }
        float damage = baseDamage * damageMultiplier;
        // Disparo principal
        gameScene.bulletManager.addBullet(position.x, position.y, direction, false);

        // Disparos laterales (multishot): disparos a ángulos pequeños a la izquierda y derecha
        if (extraShots > 0) {
            float spreadAngle = 10f; // grados de separación
            for (int i = 1; i <= extraShots; i++) {
                Vector2 leftDir = direction.cpy().rotateDeg(spreadAngle * i);
                Vector2 rightDir = direction.cpy().rotateDeg(-spreadAngle * i);
                gameScene.bulletManager.addBullet(position.x, position.y, leftDir, false);
                gameScene.bulletManager.addBullet(position.x, position.y, rightDir, false);
            }
        }
        // Disparo extra frontal, si está activado
        if (frontShotEnabled) {
            float extraDamage = damage * 1.2f; // Bono de daño extra
            gameScene.bulletManager.addBullet(position.x, position.y, direction, false);
        }
    }

    public void render(SpriteBatch batch) {
        float x = body.getPosition().x;
        float y = body.getPosition().y;
        setPosition(x - getWidth() / 2f, y - getHeight() / 2f);
        super.draw(batch);
    }

    public void dispose() {
        super.getTexture().dispose();
    }

    public void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        // Utilizar los mismos offsets que en createWorldBounds()
        float offsetX = 100f;
        float offsetY = 100f;
        float initialX = GameScene.WORLD_WIDTH / 2f + offsetX;
        float initialY = GameScene.WORLD_HEIGHT / 2f + offsetY;
        bodyDef.position.set(initialX, initialY);
        bodyDef.linearDamping = 2.5f;
        this.body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(getWidth() / 2f, getHeight() / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GameScene.CATEGORY_PLAYER;
        fixtureDef.filter.maskBits = GameScene.MASK_PLAYER;
        body.createFixture(fixtureDef).setUserData("player");
        shape.dispose();
    }

    // --- Métodos para progresión ---
    public void upgradeDamage(float increase) {
        damageMultiplier += increase;
    }

    public void upgradeFireRate(float newFireRate) {
        // Si el nuevo fireRate es menor (más rápido), se actualiza
        if (newFireRate < fireRate) {
            fireRate = newFireRate;
        }
    }

    public void upgradeMultishot(int extra) {
        extraShots += extra;
    }

    public void enableFrontShot() {
        frontShotEnabled = true;
    }
    // ---------------------------------

    // Getters y setters para vida, daño, etc.
    public void setLife(int life) {
        this.life = life;
    }

    public int getLife() {
        return life;
    }

    public void setMaxLife(int maxLife) {
        this.maxLife = maxLife;
    }

    public int getMaxLife() {
        return maxLife;
    }

    public void setBassedDamage(float bassedDamage) {
        this.baseDamage = bassedDamage;
    }

    public float getBassedDamage() {
        return baseDamage;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public Body getBody() {
        return body;
    }

    public Vector2 joystickDirection() {
        return joystick.getJoystickDirection().cpy();
    }

    public boolean isDead() {
        return isDead;
    }

    public void takeDamage(int damage) {
        if (isDead) return;
        setLife(getLife() - damage);
        if (getLife() <= 0) {
            setLife(0);
            //isDead = true;
        }
    }

    public void heal(int amount) {
        setLife(Math.min(getLife() + amount, getMaxLife()));
    }

    public float getFireRate() {
        return fireRate;
    }

    public void setFireRate(float fireRate) {
        this.fireRate = fireRate;
    }

    public void setHasPenetratingShots(boolean hasPenetratingShots) {
        this.hasPenetratingShots = hasPenetratingShots;
    }

    public boolean hasPenetratingShots() {
        return hasPenetratingShots;
    }
}
