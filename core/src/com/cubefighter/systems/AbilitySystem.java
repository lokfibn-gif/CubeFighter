package com.cubefighter.systems;

import com.cubefighter.entities.Player;
import java.util.HashMap;
import java.util.Map;

public class AbilitySystem {
    
    public enum AbilityType {
        DASH,
        SHIELD,
        HEAL,
        ULTIMATE
    }
    
    private Map<AbilityType, Ability> abilities;
    private float dashDistance;
    private boolean isDashing;
    private float dashInvincibilityTime;
    
    private static final float DASH_COOLDOWN = 1.5f;
    private static final float SHIELD_COOLDOWN = 8.0f;
    private static final float HEAL_COOLDOWN = 15.0f;
    private static final float ULTIMATE_COOLDOWN = 30.0f;
    
    private static final float DASH_DURATION = 0.2f;
    private static final float SHIELD_DURATION = 3.0f;
    private static final int BASE_HEAL_AMOUNT = 50;
    
    public AbilitySystem() {
        this.abilities = new HashMap<AbilityType, Ability>();
        this.dashDistance = 150f;
        this.isDashing = false;
        this.dashInvincibilityTime = 0f;
        
        initializeAbilities();
    }
    
    private void initializeAbilities() {
        abilities.put(AbilityType.DASH, new Ability(DASH_COOLDOWN, 1));
        abilities.put(AbilityType.SHIELD, new Ability(SHIELD_COOLDOWN, 1));
        abilities.put(AbilityType.HEAL, new Ability(HEAL_COOLDOWN, 1));
        abilities.put(AbilityType.ULTIMATE, new Ability(ULTIMATE_COOLDOWN, 1));
    }
    
    public void update(float deltaTime) {
        for (Ability ability : abilities.values()) {
            ability.update(deltaTime);
        }
        
        if (isDashing) {
            dashInvincibilityTime -= deltaTime;
            if (dashInvincibilityTime <= 0) {
                isDashing = false;
            }
        }
    }
    
    public boolean canUseAbility(AbilityType type) {
        return abilities.get(type).isReady();
    }
    
    public void useAbility(AbilityType type, Player player) {
        Ability ability = abilities.get(type);
        if (!ability.isReady()) {
            return;
        }
        
        ability.activate();
        
        switch (type) {
            case DASH:
                activateDash(player);
                break;
            case SHIELD:
                activateShield(player);
                break;
            case HEAL:
                activateHeal(player);
                break;
            case ULTIMATE:
                activateUltimate(player);
                break;
        }
    }
    
    private void activateDash(Player player) {
        isDashing = true;
        dashInvincibilityTime = DASH_DURATION;
    }
    
    private void activateShield(Player player) {
        int shieldLevel = abilities.get(AbilityType.SHIELD).getLevel();
        player.activateShield();
    }
    
    private void activateHeal(Player player) {
        int healLevel = abilities.get(AbilityType.HEAL).getLevel();
        player.heal();
    }
    
    private void activateUltimate(Player player) {
        int ultimateLevel = abilities.get(AbilityType.ULTIMATE).getLevel();
    }
    
    public float getCooldown(AbilityType type) {
        return abilities.get(type).getRemainingCooldown();
    }
    
    public float getCooldownPercent(AbilityType type) {
        return abilities.get(type).getCooldownPercent();
    }
    
    public boolean isAbilityReady(AbilityType type) {
        return abilities.get(type).isReady();
    }
    
    public int getAbilityLevel(AbilityType type) {
        return abilities.get(type).getLevel();
    }
    
    public void upgradeAbility(AbilityType type) {
        abilities.get(type).upgrade();
    }
    
    public void setAbilityLevel(AbilityType type, int level) {
        abilities.get(type).setLevel(level);
    }
    
    public boolean isDashing() {
        return isDashing;
    }
    
    public boolean isInvincible() {
        return isDashing && dashInvincibilityTime > 0;
    }
    
    public float getDashDistance() {
        return dashDistance;
    }
    
    public void setDashDistance(float distance) {
        this.dashDistance = distance;
    }
    
    public int getHealAmount() {
        int healLevel = abilities.get(AbilityType.HEAL).getLevel();
        return BASE_HEAL_AMOUNT + (healLevel * 20);
    }
    
    public float getShieldDuration() {
        int shieldLevel = abilities.get(AbilityType.SHIELD).getLevel();
        return SHIELD_DURATION + (shieldLevel * 0.5f);
    }
    
    public void resetAllCooldowns() {
        for (Ability ability : abilities.values()) {
            ability.reset();
        }
        isDashing = false;
        dashInvincibilityTime = 0f;
    }
    
    private static class Ability {
        private float baseCooldown;
        private float remainingCooldown;
        private int level;
        
        public Ability(float cooldown, int level) {
            this.baseCooldown = cooldown;
            this.remainingCooldown = 0f;
            this.level = level;
        }
        
        public void update(float deltaTime) {
            if (remainingCooldown > 0) {
                remainingCooldown -= deltaTime;
            }
        }
        
        public void activate() {
            remainingCooldown = getEffectiveCooldown();
        }
        
        public boolean isReady() {
            return remainingCooldown <= 0;
        }
        
        public float getRemainingCooldown() {
            return Math.max(0, remainingCooldown);
        }
        
        public float getCooldownPercent() {
            return remainingCooldown / getEffectiveCooldown();
        }
        
        public float getEffectiveCooldown() {
            return baseCooldown - (level * 0.2f);
        }
        
        public int getLevel() {
            return level;
        }
        
        public void setLevel(int level) {
            this.level = level;
        }
        
        public void upgrade() {
            level++;
        }
        
        public void reset() {
            remainingCooldown = 0f;
        }
    }
}