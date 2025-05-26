package io.github.space_wars.entities;

import com.badlogic.gdx.graphics.Texture;

public class Bullet extends GameObject {

    public Bullet(Texture texture) {
        super(texture);
        this.speedY = 400;
    }

    @Override
    public void update(float delta) {
        y += speedY * delta;
        updateBounds();
    }
}
