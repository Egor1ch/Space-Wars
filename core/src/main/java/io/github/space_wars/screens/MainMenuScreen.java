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

public class MainMenuScreen implements Screen {
    private final Main game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Texture backgroundTexture;
    private Rectangle playBounds;
    private Rectangle exitBounds;

    public MainMenuScreen(Main game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
        viewport = new FitViewport(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, camera);
        viewport.setScreenBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        playBounds = new Rectangle(Main.SCREEN_WIDTH / 2 - 300, Main.SCREEN_HEIGHT / 2, 300, 75);
        exitBounds = new Rectangle(Main.SCREEN_WIDTH / 2 - 300, Main.SCREEN_HEIGHT / 2 - 200, 300, 75);

        try {
            if (Gdx.files.internal("gpt.png").exists()) {
                backgroundTexture = new Texture(Gdx.files.internal("gpt.png"));
            } else {
                backgroundTexture = createColorTexture(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, new Color(0.1f, 0.1f, 0.2f, 1));
            }
        } catch (Exception e) {
            backgroundTexture = createColorTexture(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, new Color(0.1f, 0.1f, 0.2f, 1));
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
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);

        float titleWidth = game.font.draw(game.batch, "SPACE WARS", 0, 0).width;
        game.font.draw(game.batch, "SPACE WARS",
                      Main.SCREEN_WIDTH / 2 - titleWidth / 2,
                      Main.SCREEN_HEIGHT - 200);

        float playWidth = game.font.draw(game.batch, "PLAY", 0, 0).width;
        float exitWidth = game.font.draw(game.batch, "EXIT", 0, 0).width;

        game.font.draw(game.batch, "PLAY",
                      Main.SCREEN_WIDTH / 2 - playWidth / 2,
                      playBounds.y + 30);
        game.font.draw(game.batch, "EXIT",
                      Main.SCREEN_WIDTH / 2 - exitWidth / 2,
                      exitBounds.y + 30);

        game.batch.end();

        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if (playBounds.contains(touchPos.x, touchPos.y)) {
                try {
                    game.setScreen(new GameScreen(game));
                    dispose();
                } catch (Exception e) {
                    Gdx.app.error("MainMenuScreen", "Error creating GameScreen", e);
                }
            } else if (exitBounds.contains(touchPos.x, touchPos.y)) {
                Gdx.app.exit();
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
