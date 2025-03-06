package com.juego.Javier.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class BackgroundManager {
    private Texture backgroundTexture;
    private Vector2 position;
    private float scaleFactor;

    public BackgroundManager(String backgroundPath, float scaleFactor) {
        // Cargar la textura del fondo
        this.backgroundTexture = new Texture(Gdx.files.internal(backgroundPath));
        this.scaleFactor = scaleFactor;

        // La posición inicial será en la esquina inferior izquierda
        this.position = new Vector2(0, 0);
    }

    public void render(SpriteBatch batch, float cameraWidth, float cameraHeight, float ppm) {
        // Calcular el ancho y alto en píxeles ajustado al PPM y escala
        float widthInPixels = cameraWidth * ppm * scaleFactor;
        float heightInPixels = cameraHeight * ppm * scaleFactor;

        batch.draw(
            backgroundTexture,
            position.x,
            position.y,
            widthInPixels,
            heightInPixels
        );
    }

    /**
     * Libera los recursos asociados con el fondo.
     */
    public void dispose() {
        backgroundTexture.dispose();
    }

    public Vector2 getSize() {
        return new Vector2(backgroundTexture.getWidth(), backgroundTexture.getHeight());
    }
}
