package com.cubefighter.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;

public class Boss extends Enemy {
    public enum BossType {
        SLIME_KING("Slime King", 300, 20, 80f, "Splits into smaller slimes"),
        GOBLIN_CHIEF("Goblin Chief", 500, 30, 100f, "Summons goblin minions"),
        FROST_TITAN("Frost Titan", 800, 35, 60f, "Freeze attack, slows player"),
        FIRE_DEMON("Fire Demon", 1000, 40, 70f, "Fire trail, burn damage"),
        DRAGON_CUBE("Dragon Cube", 1500, 50, 50f, "Fire breath, flying"),
        SHADOW_LORD("Shadow Lord", 2000, 60, 90f, "Shadow clones, teleport"),
        ANCIENT_ONE("Ancient One", 5000, 100, 40f, "All abilities, phases");
        
        public final String name;
        public final int baseHp;
        public final int baseDamage;
        public final float baseSpeed;
        public final String specialAbility;
        
        BossType(String name, int baseHp, int baseDamage, float baseSpeed, String specialAbility) {
            this.name = name;
            this.baseHp = baseHp;
            this.baseDamage = baseDamage;
            this.baseSpeed = baseSpeed;
            this.specialAbility = specialAbility;
        }
    }
    
    private BossType bossType;
    private String name;
    private List<BossAbility> abilities;
    private int phase;
    private int maxPhase;
    private float abilityCooldown;
    private float abilityTimer;
    private BossAbility currentAbility;
    
    public static class BossAbility {
        public String name;
        public int damage;
        public float range;
        public float duration;
        public float cooldown;
        
        public BossAbility(String name, int damage, float range, float duration, float cooldown) {
            this.name = name;
            this.damage = damage;
            this.range = range;
            this.duration = duration;
            this.cooldown = cooldown;
        }
    }
    
    public Boss(BossType bossType, float x, float y) {
        super(EnemyType.HUGE, x, y);
        this.bossType = bossType;
        this.name = bossType.name;
        this.maxHp = bossType.baseHp;
        this.hp = maxHp;
        this.damage = bossType.baseDamage;
        this.speed = bossType.baseSpeed;
        this.attackRange = 100f;
        this.detectionRange = 400f;
        this.phase = 1;
        this.maxPhase = 3;
        this.abilities = new ArrayList<>();
        this.abilityCooldown = 5f;
        this.abilityTimer = 0f;
        
        initializeAbilities();
    }
    
    private void initializeAbilities() {
        switch (bossType) {
            case SLIME_KING:
                abilities.add(new BossAbility("Split", 0, 0, 1f, 10f));
                abilities.add(new BossAbility("Absorb", 15, 80f, 0.5f, 5f));
                break;
            case GOBLIN_CHIEF:
                abilities.add(new BossAbility("Summon", 0, 0, 2f, 8f));
                abilities.add(new BossAbility("Throw", 25, 200f, 0.3f, 3f));
                break;
            case FROST_TITAN:
                abilities.add(new BossAbility("Freeze Aura", 10, 150f, 3f, 6f));
                abilities.add(new BossAbility("Ice Spike", 40, 120f, 0.5f, 4f));
                break;
            case FIRE_DEMON:
                abilities.add(new BossAbility("Fire Trail", 5, 0, 5f, 8f));
                abilities.add(new BossAbility("Fireball", 35, 180f, 0.4f, 3f));
                break;
            case DRAGON_CUBE:
                abilities.add(new BossAbility("Fire Breath", 20, 150f, 2f, 5f));
                abilities.add(new BossAbility("Wing Attack", 45, 80f, 0.5f, 4f));
                break;
            case SHADOW_LORD:
                abilities.add(new BossAbility("Shadow Clone", 0, 0, 0f, 12f));
                abilities.add(new BossAbility("Teleport Strike", 55, 200f, 0.2f, 5f));
                break;
            case ANCIENT_ONE:
                abilities.add(new BossAbility("Phase Shift", 0, 0, 0f, 20f));
                abilities.add(new BossAbility("Annihilation", 100, 300f, 1f, 15f));
                abilities.add(new BossAbility("Regeneration", 0, 0, 3f, 10f));
                break;
        }
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        
        if (!active) return;
        
        updatePhase();
        
        if (abilityTimer > 0) {
            abilityTimer -= deltaTime;
        }
    }
    
    private void updatePhase() {
        float hpPercent = (float) hp / maxHp;
        
        if (hpPercent <= 0.33f && phase < 3) {
            phase = 3;
            onPhaseChange();
        } else if (hpPercent <= 0.66f && phase < 2) {
            phase = 2;
            onPhaseChange();
        }
    }
    
    private void onPhaseChange() {
        speed *= 1.2f;
        damage *= 1.2f;
        abilityCooldown *= 0.8f;
    }
    
    public BossAbility useAbility() {
        if (abilityTimer > 0 || abilities.isEmpty()) return null;
        
        BossAbility ability = abilities.get((int)(Math.random() * abilities.size()));
        abilityTimer = ability.cooldown;
        currentAbility = ability;
        return ability;
    }
    
    public boolean canUseAbility() {
        return abilityTimer <= 0;
    }
    
    public void setStunned(float duration) {
        velocity.set(0, 0);
        abilityTimer += duration;
    }
    
    public BossType getBossType() { return bossType; }
    public String getBossName() { return name; }
    public int getPhase() { return phase; }
    public int getMaxPhase() { return maxPhase; }
    public List<BossAbility> getAbilities() { return abilities; }
    public BossAbility getCurrentAbility() { return currentAbility; }
}