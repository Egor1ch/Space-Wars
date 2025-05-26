package io.github.space_wars;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.space_wars.screens.MainMenuScreen;
import io.github.space_wars.utils.SoundManager;

public class Main extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public SoundManager soundManager;

    public static int SCREEN_WIDTH = 800;
    public static int SCREEN_HEIGHT = 480;

    @Override
    public void create() {
        SCREEN_WIDTH = Gdx.graphics.getWidth();
        SCREEN_HEIGHT = Gdx.graphics.getHeight();

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(5.0f);

        soundManager = new SoundManager();
        soundManager.playBackgroundMusic();

        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        soundManager.dispose();
        getScreen().dispose();
    }
}
