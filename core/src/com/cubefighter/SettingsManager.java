package com.cubefighter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.cubefighter.save.SaveManager;

public class SettingsManager {
    
    private static SettingsManager instance;
    private Preferences preferences;
    
    private float musicVolume;
    private float sfxVolume;
    private boolean vibrationEnabled;
    private int graphicsQuality;
    private String language;
    
    public static final int QUALITY_LOW = 0;
    public static final int QUALITY_MEDIUM = 1;
    public static final int QUALITY_HIGH = 2;
    
    private static final String PREFS_NAME = "CubeFighterSettings";
    private static final String KEY_MUSIC_VOLUME = "musicVolume";
    private static final String KEY_SFX_VOLUME = "sfxVolume";
    private static final String KEY_VIBRATION = "vibrationEnabled";
    private static final String KEY_GRAPHICS = "graphicsQuality";
    private static final String KEY_LANGUAGE = "language";
    
    private SettingsManager() {
        preferences = Gdx.app.getPreferences(PREFS_NAME);
        loadSettings();
    }
    
    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }
    
    private void loadSettings() {
        musicVolume = preferences.getFloat(KEY_MUSIC_VOLUME, 1.0f);
        sfxVolume = preferences.getFloat(KEY_SFX_VOLUME, 1.0f);
        vibrationEnabled = preferences.getBoolean(KEY_VIBRATION, true);
        graphicsQuality = preferences.getInteger(KEY_GRAPHICS, QUALITY_HIGH);
        language = preferences.getString(KEY_LANGUAGE, "en");
        
        SaveManager.getInstance().getPlayerData().setVibrationEnabled(vibrationEnabled);
    }
    
    public void saveSettings() {
        preferences.putFloat(KEY_MUSIC_VOLUME, musicVolume);
        preferences.putFloat(KEY_SFX_VOLUME, sfxVolume);
        preferences.putBoolean(KEY_VIBRATION, vibrationEnabled);
        preferences.putInteger(KEY_GRAPHICS, graphicsQuality);
        preferences.putString(KEY_LANGUAGE, language);
        preferences.flush();
        
        SaveManager.getInstance().getPlayerData().setVibrationEnabled(vibrationEnabled);
        SaveManager.getInstance().save();
    }
    
    public float getMusicVolume() {
        return musicVolume;
    }
    
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0f, Math.min(1f, volume));
        saveSettings();
    }
    
    public float getSfxVolume() {
        return sfxVolume;
    }
    
    public void setSfxVolume(float volume) {
        this.sfxVolume = Math.max(0f, Math.min(1f, volume));
        saveSettings();
    }
    
    public boolean isVibrationEnabled() {
        return vibrationEnabled;
    }
    
    public void setVibrationEnabled(boolean enabled) {
        this.vibrationEnabled = enabled;
        saveSettings();
        VibrationManager.getInstance().setEnabled(enabled);
    }
    
    public int getGraphicsQuality() {
        return graphicsQuality;
    }
    
    public void setGraphicsQuality(int quality) {
        this.graphicsQuality = Math.max(QUALITY_LOW, Math.min(QUALITY_HIGH, quality));
        saveSettings();
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String lang) {
        this.language = lang;
        saveSettings();
    }
    
    public String getGraphicsQualityName() {
        switch (graphicsQuality) {
            case QUALITY_LOW: return "Low";
            case QUALITY_MEDIUM: return "Medium";
            case QUALITY_HIGH: return "High";
            default: return "High";
        }
    }
    
    public void cycleGraphicsQuality() {
        graphicsQuality = (graphicsQuality + 1) % 3;
        saveSettings();
    }
    
    public void resetProgress() {
        SaveManager.getInstance().reset();
        musicVolume = 1.0f;
        sfxVolume = 1.0f;
        vibrationEnabled = true;
        graphicsQuality = QUALITY_HIGH;
        language = "en";
        saveSettings();
    }
    
    public void restoreDefaults() {
        musicVolume = 1.0f;
        sfxVolume = 1.0f;
        vibrationEnabled = true;
        graphicsQuality = QUALITY_HIGH;
        language = "en";
        saveSettings();
    }
    
    public static void dispose() {
        instance = null;
    }
}