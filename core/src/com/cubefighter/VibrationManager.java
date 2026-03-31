package com.cubefighter;

import com.badlogic.gdx.Gdx;
import com.cubefighter.save.SaveManager;

public class VibrationManager {
    
    private static VibrationManager instance;
    private boolean enabled;
    
    private VibrationManager() {
        enabled = true;
    }
    
    public static VibrationManager getInstance() {
        if (instance == null) {
            instance = new VibrationManager();
        }
        return instance;
    }
    
    public void initialize() {
        enabled = SaveManager.getInstance().getPlayerData().isVibrationEnabled();
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        SaveManager.getInstance().getPlayerData().setVibrationEnabled(enabled);
        SaveManager.getInstance().save();
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void vibrate(int milliseconds) {
        if (enabled && Gdx.input != null) {
            Gdx.input.vibrate(milliseconds);
        }
    }
    
    public void vibrateHit() {
        vibrate(50);
    }
    
    public void vibrateCriticalHit() {
        vibrate(100);
    }
    
    public void vibrateUpgrade() {
        vibrate(80);
    }
    
    public void vibrateBossAppear() {
        vibrate(500);
    }
    
    public void vibrateGameOver() {
        vibrate(700);
    }
    
    public void vibrateComboBreak() {
        vibrate(300);
    }
    
    public void vibrateAchievement() {
        vibrate(300);
    }
    
    public void vibratePowerUp() {
        vibrate(100);
    }
    
    public void vibrateCustom(int duration) {
        vibrate(duration);
    }
    
    public static void dispose() {
        instance = null;
    }
}