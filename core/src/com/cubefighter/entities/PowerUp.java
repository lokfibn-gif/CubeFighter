package com.cubefighter.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import java.util.ArrayList;
import java.util.List;

public class PowerUp extends GameObject implements Pool.Poolable {
    
    public enum PowerUpType {
        HEALTH_RESTORE("Health", Color.RED, 0),
        DAMAGE_BOOST("Damage+", Color.ORANGE, 10f),
        SPEED_BOOST("Speed+", Color.CYAN, 10f),
        INVINCIBILITY("Shield", Color.GOLD, 5f),
        SCORE_MULTIPLIER("x2 Score", Color.PURPLE, 30f);
        
        public final String name;
        public final Color color;
        public final float duration;
        
        PowerUpType(String name, Color color, float duration) {
            this.name = name;
            this.color = color;
            this.duration = duration;
        }
    }
    
    private PowerUpType type;
    private boolean active;
    private boolean collected;
    private float lifetime;
    private float maxLifetime;
    private float floatOffset;
    private float floatTimer;
    private float pulseTimer;
    
    public PowerUp(float x, float y, PowerUpType type) {
        super(x, y, 24f, 24f);
        this.type = type;
        this.active = true;
        this.collected = false;
        this.maxLifetime = 15f;
        this.lifetime = maxLifetime;
        this.floatOffset = 0f;
        this.floatTimer = 0f;
        this.pulseTimer = 0f;
    }
    
    public PowerUp() {
        super(0, 0, 24f, 24f);
        this.active = false;
        this.collected = false;
        this.maxLifetime = 15f;
        this.lifetime = maxLifetime;
    }
    
    public void init(float x, float y, PowerUpType type) {
        position.set(x, y);
        this.type = type;
        this.active = true;
        this.collected = false;
        this.lifetime = maxLifetime;
        this.floatOffset = 0f;
        this.floatTimer = 0f;
        this.pulseTimer = 0f;
    }
    
    @Override
    public void update(float deltaTime) {
        if (!active) return;
        
        lifetime -= deltaTime;
        if (lifetime <= 0) {
            active = false;
            collected = true;
            return;
        }
        
        floatTimer += deltaTime * 3f;
        floatOffset = MathUtils.sin(floatTimer) * 5f;
        
        pulseTimer += deltaTime * 5f;
    }
    
    @Override
    public void render(SpriteBatch batch) {
    }
    
    public int getHealthRestore() {
        if (type == PowerUpType.HEALTH_RESTORE) {
            return 25;
        }
        return 0;
    }
    
    public float getDamageMultiplier() {
        if (type == PowerUpType.DAMAGE_BOOST) {
            return 1.5f;
        }
        return 1.0f;
    }
    
    public float getSpeedMultiplier() {
        if (type == PowerUpType.SPEED_BOOST) {
            return 1.3f;
        }
        return 1.0f;
    }
    
    public float getScoreMultiplier() {
        if (type == PowerUpType.SCORE_MULTIPLIER) {
            return 2.0f;
        }
        return 1.0f;
    }
    
    public boolean isInvincibility() {
        return type == PowerUpType.INVINCIBILITY;
    }
    
    public float getDuration() {
        return type.duration;
    }
    
    public float getTimeRemaining() {
        return lifetime;
    }
    
    public float getTimePercentage() {
        return lifetime / maxLifetime;
    }
    
    public boolean isExpiring() {
        return lifetime < 3f;
    }
    
    public PowerUpType getType() {
        return type;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public boolean isCollected() {
        return collected;
    }
    
    public void collect() {
        active = false;
        collected = true;
    }
    
    public float getFloatOffset() {
        return floatOffset;
    }
    
    public float getPulseScale() {
        return 1.0f + MathUtils.sin(pulseTimer) * 0.1f;
    }
    
    @Override
    public void reset() {
        position.set(0, 0);
        type = null;
        active = false;
        collected = false;
        lifetime = maxLifetime;
        floatOffset = 0f;
        floatTimer = 0f;
        pulseTimer = 0f;
    }
    
    public static PowerUpType getRandomType() {
        PowerUpType[] types = PowerUpType.values();
        int index = MathUtils.random(types.length - 1);
        return types[index];
    }
    
    public static PowerUpType getWeightedRandomType() {
        float rand = MathUtils.random();
        
        if (rand < 0.30f) {
            return PowerUpType.HEALTH_RESTORE;
        } else if (rand < 0.55f) {
            return PowerUpType.DAMAGE_BOOST;
        } else if (rand < 0.75f) {
            return PowerUpType.SPEED_BOOST;
        } else if (rand < 0.90f) {
            return PowerUpType.INVINCIBILITY;
        } else {
            return PowerUpType.SCORE_MULTIPLIER;
        }
    }
    
    public static class PowerUpEffect {
        private PowerUpType type;
        private float remainingDuration;
        private float totalDuration;
        private float multiplier;
        
        public PowerUpEffect(PowerUpType type) {
            this.type = type;
            this.totalDuration = type.duration;
            this.remainingDuration = type.duration;
            this.multiplier = 1.0f;
            
            if (type == PowerUpType.DAMAGE_BOOST) {
                multiplier = 1.5f;
            } else if (type == PowerUpType.SPEED_BOOST) {
                multiplier = 1.3f;
            } else if (type == PowerUpType.SCORE_MULTIPLIER) {
                multiplier = 2.0f;
            }
        }
        
        public void update(float deltaTime) {
            if (totalDuration > 0) {
                remainingDuration -= deltaTime;
            }
        }
        
        public boolean isExpired() {
            if (totalDuration == 0) return true;
            return remainingDuration <= 0;
        }
        
        public float getMultiplier() {
            return multiplier;
        }
        
        public float getRemainingTime() {
            return Math.max(0, remainingDuration);
        }
        
        public float getTotalTime() {
            return totalDuration;
        }
        
        public float getProgress() {
            if (totalDuration == 0) return 0;
            return remainingDuration / totalDuration;
        }
        
        public PowerUpType getType() {
            return type;
        }
        
        public boolean isInvincibility() {
            return type == PowerUpType.INVINCIBILITY;
        }
    }
}