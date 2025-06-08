package com.juego.Javier.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.juego.Javier.scenes.GameScene;

public class Bullet extends Sprite {

    private OrthographicCamera gameCam;
    private Body body;
    private World world;
    private static final float SPEED = 90f;
    private static final float BULLET_SCALE = 5f; // Tamaño ajustado para que coincida con el escalado

    private Animation<TextureRegion> animation;
    private float stateTime;

    private float damegeMultipler;
    private boolean isEnemyBullet;

    private boolean markedForRemoval = false;

    private boolean isPenetrating;
    private int penetrationsLeft = 1;
    public Bullet(World world, float x, float y, Vector2 direction, OrthographicCamera gameCam) {
        super(new Texture(Gdx.files.internal("bullets/All_Fire_Bullet_Pixel_16x16_00.png")));
        this.world = world;
        this.gameCam = gameCam;
        setSize(BULLET_SCALE * 0.5f, BULLET_SCALE * 0.5f);
        setOrigin(getWidth() / 2f, getHeight() / 2f);


        loadAnimation();
        createBody(x, y, direction);

        setDamegeMultipler(5f);
    }

    public Bullet(World world, float x, float y, Vector2 direction, OrthographicCamera gameCam, boolean isEnemyBullet) {
        super(new Texture(Gdx.files.internal(
            isEnemyBullet ?
                "bullets/All_Fire_Bullet_Pixel_16x16_05.png" :
                "bullets/All_Fire_Bullet_Pixel_16x16_00.png"
        )));
        this.world = world;
        this.gameCam = gameCam;
        this.isEnemyBullet = isEnemyBullet;
        setSize(BULLET_SCALE * 0.5f, BULLET_SCALE * 0.5f);
        setOrigin(getWidth() / 2f, getHeight() / 2f);


        loadAnimation();
        createBody(x, y, direction);

        setDamegeMultipler(5f);
    }

    ///
    private void loadAnimation() {
        Texture texture = getTexture();
        TextureRegion[][] tmp = TextureRegion.split(texture, 16, 16);

        Array<TextureRegion> frames = new Array<>();
        for (int row = 3; row >= 0; row--) {
            frames.add(tmp[row][2]);
        }
        animation = new Animation<>(0.1f, frames, Animation.PlayMode.NORMAL);
        stateTime = 0f;
    }

    private void createBody(float x, float y, Vector2 direction) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        // bodyDef.bullet = true; // Hace que el cuerpo se comporte como una bala para evitar túneles
        //bodyDef.linearVelocity.set(direction); // Movimiento recto hacia arriba por ahora

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius((getWidth() / 3f));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = isEnemyBullet ? GameScene.CATEGORY_BULLET_E : GameScene.CATEGORY_BULLET;
        fixtureDef.filter.maskBits = isEnemyBullet ? GameScene.MASK_BULLET_E : GameScene.MASK_BULLET;
        body.createFixture(fixtureDef).setUserData("bullet");
        shape.dispose();

        setSpeed(isEnemyBullet ? SPEED : SPEED, direction);
        body.setAngularVelocity(direction.angleDeg());
    }

    public void update(float delta) {
        if (body != null && body.getWorld() != null) { // Verificar que el cuerpo aún existe
            stateTime += delta;
            setRegion(animation.getKeyFrame(stateTime));

            // Ajustar la posición del sprite respecto al cuerpo
            setPosition(body.getPosition().x - getWidth() / 2f,
                body.getPosition().y - getHeight() / 2f);
        }
    }

    public void render(SpriteBatch batch) {
        if (body != null && body.getWorld() != null) { // Verificar que el cuerpo aún existe
            draw(batch);
        }
    }

    public void dispose() {
        getTexture().dispose();
        world.destroyBody(body);
    }

    public void setSpeed(float speed, Vector2 direction) {
        if (direction.len() > 0) {
            direction.nor().scl(speed);
            body.setLinearVelocity(direction); // Aplica la velocidad al cuerpo
        }
    }

    public boolean isOutOfBounds() {
        float x = body.getPosition().x;
        float y = body.getPosition().y;

        // Obtén las coordenadas de la cámara (centro de la pantalla)
        float cameraX = gameCam.position.x;
        float cameraY = gameCam.position.y;

        // Obtén el ancho y alto del viewport (área visible de la pantalla)
        float viewportWidth = gameCam.viewportWidth;
        float viewportHeight = gameCam.viewportHeight;

        // Calcula los límites de la pantalla
        float screenLeft = cameraX - viewportWidth / 2;
        float screenRight = cameraX + viewportWidth / 2;
        float screenBottom = cameraY - viewportHeight / 2;
        float screenTop = cameraY + viewportHeight / 2;

        // Verifica si la bala está fuera de la pantalla
        return x < screenLeft || x > screenRight || y < screenBottom || y > screenTop;
    }

    public Body getBody() {
        return body;
    }

    public void setDamegeMultipler(float damegeMultipler) {
        this.damegeMultipler = damegeMultipler;
    }

    public float getDamageMultipler() {
        return damegeMultipler;
    }

    public void markForRemoval() {
        markedForRemoval = true;
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }
}
