package com.cubefighter.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GameUI {
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Array<FloatingDamage> damageNumbers;
    
    private static final float HP_BAR_WIDTH = 300f;
    private static final float HP_BAR_HEIGHT = 25f;
    private static final float ABILITY_SIZE = 60f;
    private static final float ULTIMATE_BAR_WIDTH = 200f;
    private static final float ULTIMATE_BAR_HEIGHT = 15f;
    
    private int currentHp = 100;
    private int maxHp = 100;
    private int wave = 1;
    private int score = 0;
    private float ultimateCharge = 0f;
    private float ultMax = 100f;
    
    private Ability[] abilities;
    
    private static class FloatingDamage {
        Vector2 position;
        String text;
        Color color;
        float alpha = 1f;
        float lifetime;
        float maxLifetime;
        Vector2 velocity;
        
        FloatingDamage(float x, float y, String text, Color color) {
            this.position = new Vector2(x, y);
            this.text = text;
            this.color = color;
            this.lifetime = 1.5f;
            this.maxLifetime = 1.5f;
            this.velocity = new Vector2(0, 50f);
        }
        
        void update(float delta) {
            lifetime -= delta;
            alpha = lifetime / maxLifetime;
            position.y += velocity.y * delta;
        }
        
        boolean isDead() {
            return lifetime <= 0;
        }
    }
    
    private static class Ability {
        String name;
        float cooldown;
        float maxCooldown;
        boolean ready = true;
        
        Ability(String name, float maxCooldown) {
            this.name = name;
            this.maxCooldown = maxCooldown;
            this.cooldown = 0;
        }
        
        void update(float delta) {
            if (!ready) {
                cooldown -= delta;
                if (cooldown <= 0) {
                    cooldown = 0;
                    ready = true;
                }
            }
        }
        
        void use() {
            if (ready) {
                ready = false;
                cooldown = maxCooldown;
            }
        }
        
        float getCooldownPercent() {
            return ready ? 0f : cooldown / maxCooldown;
        }
    }
    
    public GameUI() {
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        damageNumbers = new Array<>();
        
        abilities = new Ability[4];
        abilities[0] = new Ability("Dash", 3f);
        abilities[1] = new Ability("Shield", 8f);
        abilities[2] = new Ability("Bomb", 15f);
        abilities[3] = new Ability("Aura", 20f);
    }
    
    public void update(float delta) {
        for (Ability ability : abilities) {
            ability.update(delta);
        }
        
        for (int i = damageNumbers.size - 1; i >= 0; i--) {
            damageNumbers.get(i).update(delta);
            if (damageNumbers.get(i).isDead()) {
                damageNumbers.removeIndex(i);
            }
        }
    }
    
    public void render(SpriteBatch batch) {
        batch.end();
        renderHPBar();
        renderUltimateBar();
        renderAbilityButtons();
        batch.begin();
        renderText(batch);
        renderDamageNumbers(batch);
    }
    
    private void renderHPBar() {
        float x = 20;
        float y = Gdx.graphics.getHeight() - 40;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(x, y, HP_BAR_WIDTH, HP_BAR_HEIGHT);
        
        float hpPercent = (float) currentHp / maxHp;
        Color hpColor = hpPercent > 0.5f ? Color.GREEN : 
                       hpPercent > 0.25f ? Color.ORANGE : Color.RED;
        shapeRenderer.setColor(hpColor);
        shapeRenderer.rect(x, y, HP_BAR_WIDTH * hpPercent, HP_BAR_HEIGHT);
        
        shapeRenderer.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(x, y, HP_BAR_WIDTH, HP_BAR_HEIGHT);
        shapeRenderer.end();
    }
    
    private void renderUltimateBar() {
        float x = Gdx.graphics.getWidth() / 2f - ULTIMATE_BAR_WIDTH / 2;
        float y = 20;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(x, y, ULTIMATE_BAR_WIDTH, ULTIMATE_BAR_HEIGHT);
        
        float charge = ultimateCharge / ultMax;
        shapeRenderer.setColor(new Color(0.3f, 0.7f, 1f, 1f));
        shapeRenderer.rect(x, y, ULTIMATE_BAR_WIDTH * charge, ULTIMATE_BAR_HEIGHT);
        shapeRenderer.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(x, y, ULTIMATE_BAR_WIDTH, ULTIMATE_BAR_HEIGHT);
        shapeRenderer.end();
    }
    
    private void renderAbilityButtons() {
        float startX = Gdx.graphics.getWidth() / 2f - (abilities.length * (ABILITY_SIZE + 10)) / 2;
        float y = 60;
        
        for (int i = 0; i < abilities.length; i++) {
            float x = startX + i * (ABILITY_SIZE + 10);
            Ability ability = abilities[i];
            
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            
            if (ability.ready) {
                shapeRenderer.setColor(new Color(0.2f, 0.5f, 0.8f, 1f));
            } else {
                shapeRenderer.setColor(Color.GRAY);
            }
            shapeRenderer.rect(x, y, ABILITY_SIZE, ABILITY_SIZE);
            
            if (!ability.ready) {
                shapeRenderer.setColor(new Color(0, 0, 0, 0.7f));
                float cooldownHeight = ABILITY_SIZE * ability.getCooldownPercent();
                shapeRenderer.rect(x, y, ABILITY_SIZE, cooldownHeight);
            }
            
            shapeRenderer.end();
            
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.rect(x, y, ABILITY_SIZE, ABILITY_SIZE);
            shapeRenderer.end();
        }
    }
    
    private void renderText(SpriteBatch batch) {
        float hpX = 30;
        float hpY = Gdx.graphics.getHeight() - 55;
        font.draw(batch, currentHp + "/" + maxHp, hpX, hpY);
        
        font.draw(batch, "Wave: " + wave, 20, Gdx.graphics.getHeight() - 80);
        font.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 110);
        
        if (ultimateCharge >= ultMax) {
            font.setColor(Color.CYAN);
            font.draw(batch, "ULTIMATE READY!", Gdx.graphics.getWidth() / 2f - 80, 50);
            font.setColor(Color.WHITE);
        }
    }
    
    private void renderDamageNumbers(SpriteBatch batch) {
        for (FloatingDamage dmg : damageNumbers) {
            font.setColor(dmg.color.r, dmg.color.g, dmg.color.b, dmg.alpha);
            font.getData().setScale(1.5f + (1f - dmg.alpha) * 0.5f);
            font.draw(batch, dmg.text, dmg.position.x, dmg.position.y);
        }
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
    }
    
    public void addDamageNumber(float x, float y, int damage, boolean critical) {
        Color color = critical ? Color.YELLOW : Color.RED;
        String text = critical ? damage + "!" : String.valueOf(damage);
        damageNumbers.add(new FloatingDamage(x, y, text, color));
    }
    
    public void addHealNumber(float x, float y, int amount) {
        damageNumbers.add(new FloatingDamage(x, y, "+" + amount, Color.GREEN));
    }
    
    public void setHP(int current, int max) {
        this.currentHp = current;
        this.maxHp = max;
    }
    
    public void setWave(int wave) {
        this.wave = wave;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public void setUltimateCharge(float charge) {
        this.ultimateCharge = Math.min(charge, ultMax);
    }
    
    public void triggerAbilityCooldown(int index) {
        if (index >= 0 && index < abilities.length) {
            abilities[index].use();
        }
    }
    
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
    }
}