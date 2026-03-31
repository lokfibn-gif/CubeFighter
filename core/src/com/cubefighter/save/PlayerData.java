package com.cubefighter.save;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    private int points;
    private int gems;
    private int sizeLevel;
    private String currentWeapon;
    private int weaponLevel;
    private int hpLevel;
    private int dmgLevel;
    private int speedLevel;
    private int critLevel;
    private int armorLevel;
    private int dashLevel;
    private int shieldLevel;
    private int regenerationLevel;
    private List<String> unlockedWeapons;
    private int bestWave;
    private int totalKills;
    private float highScore;
    private boolean soundEnabled;
    private boolean musicEnabled;
    private boolean vibrationEnabled;

    public PlayerData() {
        points = 0;
        gems = 0;
        sizeLevel = 1;
        currentWeapon = "basic";
        weaponLevel = 1;
        hpLevel = 1;
        dmgLevel = 1;
        speedLevel = 1;
        critLevel = 1;
        armorLevel = 1;
        dashLevel = 0;
        shieldLevel = 0;
        regenerationLevel = 0;
        unlockedWeapons = new ArrayList<>();
        unlockedWeapons.add("basic");
        bestWave = 0;
        totalKills = 0;
        highScore = 0;
        soundEnabled = true;
        musicEnabled = true;
        vibrationEnabled = true;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addPoints(int amount) {
        this.points += amount;
    }

    public int getGems() {
        return gems;
    }

    public void setGems(int gems) {
        this.gems = gems;
    }

    public void addGems(int amount) {
        this.gems += amount;
    }

    public int getSizeLevel() {
        return sizeLevel;
    }

    public void setSizeLevel(int sizeLevel) {
        this.sizeLevel = Math.max(1, Math.min(5, sizeLevel));
    }

    public String getCurrentWeapon() {
        return currentWeapon;
    }

    public void setCurrentWeapon(String currentWeapon) {
        this.currentWeapon = currentWeapon;
    }

    public int getWeaponLevel() {
        return weaponLevel;
    }

    public void setWeaponLevel(int weaponLevel) {
        this.weaponLevel = Math.max(1, weaponLevel);
    }

    public int getHpLevel() {
        return hpLevel;
    }

    public void setHpLevel(int hpLevel) {
        this.hpLevel = Math.max(1, hpLevel);
    }

    public int getDmgLevel() {
        return dmgLevel;
    }

    public void setDmgLevel(int dmgLevel) {
        this.dmgLevel = Math.max(1, dmgLevel);
    }

    public int getSpeedLevel() {
        return speedLevel;
    }

    public void setSpeedLevel(int speedLevel) {
        this.speedLevel = Math.max(1, speedLevel);
    }

    public int getCritLevel() {
        return critLevel;
    }

    public void setCritLevel(int critLevel) {
        this.critLevel = Math.max(1, critLevel);
    }

    public int getArmorLevel() {
        return armorLevel;
    }

    public void setArmorLevel(int armorLevel) {
        this.armorLevel = Math.max(1, armorLevel);
    }

    public int getDashLevel() {
        return dashLevel;
    }

    public void setDashLevel(int dashLevel) {
        this.dashLevel = Math.max(0, dashLevel);
    }

    public int getShieldLevel() {
        return shieldLevel;
    }

    public void setShieldLevel(int shieldLevel) {
        this.shieldLevel = Math.max(0, shieldLevel);
    }

    public int getRegenerationLevel() {
        return regenerationLevel;
    }

    public void setRegenerationLevel(int regenerationLevel) {
        this.regenerationLevel = Math.max(0, regenerationLevel);
    }

    public List<String> getUnlockedWeapons() {
        return unlockedWeapons;
    }

    public void unlockWeapon(String weapon) {
        if (!unlockedWeapons.contains(weapon)) {
            unlockedWeapons.add(weapon);
        }
    }

    public boolean isWeaponUnlocked(String weapon) {
        return unlockedWeapons.contains(weapon);
    }

    public int getBestWave() {
        return bestWave;
    }

    public void setBestWave(int bestWave) {
        this.bestWave = Math.max(this.bestWave, bestWave);
    }

    public int getTotalKills() {
        return totalKills;
    }

    public void setTotalKills(int totalKills) {
        this.totalKills = totalKills;
    }

    public void addKill() {
        this.totalKills++;
    }

    public float getHighScore() {
        return highScore;
    }

    public void setHighScore(float highScore) {
        this.highScore = Math.max(this.highScore, highScore);
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
    }

    public boolean isVibrationEnabled() {
        return vibrationEnabled;
    }

    public void setVibrationEnabled(boolean vibrationEnabled) {
        this.vibrationEnabled = vibrationEnabled;
    }

    public int getStatLevel(String statName) {
        switch (statName.toLowerCase()) {
            case "hp":
                return hpLevel;
            case "dmg":
                return dmgLevel;
            case "speed":
                return speedLevel;
            case "crit":
                return critLevel;
            case "armor":
                return armorLevel;
            case "dash":
                return dashLevel;
            case "shield":
                return shieldLevel;
            case "regeneration":
                return regenerationLevel;
            default:
                return 0;
        }
    }

    public void setStatLevel(String statName, int level) {
        switch (statName.toLowerCase()) {
            case "hp":
                setHpLevel(level);
                break;
            case "dmg":
                setDmgLevel(level);
                break;
            case "speed":
                setSpeedLevel(level);
                break;
            case "crit":
                setCritLevel(level);
                break;
            case "armor":
                setArmorLevel(level);
                break;
            case "dash":
                setDashLevel(level);
                break;
            case "shield":
                setShieldLevel(level);
                break;
            case "regeneration":
                setRegenerationLevel(level);
                break;
        }
    }
}