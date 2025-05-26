package io.github.space_wars.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class GameObject {
    protected Texture texture;
    protected float x, y;
    protected float speedX, speedY;
    protected Rectangle bounds;

    public GameObject(Texture texture) {
        this.texture = texture;
        this.x = 0;
        this.y = 0;
        this.speedX = 0;
        this.speedY = 0;

        if (texture != null) {
            this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        } else {
            Gdx.app.error("GameObject", "Texture is null, using default size for bounds");
            this.bounds = new Rectangle(x, y, 32, 32);
        }
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        updateBounds();
    }

    public void setX(float x) {
        this.x = x;
        updateBounds();
    }

    public void setY(float y) {
        this.y = y;
        updateBounds();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return texture != null ? texture.getWidth() : 32;
    }

    public float getHeight() {
        return texture != null ? texture.getHeight() : 32;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    protected void updateBounds() {
        bounds.setPosition(x, y);
    }

    public abstract void update(float delta);

    public void render(SpriteBatch batch) {
        if (texture != null && batch != null) {
            batch.draw(texture, x, y);
        }
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
