package com.juego.Javier.scenes;

import static com.juego.Javier.GameClass.bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.juego.Javier.GameClass;
import com.juego.Javier.manager.MusicManager;

public class MainMenuScreen implements Screen {
    private GameClass game;
    private Stage stage;
    private TextureAtlas atlas;
    private Skin skin;
    private SpriteBatch batch;

    private Texture backgroundTexture; // Textura de fondo

    public MainMenuScreen(SpriteBatch batch, GameClass game) {
        this.game = game;
        this.batch = batch;
        backgroundTexture = new Texture(Gdx.files.internal("background/space_Background_4096x2048.png"));
    }

    @Override
    public void show() {
        BitmapFont font = new BitmapFont();
        font.getData().setScale(2.5f); // Aumenta el tamaño de la fuente

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        atlas = new TextureAtlas(Gdx.files.internal("ui/uiatlas.atlas"));
        skin = new Skin(atlas);

        // Crear el estilo del botón
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = skin.getDrawable("button_up");
        buttonStyle.down = skin.getDrawable("button_down");
        buttonStyle.font = font;

        // Crear botones
        TextButton playButton = new TextButton(bundle.get("play"), buttonStyle);
        TextButton settingsButton = new TextButton(bundle.get("settings"), buttonStyle);
        TextButton exitButton = new TextButton(bundle.get("exit"), buttonStyle);
        TextButton recordsButton = new TextButton(bundle.get("records"), buttonStyle);

        // Listeners de los botones
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.vibrate(10);
                game.setScreen(new GameScene(batch, game)); // Cambia a la pantalla del juego
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.vibrate(10);
                game.setScreen(new SettingScreen(batch,game)); // Cambia a la pantalla de ajustes (debes crear SettingsScreen)
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.vibrate(30);
                Gdx.app.exit(); // Salir del juego
            }
        });

        recordsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.vibrate(10);
                game.setScreen(new RecordsScreen(batch, game));
            }
        });

        // Crear una tabla y agregar los botones
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(playButton).width(600).height(120).pad(20).row();
        table.add(settingsButton).width(600).height(120).pad(20).row();
        table.add(recordsButton).width(600).height(120).pad(20).row();
        table.add(exitButton).width(600).height(120).pad(20);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        //menuMusic.pause();
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
        //menuMusic.stop();

    }

    @Override
    public void dispose() {
        stage.dispose();
        atlas.dispose();
        skin.dispose();
    }
}
