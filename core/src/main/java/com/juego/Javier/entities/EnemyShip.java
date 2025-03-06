package com.juego.Javier.entities;

import static com.juego.Javier.scenes.GameScene.PPM;
import static com.juego.Javier.GameClass.explosionSound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.juego.Javier.GameClass;
import com.juego.Javier.manager.DropManager;
import com.juego.Javier.scenes.GameScene;

public class EnemyShip extends Sprite {

    private Body enemyBody;
    private World world;

    private Texture enemyTexture;
    public float scaleFactor = 2f;
    private float currentAngle = 0f;  // Variable para almacenar el ángulo de rotación
    private float velocity = 1f;
    private Vector2 direction;
    private boolean isDestroyed = false;

    private float life;

    private float hitTimer = 0f;
    private boolean isHit = false;
    private float blinkTimer = 0f;
    private boolean isVisible = true; // Controla la visibilidad del sprite

    private static final float DROP_CHANCE = 0.15f; // 30% de probabilidad
    private DropManager dropManager;

    public EnemyShip(World world, DropManager dropManager) {
        super(new Texture(Gdx.files.internal("ships/playerships/copper.png")));
        this.dropManager = dropManager;
        // Ajustar el tamaño del sprite para que se mantenga en proporción con el cuerpo
        setSize(getTexture().getWidth() / PPM * scaleFactor, getTexture().getHeight() / PPM * scaleFactor);
        setOrigin(getWidth() / 2f, getHeight() / 2f);

        this.world = world;

        createBody();

        setLife(50f);
    }


    public void render(SpriteBatch batch) {
        float x = enemyBody.getPosition().x; // Convertir de metros a píxeles
        float y = enemyBody.getPosition().y; // Convertir de metros a píxeles

        setPosition(x - getWidth() / 2f, y - getHeight() / 2f); // Centrar el sprite
        super.draw(batch);
    }

    public void update(float delta, Vector2 playerPosition) {
        this.direction = playerPosition;
        Vector2 chaseDirectio = new Vector2(direction.x - enemyBody.getPosition().x, direction.y - enemyBody.getPosition().y);
        chaseDirectio.nor().scl(1f);
        enemyBody.applyLinearImpulse(chaseDirectio, enemyBody.getWorldCenter(), true);

        // Obtener la dirección del movimiento del enemigo
        Vector2 velocity = enemyBody.getLinearVelocity();

        // Si la velocidad es significativa, actualizar la rotación
        if (velocity.len2() > 0.01f) {
            float angle = MathUtils.atan2(velocity.y, velocity.x); // Ángulo en radianes
            currentAngle = MathUtils.radiansToDegrees * angle; // Convertir a grados
            setRotation(currentAngle - 90); // Ajustar para que la nave apunte en la dirección correcta
        }

        if (isHit) {
            hitTimer -= delta;
            if (hitTimer <= 0) {
                setColor(1, 1, 1, 1); // Restaurar color
                isHit = false;
            }
        }
    }

    public void dispose() {
        super.getTexture().dispose();
    }

    public void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        // Colocamos la nave en el centro de la pantalla (en unidades físicas)
        float initialX = GameScene.WORLD_WIDTH / 2f;  // Coordenada X en metros (ajustada por PPM)
        float initialY = GameScene.WORLD_HEIGHT * 0.2f / 2f; // Coordenada Y en metros (ajustada por PPM)
        bodyDef.position.set(initialX, initialY);
        bodyDef.linearDamping = 5f;
        this.enemyBody = world.createBody(bodyDef);

        // Definimos el tamaño del cuerpo en unidades físicas, ajustando por PPM
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(((getWidth() / 2f)), ((getHeight() / 2f))); // Tamaño ajustado a PPM

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        enemyBody.createFixture(fixtureDef).setUserData("body");
        //body.createFixture(fixtureDef);

        shape.dispose();
    }

    public float getLife() {
        return life;
    }

    public void setLife(float life) {
        this.life = life;
    }

    public Body getBody() {
        return enemyBody;
    }

    public void markForRemoval() {
        isDestroyed = true;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public void hit(float damage) {
        life -= damage;
        if (life <= 0) {
            tryDropHealth();
            markForRemoval();
            if (GameClass.prefs.getBoolean("sfxOn", true)) {
//                explosionSound.setVolume(GameClass.explosionSound.play(), GameClass.prefs.getFloat("sfxVolume", 1f));
//                explosionSound.play();
            }
        } else {
            isHit = true;
            hitTimer = 0.2f; // Duración del efecto
            setColor(1, 0, 0, 1); // Rojo
        }
    }

    public void setBody(Body body) {
        this.enemyBody = body;
    }

    public Body getEnemyBody() {
        return enemyBody;
    }

    public World getWorld() {
        return world;
    }

    private void tryDropHealth() {
        if (MathUtils.random() <= DROP_CHANCE) {
            final Vector2 dropPosition = new Vector2(enemyBody.getPosition());
            // Posponer la creación del drop fuera del step de Box2D
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    dropManager.addDrop(new HealthDrop(world, dropPosition));
                }
            });
        }
    }
}

