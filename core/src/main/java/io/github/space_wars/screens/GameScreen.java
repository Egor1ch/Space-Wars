package io.github.space_wars.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.space_wars.Main;
import io.github.space_wars.entities.Bullet;
import io.github.space_wars.entities.EnemyShip;
import io.github.space_wars.entities.PlayerShip;
import io.github.space_wars.entities.BossShip;

import java.util.Iterator;

public class GameScreen implements Screen {
    private final Main game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Texture backgroundTexture;
    private PlayerShip playerShip;
    private Array<EnemyShip> enemies;
    private Array<Bullet> bullets;
    private long lastEnemySpawnTime;
    private long lastBulletTime;
    private int score;
    private int lives;
    private boolean initialized = false;
    private static final int PLAYER_SHIP_WIDTH = 128;
    private static final int PLAYER_SHIP_HEIGHT = 128;
    private BossShip boss;
    private boolean bossMode = false;
    private Texture bossTexture;
    private float gameTime;

    public GameScreen(Main game) {
        this.game = game;

        try {
            Gdx.app.log("GameScreen", "Initializing GameScreen");

            camera = new OrthographicCamera();
            camera.setToOrtho(false, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
            viewport = new FitViewport(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, camera);

            viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
            camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);

            enemies = new Array<>();
            bullets = new Array<>();

            score = 0;
            lives = 3;

            loadTextures();

            initialized = true;
            Gdx.app.log("GameScreen", "GameScreen initialized successfully");
            gameTime = 0;
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error initializing game: " + e.getMessage(), e);
            if (game != null) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new MainMenuScreen(game));
                    }
                });
            }
        }
    }

    private void loadTextures() {
        try {
            if (Gdx.files.internal("gamegpt.png").exists()) {
                backgroundTexture = new Texture(Gdx.files.internal("gamegpt.png"));
                Gdx.app.log("GameScreen", "Background texture gamegpt.png loaded successfully");
            } else if (Gdx.files.internal("background.png").exists()) {
                backgroundTexture = new Texture(Gdx.files.internal("background.png"));
                Gdx.app.log("GameScreen", "Background texture loaded successfully");
            } else {
                Gdx.app.error("GameScreen", "Background texture not found, creating fallback");
                backgroundTexture = createColorTexture(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, new Color(0.1f, 0.1f, 0.3f, 1));
            }

            Texture playerTexture;
            if (Gdx.files.internal("player_ship.png").exists()) {
                playerTexture = new Texture(Gdx.files.internal("player_ship.png"));
                Gdx.app.log("GameScreen", "Player ship texture loaded successfully");
            } else {
                Gdx.app.error("GameScreen", "Player ship texture not found, creating fallback");
                playerTexture = createColorTexture(PLAYER_SHIP_WIDTH, PLAYER_SHIP_HEIGHT, Color.GREEN);
            }

            playerShip = new PlayerShip(playerTexture);
            playerShip.setSize(PLAYER_SHIP_WIDTH, PLAYER_SHIP_HEIGHT);
            playerShip.setPosition(Main.SCREEN_WIDTH / 2 - playerShip.getWidth() / 2, 20);

            spawnEnemy();

        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error loading textures: " + e.getMessage(), e);
            throw e;
        }
    }

    private Texture createColorTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void spawnEnemy() {
        try {
            Texture enemyTexture;

            boolean useSecondTexture = MathUtils.randomBoolean();

            if (useSecondTexture && Gdx.files.internal("enemy_ship2.png").exists()) {
                enemyTexture = new Texture(Gdx.files.internal("enemy_ship2.png"));
                Gdx.app.log("GameScreen", "Using enemy_ship2.png texture");
            } else if (Gdx.files.internal("enemy_ship.png").exists()) {
                enemyTexture = new Texture(Gdx.files.internal("enemy_ship.png"));
                Gdx.app.log("GameScreen", "Using enemy_ship.png texture");
            } else {
                Gdx.app.error("GameScreen", "Enemy ship textures not found, using fallback texture");
                enemyTexture = createColorTexture(32, 32, Color.RED);
            }

            EnemyShip enemy = new EnemyShip(enemyTexture);
            enemy.setSize(96, 96);
            enemy.setPosition(MathUtils.random(0, Main.SCREEN_WIDTH - enemy.getWidth()), Main.SCREEN_HEIGHT);
            enemies.add(enemy);
            lastEnemySpawnTime = TimeUtils.nanoTime();
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error spawning enemy: " + e.getMessage(), e);
        }
    }

    private void spawnBullet() {
        try {
            Texture bulletTexture;
            if (Gdx.files.internal("bullet.png").exists()) {
                bulletTexture = new Texture(Gdx.files.internal("bullet.png"));
            } else {
                bulletTexture = createColorTexture(16, 32, Color.YELLOW);
            }

            Bullet bullet = new Bullet(bulletTexture);
            bullet.setPosition(playerShip.getX() + playerShip.getWidth() / 2 - bullet.getWidth() / 2,
                           playerShip.getY() + playerShip.getHeight());
            bullets.add(bullet);
            lastBulletTime = TimeUtils.nanoTime();
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error spawning bullet: " + e.getMessage(), e);
        }
    }

    private void checkBossSpawn() {
        if (score >= 999 && !bossMode) {
            bossMode = true;
            if (Gdx.files.internal("boss.png").exists()) {
                bossTexture = new Texture(Gdx.files.internal("boss.png"));
            } else {
                bossTexture = createColorTexture(128, 128, Color.PURPLE);
            }
            boss = new BossShip(bossTexture);
            boss.setPosition(Main.SCREEN_WIDTH / 2 - boss.getWidth() / 2, Main.SCREEN_HEIGHT - 150);

            for (EnemyShip enemy : enemies) {
                enemy.dispose();
            }
            enemies.clear();
        }
    }

    @Override
    public void render(float delta) {
        if (!initialized) {
            Gdx.app.log("GameScreen", "Skipping render - not initialized");
            return;
        }

        try {
            gameTime += delta;
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            camera.update();
            game.batch.setProjectionMatrix(camera.combined);

            if (Gdx.input.isTouched()) {
                Vector3 touchPos = new Vector3();
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);

                float x = touchPos.x - playerShip.getWidth() / 2;
                float y = touchPos.y - playerShip.getHeight() / 2;

                x = Math.max(0, Math.min(x, Main.SCREEN_WIDTH - playerShip.getWidth()));
                y = Math.max(0, Math.min(y, Main.SCREEN_HEIGHT - playerShip.getHeight()));

                playerShip.setPosition(x, y);
            }

            if (TimeUtils.nanoTime() - lastBulletTime > 500000000) {
                spawnBullet();
            }

            checkBossSpawn();

            if (!bossMode && TimeUtils.nanoTime() - lastEnemySpawnTime > 1000000000) {
                spawnEnemy();
            }

            for (Iterator<EnemyShip> iter = enemies.iterator(); iter.hasNext(); ) {
                EnemyShip enemy = iter.next();
                enemy.update(delta);

                if (enemy.getY() + enemy.getHeight() < 0) {
                    iter.remove();
                    lives--;

                    if (lives <= 0) {
                        game.soundManager.pauseBackgroundMusic();
                        game.setScreen(new GameOverScreen(game, score));
                        dispose();
                        return;
                    }
                }
            }

            for (Iterator<Bullet> iter = bullets.iterator(); iter.hasNext(); ) {
                Bullet bullet = iter.next();
                bullet.update(delta);

                if (bullet.getY() > Main.SCREEN_HEIGHT) {
                    iter.remove();
                }
            }

            for (Iterator<Bullet> bulletIter = bullets.iterator(); bulletIter.hasNext(); ) {
                Bullet bullet = bulletIter.next();

                for (Iterator<EnemyShip> enemyIter = enemies.iterator(); enemyIter.hasNext(); ) {
                    EnemyShip enemy = enemyIter.next();

                    if (bullet.getBounds().overlaps(enemy.getBounds())) {
                        bulletIter.remove();
                        enemyIter.remove();
                        score += 10;
                        break;
                    }
                }
            }

            if (bossMode && boss != null) {
                boss.update(delta);

                if (boss.hasReachedBottom()) {
                    lives = 0;
                    game.soundManager.pauseBackgroundMusic();
                    game.setScreen(new GameOverScreen(game, score));
                    dispose();
                    return;
                }

                for (Iterator<Bullet> bulletIter = bullets.iterator(); bulletIter.hasNext();) {
                    Bullet bullet = bulletIter.next();
                    if (bullet.getBounds().overlaps(boss.getBounds())) {
                        boss.hit();
                        bulletIter.remove();
                        score += 20;

                        if (boss.isDefeated()) {
                            game.setScreen(new VictoryScreen(game, score));
                            dispose();
                            return;
                        }
                    }
                }
            }

            game.batch.begin();

            game.batch.draw(backgroundTexture, 0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);

            playerShip.render(game.batch);

            for (EnemyShip enemy : enemies) {
                enemy.render(game.batch);
            }

            for (Bullet bullet : bullets) {
                bullet.render(game.batch);
            }

            if (bossMode && boss != null) {
                boss.render(game.batch);
            }

            float scoreWidth = game.font.draw(game.batch, "Score: " + score, 0, 0).width;
            float livesWidth = game.font.draw(game.batch, "Lives: " + lives, 0, 0).width;

            // Formato del tiempo: MM:SS
            int minutes = (int)(gameTime / 60);
            int seconds = (int)(gameTime % 60);
            String timeString = String.format("%02d:%02d", minutes, seconds);
            float timeWidth = game.font.draw(game.batch, timeString, 0, 0).width;

            game.font.draw(game.batch, "Score: " + score,
                         20,
                         Main.SCREEN_HEIGHT - 20);

            game.font.draw(game.batch, timeString,
                         Main.SCREEN_WIDTH / 2 - timeWidth / 2,
                         Main.SCREEN_HEIGHT - 20);

            game.font.draw(game.batch, "Lives: " + lives,
                         Main.SCREEN_WIDTH - livesWidth - 20,
                         Main.SCREEN_HEIGHT - 20);

            game.batch.end();
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error in render method: " + e.getMessage(), e);
        }
    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);
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
        try {
            if (backgroundTexture != null) backgroundTexture.dispose();
            if (playerShip != null) playerShip.dispose();

            for (EnemyShip enemy : enemies) {
                if (enemy != null) enemy.dispose();
            }

            for (Bullet bullet : bullets) {
                if (bullet != null) bullet.dispose();
            }

            if (bossTexture != null) bossTexture.dispose();
            if (boss != null) boss.dispose();
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error disposing resources: " + e.getMessage(), e);
        }
    }
}

