package com.cubefighter.effects;

import com.badlogic.gdx.math.Vector2;

public class ScreenShake {
    
    private float intensity;
    private float duration;
    private float elapsed;
    private float currentIntensity;
    private Vector2 offset;
    private boolean isActive;
    
    private static final float DEFAULT_DURATION = 0.3f;
    private static final float DEFAULT_INTENSITY = 10f;
    
    public ScreenShake() {
        this.intensity = 0f;
        this.duration = 0f;
        this.elapsed = 0f;
        this.currentIntensity = 0f;
        this.offset = new Vector2(0, 0);
        this.isActive = false;
    }
    
    public void shake() {
        shake(DEFAULT_INTENSITY);
    }
    
    public void shake(float intensity) {
        shake(intensity, DEFAULT_DURATION);
    }
    
    public void shake(float intensity, float duration) {
        this.intensity = Math.max(0, intensity);
        this.duration = Math.max(0.01f, duration);
        this.currentIntensity = this.intensity;
        this.elapsed = 0f;
        this.isActive = true;
    }
    
    public void shakeLight() {
        shake(5f, 0.15f);
    }
    
    public void shakeMedium() {
        shake(10f, 0.25f);
    }
    
    public void shakeHeavy() {
        shake(20f, 0.4f);
    }
    
    public void shakeExplosion() {
        shake(30f, 0.5f);
    }
    
    public Vector2 update(float delta) {
        if (!isActive) {
            offset.set(0, 0);
            return offset;
        }
        
        elapsed += delta;
        
        if (elapsed >= duration) {
            isActive = false;
            offset.set(0, 0);
            return offset;
        }
        
        float progress = elapsed / duration;
        float decayFactor = 1f - progress;
        currentIntensity = intensity * decayFactor;
        
        float offsetX = (float) (Math.random() * 2f - 1f) * currentIntensity;
        float offsetY = (float) (Math.random() * 2f - 1f) * currentIntensity;
        
        offset.set(offsetX, offsetY);
        
        return offset;
    }
    
    public void reset() {
        intensity = 0f;
        duration = 0f;
        elapsed = 0f;
        currentIntensity = 0f;
        offset.set(0, 0);
        isActive = false;
    }
    
    public float getOffsetX() {
        return offset.x;
    }
    
    public float getOffsetY() {
        return offset.y;
    }
    
    public Vector2 getOffset() {
        return offset.cpy();
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public float getCurrentIntensity() {
        return currentIntensity;
    }
    
    public float getRemainingDuration() {
        return Math.max(0, duration - elapsed);
    }
    
    public float getProgress() {
        if (duration <= 0) return 1f;
        return Math.min(1f, elapsed / duration);
    }
}