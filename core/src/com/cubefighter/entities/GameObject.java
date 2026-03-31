package com.cubefighter.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;

public abstract class GameObject {
    protected Vector2 position;
    protected Vector2 size;
    protected Vector2 velocity;
    protected boolean active;
    protected Rectangle bounds;
    protected List<Object> statusEffects;
    
    protected static float WORLD_MIN_X = 0;
    protected static float WORLD_MIN_Y = 0;
    protected static float WORLD_MAX_X = 800;
    protected static float WORLD_MAX_Y = 600;
    
    public GameObject(float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
        this.size = new Vector2(width, height);
        this.velocity = new Vector2(0, 0);
        this.active = true;
        this.bounds = new Rectangle(x, y, width, height);
        this.statusEffects = new ArrayList<Object>();
    }
    
    public static void setWorldBounds(float minX, float minY, float maxX, float maxY) {
        WORLD_MIN_X = minX;
        WORLD_MIN_Y = minY;
        WORLD_MAX_X = maxX;
        WORLD_MAX_Y = maxY;
    }
    
    public abstract void update(float deltaTime);
    public abstract void render(SpriteBatch batch);
    
    public void clampToWorld() {
        if (position.x < WORLD_MIN_X) position.x = WORLD_MIN_X;
        if (position.y < WORLD_MIN_Y) position.y = WORLD_MIN_Y;
        if (position.x + size.x > WORLD_MAX_X) position.x = WORLD_MAX_X - size.x;
        if (position.y + size.y > WORLD_MAX_Y) position.y = WORLD_MAX_Y - size.y;
        bounds.setPosition(position.x, position.y);
    }
    
    public void setPosition(float x, float y) {
        position.set(x, y);
        bounds.setPosition(x, y);
    }
    
    public Vector2 getPosition() {
        return position;
    }
    
    public float getX() {
        return position.x;
    }
    
    public float getY() {
        return position.y;
    }
    
    public void setSize(float width, float height) {
        size.set(width, height);
        bounds.setSize(width, height);
    }
    
    public Vector2 getSize() {
        return size;
    }
    
    public float getWidth() {
        return size.x;
    }
    
    public float getHeight() {
        return size.y;
    }
    
    public void setVelocity(float x, float y) {
        velocity.set(x, y);
    }
    
    public Vector2 getVelocity() {
        return velocity;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public boolean collidesWith(GameObject other) {
        return position.x < other.position.x + other.size.x &&
               position.x + size.x > other.position.x &&
               position.y < other.position.y + other.size.y &&
               position.y + size.y > other.position.y;
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
    
    public float getCenterX() {
        return position.x + size.x / 2;
    }
    
    public float getCenterY() {
        return position.y + size.y / 2;
    }
    
    public void takeDamage(int damage) {
    }
    
    public void addStatusEffect(Object effect) {
        statusEffects.add(effect);
    }
    
    public List<Object> getStatusEffects() {
        return statusEffects;
    }
}