package com.cubefighter.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class GameRenderer {
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    
    private float screenShakeIntensity = 0f;
    private float screenShakeDuration = 0f;
    private Vector2 shakeOffset = new Vector2();
    
    private Array<HitParticle> hitParticles;
    private Array<HitParticle> deathParticles;
    
    private float arenaWidth = 800;
    private float arenaHeight = 600;
    
    private EnemyRenderData[] enemies;
    private PlayerRenderData player;
    private WeaponEffectData[] weaponEffects;
    
    public static class PlayerRenderData {
        public float x, y;
        public float size = 40f;
        public Color color = Color.CYAN;
        public int hp, maxHp;
        public float rotation = 0f;
    }
    
    public static class EnemyRenderData {
        public float x, y;
        public float size = 30f;
        public int type;
        public Color color;
        public float alpha = 1f;
        public float rotation = 0f;
    }
    
    public static class WeaponEffectData {
        public float startX, startY;
        public float endX, endY;
        public Color color = Color.YELLOW;
        public float width = 3f;
        public float lifetime = 0.1f;
        public int type = 0;
    }
    
    private static class HitParticle {
        Vector2 position;
        Vector2 velocity;
        Color color;
        float size;
        float lifetime;
        float maxLifetime;
        
        HitParticle(float x, float y, Color color, float size) {
            this.position = new Vector2(x, y);
            float angle = MathUtils.random(360) * MathUtils.degreesToRadians;
            float speed = MathUtils.random(50, 150);
            this.velocity = new Vector2(
                MathUtils.cos(angle) * speed,
                MathUtils.sin(angle) * speed
            );
            this.color = color;
            this.size = size;
            this.maxLifetime = MathUtils.random(0.3f, 0.8f);
            this.lifetime = maxLifetime;
        }
        
        void update(float delta) {
            lifetime -= delta;
            position.x += velocity.x * delta;
            position.y += velocity.y * delta;
            velocity.scl(0.95f);
        }
        
        boolean isDead() {
            return lifetime <= 0;
        }
        
        float getAlpha() {
            return Math.max(0, lifetime / maxLifetime);
        }
    }
    
    public GameRenderer() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        
        hitParticles = new Array<>();
        deathParticles = new Array<>();
        enemies = new EnemyRenderData[0];
        weaponEffects = new WeaponEffectData[0];
        player = new PlayerRenderData();
    }
    
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }
    
    public void setPlayerData(float x, float y, float size, int hp, int maxHp) {
        player.x = x;
        player.y = y;
        player.size = size;
        player.hp = hp;
        player.maxHp = maxHp;
    }
    
    public void setEnemyData(EnemyRenderData[] enemies) {
        this.enemies = enemies != null ? enemies : new EnemyRenderData[0];
    }
    
    public void setWeaponEffects(WeaponEffectData[] effects) {
        this.weaponEffects = effects != null ? effects : new WeaponEffectData[0];
    }
    
    public void addHitParticles(float x, float y, Color color, int count) {
        for (int i = 0; i < count; i++) {
            hitParticles.add(new HitParticle(x, y, color, MathUtils.random(3, 8)));
        }
    }
    
    public void addDeathParticles(float x, float y, Color color) {
        for (int i = 0; i < 20; i++) {
            deathParticles.add(new HitParticle(x, y, color, MathUtils.random(5, 15)));
        }
        triggerScreenShake(5f, 0.3f);
    }
    
    public void triggerScreenShake(float intensity, float duration) {
        this.screenShakeIntensity = Math.max(screenShakeIntensity, intensity);
        this.screenShakeDuration = Math.max(screenShakeDuration, duration);
    }
    
    public void update(float delta) {
        updateScreenShake(delta);
        updateParticles(delta);
    }
    
    private void updateScreenShake(float delta) {
        if (screenShakeDuration > 0) {
            screenShakeDuration -= delta;
            float intensity = screenShakeIntensity * (screenShakeDuration / 0.3f);
            shakeOffset.x = MathUtils.random(-intensity, intensity);
            shakeOffset.y = MathUtils.random(-intensity, intensity);
        } else {
            shakeOffset.set(0, 0);
            screenShakeIntensity = 0;
        }
    }
    
    private void updateParticles(float delta) {
        for (int i = hitParticles.size - 1; i >= 0; i--) {
            hitParticles.get(i).update(delta);
            if (hitParticles.get(i).isDead()) {
                hitParticles.removeIndex(i);
            }
        }
        
        for (int i = deathParticles.size - 1; i >= 0; i--) {
            deathParticles.get(i).update(delta);
            if (deathParticles.get(i).isDead()) {
                deathParticles.removeIndex(i);
            }
        }
    }
    
    public void render() {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.position.x = Gdx.graphics.getWidth() / 2f + shakeOffset.x;
        camera.position.y = Gdx.graphics.getHeight() / 2f + shakeOffset.y;
        camera.update();
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        renderArenaBackground();
        renderEnemies();
        renderPlayer();
        renderWeaponEffects();
        renderParticles();
    }
    
    private void renderArenaBackground() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        shapeRenderer.setColor(new Color(0.08f, 0.08f, 0.12f, 1f));
        shapeRenderer.rect(0, 0, arenaWidth, arenaHeight);
        
        shapeRenderer.setColor(new Color(0.15f, 0.15f, 0.2f, 1f));
        for (int i = 0; i < arenaWidth; i += 50) {
            shapeRenderer.line(i, 0, i, arenaHeight);
        }
        for (int i = 0; i < arenaHeight; i += 50) {
            shapeRenderer.line(0, i, arenaWidth, i);
        }
        
        shapeRenderer.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(0.3f, 0.3f, 0.4f, 1f));
        shapeRenderer.rect(0, 0, arenaWidth, arenaHeight);
        shapeRenderer.end();
    }
    
    private void renderPlayer() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        float hpPercent = (float) player.hp / player.maxHp;
        float glowSize = player.size * 1.3f;
        shapeRenderer.setColor(new Color(player.color.r, player.color.g, player.color.b, 0.3f));
        shapeRenderer.rect(
            player.x - glowSize / 2, player.y - glowSize / 2,
            glowSize, glowSize
        );
        
        shapeRenderer.setColor(player.color);
        shapeRenderer.rect(
            player.x - player.size / 2, player.y - player.size / 2,
            player.size, player.size
        );
        
        float innerSize = player.size * 0.6f;
        shapeRenderer.setColor(new Color(1, 1, 1, 0.3f));
        shapeRenderer.rect(
            player.x - innerSize / 2, player.y - innerSize / 2,
            innerSize, innerSize
        );
        
        shapeRenderer.end();
    }
    
    private void renderEnemies() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (EnemyRenderData enemy : enemies) {
            if (enemy.alpha <= 0) continue;
            
            Color baseColor = getEnemyColor(enemy.type);
            shapeRenderer.setColor(new Color(baseColor.r, baseColor.g, baseColor.b, enemy.alpha));
            
            shapeRenderer.rect(
                enemy.x - enemy.size / 2,
                enemy.y - enemy.size / 2,
                enemy.size, enemy.size
            );
            
            float eyeSize = enemy.size * 0.15f;
            shapeRenderer.setColor(new Color(1, 1, 1, enemy.alpha * 0.8f));
            float eyeOffset = enemy.size * 0.2f;
            shapeRenderer.rect(enemy.x - eyeOffset - eyeSize/2, enemy.y + eyeSize/2, eyeSize, eyeSize);
            shapeRenderer.rect(enemy.x + eyeOffset - eyeSize/2, enemy.y + eyeSize/2, eyeSize, eyeSize);
        }
        
        shapeRenderer.end();
    }
    
    private Color getEnemyColor(int type) {
        switch (type) {
            case 0: return Color.RED;
            case 1: return Color.ORANGE;
            case 2: return Color.PURPLE;
            case 3: return Color.MAGENTA;
            case 4: return new Color(0.5f, 0.2f, 0.2f, 1f);
            default: return Color.RED;
        }
    }
    
    private void renderWeaponEffects() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        
        for (WeaponEffectData effect : weaponEffects) {
            shapeRenderer.setColor(effect.color);
            Gdx.gl.glLineWidth((int)effect.width);
            shapeRenderer.line(effect.startX, effect.startY, effect.endX, effect.endY);
        }
        
        Gdx.gl.glLineWidth(1);
        shapeRenderer.end();
    }
    
    private void renderParticles() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (HitParticle p : hitParticles) {
            shapeRenderer.setColor(new Color(p.color.r, p.color.g, p.color.b, p.getAlpha()));
            shapeRenderer.rect(p.position.x - p.size/2, p.position.y - p.size/2, p.size, p.size);
        }
        
        for (HitParticle p : deathParticles) {
            shapeRenderer.setColor(new Color(p.color.r, p.color.g, p.color.b, p.getAlpha()));
            shapeRenderer.rect(p.position.x - p.size/2, p.position.y - p.size/2, p.size, p.size);
        }
        
        shapeRenderer.end();
    }
    
    public void setArenaSize(float width, float height) {
        this.arenaWidth = width;
        this.arenaHeight = height;
    }
    
    public Vector2 getShakeOffset() {
        return shakeOffset;
    }
    
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }
}