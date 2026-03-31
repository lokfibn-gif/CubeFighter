package com.cubefighter.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class DamageNumber implements Pool.Poolable {
    
    public enum DamageType {
        NORMAL,
        CRITICAL,
        HEAL,
        XP_GAIN,
        LEVEL_UP
    }
    
    private String text;
    private Vector2 position;
    private Vector2 velocity;
    private Color color;
    private float alpha;
    private float lifetime;
    private float maxLifetime;
    private float scale;
    private float startScale;
    private float targetScale;
    private boolean active;
    private DamageType type;
    
    private static final float RISE_SPEED = 80f;
    private static final float BASE_LIFETIME = 1.2f;
    
    public DamageNumber() {
        this.position = new Vector2();
        this.velocity = new Vector2(0, RISE_SPEED);
        this.color = new Color(Color.WHITE);
        this.active = false;
        reset();
    }
    
    public void init(float x, float y, int value, DamageType type) {
        this.position.set(x, y);
        this.type = type;
        this.active = true;
        this.alpha = 1f;
        this.lifetime = BASE_LIFETIME;
        this.maxLifetime = BASE_LIFETIME;
        this.velocity.set(0, RISE_SPEED);
        
        initializeForType(value);
    }
    
    private void initializeForType(int value) {
        switch (type) {
            case NORMAL:
                text = String.valueOf(value);
                color.set(Color.RED);
                scale = 1.2f;
                startScale = 1.5f;
                targetScale = 1.2f;
                break;
            case CRITICAL:
                text = value + "!";
                color.set(Color.YELLOW);
                scale = 1.8f;
                startScale = 2.2f;
                targetScale = 1.8f;
                lifetime = BASE_LIFETIME * 1.3f;
                maxLifetime = lifetime;
                velocity.y = RISE_SPEED * 1.2f;
                break;
            case HEAL:
                text = "+" + value;
                color.set(Color.GREEN);
                scale = 1.4f;
                startScale = 1.6f;
                targetScale = 1.4f;
                break;
            case XP_GAIN:
                text = "+" + value + " XP";
                color.set(Color.CYAN);
                scale = 1.1f;
                startScale = 1.3f;
                targetScale = 1.1f;
                velocity.y = RISE_SPEED * 0.8f;
                break;
            case LEVEL_UP:
                text = "LEVEL UP!";
                color.set(Color.GOLD);
                scale = 2.0f;
                startScale = 2.5f;
                targetScale = 2.0f;
                lifetime = BASE_LIFETIME * 2f;
                maxLifetime = lifetime;
                break;
        }
    }
    
    public void update(float delta) {
        if (!active) {
            return;
        }
        
        lifetime -= delta;
        
        if (lifetime <= 0) {
            active = false;
            return;
        }
        
        position.add(velocity.x * delta, velocity.y * delta);
        
        float lifePercent = 1f - (lifetime / maxLifetime);
        alpha = 1f - lifePercent * 0.5f;
        
        if (lifePercent < 0.15f) {
            float t = lifePercent / 0.15f;
            scale = startScale + (targetScale - startScale) * t;
        }
    }
    
    public void render(SpriteBatch batch, BitmapFont font) {
        if (!active || alpha <= 0) {
            return;
        }
        
        Color oldColor = font.getColor().cpy();
        float oldScale = font.getScaleX();
        
        font.setColor(color.r, color.g, color.b, alpha);
        font.getData().setScale(scale);
        
        float textWidth = getTextWidth(font);
        float textHeight = 16f * scale;
        
        font.draw(batch, text,
            position.x - textWidth / 2,
            position.y + textHeight / 2);
        
        font.setColor(oldColor);
        font.getData().setScale(oldScale);
    }
    
    private float getTextWidth(BitmapFont font) {
        return text.length() * 8f * scale;
    }
    
    @Override
    public void reset() {
        position.set(0, 0);
        velocity.set(0, RISE_SPEED);
        text = "";
        color.set(Color.WHITE);
        alpha = 1f;
        lifetime = BASE_LIFETIME;
        maxLifetime = BASE_LIFETIME;
        scale = 1f;
        startScale = 1.5f;
        targetScale = 1f;
        active = false;
        type = DamageType.NORMAL;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
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
    
    public String getText() {
        return text;
    }
    
    public Color getColor() {
        return color;
    }
    
    public float getAlpha() {
        return alpha;
    }
    
    public float getScale() {
        return scale;
    }
    
    public DamageType getType() {
        return type;
    }
    
    public static class DamageNumberPool {
        private com.badlogic.gdx.utils.Array<DamageNumber> pool;
        private com.badlogic.gdx.utils.Pool<DamageNumber> gdxPool;
        
        public DamageNumberPool(int initialSize) {
            gdxPool = new com.badlogic.gdx.utils.Pool<DamageNumber>(initialSize) {
                @Override
                protected DamageNumber newObject() {
                    return new DamageNumber();
                }
            };
            pool = new com.badlogic.gdx.utils.Array<>(initialSize);
        }
        
        public DamageNumber obtain() {
            DamageNumber dn = gdxPool.obtain();
            return dn;
        }
        
        public void free(DamageNumber dn) {
            gdxPool.free(dn);
        }
        
        public void update(float delta) {
            for (int i = pool.size - 1; i >= 0; i--) {
                DamageNumber dn = pool.get(i);
                dn.update(delta);
                if (!dn.isActive()) {
                    gdxPool.free(dn);
                    pool.removeIndex(i);
                }
            }
        }
        
        public void render(SpriteBatch batch, BitmapFont font) {
            for (DamageNumber dn : pool) {
                dn.render(batch, font);
            }
        }
        
        public void spawn(float x, float y, int value, DamageType type) {
            DamageNumber dn = obtain();
            dn.init(x, y, value, type);
            pool.add(dn);
        }
        
        public void clear() {
            for (DamageNumber dn : pool) {
                gdxPool.free(dn);
            }
            pool.clear();
        }
        
        public int size() {
            return pool.size;
        }
    }
}