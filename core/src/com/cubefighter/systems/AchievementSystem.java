package com.cubefighter.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class AchievementSystem {
    
    public enum Achievement {
        FIRST_BLOOD("First Blood", "Defeat your first enemy", 0, 1),
        COMBO_MASTER("Combo Master", "Achieve a x10 combo", 0, 1),
        BOSS_SLAYER("Boss Slayer", "Defeat all bosses", 0, 4),
        SIZE_MAX("Size Max", "Reach maximum size (5x5)", 0, 1),
        WEAPON_MASTER("Weapon Master", "Upgrade all weapons", 0, 1),
        SURVIVOR("Survivor", "Complete a wave without taking damage", 0, 1),
        SPEED_RUN("Speed Run", "Complete a level in under 2 minutes", 0, 1);
        
        public final String name;
        public final String description;
        public final int currentProgress;
        public final int targetProgress;
        
        Achievement(String name, String description, int currentProgress, int targetProgress) {
            this.name = name;
            this.description = description;
            this.currentProgress = currentProgress;
            this.targetProgress = targetProgress;
        }
    }
    
    private Map<Achievement, Integer> progress;
    private List<Achievement> unlockedAchievements;
    private List<AchievementUnlockListener> listeners;
    private Preferences preferences;
    
    public interface AchievementUnlockListener {
        void onAchievementUnlocked(Achievement achievement);
        void onProgressUpdated(Achievement achievement, int progress, int target);
    }
    
    public AchievementSystem() {
        progress = new HashMap<Achievement, Integer>();
        unlockedAchievements = new ArrayList<Achievement>();
        listeners = new ArrayList<AchievementUnlockListener>();
        preferences = Gdx.app.getPreferences("CubeFighterAchievements");
        
        for (Achievement achievement : Achievement.values()) {
            progress.put(achievement, 0);
        }
        
        loadAchievements();
    }
    
    public void addListener(AchievementUnlockListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(AchievementUnlockListener listener) {
        listeners.remove(listener);
    }
    
    public void updateProgress(Achievement achievement, int amount) {
        if (isUnlocked(achievement)) return;
        
        int current = progress.get(achievement);
        int newProgress = Math.min(current + amount, achievement.targetProgress);
        progress.put(achievement, newProgress);
        
        for (AchievementUnlockListener listener : listeners) {
            listener.onProgressUpdated(achievement, newProgress, achievement.targetProgress);
        }
        
        if (newProgress >= achievement.targetProgress) {
            unlockAchievement(achievement);
        }
    }
    
    public void setProgress(Achievement achievement, int value) {
        if (isUnlocked(achievement)) return;
        
        int newProgress = Math.min(value, achievement.targetProgress);
        progress.put(achievement, newProgress);
        
        for (AchievementUnlockListener listener : listeners) {
            listener.onProgressUpdated(achievement, newProgress, achievement.targetProgress);
        }
        
        if (newProgress >= achievement.targetProgress) {
            unlockAchievement(achievement);
        }
    }
    
    private void unlockAchievement(Achievement achievement) {
        if (!unlockedAchievements.contains(achievement)) {
            unlockedAchievements.add(achievement);
            
            for (AchievementUnlockListener listener : listeners) {
                listener.onAchievementUnlocked(achievement);
            }
            
            saveAchievements();
        }
    }
    
    public boolean isUnlocked(Achievement achievement) {
        return unlockedAchievements.contains(achievement);
    }
    
    public int getProgress(Achievement achievement) {
        return progress.get(achievement);
    }
    
    public float getProgressPercentage(Achievement achievement) {
        return (float) progress.get(achievement) / achievement.targetProgress;
    }
    
    public List<Achievement> getUnlockedAchievements() {
        return new ArrayList<Achievement>(unlockedAchievements);
    }
    
    public int getTotalUnlockedCount() {
        return unlockedAchievements.size();
    }
    
    public int getTotalAchievementCount() {
        return Achievement.values().length;
    }
    
    public float getTotalCompletionPercentage() {
        return (float) getTotalUnlockedCount() / getTotalAchievementCount() * 100;
    }
    
    public void saveAchievements() {
        for (Achievement achievement : Achievement.values()) {
            preferences.putInteger(achievement.name() + "_progress", progress.get(achievement));
            preferences.putBoolean(achievement.name() + "_unlocked", unlockedAchievements.contains(achievement));
        }
        preferences.flush();
    }
    
    public void loadAchievements() {
        for (Achievement achievement : Achievement.values()) {
            int prog = preferences.getInteger(achievement.name() + "_progress", 0);
            boolean unlocked = preferences.getBoolean(achievement.name() + "_unlocked", false);
            
            progress.put(achievement, prog);
            if (unlocked && !unlockedAchievements.contains(achievement)) {
                unlockedAchievements.add(achievement);
            }
        }
    }
    
    public void resetAchievements() {
        progress.clear();
        unlockedAchievements.clear();
        
        for (Achievement achievement : Achievement.values()) {
            progress.put(achievement, 0);
        }
        
        preferences.clear();
        preferences.flush();
    }
    
    public void notifyFirstKill() {
        updateProgress(Achievement.FIRST_BLOOD, 1);
    }
    
    public void notifyComboReached(int comboCount) {
        if (comboCount >= 10) {
            updateProgress(Achievement.COMBO_MASTER, 1);
        }
    }
    
    public void notifyBossDefeated(int bossType) {
        updateProgress(Achievement.BOSS_SLAYER, 1);
    }
    
    public void notifyMaxSizeReached() {
        updateProgress(Achievement.SIZE_MAX, 1);
    }
    
    public void notifyAllWeaponsUpgraded() {
        updateProgress(Achievement.WEAPON_MASTER, 1);
    }
    
    public void notifyWaveCompletedNoDamage() {
        updateProgress(Achievement.SURVIVOR, 1);
    }
    
    public void notifyLevelCompletedUnderTime(float timeSeconds) {
        if (timeSeconds < 120f) {
            updateProgress(Achievement.SPEED_RUN, 1);
        }
    }
}