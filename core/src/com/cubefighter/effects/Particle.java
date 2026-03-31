package com.cubefighter.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class Particle implements Pool.Poolable {
    
    public enum Type {
        SPARK,
        SMOKE,
        STAR,
        EXPLOSION
    }
    
    private Vector2 position;
    private Vector2 velocity;
    private Vector2 acceleration;
    private Color color;
    private float alpha;
    private float size;
    private float startSize;
    private float lifetime;
    private float age;
    private Type type;
    private boolean isActive;
    private float rotation;
    private float rotationSpeed;
    private boolean fadeOut;
    private boolean shrink;
    
    public Particle() {
        position = new Vector2();
        velocity = new Vector2();
        acceleration = new Vector2(0, 0);
        color = new Color(Color.WHITE);
        alpha = 1f;
        size = 4f;
        startSize = 4f;
        lifetime = 1f;
        age = 0f;
        type = Type.SPARK;
        isActive = false;
        rotation = 0f;
        rotationSpeed = 0f;
        fadeOut = true;
        shrink = true;
    }
    
    public void initAsSpark(float x, float y) {
        reset();
        position.set(x, y);
        
        float angle = (float) (Math.random() * Math.PI * 2);
        float speed = 50f + (float) (Math.random() * 100f);
        velocity.set((float) Math.cos(angle) * speed, (float) Math.sin(angle) * speed);
        
        acceleration.set(0, -50f);
        color.set(Color.YELLOW);
        size = 3f + (float) (Math.random() * 2f);
        startSize = size;
        lifetime = 0.3f + (float) (Math.random() * 0.2f);
        alpha = 1f;
        type = Type.SPARK;
        isActive = true;
        fadeOut = true;
        shrink = true;
        rotationSpeed = 0f;
    }
    
    public void initAsSmoke(float x, float y) {
        reset();
        position.set(x, y);
        
        float angle = (float) (Math.random() * Math.PI * 2);
        float speed = 10f + (float) (Math.random() * 20f);
        velocity.set((float) Math.cos(angle) * speed, (float) Math.sin(angle) * speed + 30f);
        
        acceleration.set(0, 10f);
        color.set(0.3f, 0.3f, 0.3f, 0.7f);
        size = 8f + (float) (Math.random() * 8f);
        startSize = size;
        lifetime = 0.8f + (float) (Math.random() * 0.5f);
        alpha = 0.7f;
        type = Type.SMOKE;
        isActive = true;
        fadeOut = true;
        shrink = false;
        rotationSpeed = (float) (Math.random() * 90f - 45f);
    }
    
    public void initAsStar(float x, float y) {
        reset();
        position.set(x, y);
        
        float angle = (float) (Math.random() * Math.PI * 2);
        float speed = 80f + (float) (Math.random() * 80f);
        velocity.set((float) Math.cos(angle) * speed, (float) Math.sin(angle) * speed);
        
        acceleration.set(0, -30f);
        int colorIndex = (int) (Math.random() * 3);
        switch (colorIndex) {
            case 0:
                color.set(Color.GOLD);
                break;
            case 1:
                color.set(Color.CYAN);
                break;
            case 2:
                color.set(Color.MAGENTA);
                break;
        }
        size = 4f + (float) (Math.random() * 4f);
        startSize = size;
        lifetime = 0.5f + (float) (Math.random() * 0.5f);
        alpha = 1f;
        type = Type.STAR;
        isActive = true;
        fadeOut = true;
        shrink = true;
        rotationSpeed = (float) (Math.random() * 180f);
    }
    
    public void initAsExplosion(float x, float y) {
        initAsExplosion(x, y, 50f);
    }
    
    public void initAsExplosion(float x, float y, float radius) {
        reset();
        position.set(x, y);
        
        float angle = (float) (Math.random() * Math.PI * 2);
        float speed = radius * (0.5f + (float) Math.random() * 0.5f);
        velocity.set((float) Math.cos(angle) * speed, (float) Math.sin(angle) * speed);
        
        acceleration.set(0, 0);
        int colorIndex = (int) (Math.random() * 3);
        switch (colorIndex) {
            case 0:
                color.set(Color.ORANGE);
                break;
            case 1:
                color.set(Color.RED);
                break;
            case 2:
                color.set(Color.YELLOW);
                break;
        }
        size = 6f + (float) (Math.random() * 6f);
        startSize = size;
        lifetime = 0.4f + (float) (Math.random() * 0.3f);
        alpha = 1f;
        type = Type.EXPLOSION;
        isActive = true;
        fadeOut = true;
        shrink = true;
        rotationSpeed = (float) (Math.random() * 180f);
    }
    
    public boolean update(float delta) {
        if (!isActive) {
            return true;
        }
        
        age += delta;
        
        if (age >= lifetime) {
            return true;
        }
        
        velocity.add(acceleration.cpy().scl(delta));
        position.add(velocity.cpy().scl(delta));
        rotation += rotationSpeed * delta;
        
        float lifePercent = age / lifetime;
        
        if (fadeOut) {
            alpha = 1f - lifePercent;
        }
        
        if (shrink) {
            size = startSize * (1f - lifePercent * 0.5f);
        } else {
            size = startSize * (1f + lifePercent);
        }
        
        return false;
    }
    
    public void render(SpriteBatch batch) {
        if (!isActive || alpha <= 0) {
            return;
        }
    }
    
    public void render(ShapeRenderer shapes) {
        if (!isActive || alpha <= 0) {
            return;
        }
        
        shapes.setColor(color.r, color.g, color.b, color.a * alpha);
        
        float halfSize = size / 2f;
        
        switch (type) {
            case SPARK:
                shapes.rect(position.x - halfSize, position.y - halfSize, size, size);
                break;
            case SMOKE:
                shapes.circle(position.x, position.y, halfSize);
                break;
            case STAR:
                shapes.rect(position.x - halfSize, position.y - halfSize, size, size);
                break;
            case EXPLOSION:
                shapes.circle(position.x, position.y, halfSize);
                break;
        }
    }
    
    @Override
    public void reset() {
        position.set(0, 0);
        velocity.set(0, 0);
        acceleration.set(0, 0);
        color.set(Color.WHITE);
        alpha = 1f;
        size = 4f;
        startSize = 4f;
        lifetime = 1f;
        age = 0f;
        type = Type.SPARK;
        isActive = false;
        rotation = 0f;
        rotationSpeed = 0f;
        fadeOut = true;
        shrink = true;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public float getX() {
        return position.x;
    }
    
    public float getY() {
        return position.y;
    }
    
    public Type getType() {
        return type;
    }
    
    public float getAge() {
        return age;
    }
    
    public float getLifetime() {
        return lifetime;
    }
}