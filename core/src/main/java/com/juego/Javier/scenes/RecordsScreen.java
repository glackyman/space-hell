package com.juego.Javier.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.juego.Javier.GameClass;
import com.juego.Javier.entities.GameRecords;

public class RecordsScreen implements Screen {
    private GameClass game;
    private Stage stage;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private GameRecords records;

    public RecordsScreen(SpriteBatch batch, GameClass game) {
        this.game = game;
        this.batch = batch;
        this.records = new GameRecords();
        backgroundTexture = new Texture(Gdx.files.internal("background/space_Background_4096x2048.png"));
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        BitmapFont font = new BitmapFont();
        font.getData().setScale(2f);

        // Tabla principal
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        // Etiquetas de records
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        Label titleLabel = new Label("RECORDS", labelStyle);
        Label killsLabel = new Label("Max Kills: " + records.getMaxKills(), labelStyle);
        Label deathsLabel = new Label("Max Deaths: " + records.getMaxDeaths(), labelStyle);
        Label waveLabel = new Label("Highest Wave: " + records.getHighestWave(), labelStyle);

        // Botón de regreso
        TextButton backButton = new TextButton("Back", createButtonStyle(font));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.vibrate(10);
                game.setScreen(new MainMenuScreen(batch, game));
            }
        });

        // Añadir elementos a la tabla
        mainTable.add(titleLabel).padBottom(50).row();
        mainTable.add(killsLabel).padBottom(20).row();
        mainTable.add(deathsLabel).padBottom(20).row();
        mainTable.add(waveLabel).padBottom(50).row();
        mainTable.add(backButton).width(400).height(80);

        stage.addActor(mainTable);
    }

    private TextButton.TextButtonStyle createButtonStyle(BitmapFont font) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("ui/uiatlas.atlas"));
        Skin skin = new Skin(atlas);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = skin.getDrawable("button_up");
        style.down = skin.getDrawable("button_down");
        style.font = font;
        return style;
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

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    // Resto de métodos de Screen (igual que MainMenuScreen)
    // ... resize(), pause(), resume(), hide(), dispose() ...
}
