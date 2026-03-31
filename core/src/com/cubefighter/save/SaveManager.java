package com.cubefighter.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SaveManager {
    private static final String PREFS_NAME = "CubeFighterSave";
    public static SaveManager instance;
    private Preferences preferences;
    private PlayerData playerData;

    private SaveManager() {
        preferences = Gdx.app.getPreferences(PREFS_NAME);
        playerData = new PlayerData();
    }

    public static SaveManager getInstance() {
        if (instance == null) {
            instance = new SaveManager();
        }
        return instance;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public void save() {
        preferences.putInteger("points", playerData.getPoints());
        preferences.putInteger("gems", playerData.getGems());
        preferences.putInteger("sizeLevel", playerData.getSizeLevel());
        preferences.putString("currentWeapon", playerData.getCurrentWeapon());
        preferences.putInteger("weaponLevel", playerData.getWeaponLevel());
        preferences.putInteger("hpLevel", playerData.getHpLevel());
        preferences.putInteger("dmgLevel", playerData.getDmgLevel());
        preferences.putInteger("speedLevel", playerData.getSpeedLevel());
        preferences.putInteger("critLevel", playerData.getCritLevel());
        preferences.putInteger("armorLevel", playerData.getArmorLevel());
        preferences.putInteger("dashLevel", playerData.getDashLevel());
        preferences.putInteger("shieldLevel", playerData.getShieldLevel());
        preferences.putInteger("regenerationLevel", playerData.getRegenerationLevel());
        preferences.putString("unlockedWeapons", String.join(",", playerData.getUnlockedWeapons()));
        preferences.putInteger("bestWave", playerData.getBestWave());
        preferences.putInteger("totalKills", playerData.getTotalKills());
        preferences.putFloat("highScore", playerData.getHighScore());
        preferences.putBoolean("soundEnabled", playerData.isSoundEnabled());
        preferences.putBoolean("musicEnabled", playerData.isMusicEnabled());
        preferences.putBoolean("vibrationEnabled", playerData.isVibrationEnabled());
        preferences.flush();
    }

    public void load() {
        playerData.setPoints(preferences.getInteger("points", 0));
        playerData.setGems(preferences.getInteger("gems", 0));
        playerData.setSizeLevel(preferences.getInteger("sizeLevel", 1));
        playerData.setCurrentWeapon(preferences.getString("currentWeapon", "basic"));
        playerData.setWeaponLevel(preferences.getInteger("weaponLevel", 1));
        playerData.setHpLevel(preferences.getInteger("hpLevel", 1));
        playerData.setDmgLevel(preferences.getInteger("dmgLevel", 1));
        playerData.setSpeedLevel(preferences.getInteger("speedLevel", 1));
        playerData.setCritLevel(preferences.getInteger("critLevel", 1));
        playerData.setArmorLevel(preferences.getInteger("armorLevel", 1));
        playerData.setDashLevel(preferences.getInteger("dashLevel", 0));
        playerData.setShieldLevel(preferences.getInteger("shieldLevel", 0));
        playerData.setRegenerationLevel(preferences.getInteger("regenerationLevel", 0));
        String weaponsStr = preferences.getString("unlockedWeapons", "basic");
        String[] weapons = weaponsStr.split(",");
        playerData.getUnlockedWeapons().clear();
        for (String weapon : weapons) {
            if (!weapon.isEmpty()) {
                playerData.getUnlockedWeapons().add(weapon);
            }
        }
        playerData.setBestWave(preferences.getInteger("bestWave", 0));
        playerData.setTotalKills(preferences.getInteger("totalKills", 0));
        playerData.setHighScore(preferences.getFloat("highScore", 0));
        playerData.setSoundEnabled(preferences.getBoolean("soundEnabled", true));
        playerData.setMusicEnabled(preferences.getBoolean("musicEnabled", true));
        playerData.setVibrationEnabled(preferences.getBoolean("vibrationEnabled", true));
    }

    public void reset() {
        preferences.clear();
        preferences.flush();
        playerData = new PlayerData();
    }

    public static void dispose() {
        instance = null;
    }
}