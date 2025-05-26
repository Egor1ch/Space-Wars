package io.github.space_wars.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.space_wars.Main;

public class GameOverScreen implements Screen {
    private final Main game;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Texture backgroundTexture;

    private final int finalScore;
    private Rectangle menuBounds;
    private boolean soundPlayed = false;

    public GameOverScreen(Main game, int score) {
        this.game = game;
        this.finalScore = score;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
        viewport = new FitViewport(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, camera);
        viewport.setScreenBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        menuBounds = new Rectangle(Main.SCREEN_WIDTH / 2 - 300, Main.SCREEN_HEIGHT / 2 - 200, 300, 75);

        try {
            if (Gdx.files.internal("gpt.png").exists()) {
                backgroundTexture = new Texture(Gdx.files.internal("gpt.png"));
                Gdx.app.log("GameOverScreen", "Background texture loaded successfully");
            } else {
                Gdx.app.error("GameOverScreen", "main.jpg not found, creating fallback texture");
                backgroundTexture = createColorTexture(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, new Color(0, 0, 0.2f, 1));
            }
        } catch (Exception e) {
            Gdx.app.error("GameOverScreen", "Error loading background texture: " + e.getMessage(), e);
            backgroundTexture = createColorTexture(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, new Color(0, 0, 0.2f, 1));
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

    @Override
    public void render(float delta) {
        if (!soundPlayed) {
            game.soundManager.playGameOverSound();
            soundPlayed = true;
        }

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);

        float gameOverWidth = game.font.draw(game.batch, "GAME OVER", 0, 0).width;
        float scoreWidth = game.font.draw(game.batch, "FINAL SCORE: " + finalScore, 0, 0).width;
        float menuWidth = game.font.draw(game.batch, "MAIN MENU", 0, 0).width;

        game.font.draw(game.batch, "GAME OVER",
                      Main.SCREEN_WIDTH / 2 - gameOverWidth / 2,
                      Main.SCREEN_HEIGHT / 2 + 200);
        game.font.draw(game.batch, "FINAL SCORE: " + finalScore,
                      Main.SCREEN_WIDTH / 2 - scoreWidth / 2,
                      Main.SCREEN_HEIGHT / 2 + 100);
        game.font.draw(game.batch, "MAIN MENU",
                      Main.SCREEN_WIDTH / 2 - menuWidth / 2,
                      menuBounds.y + 90);

        game.batch.end();

        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if (menuBounds.contains(touchPos.x, touchPos.y)) {
                try {
                    game.soundManager.playBackgroundMusic();
                    game.setScreen(new MainMenuScreen(game));
                    dispose();
                } catch (Exception e) {
                    Gdx.app.error("GameOverScreen", "Error creating MainMenuScreen", e);
                }
            }
        }
    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        viewport.setScreenBounds(0, 0, width, height);
        camera.position.set(Main.SCREEN_WIDTH/2, Main.SCREEN_HEIGHT/2, 0);
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
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
