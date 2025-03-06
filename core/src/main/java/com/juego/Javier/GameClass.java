package com.juego.Javier;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.juego.Javier.manager.MusicManager;
import com.juego.Javier.scenes.GameScene;
import com.juego.Javier.scenes.MainMenuScreen;

import java.util.Arrays;
import java.util.Locale;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class GameClass extends Game {
    public SpriteBatch batch;
    private BitmapFont font;
    public static I18NBundle bundle;

    public MusicManager musicManager;

    static public Sound sound;
    static public Sound explosionSound;
    static public Preferences prefs;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        musicManager = MusicManager.getInstance();
        sound = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.ogg"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.ogg"));
        prefs = Gdx.app.getPreferences("MyGamePreferences");

        // 1. Configuración inicial de preferencias
        if (!prefs.contains("musicVolume")) prefs.putFloat("musicVolume", 1f);
        if (!prefs.contains("sfxVolume")) prefs.putFloat("sfxVolume", 1f);
        if (!prefs.contains("sfxOn")) prefs.putBoolean("sfxOn", true);

        // 2. Cargar idioma
        String systemLanguage = Locale.getDefault().getLanguage();
        String lang = Arrays.asList("es", "en", "pl").contains(systemLanguage) ? systemLanguage : "en";
        lang = prefs.getString("language", lang);

        if (!prefs.contains("language")) {
            prefs.putString("language", lang);
        }

        // 3. Aplicar configuraciones de audio
        if (prefs.getBoolean("musicOn", true)) {
            musicManager.play("music/main_menu.ogg");
        }
        // 4. Mute inicial si es necesario
        if (!prefs.getBoolean("sfxOn", true)) {
            explosionSound.setVolume(explosionSound.play(), 0f);
            sound.setVolume(sound.play(), 0f);
        }

        // 5. Guardar todas las preferencias
        prefs.flush();

        // 6. Cargar bundle de idiomas
        Locale locale = new Locale(lang);
        bundle = I18NBundle.createBundle(Gdx.files.internal("i18n/MyBundle"), locale);

        this.setScreen(new MainMenuScreen(batch, this));
    }

    @Override
    public void render() {
        super.render();
        // Obtener el consumo de memoria
        //System.out.println("Delta: " + Gdx.graphics.getDeltaTime());
        // Convertir a megabytes para facilitar la lectura

//        long javaHeap = Gdx.app.getJavaHeap(); // Memoria heap de Java
//        long nativeHeap = Gdx.app.getNativeHeap(); // Memoria nativa
//
//        float javaHeapMB = javaHeap / (1024f * 1024f);
//        float nativeHeapMB = nativeHeap / (1024f * 1024f);
//
//        // Mostrar en consola
//        Gdx.app.log("Memory Usage", String.format("Java Heap: %.2f MB | Native Heap: %.2f MB", javaHeapMB, nativeHeapMB));
//
//        // También puedes mostrarlo en pantalla
//        batch.begin();
//        font.draw(batch, String.format("Java Heap: %.2f MB", javaHeapMB), 20, Gdx.graphics.getHeight() - 20);
//        font.draw(batch, String.format("Native Heap: %.2f MB", nativeHeapMB), 20, Gdx.graphics.getHeight() - 40);
//        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        getScreen().dispose();
    }
}
