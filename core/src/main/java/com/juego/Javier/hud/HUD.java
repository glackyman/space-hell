package com.juego.Javier.hud;

import static com.juego.Javier.GameClass.bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Locale;

public class HUD {
    private Stage stage;
    private HealthBar healthBar;
    private Label timeLabel;
    private Label waveLabel;
    private Label enemiesLabel;
    private Label scoreLabel;
    private Label countdownLabel;
    private float timeElapsed;


    // Actor personalizado para la barra de vida
    private class HealthBar extends Actor {
        private ShapeRenderer shapeRenderer;
        private float currentHealthPercent; // Ahora se maneja como porcentaje
        private float maxHealthPercent; // Para poder actualizar con porcentaje

        public HealthBar() {
            shapeRenderer = new ShapeRenderer();
            this.setSize(200, 10);
        }

        @Override
        public void draw(com.badlogic.gdx.graphics.g2d.Batch batch, float parentAlpha) {
            batch.end();
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

            // Fondo
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());

            // Vida actual (como porcentaje)
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect(getX(), getY(), currentHealthPercent * getWidth(), getHeight());
            shapeRenderer.end();

            batch.begin();
        }

        public void update(float currentHealth, float maxHealth) {
            // Convertir la salud a un porcentaje
            if (maxHealth > 0) {
                this.currentHealthPercent = currentHealth / maxHealth;
            } else {
                this.currentHealthPercent = 0; // Si maxHealth es 0, no se muestra barra de salud
            }
        }
    }

    public HUD() {
        stage = new Stage(new ScreenViewport());


        // Configurar elementos
        BitmapFont font = new BitmapFont();
        LabelStyle style = new LabelStyle(font, Color.WHITE);

        healthBar = new HealthBar();
        timeLabel = new Label("", style);
        scoreLabel = new Label("", style);
        waveLabel = new Label("", style);
        enemiesLabel = new Label("", style);
        countdownLabel = new Label("", style);

        // Ajustar escalados
        timeLabel.setFontScale(3.5f);
        scoreLabel.setFontScale(2.5f);
        waveLabel.setFontScale(3f);
        enemiesLabel.setFontScale(2.5f);
        countdownLabel.setFontScale(2f);  // Reducido para mejor proporción

        TextButton.TextButtonStyle styleP = new TextButton.TextButtonStyle();
        styleP.font = new BitmapFont();
        styleP.fontColor = Color.WHITE;
        styleP.downFontColor = Color.GRAY;

        // Configurar layout principal
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().padTop(20);

        // ------------------------------------------
        // GRUPO IZQUIERDO (Barra vida + countdown)
        // ------------------------------------------
        Table leftGroup = new Table();
        leftGroup.add(healthBar)
            .left().padLeft(20)
            .width(250).height(25)
            .row();
        leftGroup.add(countdownLabel)
            .left().padLeft(20)
            .padTop(5);

        // ------------------------------------------
        // CENTRO (Tiempo + Puntos - Mantenemos original)
        // ------------------------------------------
        Table centerContainer = new Table();
        centerContainer.add(timeLabel).row();
        centerContainer.add(scoreLabel).padTop(5);

        // ------------------------------------------
        // DERECHA (Oleada + Enemigos - Mantenemos original)
        // ------------------------------------------
        Table rightGroup = new Table();
        rightGroup.add(waveLabel).right().padBottom(3).row();
        rightGroup.add(enemiesLabel).right();

        // Ensamblar la tabla principal
        mainTable.add(leftGroup)
            .uniformY().left();

        mainTable.add(centerContainer)
            .expandX().center()
            .padTop(8);  // Mantenemos el padding original

        mainTable.add(rightGroup)
            .uniformY().right()
            .padRight(25)
            .padTop(8);

        stage.addActor(mainTable);
        timeElapsed = 0;
    }

    public void update(float deltaTime, int waveNumber, int enemiesRemaining, float currentHealth, float maxHealth, int score, String countdownText) {
        timeElapsed += deltaTime;
        timeLabel.setText(String.format("%02d:%02d",
            (int) (timeElapsed / 60),
            (int) (timeElapsed % 60)));
        waveLabel.setText(String.format("%s %d", bundle.get("wave"), waveNumber));
        enemiesLabel.setText(String.format("%s %d", bundle.get("enemies"), enemiesRemaining));
        scoreLabel.setText(score);
        healthBar.update(currentHealth, maxHealth);
        countdownLabel.setText(countdownText);
    }

    public void render() {
        stage.act();
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
    }

}
