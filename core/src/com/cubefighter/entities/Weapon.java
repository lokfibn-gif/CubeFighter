package com.cubefighter.entities;

import java.util.HashMap;
import java.util.Map;

public class Weapon {
    public enum WeaponType {
        FIST("Fist", 0, 20f, "Basic attack"),
        WOODEN_SWORD("Wooden Sword", 5, 25f, "Simple melee weapon"),
        IRON_SWORD("Iron Sword", 12, 30f, "Standard melee weapon"),
        STEEL_SWORD("Steel Sword", 20, 35f, "Enhanced melee weapon"),
        GOLDEN_BLADE("Golden Blade", 30, 40f, "Slower but stronger"),
        DIAMOND_EDGE("Diamond Edge", 45, 45f, "Sharp and powerful"),
        FLAME_BLADE("Flame Blade", 35, 35f, "Burns enemies"),
        FROST_EDGE("Frost Edge", 35, 35f, "Slows enemies"),
        THUNDER_HAMMER("Thunder Hammer", 50, 50f, "Stuns enemies"),
        SHADOW_DAGGER("Shadow Dagger", 25, 20f, "Fast attack speed"),
        LEGENDARY_CUBE("Legendary Cube", 100, 60f, "Ultimate weapon");
        
        public final String name;
        public final int baseDamage;
        public final float range;
        public final String description;
        
        WeaponType(String name, int baseDamage, float range, String description) {
            this.name = name;
            this.baseDamage = baseDamage;
            this.range = range;
            this.description = description;
        }
    }
    
    private WeaponType type;
    private int upgradeLevel;
    private int damage;
    private float range;
    private float attackSpeed;
    private float criticalChance;
    private Map<String, String> specialEffects;
    
    public Weapon(WeaponType type) {
        this.type = type;
        this.upgradeLevel = 1;
        this.damage = type.baseDamage;
        this.range = type.range;
        this.attackSpeed = 1f;
        this.criticalChance = 0f;
        this.specialEffects = new HashMap<>();
        initializeSpecialEffects();
    }
    
    public static Weapon createFist() {
        return new Weapon(WeaponType.FIST);
    }
    
    public static Weapon createWoodenSword() {
        return new Weapon(WeaponType.WOODEN_SWORD);
    }
    
    public static Weapon createIronSword() {
        return new Weapon(WeaponType.IRON_SWORD);
    }
    
    public static Weapon createSteelSword() {
        return new Weapon(WeaponType.STEEL_SWORD);
    }
    
    public static Weapon createGoldenBlade() {
        return new Weapon(WeaponType.GOLDEN_BLADE);
    }
    
    public static Weapon createDiamondEdge() {
        return new Weapon(WeaponType.DIAMOND_EDGE);
    }
    
    public static Weapon createFlameBlade() {
        return new Weapon(WeaponType.FLAME_BLADE);
    }
    
    public static Weapon createFrostEdge() {
        return new Weapon(WeaponType.FROST_EDGE);
    }
    
    public static Weapon createThunderHammer() {
        return new Weapon(WeaponType.THUNDER_HAMMER);
    }
    
    public static Weapon createShadowDagger() {
        return new Weapon(WeaponType.SHADOW_DAGGER);
    }
    
    public static Weapon createLegendaryCube() {
        return new Weapon(WeaponType.LEGENDARY_CUBE);
    }
    
    private void initializeSpecialEffects() {
        switch (type) {
            case FLAME_BLADE:
                specialEffects.put("burn", "3");
                specialEffects.put("burnChance", "0.3");
                break;
            case FROST_EDGE:
                specialEffects.put("slow", "0.5");
                specialEffects.put("slowDuration", "2");
                break;
            case THUNDER_HAMMER:
                specialEffects.put("stun", "1");
                specialEffects.put("stunChance", "0.2");
                break;
            case SHADOW_DAGGER:
                specialEffects.put("critBonus", "0.15");
                criticalChance = 0.15f;
                break;
            case LEGENDARY_CUBE:
                specialEffects.put("critBonus", "0.25");
                specialEffects.put("lifeSteal", "0.1");
                criticalChance = 0.25f;
                break;
            default:
                break;
        }
    }
    
    public void upgrade() {
        if (upgradeLevel >= 10) return;
        
        upgradeLevel++;
        damage = (int)(type.baseDamage * (1 + upgradeLevel * 0.1f));
        range = type.range + upgradeLevel * 2f;
        attackSpeed += 0.05f;
        
        if (upgradeLevel >= 5) {
            criticalChance += 0.02f;
        }
    }
    
    public int getUpgradeCost() {
        return upgradeLevel * 100 + type.baseDamage * 10;
    }
    
    public int getDamage() { return damage; }
    public float getRange() { return range; }
    public float getAttackSpeed() { return attackSpeed; }
    public float getCriticalChance() { return criticalChance; }
    public int getUpgradeLevel() { return upgradeLevel; }
    public WeaponType getType() { return type; }
    public String getName() { return type.name; }
    public String getDescription() { return type.description; }
    public Map<String, String> getSpecialEffects() { return specialEffects; }
    
    public boolean hasSpecialEffect(String effect) {
        return specialEffects.containsKey(effect);
    }
    
    public String getSpecialEffectValue(String effect) {
        return specialEffects.get(effect);
    }
    
    public float getSpecialEffectFloat(String effect, float defaultValue) {
        String value = specialEffects.get(effect);
        if (value == null) return defaultValue;
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public int getSpecialEffectInt(String effect, int defaultValue) {
        String value = specialEffects.get(effect);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public boolean isMaxLevel() {
        return upgradeLevel >= 10;
    }
}