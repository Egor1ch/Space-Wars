package io.github.space_wars.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class PlayerShip {
    private Sprite sprite;
    private Rectangle bounds;
    private Texture texture;

    public PlayerShip(Texture texture) {
        this.texture = texture;
        this.sprite = new Sprite(texture);
        this.bounds = new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }

    public void setPosition(float x, float y) {
        sprite.setPosition(x, y);
        bounds.setPosition(x, y);
    }

    public void setX(float x) {
        setPosition(x, sprite.getY());
    }

    public void setY(float y) {
        setPosition(sprite.getX(), y);
    }

    public void setSize(float width, float height) {
        sprite.setSize(width, height);
        bounds.setSize(width, height);
    }

    public float getX() {
        return sprite.getX();
    }

    public float getY() {
        return sprite.getY();
    }

    public float getWidth() {
        return sprite.getWidth();
    }

    public float getHeight() {
        return sprite.getHeight();
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
