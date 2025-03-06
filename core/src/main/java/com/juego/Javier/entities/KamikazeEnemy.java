package com.juego.Javier.entities;

import static com.juego.Javier.scenes.GameScene.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.juego.Javier.manager.DropManager;
import com.juego.Javier.scenes.GameScene;

public class KamikazeEnemy extends EnemyShip {

    public KamikazeEnemy(World world, DropManager dropManager) {
        super(world, dropManager);
        setTexture(new Texture("ships/enemies/kamikace.png"));
        setScale(2.5f);
        setSize(getTexture().getWidth() / PPM * scaleFactor, getTexture().getHeight() / PPM * scaleFactor);
        setOrigin(getWidth() / 2f, getHeight() / 2f);
        setLife(10f); // Vida reducida para equilibrar el juego
    }

    @Override
    public void update(float delta, Vector2 playerPosition) {
        // Movimiento más rápido hacia el jugador
        Vector2 direction = new Vector2(
            playerPosition.x - getBody().getPosition().x,
            playerPosition.y - getBody().getPosition().y
        );

        direction.nor().scl(30f); // Aumenta la velocidad (antes estaba en 5f)
        getBody().setLinearVelocity(direction);

        // Rotación hacia el jugador
        float angle = MathUtils.atan2(direction.y, direction.x);
        setRotation(MathUtils.radiansToDegrees * angle - 90);
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        //setColor(Color.BLUE);
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
        shape.setAsBox((getWidth()) * 0.9f, (getHeight())* 0.9f); // Reducir el tamaño del hitbox (antes era / 2f)

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GameScene.CATEGORY_KAMIKAZE;
        fixtureDef.filter.maskBits = GameScene.MASK_KAMIKAZE;
        getEnemyBody().createFixture(fixtureDef).setUserData("kamikaze");

        shape.dispose();
    }
}
