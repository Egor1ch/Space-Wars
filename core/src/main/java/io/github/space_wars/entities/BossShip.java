package io.github.space_wars.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class BossShip {
    private Sprite sprite;
    private Rectangle bounds;
    private Texture texture;
    private float speed = 100f;
    private int health = 35;

    public BossShip(Texture texture) {
        this.texture = texture;
        this.sprite = new Sprite(texture);
        this.sprite.setSize(512, 512);
        this.bounds = new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }

    public void update(float delta) {
        float y = sprite.getY() - speed * delta;
        setPosition(sprite.getX(), y);
    }

    public boolean hasReachedBottom() {
        return sprite.getY() <= 0;
    }

    public void hit() {
        health--;
    }

    public boolean isDefeated() {
        return health <= 0;
    }

    public int getHealth() {
        return health;
    }

    public void setPosition(float x, float y) {
        sprite.setPosition(x, y);
        bounds.setPosition(x, y);
    }

    public float getX() { return sprite.getX(); }
    public float getY() { return sprite.getY(); }
    public float getWidth() { return sprite.getWidth(); }
    public float getHeight() { return sprite.getHeight(); }
    public Rectangle getBounds() { return bounds; }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
