package com.juego.Javier.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.juego.Javier.GameClass;
import com.juego.Javier.entities.Bullet;
import com.juego.Javier.entities.EnemyShip;
import com.juego.Javier.entities.GameRecords;
import com.juego.Javier.entities.HealthDrop;
import com.juego.Javier.entities.PlayerShip;
import com.juego.Javier.hud.HUD;
import com.juego.Javier.hud.Joystick;
import com.juego.Javier.manager.BackgroundManager;
import com.juego.Javier.manager.BulletManager;
import com.juego.Javier.manager.DropManager;
import com.juego.Javier.manager.EnemyManager;
import com.juego.Javier.manager.MusicManager;
import com.juego.Javier.manager.PowerUpSelectionScreen;
import com.juego.Javier.manager.WaveManager;

public class GameScene extends ScreenAdapter {

    public static final short CATEGORY_PLAYER = 0x0001;
    public static final short CATEGORY_BULLET = 0x0002;
    public static final short CATEGORY_BULLET_E = 0x0004;
    public static final short CATEGORY_ROTATE_ENEMY = 0x0008;
    public static final short CATEGORY_KAMIKAZE = 0x0016;
    public static final short CATEGORY_SHOOTER = 0x0032;
    public static final short CATEGORY_HEAL = 0x064;
    public static final short CATEGORY_WALL = 0x0100; // Nueva categoría
    public static final short MASK_PLAYER = CATEGORY_BULLET_E | CATEGORY_KAMIKAZE | CATEGORY_WALL; // JUGADOR CON BALAS ENEMIGAS Y KAMIKACES
    public static final short MASK_BULLET = CATEGORY_ROTATE_ENEMY | CATEGORY_KAMIKAZE | CATEGORY_WALL; // Colisiona con enemigos y kamikazes
    public static final short MASK_BULLET_E = CATEGORY_PLAYER | CATEGORY_ROTATE_ENEMY; // Las balas enemigas con jugador y los rotate
    public static final short MASK_ROTATE_ENEMY = CATEGORY_BULLET | CATEGORY_KAMIKAZE | CATEGORY_BULLET_E | CATEGORY_ROTATE_ENEMY; // Los rotate con balas enemigas y del jugador y los kamikaces
    public static final short MASK_KAMIKAZE = CATEGORY_PLAYER | CATEGORY_BULLET | CATEGORY_ROTATE_ENEMY; // Los kamikaces con el jugador las balas y los rotate.
    public static final short MASK_SHOOTER = CATEGORY_PLAYER | CATEGORY_BULLET | CATEGORY_SHOOTER; // Los shooters con el jugador las balas y los shooters.
    public static final short MASK_HEAL = CATEGORY_HEAL | CATEGORY_PLAYER | CATEGORY_WALL;
    public static final short MASK_WALL = CATEGORY_PLAYER | CATEGORY_BULLET;

    public static final float PPM = 32;
    public static float WORLD_WIDTH = Gdx.graphics.getWidth() / PPM;
    public static float WORLD_HEIGHT = Gdx.graphics.getHeight() / PPM;

    private GameClass gameClass;

    private OrthographicCamera gameCamera;
    private FitViewport viewport;
    private SpriteBatch batch;

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private PlayerShip playerShip;

    private HUD hud;
    private Joystick joystick;

    private BackgroundManager backgroundManager;
    public BulletManager bulletManager;
    private EnemyManager enemyManager;
    private WaveManager waveManager;
    private DropManager dropManager;

    private int score;


    private PowerUpSelectionScreen powerUpScreen;
    private boolean showingPowerUpSelection = false;
    private TextureRegion buttonAtlas;

    private GameRecords gameRecords;
    private int currentKills;
    private int currentDeaths;
    private int currentWave;

