
package com.juego.Javier.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.juego.Javier.scenes.GameScene;


public class Joystick {

    private Stage stage;
    private Touchpad touchpad;
    private Table table;

    private GameScene gameScene;
    private boolean isJoystickVisible = false;

    private boolean invertX = false;
    private boolean invertY = false;

    public Joystick(GameScene scene) {
        stage = new Stage();
        table = new Table();
        table.bottom().left();  // Alinea la tabla en la esquina inferior izquierda
        table.pad(15);  // Agrega un relleno para que no esté pegado al borde

        Skin skin = new Skin();
        skin.add("JoystickBackground", new Texture("Joystick/JoystickSplitted.png"));
        skin.add("JoystickKnob", new Texture("Joystick/SmallHandleFilledGrey.png"));
        Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();
        touchpadStyle.background = skin.getDrawable("JoystickBackground");
        touchpadStyle.knob = skin.getDrawable("JoystickKnob");

        touchpad = new Touchpad(10, touchpadStyle);
        touchpad.setSize(200, 200);  // Ajusta el tamaño del touchpad

        table.add(touchpad);  // No es necesario especificar el tamaño aquí

        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    public Vector2 getJoystickDirection() {
        float knobPercentX = touchpad.getKnobPercentX();
        float knobPercentY = touchpad.getKnobPercentY();

        // Aplica la inversión si está activada
        float x = invertX ? -knobPercentX : knobPercentX;
        float y = invertY ? -knobPercentY : knobPercentY;

        return new Vector2(x, y);
    }

    public void draw() {
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    // Métodos para activar/desactivar inversión
    public void setInvertX(boolean invertX) {
        this.invertX = invertX;
    }

    public void setInvertY(boolean invertY) {
        this.invertY = invertY;
    }

    public Stage getStage() {
        return stage;
    }

    public void reset(){
      //  touchpad.setValue(0, 0);
    }
}

