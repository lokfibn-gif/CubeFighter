package com.cubefighter.systems;

import java.util.HashMap;
import java.util.Map;

public class UpgradeSystem {
    
    private int currentPoints;
    private Map<String, Integer> upgradeLevels;
    
    private static final int[] SIZE_LEVELS = {1, 2, 3, 4, 5};
    private static final int[] SIZE_COSTS = {0, 100, 250, 500, 1000};
    
    public static final String[] WEAPONS = {
        "Pistol", "Shotgun", "Rifle", "Sniper", "Laser",
        "Plasma", "Rocket", "Flamethrower", "FreezeGun",
        "Lightning", "OmegaCannon"
    };
    
    private static final int MAX_WEAPON_LEVEL = 10;
    
    private static final String[] STAT_UPGRADES = {"HP", "Damage", "Speed", "Critical", "Armor"};
    private static final int MAX_STAT_LEVEL = 20;
    
    private static final String[] ABILITY_UPGRADES = {"Dash", "Shield", "Heal", "Ultimate"};
    private static final int MAX_ABILITY_LEVEL = 10;
    
    public UpgradeSystem() {
        this.currentPoints = 0;
        this.upgradeLevels = new HashMap<String, Integer>();
        initializeUpgrades();
    }
    
    private void initializeUpgrades() {
        upgradeLevels.put("Size", 1);
        for (String weapon : WEAPONS) {
            upgradeLevels.put("Weapon_" + weapon, 0);
        }
        for (String stat : STAT_UPGRADES) {
            upgradeLevels.put("Stat_" + stat, 0);
        }
        for (String ability : ABILITY_UPGRADES) {
            upgradeLevels.put("Ability_" + ability, 0);
        }
    }
    
    public void addPoints(int points) {
        currentPoints += points;
    }
    
    public int getPoints() {
        return currentPoints;
    }
    
    public int getUpgradeLevel(String upgradeName) {
        return upgradeLevels.getOrDefault(upgradeName, 0);
    }
    
    public int getUpgradeCost(String upgradeName) {
        int level = getUpgradeLevel(upgradeName);
        
        if (upgradeName.equals("Size")) {
            if (level >= SIZE_LEVELS.length) return -1;
            return SIZE_COSTS[level];
        }
        
        if (upgradeName.startsWith("Weapon_")) {
            if (level >= MAX_WEAPON_LEVEL) return -1;
            return 50 + (level * 25);
        }
        
        if (upgradeName.startsWith("Stat_")) {
            if (level >= MAX_STAT_LEVEL) return -1;
            return 30 + (level * 15);
        }
        
        if (upgradeName.startsWith("Ability_")) {
            if (level >= MAX_ABILITY_LEVEL) return -1;
            return 40 + (level * 20);
        }
        
        return -1;
    }
    
    public boolean canAfford(String upgradeName) {
        int cost = getUpgradeCost(upgradeName);
        return cost > 0 && currentPoints >= cost;
    }
    
    public boolean purchase(String upgradeName) {
        if (!canAfford(upgradeName)) {
            return false;
        }
        
        int cost = getUpgradeCost(upgradeName);
        currentPoints -= cost;
        upgradeLevels.put(upgradeName, getUpgradeLevel(upgradeName) + 1);
        return true;
    }
    
    public int getSizeLevel() {
        return upgradeLevels.get("Size");
    }
    
    public int getSizeMultiplier() {
        return SIZE_LEVELS[getSizeLevel() - 1];
    }
    
    public int getWeaponLevel(String weapon) {
        return upgradeLevels.getOrDefault("Weapon_" + weapon, 0);
    }
    
    public int getStatLevel(String stat) {
        return upgradeLevels.getOrDefault("Stat_" + stat, 0);
    }
    
    public int getAbilityLevel(String ability) {
        return upgradeLevels.getOrDefault("Ability_" + ability, 0);
    }
    
    public float getStatMultiplier(String stat) {
        int level = getStatLevel(stat);
        return 1.0f + (level * 0.1f);
    }
    
    public int getMaxUpgradeLevel(String upgradeName) {
        if (upgradeName.equals("Size")) return SIZE_LEVELS.length;
        if (upgradeName.startsWith("Weapon_")) return MAX_WEAPON_LEVEL;
        if (upgradeName.startsWith("Stat_")) return MAX_STAT_LEVEL;
        if (upgradeName.startsWith("Ability_")) return MAX_ABILITY_LEVEL;
        return 0;
    }
    
    public boolean isMaxed(String upgradeName) {
        return getUpgradeLevel(upgradeName) >= getMaxUpgradeLevel(upgradeName);
    }
    
    public int getTotalUpgradePoints() {
        int total = 0;
        total += upgradeLevels.get("Size") - 1;
        for (String weapon : WEAPONS) {
            total += getWeaponLevel(weapon);
        }
        for (String stat : STAT_UPGRADES) {
            total += getStatLevel(stat);
        }
        for (String ability : ABILITY_UPGRADES) {
            total += getAbilityLevel(ability);
        }
        return total;
    }
    
    public void resetUpgrades() {
        int refundPoints = 0;
        for (Map.Entry<String, Integer> entry : upgradeLevels.entrySet()) {
            if (!entry.getKey().equals("Size")) {
                refundPoints += entry.getValue() * 50;
            }
        }
        initializeUpgrades();
        upgradeLevels.put("Size", 1);
        currentPoints += refundPoints;
    }
}