    /**
     * Constructor del  mundo, se instancia toda la logica del juego
     *
     * @param batch
     * @param gameClass
     */
    public GameScene(SpriteBatch batch, GameClass gameClass) {
        MusicManager.getInstance().pause();
        this.world = new World(new Vector2(0, 0f), true);
        contactlistener();
        this.debugRenderer = new Box2DDebugRenderer();
        this.batch = batch;
        this.gameClass = gameClass;
        this.joystick = new Joystick(this);
        this.playerShip = new PlayerShip(world, joystick, gameClass, this);
        this.hud = new HUD();
        this.score = 0;
        createWorldBounds();
        setGameCameraAndViewport();
        setManagers();
        survivePoints(5);

        Texture atlasTexture = new Texture(Gdx.files.internal("ui/uiatlas.png"));
        buttonAtlas = new TextureRegion(atlasTexture);
        powerUpScreen = new PowerUpSelectionScreen(buttonAtlas);

        waveManager.startNextWave(playerShip.getBody().getPosition());

        Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
        boolean musicOn = prefs.getBoolean("musicOn", true); // Valor por defecto true


        if (musicOn) {
            MusicManager.getInstance().play("music/game_music_1.ogg");
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(.255f, .0f, .0f, 1);

        hud.update(delta, waveManager.getCurrentWave(), enemyManager.getActiveEnemyCount(),
            playerShip.getLife(), playerShip.getMaxLife(), score, waveManager.getCountdownText());
        Vector2 playerPos = playerShip.getBody().getPosition();
        gameCamera.position.set(playerPos.x, playerPos.y, 0);
        batch.setProjectionMatrix(gameCamera.combined);

        // Actualizar entidades
        setRenderUpdates(delta);

        // Renderizar entidades
        setRender(batch);
        hud.render();

        // Mostrar menú solo si la oleada está completa y no está visible
        if (waveManager.isWaveComplete() && !showingPowerUpSelection) {
            showingPowerUpSelection = true;
            powerUpScreen.show();
            currentWave++;
            gameRecords.updateWave(currentWave);
            Gdx.input.setInputProcessor(powerUpScreen.getStage());

            //playerShip.getBody().setTransform(0, 0, 0);
            //joystick.reset();
        }

        // Manejar el menú de power-ups
        if (showingPowerUpSelection) {
            powerUpScreen.render();
            if (powerUpScreen.isSelectionComplete()) {
                powerUpScreen.hide();
                showingPowerUpSelection = false;
                powerUpScreen.applySelection(playerShip, bulletManager);
                Gdx.input.setInputProcessor(joystick.getStage());
                waveManager.startNextWave(playerShip.getBody().getPosition());
            }
        }

        if (playerShip.isDead()) {
            currentDeaths = currentDeaths + 1;
            gameRecords.updateDeaths(currentDeaths);
            // Marcar todos los enemigos y balas para eliminación
            for (EnemyShip enemy : enemyManager.getEnemies()) {
                enemy.markForRemoval();
            }
            for (Bullet bullet : bulletManager.getBullets()) {
                bullet.markForRemoval();
            }
            showingPowerUpSelection = true;
            // Esperar a que todos los cuerpos se hayan destruido
            if (enemyManager.getActiveEnemyCount() == 0 && bulletManager.getBullets().size == 0) {
                Gdx.app.postRunnable(() -> {
                    dispose();
                    gameClass.setScreen(new MainMenuScreen(batch, gameClass));
                });
            }
        }


        world.step(1 / 60f, 6, 2);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        gameCamera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        gameCamera.update();
    }

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
        playerShip.dispose();
        backgroundManager.dispose();
        bulletManager.dispose();
        enemyManager.dispose();
        hud.dispose();
        dropManager.dispose();
        Timer.instance().clear();
    }

