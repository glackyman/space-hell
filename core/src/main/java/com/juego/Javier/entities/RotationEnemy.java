package com.juego.Javier.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.juego.Javier.manager.DropManager;
import com.juego.Javier.scenes.GameScene;

public class RotationEnemy extends EnemyShip {
    public enum DebuffEffect {
        SLOW,       // Ralentizar al jugador
        INVERT,     // Invertir los ejes del joystick
        PUSH        // Impulsar al jugador en una dirección aleatoria
    }

    private static int orbitingEnemies = 0; // Cuenta de enemigos orbitando
    private static final int MAX_ORBITING = 2; // Máximo de enemigos en órbita

    private float orbitRadius = 10f; // Distancia deseada al jugador
    private float orbitSpeed = 2f;  // Velocidad de la órbita
    private float currentAngle = 0f; // Ángulo actual en la órbita
    private PlayerShip playerShip; // Referencia al jugador

    private DebuffEffect debuffEffect; // Efecto asignado a este enemigo
    private boolean isEffectActive = false; // Indica si el efecto está activo
    private static final Array<RotationEnemy> activeSlowEnemies = new Array<>();

    private static final String ROTATION_ENEMY_TEXTURE = "ships/enemies/rotate.png";
    public RotationEnemy(World world, PlayerShip playerShip, DropManager dropManager) {
        super(world,dropManager);
        setTexture(new Texture("ships/enemies/rotate.png"));
        setScale(2f);
        setLife(50f);
        this.playerShip = playerShip;
        orbitingEnemies++; // Incrementar la cuenta de enemigos orbitando

        debuffEffect = DebuffEffect.SLOW;
    }

    @Override
    public void update(float delta, Vector2 playerPosition) {
        super.update(delta, playerPosition);
        Vector2 enemyPos = getBody().getPosition();
        Vector2 toPlayer = new Vector2(playerPosition).sub(enemyPos);

        // Actualiza el ángulo de la órbita
        currentAngle += orbitSpeed * delta;

        // Calcula la nueva posición en la órbita
        float newX = playerPosition.x + orbitRadius * MathUtils.cos(currentAngle);
        float newY = playerPosition.y + orbitRadius * MathUtils.sin(currentAngle);

        // Mueve al enemigo a la nueva posición
        Vector2 newPosition = new Vector2(newX, newY);
        Vector2 velocity = newPosition.sub(enemyPos).scl(10f); // Ajusta la velocidad para suavizar el movimiento
        getBody().setLinearVelocity(velocity);

        // Rotación hacia el jugador
        float angle = MathUtils.atan2(toPlayer.y, toPlayer.x);
        setRotation(MathUtils.radiansToDegrees * angle - 90);

        if (isOrbiting()) {
            if (!isEffectActive) {
                applyDebuffEffect(playerShip);
                isEffectActive = true;
            }
        } else if (isEffectActive) {
            removeDebuffEffect(playerShip);
            isEffectActive = false;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        //setColor(Color.RED);
    }

    @Override
    public void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        // Colocamos la nave en el centro de la pantalla (en unidades físicas)
        float initialX = GameScene.WORLD_WIDTH / 2f;  // Coordenada X en metros (ajustada por PPM)
        float initialY = GameScene.WORLD_HEIGHT * 0.2f / 2f; // Coordenada Y en metros (ajustada por PPM)
        bodyDef.position.set(initialX, initialY);
        bodyDef.linearDamping = 5f;
        this.setBody(getWorld().createBody(bodyDef));

        // Definimos el tamaño del cuerpo en unidades físicas, ajustando por PPM
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(((getWidth() / 2f)), ((getHeight() / 2f))); // Tamaño ajustado a PPM

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GameScene.CATEGORY_ROTATE_ENEMY;
        fixtureDef.filter.maskBits = GameScene.MASK_ROTATE_ENEMY;
        getEnemyBody().createFixture(fixtureDef).setUserData("rotate_enemy");
        //body.createFixture(fixtureDef);

        shape.dispose();
    }

    public static int getOrbitingEnemies() {
        return orbitingEnemies;
    }

    private void applyDebuffEffect(PlayerShip player) {
        switch (debuffEffect) {
            case SLOW:
                if (!activeSlowEnemies.contains(this, true)) {
                    activeSlowEnemies.add(this);
                    recalculateSpeed(player);
                }
                break;
            case INVERT:
                //player.setInvertedControls(true);
                break;
            case PUSH:
                pushPlayer(player);
                break;
        }
    }

    public void removeDebuffEffect(PlayerShip player) {
        switch (debuffEffect) {
            case SLOW:
                activeSlowEnemies.removeValue(this, true);
                recalculateSpeed(player);
                break;
            case INVERT:

                break;
        }
    }

    private void recalculateSpeed(PlayerShip player) {
        int activeCount = activeSlowEnemies.size;
        float newSpeed = 150f * (float) Math.pow(0.5f, activeCount); // Reducción del 50% por enemigo
        player.setVelocity(newSpeed);
    }

    private void pushPlayer(PlayerShip player) {
        Vector2 pushDirection = new Vector2(MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f)).nor();
        player.getBody().applyLinearImpulse(pushDirection.scl(10f), player.getBody().getWorldCenter(), true);
    }

    public boolean isOrbiting() {
        return orbitingEnemies > 0; // Si hay enemigos orbitando, devuelve true
    }

    @Override
    public void dispose() {
        super.dispose();
        orbitingEnemies = Math.max(0, orbitingEnemies - 1); // Evita valores negativos
        removeDebuffEffect(playerShip); // Remueve el debuff si estaba activo
    }
}

