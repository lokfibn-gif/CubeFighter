package com.cubefighter.systems;

import com.cubefighter.entities.GameObject;
import com.cubefighter.entities.Player;
import com.cubefighter.entities.Enemy;
import java.util.List;
import java.util.ArrayList;

public class CombatSystem {
    
    private float attackCooldown;
    private float cooldownTimer;
    private boolean isCriticalHit;
    private float criticalChance;
    private float criticalMultiplier;
    
    private List<StatusEffect> activeEffects;
    
    public enum StatusEffectType {
        BURN,
        FREEZE,
        STUN
    }
    
    public CombatSystem() {
        this.attackCooldown = 0.5f;
        this.cooldownTimer = 0f;
        this.criticalChance = 0.1f;
        this.criticalMultiplier = 2.0f;
        this.activeEffects = new ArrayList<StatusEffect>();
    }
    
    public boolean checkCollision(GameObject a, GameObject b) {
        return a.getBounds().overlaps(b.getBounds());
    }
    
    public List<Enemy> getCollidingEnemies(Player player, List<Enemy> enemies) {
        List<Enemy> colliding = new ArrayList<Enemy>();
        for (Enemy enemy : enemies) {
            if (checkCollision(player, enemy)) {
                colliding.add(enemy);
            }
        }
        return colliding;
    }
    
    public int calculateDamage(int baseDamage, float critChance) {
        isCriticalHit = Math.random() < (criticalChance + critChance);
        if (isCriticalHit) {
            return (int)(baseDamage * criticalMultiplier);
        }
        return baseDamage;
    }
    
    public boolean canAttack(float deltaTime) {
        cooldownTimer -= deltaTime;
        if (cooldownTimer <= 0) {
            cooldownTimer = attackCooldown;
            return true;
        }
        return false;
    }
    
    public void applyDamage(GameObject target, int damage) {
        target.takeDamage(damage);
    }
    
    public void applyStatusEffect(GameObject target, StatusEffectType type, float duration, int damagePerTick) {
        StatusEffect effect = new StatusEffect(type, duration, damagePerTick);
        activeEffects.add(effect);
        target.addStatusEffect(effect);
    }
    
    public void updateStatusEffects(float deltaTime) {
        for (int i = activeEffects.size() - 1; i >= 0; i--) {
            StatusEffect effect = activeEffects.get(i);
            effect.update(deltaTime);
            if (effect.isExpired()) {
                activeEffects.remove(i);
            }
        }
    }
    
    public void activateUltimate(Player player, List<Enemy> enemies) {
        int ultimateDamage = player.getDamage() *5;
        for (Enemy enemy : enemies) {
            applyDamage(enemy, ultimateDamage);
            applyStatusEffect(enemy, StatusEffectType.STUN, 2.0f, 0);
        }
        player.resetUltimateCharge();
    }
    
    public boolean isLastHitCritical() {
        return isCriticalHit;
    }
    
    public void setAttackCooldown(float cooldown) {
        this.attackCooldown = cooldown;
    }
    
    public void setCriticalChance(float chance) {
        this.criticalChance = chance;
    }
    
    public void setCriticalMultiplier(float multiplier) {
        this.criticalMultiplier = multiplier;
    }
    
    public static class StatusEffect {
        private StatusEffectType type;
        private float duration;
        private float remainingTime;
        private int damagePerTick;
        private float tickInterval;
        private float tickTimer;
        
        public StatusEffect(StatusEffectType type, float duration, int damagePerTick) {
            this.type = type;
            this.duration = duration;
            this.remainingTime = duration;
            this.damagePerTick = damagePerTick;
            this.tickInterval = 0.5f;
            this.tickTimer = 0f;
        }
        
        public void update(float deltaTime) {
            remainingTime -= deltaTime;
            tickTimer += deltaTime;
            if (tickTimer >= tickInterval && damagePerTick > 0) {
                tickTimer = 0f;
            }
        }
        
        public boolean isExpired() {
            return remainingTime <= 0;
        }
        
        public StatusEffectType getType() {
            return type;
        }
        
        public int getDamagePerTick() {
            return damagePerTick;
        }
        
        public float getRemainingTime() {
            return remainingTime;
        }
    }
}