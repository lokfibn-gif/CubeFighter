package com.cubefighter.gamemodes;

import com.badlogic.gdx.math.MathUtils;

public enum GameMode {
    ENDLESS("Endless Mode", "Infinite waves with scaling difficulty"),
    LEVELS("Level Mode", "100 levels with unique challenges"),
    BOSS_RUSH("Boss Rush", "Sequential boss fights"),
    DAILY_CHALLENGE("Daily Challenge", "New challenge every day");

    private final String displayName;
    private final String description;

    GameMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static GameMode fromOrdinal(int ordinal) {
        GameMode[] modes = values();
        if (ordinal < 0 || ordinal >= modes.length) {
            return ENDLESS;
        }
        return modes[ordinal];
    }

    public boolean hasFixedProgression() {
        return this == LEVELS;
    }

    public boolean hasUnlimitedWaves() {
        return this == ENDLESS || this == BOSS_RUSH;
    }

    public int getStartingLives() {
        switch (this) {
            case BOSS_RUSH:
                return 5;
            case DAILY_CHALLENGE:
                return 1;
            default:
                return 3;
        }
    }

    public int getBaseScoreMultiplier() {
        switch (this) {
            case BOSS_RUSH:
                return 3;
            case DAILY_CHALLENGE:
                return 5;
            case LEVELS:
                return 2;
            default:
                return 1;
        }
    }

    public String getTimeLimit() {
        switch (this) {
            case DAILY_CHALLENGE:
                return "5:00";
            default:
                return null;
        }
    }
}