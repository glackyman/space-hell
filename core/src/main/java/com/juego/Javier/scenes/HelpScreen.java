package com.juego.Javier.scenes;

import static com.juego.Javier.GameClass.bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.juego.Javier.GameClass;

public class HelpScreen implements Screen {
    private GameClass game;
    private Stage stage;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private TextureAtlas textureAtlas;
    private Skin skin;

    public HelpScreen(SpriteBatch batch, GameClass game) {
        this.game = game;
        this.batch = batch;
        backgroundTexture = new Texture(Gdx.files.internal("background/space_Background_4096x2048.png"));
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Cargar el atlas y crear la skin

        textureAtlas = new TextureAtlas(Gdx.files.internal("ui/uiatlas.atlas"));
        skin = new Skin(textureAtlas);

        // Crear una fuente para el texto
        BitmapFont font = new BitmapFont();
        font.getData().setScale(2.0f); // Ajustar el tamaño de la fuente

        // Crear un estilo para la etiqueta
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;

        // Crear el texto de ayuda
        // Crear el texto de ayuda usando el bundle
        String helpText = bundle.get("helpTitle") + "\n\n"
            + bundle.get("helpMove") + "\n"
            + bundle.get("helpDodge") + "\n"
            + bundle.get("helpPowerUps") + "\n"
            + bundle.get("helpHeal") + "\n\n"
            + bundle.get("helpEnemies") + "\n"
            + bundle.get("helpRotatingEnemies") + "\n"
            + bundle.get("helpKamikazeEnemies") + "\n"
            + bundle.get("helpDodgeBullets") + "\n\n"
            + bundle.get("helpObjective");

        Label helpLabel = new Label(helpText, labelStyle);
        helpLabel.setAlignment(Align.center);

        // Crear el estilo del botón usando la skin
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = skin.getDrawable("button_up"); // Usar la textura "button_up" del atlas
        buttonStyle.down = skin.getDrawable("button_down"); // Usar la textura "button_down" del atlas
        buttonStyle.font = font;

        // Botón de regreso
        TextButton.TextButtonStyle backButtonStyle = new TextButton.TextButtonStyle();
        backButtonStyle.up = skin.getDrawable("button_up_back");
        backButtonStyle.down = skin.getDrawable("button_down_back");
        backButtonStyle.font = font;
        TextButton backButton = new TextButton("", backButtonStyle);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.vibrate(80);
                game.setScreen(new MainMenuScreen(batch, game)); // Volver al menú principal
            }
        });
        Table backButtonTable = new Table();
        backButtonTable.top().left();
        backButtonTable.setFillParent(true);
        backButtonTable.add(backButton).size(150, 120)
            .pad(20)
            .align(Align.topLeft)
            .row();

        // Crear una tabla para organizar los elementos
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Añadir la etiqueta y el botón a la tabla
        table.add(helpLabel).width(800).pad(20).row();

        // Añadir la tabla a la etapa
        stage.addActor(table);
        stage.addActor(backButtonTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dibujar el fondo
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Dibujar la etapa
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
