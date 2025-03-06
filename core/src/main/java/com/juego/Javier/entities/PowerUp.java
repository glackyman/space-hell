package com.juego.Javier.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.juego.Javier.manager.BulletManager;

public class PowerUp {
    public enum Type {
        FIRE_RATE,
        HEALTH_BOOST,
        VELOCITY,
        PIERCING,
        DOUBLE_PARALLEL,
        DOUBLE_LINE
    }

    private final Type type;
    private final String name;
    private final TextureRegion icon;

    public PowerUp(Type type, String name, TextureRegion icon) {
        this.type = type;
        this.name = name;
        this.icon = icon;
    }


    public void applyEffect(PlayerShip player, BulletManager bulletManager) {
        switch (type) {
            case VELOCITY:
                player.setVelocity(player.getVelocity() * 1.2f);
                break;

            case FIRE_RATE:
                player.setFireRate(player.getFireRate() * 0.85f); // Disparo continuo
                break;

            case HEALTH_BOOST:
                int currentMaxLife = (int) player.getMaxLife();
                int healthIncrease = (int) (currentMaxLife * 0.2f);
                player.setMaxLife(currentMaxLife + healthIncrease);
                player.heal(healthIncrease);
                break;

            case PIERCING:
                player.setHasPenetratingShots(true);
                break;
            case DOUBLE_PARALLEL:
                bulletManager.upgradeShoot();
                break;
            case DOUBLE_LINE:
                break;
        }
    }

    public Actor createButton() {
        // Extraer regiones de la textura del atlas
        TextureRegion up = new TextureRegion(icon, 2, 60, 219, 62);   // button_up
        TextureRegion down = new TextureRegion(icon, 2, 2, 219, 56); // button_down

        // Crear el estilo del ImageButton
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(up);
        style.down = new TextureRegionDrawable(down);

        // Crear el ImageButton
        ImageButton button = new ImageButton(style);
        button.setSize(200, 100); // Ajusta según tus necesidades

        // Crear un Label con el nombre del power up
        BitmapFont font = new BitmapFont();
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = new Label(name, labelStyle);
        label.setAlignment(Align.center);
        label.setSize(button.getWidth(), button.getHeight());
        label.setFontScale(2f);
        // Crear un Stack para superponer el botón y la etiqueta
        Stack stack = new Stack();
        stack.setSize(button.getWidth(), button.getHeight());
        stack.add(button);
        stack.add(label);

        return stack;
    }
}
