package com.cubefighter.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class FloatingText {
    
    public enum TextType {
        DAMAGE,
        HEAL,
        CRIT,
        SCORE,
        LEVEL_UP
    }
    
    private String text;
    private Vector2 position;
    private Vector2 velocity;
    private Color color;
    private float alpha;
    private float lifetime;
    private float age;
    private float scale;
    private float startScale;
    private float targetScale;
    private boolean isActive;
    private TextType type;
    
    private static final float FLOAT_SPEED = 60f;
    private static final float BASE_LIFETIME = 1.0f;
    
    public FloatingText(String text, float x, float y, TextType type) {
        this.text = text;
        this.position = new Vector2(x, y);
        this.type = type;
        this.velocity = new Vector2(0, FLOAT_SPEED);
        this.alpha = 1f;
        this.lifetime = BASE_LIFETIME;
        this.age = 0f;
        this.isActive = true;
        this.scale = 1f;
        this.startScale = 1.5f;
        this.targetScale = 1f;
        
        initializeForType();
    }
    
    private void initializeForType() {
        switch (type) {
            case DAMAGE:
                color = new Color(Color.RED);
                scale = 1.0f;
                startScale = 1.2f;
                break;
            case HEAL:
                color = new Color(Color.GREEN);
                scale = 1.0f;
                startScale = 1.0f;
                break;
            case CRIT:
                color = new Color(Color.YELLOW);
                scale = 1.5f;
                startScale = 2.0f;
                lifetime = BASE_LIFETIME * 1.5f;
                break;
            case SCORE:
                color = new Color(Color.GOLD);
                scale = 1.2f;
                startScale = 1.3f;
                break;
            case LEVEL_UP:
                color = new Color(Color.CYAN);
                scale = 1.8f;
                startScale = 2.5f;
                lifetime = BASE_LIFETIME * 2f;
                break;
        }
    }
    
    public boolean update(float delta) {
        if (!isActive) {
            return true;
        }
        
        age += delta;
        
        if (age >= lifetime) {
            return true;
        }
        
        position.add(velocity.cpy().scl(delta));
        
        float lifePercent = age / lifetime;
        
        alpha = 1f - lifePercent;
        
        if (lifePercent < 0.2f) {
            scale = startScale + (targetScale - startScale) * (lifePercent / 0.2f);
        }
        
        return false;
    }
    
    public void render(SpriteBatch batch) {
        if (!isActive || alpha <= 0) {
            return;
        }
        
        BitmapFont font = getDefaultFont();
        
        if (font == null) {
            return;
        }
        
        Color oldColor = font.getColor().cpy();
        float oldScaleX = font.getScaleX();
        float oldScaleY = font.getScaleY();
        
        font.setColor(color.r, color.g, color.b, alpha);
        font.getData().setScale(scale);
        
        float textWidth = font.getScaleX() * text.length() * 8f;
        float textHeight = font.getScaleY() * 16f;
        
        font.draw(batch, text, position.x - textWidth / 2f, position.y + textHeight / 2f);
        
        font.setColor(oldColor);
        font.getData().setScale(oldScaleX, oldScaleY);
    }
    
    public void render(BitmapFont font, SpriteBatch batch) {
        if (!isActive || alpha <= 0 || font == null) {
            return;
        }
        
        Color oldColor = font.getColor().cpy();
        float oldScaleX = font.getScaleX();
        float oldScaleY = font.getScaleY();
        
        font.setColor(color.r, color.g, color.b, alpha);
        font.getData().setScale(scale);
        
        float textWidth = font.getScaleX() * text.length() * 8f;
        float textHeight = font.getScaleY() * 16f;
        
        font.draw(batch, text, position.x - textWidth / 2f, position.y + textHeight / 2f);
        
        font.setColor(oldColor);
        font.getData().setScale(oldScaleX, oldScaleY);
    }
    
    private BitmapFont getDefaultFont() {
        return null;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
    
    public float getX() {
        return position.x;
    }
    
    public float getY() {
        return position.y;
    }
    
    public TextType getType() {
        return type;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
}