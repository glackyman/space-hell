package com.juego.Javier.entities;

import static com.juego.Javier.GameClass.sound;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.juego.Javier.manager.BulletManager;
import com.juego.Javier.manager.DropManager;
import com.juego.Javier.scenes.GameScene;

public class ShooterEnemy extends EnemyShip {

    private float followDistance = 8f; // Distancia a la que seguirá al jugador
    private float followSpeed = 1.5f; // Velocidad de seguimiento


    private float shootTimer = 0;
    private float shootInterval = 2.5f; // Dispara cada 1.5 segundos
    private BulletManager bulletManager;
    public ShooterEnemy(World world, BulletManager bulletManager, DropManager dropManager) {
        super(world,dropManager);
        setTexture(new Texture("ships/enemies/shooter.png"));
        setScale(2.5f);
        this.bulletManager = bulletManager;
        setLife(40f);
    }

    @Override
    public void update(float delta, Vector2 playerPosition) {
        super.update(delta,playerPosition);
        Vector2 enemyPos = getBody().getPosition();
        Vector2 toPlayer = new Vector2(playerPosition).sub(enemyPos);
        float distance = toPlayer.len();

        // Seguir al jugador manteniendo una distancia
        if (distance > followDistance) {
            toPlayer.nor().scl(followSpeed); // Acercarse al jugador
        } else if (distance < followDistance - 2f) {
            toPlayer.nor().scl(-followSpeed); // Alejarse si está demasiado cerca
        } else {
            toPlayer.setZero(); // Mantener la distancia
        }

        getBody().setLinearVelocity(toPlayer);

        // Rotación hacia el jugador
        float angle = MathUtils.atan2(toPlayer.y, toPlayer.x);
        setRotation(MathUtils.radiansToDegrees * angle - 90);

        shootTimer += delta;
        if (shootTimer >= shootInterval) {
            shootTimer = 0;
            Vector2 shootDirection = new Vector2(playerPosition).sub(getBody().getPosition()).nor();
            //gameScene.getBulletManager().addEnemyBullet(getBody().getPosition().x, getBody().getPosition().y, shootDirection);
            bulletManager.addBullet(getBody().getPosition().x, getBody().getPosition().y, shootDirection, true);
            //sound.play();
        }
    }

    @Override
    public void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        // Colocamos la nave en el centro de la pantalla (en unidades físicas)
        float initialX = GameScene.WORLD_WIDTH / 2f;  // Coordenada X en metros (ajustada por PPM)
        float initialY = GameScene.WORLD_HEIGHT * 0.2f / 2f; // Coordenada Y en metros (ajustada por PPM)
        bodyDef.position.set(initialX, initialY);
        bodyDef.linearDamping = 1f;
        this.setBody(getWorld().createBody(bodyDef));

        // Definimos el tamaño del cuerpo en unidades físicas, ajustando por PPM
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((getWidth()) * 0.5f, (getHeight())* 0.5f); // Reducir el tamaño del hitbox (antes era / 2f)

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GameScene.CATEGORY_SHOOTER;
        fixtureDef.filter.maskBits = GameScene.MASK_SHOOTER;
        getEnemyBody().createFixture(fixtureDef).setUserData("shooter");

        shape.dispose();
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        //setColor(Color.YELLOW);
    }
}
