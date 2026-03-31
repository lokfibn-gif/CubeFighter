package com.cubefighter.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Player extends GameObject {
    public enum SizeLevel {
        LEVEL_1(1, 1),
        LEVEL_2(2, 2),
        LEVEL_3(3, 3),
        LEVEL_4(4, 4),
        LEVEL_5(5, 5);
        
        public final int tiles;
        public final int size;
        
        SizeLevel(int tiles, int size) {
            this.tiles = tiles;
            this.size = size;
        }
    }
    
    private SizeLevel sizeLevel;
    private int hp;
    private int maxHp;
    private int damage;
    private float speed;
    private float criticalChance;
    private float criticalMultiplier;
    private Weapon currentWeapon;
    
    private boolean dashAvailable;
    private float dashCooldown;
    private float dashDuration;
    private boolean isDashing;
    private float dashTimer;
    
    private boolean shieldActive;
    private float shieldCooldown;
    private float shieldDuration;
    private float shieldTimer;
    
    private boolean healAvailable;
    private float healCooldown;
    private int healAmount;
    
    private boolean ultimateAvailable;
    private float ultimateCooldown;
    private int ultimateDamageMultiplier;
    
    public Player(float x, float y) {
        super(x, y, 32, 32);
        this.sizeLevel = SizeLevel.LEVEL_1;
        this.maxHp = 100;
        this.hp = maxHp;
        this.damage = 10;
        this.speed = 150f;
        this.criticalChance = 0.05f;
        this.criticalMultiplier = 1.5f;
        this.currentWeapon = Weapon.createFist();
        
        this.dashAvailable = true;
        this.dashCooldown = 3f;
        this.dashDuration = 0.2f;
        this.isDashing = false;
        
        this.shieldActive = false;
        this.shieldCooldown = 8f;
        this.shieldDuration = 2f;
        
        this.healAvailable = true;
        this.healCooldown = 15f;
        this.healAmount = 25;
        
        this.ultimateAvailable = true;
        this.ultimateCooldown = 30f;
        this.ultimateDamageMultiplier = 3;
    }
    
    @Override
    public void update(float deltaTime) {
        position.x += velocity.x * deltaTime;
        position.y += velocity.y * deltaTime;
        
        clampToWorld();
        
        if (isDashing) {
            dashTimer -= deltaTime;
            if (dashTimer <= 0) {
                isDashing = false;
            }
        }
        
        if (shieldActive) {
            shieldTimer -= deltaTime;
            if (shieldTimer <= 0) {
                shieldActive = false;
            }
        }
        
        updateCooldowns(deltaTime);
    }
    
    @Override
    public void render(SpriteBatch batch) {
    }
    
    public int attack() {
        boolean isCritical = MathUtils.random() < criticalChance;
        int totalDamage = damage + currentWeapon.getDamage();
        if (isCritical) {
            totalDamage = (int)(totalDamage * criticalMultiplier);
        }
        return totalDamage;
    }
    
    public int attackWithUltimate() {
        if (!ultimateAvailable) return 0;
        ultimateAvailable = false;
        return attack() * ultimateDamageMultiplier;
    }
    
    public void takeDamage(int damage) {
        if (shieldActive) {
            damage = damage / 2;
        }
        hp -= damage;
        if (hp < 0) hp = 0;
    }
    
    public void heal() {
        if (!healAvailable) return;
        hp += healAmount;
        if (hp > maxHp) hp = maxHp;
        healAvailable = false;
    }
    
    public void dash(float directionX, float directionY) {
        if (!dashAvailable || isDashing) return;
        isDashing = true;
        dashTimer = dashDuration;
        dashAvailable = false;
        velocity.set(directionX * speed * 3, directionY * speed * 3);
    }
    
    public void activateShield() {
        if (!shieldAvailable()) return;
        shieldActive = true;
        shieldTimer = shieldDuration;
    }
    
    public void upgradeSize() {
        int nextLevel = sizeLevel.ordinal() + 1;
        if (nextLevel < SizeLevel.values().length) {
            sizeLevel = SizeLevel.values()[nextLevel];
            float newSize = sizeLevel.size * 32f;
            setSize(newSize, newSize);
            maxHp += 20;
            hp = maxHp;
            damage += 5;
        }
    }
    
    public void upgradeDamage(int amount) {
        damage += amount;
    }
    
    public void upgradeSpeed(float amount) {
        speed += amount;
    }
    
    public void upgradeCritical(float chanceBonus, float multiplierBonus) {
        criticalChance += chanceBonus;
        criticalMultiplier += multiplierBonus;
    }
    
    public void equipWeapon(Weapon weapon) {
        currentWeapon = weapon;
    }
    
    public void updateCooldowns(float deltaTime) {
        if (!dashAvailable) {
            dashCooldown -= deltaTime;
            if (dashCooldown <= 0) dashAvailable = true;
        }
        if (!healAvailable) {
            healCooldown -= deltaTime;
            if (healCooldown <= 0) healAvailable = true;
        }
        if (!ultimateAvailable) {
            ultimateCooldown -= deltaTime;
            if (ultimateCooldown <= 0) ultimateAvailable = true;
        }
        if (!shieldActive && !shieldAvailable()) {
            shieldCooldown -= deltaTime;
        }
    }
    
    public boolean dashAvailable() { return dashAvailable; }
    public boolean isDashing() { return isDashing; }
    public boolean shieldAvailable() { return shieldCooldown <= 0 && !shieldActive; }
    public boolean isShieldActive() { return shieldActive; }
    public boolean healAvailable() { return healAvailable; }
    public boolean ultimateAvailable() { return ultimateAvailable; }
    
    public void resetUltimateCharge() {
        ultimateAvailable = false;
        ultimateCooldown = 30f;
    }
    
    public SizeLevel getSizeLevel() { return sizeLevel; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getDamage() { return damage; }
    public float getSpeed() { return speed; }
    public float getCriticalChance() { return criticalChance; }
    public float getCriticalMultiplier() { return criticalMultiplier; }
    public Weapon getCurrentWeapon() { return currentWeapon; }
    
    public void setHp(int hp) { this.hp = Math.min(hp, maxHp); }
    public void setDamage(int damage) { this.damage = damage; }
    public void setSpeed(float speed) { this.speed = speed; }
    public void setCriticalChance(float chance) { this.criticalChance = chance; }
    public void setCriticalMultiplier(float mult) { this.criticalMultiplier = mult; }
}