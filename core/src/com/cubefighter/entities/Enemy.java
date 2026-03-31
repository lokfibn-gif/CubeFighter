package com.cubefighter.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Enemy extends GameObject {
    public enum EnemyType {
        TINY(0.5f, 20, 3, 60f),
        SMALL(0.75f, 40, 5, 50f),
        MEDIUM(1f, 80, 8, 40f),
        LARGE(2f, 150, 15, 30f),
        HUGE(4f, 300, 25, 20f);
        
        public final float sizeMultiplier;
        public final int baseHp;
        public final int baseDamage;
        public final float baseSpeed;
        
        EnemyType(float sizeMultiplier, int baseHp, int baseDamage, float baseSpeed) {
            this.sizeMultiplier = sizeMultiplier;
            this.baseHp = baseHp;
            this.baseDamage = baseDamage;
            this.baseSpeed = baseSpeed;
        }
    }
    
    protected EnemyType type;
    protected int hp;
    protected int maxHp;
    protected int damage;
    protected float speed;
    protected float attackRange;
    protected float detectionRange;
    protected float attackCooldown;
    protected float attackTimer;
    protected boolean canAttack;
    
    public Enemy(EnemyType type, float x, float y) {
        super(x, y, 32f * type.sizeMultiplier, 32f * type.sizeMultiplier);
        this.type = type;
        this.maxHp = type.baseHp;
        this.hp = maxHp;
        this.damage = type.baseDamage;
        this.speed = type.baseSpeed;
        this.attackRange = 50f;
        this.detectionRange = 200f;
        this.attackCooldown = 1f;
        this.attackTimer = 0f;
        this.canAttack = true;
    }
    
    @Override
    public void update(float deltaTime) {
        if (!active) return;
        
        position.x += velocity.x * deltaTime;
        position.y += velocity.y * deltaTime;
        
        clampToWorld();
        
        if (!canAttack) {
            attackTimer += deltaTime;
            if (attackTimer >= attackCooldown) {
                canAttack = true;
                attackTimer = 0f;
            }
        }
    }
    
    @Override
    public void render(SpriteBatch batch) {
    }
    
    public void moveTowardPlayer(Player player, float deltaTime) {
        if (!active) return;
        
        Vector2 playerPos = player.getPosition();
        Vector2 playerSize = player.getSize();
        
        float playerCenterX = playerPos.x + playerSize.x / 2;
        float playerCenterY = playerPos.y + playerSize.y / 2;
        float enemyCenterX = getCenterX();
        float enemyCenterY = getCenterY();
        
        float dx = playerCenterX - enemyCenterX;
        float dy = playerCenterY - enemyCenterY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance < detectionRange && distance > attackRange) {
            velocity.x = (dx / distance) * speed;
            velocity.y = (dy / distance) * speed;
        } else {
            velocity.x = 0;
            velocity.y = 0;
        }
    }
    
    public int attack(Player player) {
        if (!canAttack || !isInRange(player)) return 0;
        
        canAttack = false;
        attackTimer = 0f;
        return damage;
    }
    
    public boolean isInRange(Player player) {
        float distance = distanceTo(player);
        return distance <= attackRange;
    }
    
    public float distanceTo(Player player) {
        float dx = getCenterX() - player.getCenterX();
        float dy = getCenterY() - player.getCenterY();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    
    public boolean canSeePlayer(Player player) {
        return distanceTo(player) <= detectionRange;
    }
    
    public void takeDamage(int damage) {
        hp -= damage;
        if (hp <= 0) {
            hp = 0;
            active = false;
        }
    }
    
    public EnemyType getType() { return type; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getDamage() { return damage; }
    public float getSpeed() { return speed; }
    public float getAttackRange() { return attackRange; }
    public float getDetectionRange() { return detectionRange; }
    public boolean canAttack() { return canAttack; }
    
    public void setHp(int hp) { this.hp = Math.min(hp, maxHp); }
    public void setDamage(int damage) { this.damage = damage; }
    public void setSpeed(float speed) { this.speed = speed; }
    public void setAttackRange(float range) { this.attackRange = range; }
    public void setDetectionRange(float range) { this.detectionRange = range; }
}