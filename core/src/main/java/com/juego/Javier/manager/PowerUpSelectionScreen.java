package com.juego.Javier.manager;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.juego.Javier.entities.PlayerShip;
import com.juego.Javier.entities.PowerUp;

public class PowerUpSelectionScreen {
    private Stage stage;
    private Array<PowerUp> powerUps;
    private TextureRegion buttonAtlas;
    private boolean selectionMade;
    private PowerUp selectedPowerUp;

    public PowerUpSelectionScreen(TextureRegion buttonAtlas) {
        this.buttonAtlas = buttonAtlas;
        this.stage = new Stage(new ScreenViewport());
        this.powerUps = new Array<>();
    }

    public void show() {
        stage.clear();
        powerUps.clear();

        // Lista de TODOS los tipos de power-ups disponibles
        Array<PowerUp.Type> allTypes = new Array<>(PowerUp.Type.values());
        allTypes.shuffle(); // Mezcla aleatoriamente los tipos

        // Seleccionar los primeros 3 tipos después de mezclar
        for (int i = 0; i < 3; i++) {
            PowerUp.Type randomType = allTypes.get(i);
            String name = getPowerUpName(randomType); // Asignar nombre según tipo
            powerUps.add(createPowerUp(randomType, name));
        }

        // Crear botones para los 3 power-ups seleccionados
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        for (PowerUp powerUp : powerUps) {
            Actor powerUpButton = powerUp.createButton();
            powerUpButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedPowerUp = powerUp;
                    selectionMade = true;
                }
            });
            mainTable.add(powerUpButton).pad(20).width(200).height(100);
        }

        stage.addActor(mainTable);
        selectionMade = false;
        selectedPowerUp = null;
    }

    // Método para obtener nombres según el tipo
    private String getPowerUpName(PowerUp.Type type) {
        switch (type) {
            case FIRE_RATE:
                return "Rapid Fire";
            case HEALTH_BOOST:
                return "Health+";
            case VELOCITY:
                return "Speed Boost";
            case PIERCING:
                return "Piercing Shots";
            case DOUBLE_PARALLEL:
                return "Parallel Shot";
            case DAMAGE:
                return "Damage";
            default:
                type= PowerUp.Type.HEALTH_BOOST;
                return "Health+";
        }
    }

    private PowerUp createPowerUp(PowerUp.Type type, String name) {
        return new PowerUp(type, name, buttonAtlas);
    }

    public void hide() {
        stage.clear();
    }



    public void render() {
        stage.act();
        stage.draw();
    }

    public void applySelection(PlayerShip player, BulletManager bulletManager) {
        if (selectedPowerUp != null) {
            selectedPowerUp.applyEffect(player, bulletManager);
        }
    }

    public boolean isSelectionComplete() {
        return selectionMade;
    }

    public void dispose() {
        stage.dispose();
    }

    public Stage getStage() {
        return stage;
    }
}
