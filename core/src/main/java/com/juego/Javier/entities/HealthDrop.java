package com.juego.Javier.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.juego.Javier.scenes.GameScene;

import static com.juego.Javier.scenes.GameScene.PPM;

public class HealthDrop extends Sprite {

    private Body body;
    private World world;
    private boolean collected = false;
    private int healAmount = 25;

    public HealthDrop(World world, Vector2 position) {
        // Cargamos directamente la textura completa
        super(new Texture(Gdx.files.internal("heal.png")));
        this.world = world;
        setScale(3f);
        // Ajustamos el tamaño según PPM
        setSize(getWidth() / PPM, getHeight() / PPM);
        setOriginCenter();

        createBody(position);
    }

    private void createBody(Vector2 position) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(position.x, position.y);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(getWidth() * getScaleX() / 2, getHeight() * getScaleY() / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true; // Para detectar colisiones sin física
        fixtureDef.filter.categoryBits = GameScene.CATEGORY_HEAL;
        fixtureDef.filter.maskBits = GameScene.MASK_HEAL;

        body.createFixture(fixtureDef).setUserData(this);
        shape.dispose();
    }

    public void update(float delta) {
        // Actualiza el estado si es necesario; aquí solo se incrementaría delta u otra lógica.
    }

    public void render(SpriteBatch batch) {
        if (!collected) {
            // Posicionar el sprite centrado en el cuerpo
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
            // Dibuja el sprite usando la textura completa
            super.draw(batch);
        }
    }

    public void dispose() {
        getTexture().dispose();
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public boolean isCollected() {
        return collected;
    }

    public int getHealAmount() {
        return healAmount;
    }

    public Body getBody() {
        return body;
    }
}
