package com.cubefighter.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import java.util.ArrayList;
import java.util.List;

public class ParticleSystem {
    
    public enum ParticleType {
        HIT,
        DEATH,
        UPGRADE,
        LEVELUP,
        BOSS_APPEAR
    }
    
    private static final int MAX_PARTICLES = 500;
    private static final int POOL_INITIAL_SIZE = 100;
    private static final int POOL_MAX_SIZE = 300;
    
    private final Pool<Particle> particlePool;
    private final List<Particle> activeParticles;
    private final List<FloatingText> floatingTexts;
    
    public ParticleSystem() {
        activeParticles = new ArrayList<>();
        floatingTexts = new ArrayList<>();
        
        particlePool = Pools.get(Particle.class, POOL_MAX_SIZE);
        
        for (int i = 0; i < POOL_INITIAL_SIZE; i++) {
            particlePool.free(new Particle());
        }
    }
    
    public void createParticles(ParticleType type, float x, float y) {
        createParticles(type, x, y, 1.0f);
    }
    
    public void createParticles(ParticleType type, float x, float y, float intensity) {
        int particleCount = getParticleCountForType(type, intensity);
        
        for (int i = 0; i < particleCount && activeParticles.size() < MAX_PARTICLES; i++) {
            Particle particle = particlePool.obtain();
            initializeParticle(particle, type, x, y);
            activeParticles.add(particle);
        }
    }
    
    public void createExplosion(float x, float y, float radius, int particleCount) {
        for (int i = 0; i < particleCount && activeParticles.size() < MAX_PARTICLES; i++) {
            Particle particle = particlePool.obtain();
            initializeExplosionParticle(particle, x, y, radius);
            activeParticles.add(particle);
        }
    }
    
    public void addFloatingText(String text, float x, float y, FloatingText.TextType type) {
        floatingTexts.add(new FloatingText(text, x, y, type));
    }
    
    public void update(float delta) {
        for (int i = activeParticles.size() - 1; i >= 0; i--) {
            Particle particle = activeParticles.get(i);
            
            if (particle.update(delta)) {
                activeParticles.remove(i);
                particlePool.free(particle);
            }
        }
        
        for (int i = floatingTexts.size() - 1; i >= 0; i--) {
            FloatingText text = floatingTexts.get(i);
            
            if (text.update(delta)) {
                floatingTexts.remove(i);
            }
        }
    }
    
    public void render(SpriteBatch batch) {
        for (Particle particle : activeParticles) {
            particle.render(batch);
        }
        
        for (FloatingText text : floatingTexts) {
            text.render(batch);
        }
    }
    
    public void clear() {
        for (Particle particle : activeParticles) {
            particlePool.free(particle);
        }
        activeParticles.clear();
        floatingTexts.clear();
    }
    
    public int getActiveParticleCount() {
        return activeParticles.size();
    }
    
    public int getActiveTextCount() {
        return floatingTexts.size();
    }
    
    private int getParticleCountForType(ParticleType type, float intensity) {
        int baseCount;
        
        switch (type) {
            case HIT:
                baseCount = 5;
                break;
            case DEATH:
                baseCount = 20;
                break;
            case UPGRADE:
                baseCount = 15;
                break;
            case LEVELUP:
                baseCount = 30;
                break;
            case BOSS_APPEAR:
                baseCount = 50;
                break;
            default:
                baseCount = 10;
        }
        
        return (int) (baseCount * intensity);
    }
    
    private void initializeParticle(Particle particle, ParticleType type, float x, float y) {
        switch (type) {
            case HIT:
                particle.initAsSpark(x, y);
                break;
            case DEATH:
                particle.initAsExplosion(x, y);
                break;
            case UPGRADE:
                particle.initAsStar(x, y);
                break;
            case LEVELUP:
                particle.initAsStar(x, y);
                break;
            case BOSS_APPEAR:
                particle.initAsExplosion(x, y);
                break;
        }
    }
    
    private void initializeExplosionParticle(Particle particle, float x, float y, float radius) {
        particle.initAsExplosion(x, y, radius);
    }
}