    /**
     * set de la camara
     */
    public void setGameCameraAndViewport() {
        gameCamera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT); // Tamaño en metros
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, gameCamera); // Tamaño en metros
    }

    /**
     * Crea los managers de las entidades del juego
     */
    public void setManagers() {
        this.backgroundManager = new BackgroundManager("background/space_Background_4096x2048.png", 1f);
        this.dropManager = new DropManager(world);
        this.bulletManager = new BulletManager(world, gameCamera);
        this.enemyManager = new EnemyManager(world, playerShip, bulletManager, dropManager);
        this.waveManager = new WaveManager(enemyManager);
        this.gameRecords = new GameRecords();
    }

    /**
     * Actualiza las entidades del juego
     *
     * @param delta
     */
    public void setRenderUpdates(float delta) {
        playerShip.update(delta);
        bulletManager.update(delta);
        enemyManager.update(delta, playerShip.getBody().getPosition());
        gameCamera.update();
        dropManager.update(delta, playerShip);
        waveManager.update(delta);
    }

    /**
     * Renderiza las entidades del juego
     *
     * @param batch
     */
    public void setRender(SpriteBatch batch) {
        batch.begin();
        backgroundManager.render(batch, WORLD_WIDTH, WORLD_HEIGHT, PPM);
        bulletManager.render(batch);
        playerShip.render(batch);
        dropManager.render(batch);
        enemyManager.render(batch);
        batch.end();

        joystick.draw();

//        debugRenderer.render(world, gameCamera.combined);
    }

    public void incrementScore(int points) {
        score += points;
    }

    public int getScore() {
        return score;
    }

    public void survivePoints(int SCORE_INTERVAL) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                incrementScore(5);
            }
        }, 0, SCORE_INTERVAL);
    }

    /**
     * Crea el listener de colisiones
     */
    public void contactlistener() {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                short categoryA = fixtureA.getFilterData().categoryBits;
                short categoryB = fixtureB.getFilterData().categoryBits;

                // Colisión entre bala del jugador y rotate_enemy o kamikaze o shooter
                if ((categoryA == CATEGORY_BULLET && (categoryB == CATEGORY_ROTATE_ENEMY || categoryB == CATEGORY_KAMIKAZE || categoryB == CATEGORY_SHOOTER)) ||
                    (categoryB == CATEGORY_BULLET && (categoryA == CATEGORY_ROTATE_ENEMY || categoryA == CATEGORY_KAMIKAZE || categoryA == CATEGORY_SHOOTER))) {
                    handleEnemyHit(categoryA == CATEGORY_BULLET ? fixtureB.getBody() : fixtureA.getBody());
                    if (!playerShip.hasPenetratingShots()) {
                        markBulletForRemoval(categoryA == CATEGORY_BULLET ? fixtureA.getBody() : fixtureB.getBody());
                    }
                }

                // Colisión entre bala enemiga y jugador
                if ((categoryA == CATEGORY_BULLET_E && categoryB == CATEGORY_PLAYER) ||
                    (categoryB == CATEGORY_BULLET_E && categoryA == CATEGORY_PLAYER)) {
                    playerShip.takeDamage(10); // Aplicar daño al jugador
                    markBulletForRemoval(categoryA == CATEGORY_BULLET_E ? fixtureA.getBody() : fixtureB.getBody());
                    System.out.println(playerShip.getLife());
                }

                // Colisión entre kamikaze y el jugador
                if ((categoryA == CATEGORY_KAMIKAZE && categoryB == CATEGORY_PLAYER) ||
                    (categoryB == CATEGORY_KAMIKAZE && categoryA == CATEGORY_PLAYER)) {
                    markEnemyForRemoval(categoryA == CATEGORY_KAMIKAZE ? fixtureA.getBody() : fixtureB.getBody());
                    playerShip.takeDamage(20); // Ejemplo de daño por impacto de kamikaze
                }

                // Colisión entre Kamikaze y Rotate Enemy
                if ((categoryA == CATEGORY_KAMIKAZE && categoryB == CATEGORY_ROTATE_ENEMY) ||
                    (categoryB == CATEGORY_KAMIKAZE && categoryA == CATEGORY_ROTATE_ENEMY)) {
                    markEnemyForRemoval(categoryA == CATEGORY_KAMIKAZE ? fixtureA.getBody() : fixtureB.getBody());
                }

                if ((categoryA == CATEGORY_HEAL && categoryB == CATEGORY_PLAYER) ||
                    (categoryB == CATEGORY_HEAL && categoryA == CATEGORY_PLAYER)) {
                    removalDrop(categoryA == CATEGORY_HEAL ? fixtureA.getBody() : fixtureB.getBody());
                }

            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }

            private void markEnemyForRemoval(Body enemyBody) {
                for (EnemyShip enemy : enemyManager.getEnemies()) {
                    if (enemy.getBody() == enemyBody && enemy != null) {
                        enemy.markForRemoval();
                        break;

                    }
                }
            }

            private void markBulletForRemoval(Body bulletBody) {
                for (Bullet bullet : bulletManager.getBullets()) {
                    if (bullet.getBody() == bulletBody && bullet != null) {
                        bullet.markForRemoval();
                        break;
                    }
                }
            }


            private void handleEnemyHit(Body enemyBody) {
                for (EnemyShip enemy : enemyManager.getEnemies()) {
                    if (enemy.getBody() == enemyBody && enemy != null) {
                        enemy.hit(playerShip.getBassedDamage());
                        if (enemy.isDestroyed()) {
                            currentKills++;
                            gameRecords.updateKills(currentKills);
                            incrementScore(5);
                        }
                        break;
                    }
                }
            }

            private void removalDrop(Body healthDrop) {
                for (HealthDrop drop : dropManager.getActiveDrops()) {
                    if (!drop.isCollected() && drop.getBody() == healthDrop && drop != null) {
                        drop.setCollected(true);
                        playerShip.heal(drop.getHealAmount());
                    }
                }
            }
        });
    }

    private float boundsSize;  // Tamaño del lado del cuadrado en metros

    private void createWorldBounds() {
        // Definir un cuadrado mayor que WORLD_WIDTH y WORLD_HEIGHT
        boundsSize = Math.max(WORLD_WIDTH, WORLD_HEIGHT) * 12.5f; // 50% más grande que la dimensión mayor

        // Definir offsets para mover el centro a la derecha y arriba
        float offsetX = 500f; // Desplazamiento a la derecha en metros
        float offsetY = 500f; // Desplazamiento hacia arriba en metros

        // Calcular el centro desplazado
        float centerX = WORLD_WIDTH / 2f + offsetX;
        float centerY = WORLD_HEIGHT / 2f + offsetY;

        float thickness = 2f; // Grosor de las paredes en metros Box2D
        float halfThickness = thickness / 2f;

        // Pared izquierda
        createWall(
            centerX - boundsSize / 2 - halfThickness, centerY,  // Posición X, Y
            thickness, boundsSize + thickness                   // Ancho, Alto
        );

        // Pared derecha
        createWall(
            centerX + boundsSize / 2 + halfThickness, centerY,
            thickness, boundsSize + thickness
        );

        // Pared inferior
        createWall(
            centerX, centerY - boundsSize / 2 - halfThickness,
            boundsSize + thickness, thickness
        );

        // Pared superior
        createWall(
            centerX, centerY + boundsSize / 2 + halfThickness,
            boundsSize + thickness, thickness
        );
    }

    private void createWall(float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2f, height / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = CATEGORY_WALL;
        fixtureDef.filter.maskBits = MASK_WALL;

        body.createFixture(fixtureDef);
        shape.dispose();
    }


}
