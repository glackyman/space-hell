package com.juego.Javier.scenes;

import static com.juego.Javier.GameClass.bundle;
import static com.juego.Javier.GameClass.explosionSound;
import static com.juego.Javier.GameClass.prefs;
import static com.juego.Javier.GameClass.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.juego.Javier.GameClass;
import com.juego.Javier.entities.GameRecords;
import com.juego.Javier.manager.MusicManager;


import java.util.Locale;

public class SettingScreen implements Screen {
    private GameClass game;
    private Stage stage;
    private TextureAtlas atlas;
    private Skin skin;
    private SpriteBatch batch;

    private MusicManager menuMusic;
    private Texture backgroundTexture;

    private GameRecords gameRecords;
    public SettingScreen(SpriteBatch batch, GameClass game) {
        this.game = game;
        this.batch = batch;
        menuMusic = MusicManager.getInstance();
        backgroundTexture = new Texture(Gdx.files.internal("background/space_Background_4096x2048.png"));
        gameRecords = new GameRecords();
    }

    @Override
    public void show() {
        BitmapFont font = new BitmapFont();
        font.getData().setScale(2.5f); // Aumentar tamaño de la fuente

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Cargar atlas de UI y banderas
        TextureAtlas uiAtlas = new TextureAtlas(Gdx.files.internal("ui/uiatlas.atlas"));
        TextureAtlas flagsAtlas = new TextureAtlas(Gdx.files.internal("ui/uiflags.atlas"));

        skin = new Skin();
        skin.addRegions(uiAtlas);
        skin.addRegions(flagsAtlas);
        skin.add("default-font", font); // Añadir la fuente al skin

        // Estilo para el fondo del botón
        ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
        buttonStyle.up = skin.getDrawable("button_up");
        buttonStyle.down = skin.getDrawable("button_down");

        // Crear botones con fondo y bandera
        // Botón de inglés
        ImageButton englishButton = new ImageButton(buttonStyle);
        Image englishFlag = new Image(skin.getDrawable("unitedkingdom")); // Cargar bandera de Polonia
        englishButton.addActor(englishFlag);  // Añadir la bandera como actor dentro del botón
        englishFlag.setFillParent(true);  // Ajustar la bandera al tamaño del botón
        // Listeners
        englishButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.vibrate(10);
                englishFlag.setColor(0.3f, 0.3f, 0.3f, 1f); // Oscurecer al presionar

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                englishFlag.setColor(1, 1, 1, 1); // Restaurar color
                changeLanguage("en"); // Cambiar a inglés
                Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
                prefs.putString("language", "en");
                prefs.flush();
                super.touchUp(event, x, y, pointer, button);
            }
        });
        // Botón de español
        ImageButton spanishButton = new ImageButton(buttonStyle);
        Image spanishFlag = new Image(skin.getDrawable("spain")); // Cargar bandera de España
        spanishButton.addActor(spanishFlag);  // Añadir la bandera como actor dentro del botón
        spanishFlag.setFillParent(true);  // Ajustar la bandera al tamaño del botón

        spanishButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.vibrate(10);
                spanishFlag.setColor(0.3f, 0.3f, 0.3f, 1f); // Oscurecer al presionar
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                spanishFlag.setColor(1, 1, 1, 1); // Restaurar color
                changeLanguage("es"); // Cambiar a inglés
                Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
                prefs.putString("language", "es");
                prefs.flush();
                super.touchUp(event, x, y, pointer, button);
            }

        });

        ImageButton polishButton = new ImageButton(buttonStyle);
        Image polishFlag = new Image(skin.getDrawable("poland")); // Nueva bandera
        polishButton.addActor(polishFlag);
        polishFlag.setFillParent(true);

// Listener para el botón polaco
        polishButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.vibrate(10);
                polishFlag.setColor(0.3f, 0.3f, 0.3f, 1f);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                polishFlag.setColor(1, 1, 1, 1);
                changeLanguage("pl");
                Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
                prefs.putString("language", "pl");
                prefs.flush();
                super.touchUp(event, x, y, pointer, button);
            }
        });

        // Botón de "volver"
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

        //botton music, tambien se crean las preferencias de este ajuste
        ImageButton.ImageButtonStyle musicButtonStyle = new ImageButton.ImageButtonStyle();
        musicButtonStyle.up = skin.getDrawable("button_music_on"); // Estado inicial
        musicButtonStyle.down = skin.getDrawable("button_music_off"); // Al presionar
        ImageButton musicToggleButton = new ImageButton(musicButtonStyle);
        prefs = Gdx.app.getPreferences("MyGamePreferences");
        boolean musicOn = prefs.getBoolean("musicOn", true); // Valor por defecto true
        updateMusicButtonAppearance(musicToggleButton, musicOn);
        musicToggleButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                musicToggleButton.setColor(0.3f, 0.3f, 0.3f, 1f); // Efecto al presionar
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                musicToggleButton.setColor(1, 1, 1, 1);
                Gdx.input.vibrate(10);
                // Toggle del estado
                boolean newMusicState = !prefs.getBoolean("musicOn", true);
                prefs.putBoolean("musicOn", newMusicState);
                prefs.flush();

                // Actualizar apariencia y música
                updateMusicButtonAppearance(musicToggleButton, newMusicState);
                updateMusicState(newMusicState);
            }
        });

        Slider.SliderStyle volumeSliderStyle = new Slider.SliderStyle();
        volumeSliderStyle.background = skin.getDrawable("scroll"); // Fondo del slider
        volumeSliderStyle.knob = skin.getDrawable("scroll_bar"); // Control deslizante

        Slider volumeSlider = new Slider(0, 100, 1, true, volumeSliderStyle);
        volumeSlider.setValue(100); // Volumen inicial al máximo

        volumeSlider.setHeight(200); // Altura del slider
        volumeSlider.setWidth(50);   // Ancho del slider

        // Listener para cambios
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                float volume = volumeSlider.getValue() / 100f;
                MusicManager.getInstance().setVolume(volume);

                // Guardar preferencia
                prefs = Gdx.app.getPreferences("MyGamePreferences");
                prefs.putFloat("musicVolume", volume);
                prefs.flush();
            }
        });

        ImageButton.ImageButtonStyle sfxButtonStyle = new ImageButton.ImageButtonStyle();
        sfxButtonStyle.up = skin.getDrawable("button_sfx_on");
        sfxButtonStyle.down = skin.getDrawable("button_sfx_off");

        ImageButton sfxToggleButton = new ImageButton(sfxButtonStyle);
        boolean sfxOn = prefs.getBoolean("sfxOn", true);
        updateSfxButtonAppearance(sfxToggleButton, sfxOn);

        sfxToggleButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                sfxToggleButton.setColor(0.3f, 0.3f, 0.3f, 1f);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.vibrate(10);
                sfxToggleButton.setColor(1, 1, 1, 1);
                boolean newSfxState = !prefs.getBoolean("sfxOn", true);
                prefs.putBoolean("sfxOn", newSfxState);
                prefs.flush();
                updateSfxButtonAppearance(sfxToggleButton, newSfxState);
            }
        });

// Slider SFX
        Slider sfxVolumeSlider = new Slider(0, 100, 1, true, volumeSliderStyle);
        sfxVolumeSlider.setValue(prefs.getFloat("sfxVolume", 1f) * 100);
        sfxVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = sfxVolumeSlider.getValue() / 100f;

                prefs.putFloat("sfxVolume", volume);
                prefs.flush();
                explosionSound.setVolume(explosionSound.play(), volume);
                sound.setVolume(sound.play(), volume);
            }
        });

        TextButton.TextButtonStyle deleteButtonStyle = new TextButton.TextButtonStyle();
        deleteButtonStyle.up = skin.getDrawable("button_up");  // Usar mismo estilo que otros botones
        deleteButtonStyle.down = skin.getDrawable("button_down");
        deleteButtonStyle.font = font;
        TextButton deleteRecordsButton = new TextButton(bundle.get("deleteRecords"), deleteButtonStyle);

        deleteRecordsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.vibrate(50);
                resetHighScores();
                Gdx.app.log("Settings", "Records borrados!");
            }
        });

        // Configurar layout principal
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.add(backButton)
            .size(150, 120)
            .pad(20)
            .align(Align.topLeft)
            .row();


        Table centerTable = new Table();
// 2.1 Fila de banderas
        centerTable.add(englishButton).width(200).height(120).pad(10);
        centerTable.add(spanishButton).width(200).height(120).pad(10);
        centerTable.add(polishButton).width(200).height(120).pad(10);
        centerTable.row();

        Table audioControlsTable = new Table();
// Botones de música y SFX
        Table audioButtonsTable = new Table();
        audioButtonsTable.add(musicToggleButton).width(200).height(120).pad(10);
        audioButtonsTable.add(sfxToggleButton).width(200).height(120).pad(10);

// Sliders
        Table slidersTable = new Table();
        slidersTable.add(volumeSlider).width(200).height(50).pad(5);
        slidersTable.add(sfxVolumeSlider).width(200).height(50).pad(5);

        audioControlsTable.add(audioButtonsTable).row();
        //audioControlsTable.add(slidersTable).padTop(10);

        audioControlsTable.add(deleteRecordsButton)
            .width(400)
            .height(80)
            .padTop(30)
            .row();


// Añadir audio al centro
        centerTable.add(audioControlsTable)
            .colspan(3) // Ocupa el ancho de las 3 columnas
            .center()
            .padTop(20);


        mainTable.add(centerTable).expand().center();
        stage.addActor(mainTable);

    }

    private void resetHighScores() {
        gameRecords.reset();
    }

    private void updateMusicButtonAppearance(ImageButton button, boolean musicOn) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.down = skin.getDrawable("button_music_off");

        if (musicOn) {
            style.up = skin.getDrawable("button_music_on");
        } else {
            style.up = skin.getDrawable("button_music_off");
        }

        button.setStyle(style);
        button.invalidateHierarchy(); // Forzar actualización visual
    }

    private void updateMusicState(boolean musicOn) {
        if (musicOn) {
            MusicManager.getInstance().play("music/main_menu.ogg");
        } else {
            MusicManager.getInstance().pause();
        }
    }

    private void updateSfxButtonAppearance(ImageButton button, boolean sfxOn) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.down = skin.getDrawable("button_sfx_off");

        if (sfxOn) {
            style.up = skin.getDrawable("button_sfx_on");
        } else {
            style.up = skin.getDrawable("button_sfx_off");
        }

        button.setStyle(style);
        button.invalidateHierarchy();
    }

    private void changeLanguage(String languageCode) {
        // Cambiar el locale y recargar el bundle
        Locale locale = new Locale(languageCode);
        bundle = I18NBundle.createBundle(Gdx.files.internal("i18n/MyBundle"), locale);

        for (Actor actor : stage.getActors()) {
            if (actor instanceof TextButton) {
                TextButton button = (TextButton) actor;
                String text = button.getText().toString();

                if (text.equals(bundle.get("english"))) {
                    button.setText(bundle.get("english"));
                } else if (text.equals(bundle.get("spanish"))) {
                    button.setText(bundle.get("spanish"));
                } else if (text.equals(bundle.get("polish"))) { // Nueva condición
                    button.setText(bundle.get("polish"));
                } else if (text.equals(bundle.get("back"))) {
                    button.setText(bundle.get("back"));
                }
            }
        }
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
        skin.dispose();
    }


}
