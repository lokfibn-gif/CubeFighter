package com.cubefighter.gamemodes;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

public class LevelManager {
    public static final int MAX_LEVELS = 100;
    public static final int LEVELS_PER_WORLD = 10;
    public static final int WORLDS = 10;

    public enum ArenaTheme {
        NEON_CITY("Neon City", "city"),
        DESERT_RUINS("Desert Ruins", "desert"),
        ICE_FORTRESS("Ice Fortress", "ice"),
        VOLCANO_CORE("Volcano Core", "volcano"),
        CRYSTAL_CAVES("Crystal Caves", "crystal"),
        SKY_TEMPLE("Sky Temple", "sky"),
        SHADOW_REALM("Shadow Realm", "shadow"),
        CYBER_GRID("Cyber Grid", "cyber"),
        ANCIENT_TOMB("Ancient Tomb", "tomb"),
        VOID_NEXUS("Void Nexus", "void");

        private final String displayName;
        private final String texturePrefix;

        ArenaTheme(String displayName, String texturePrefix) {
            this.displayName = displayName;
            this.texturePrefix = texturePrefix;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getTexturePrefix() {
            return texturePrefix;
        }
    }

    public enum BossType {
        NONE,
        GUARDIAN_CUBE,
        SHADOW_MASTER,
        FIRE_LORD,
        ICE_QUEEN,
        MECH_TITAN,
        VOID_KING,
        CRYSTAL_DRAGON,
        TIME_WARP,
        CHAOS_CUBE
    }

    public static class LevelData {
        public final int levelNumber;
        public final int worldNumber;
        public final int enemyCount;
        public final int waveCount;
        public final BossType bossType;
        public final float difficulty;
        public final ArenaTheme theme;
        public final int unlockRequirement;
        public final int starThresholds[];
        public boolean unlocked;
        public boolean completed;
        public int starsEarned;
        public int highScore;

        public LevelData(int levelNumber, int worldNumber, int enemyCount, int waveCount,
                         BossType bossType, float difficulty, ArenaTheme theme,
                         int unlockRequirement) {
            this.levelNumber = levelNumber;
            this.worldNumber = worldNumber;
            this.enemyCount = enemyCount;
            this.waveCount = waveCount;
            this.bossType = bossType;
            this.difficulty = difficulty;
            this.theme = theme;
            this.unlockRequirement = unlockRequirement;
            this.starThresholds = new int[3];
            this.unlocked = levelNumber == 1;
            this.completed = false;
            this.starsEarned = 0;
            this.highScore = 0;
            calculateStarThresholds();
        }

        private void calculateStarThresholds() {
            int baseScore = (int) (enemyCount * 100 * difficulty);
            starThresholds[0] = baseScore;
            starThresholds[1] = (int) (baseScore * 1.5f);
            starThresholds[2] = (int) (baseScore * 2.0f);
        }

        public int calculateStars(int score, long completionTime, boolean noDamage, boolean perfectCombo) {
            int stars = 0;
            if (score >= starThresholds[0]) stars++;
            if (noDamage) stars++;
            if (perfectCombo) stars++;
            return Math.min(stars, 3);
        }

        public boolean isBossLevel() {
            return bossType != BossType.NONE;
        }
    }

    public enum StarCondition {
        SPEED("Complete under time limit"),
        NO_DAMAGE("Take no damage"),
        PERFECT_COMBO("Achieve perfect combo chain");

        private final String description;

        StarCondition(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private IntMap<LevelData> levels;
    private int currentLevel;
    private int highestUnlockedLevel;
    private int totalStarsEarned;

    public LevelManager() {
        levels = new IntMap<>(MAX_LEVELS);
        currentLevel = 1;
        highestUnlockedLevel = 1;
        totalStarsEarned = 0;
        initializeLevels();
    }

    private void initializeLevels() {
        for (int i = 1; i <= MAX_LEVELS; i++) {
            LevelData data = createLevelData(i);
            levels.put(i, data);
        }
    }

    private LevelData createLevelData(int levelNumber) {
        int worldNumber = ((levelNumber - 1) / LEVELS_PER_WORLD) + 1;
        int levelInWorld = ((levelNumber - 1) % LEVELS_PER_WORLD) + 1;

        int baseEnemyCount = 5 + (levelNumber / 5);
        int waveCount = 3 + (levelNumber / 20);
        float difficulty = 1.0f + (levelNumber * 0.02f);

        BossType bossType = BossType.NONE;
        if (levelInWorld == LEVELS_PER_WORLD) {
            bossType = getBossForWorld(worldNumber);
        }

        ArenaTheme theme = getWorldTheme(worldNumber);
        int unlockRequirement = levelNumber == 1 ? 0 : levelNumber - 1;

        return new LevelData(levelNumber, worldNumber, baseEnemyCount, waveCount,
                bossType, difficulty, theme, unlockRequirement);
    }

    private BossType getBossForWorld(int worldNumber) {
        switch (worldNumber) {
            case 1: return BossType.GUARDIAN_CUBE;
            case 2: return BossType.SHADOW_MASTER;
            case 3: return BossType.FIRE_LORD;
            case 4: return BossType.ICE_QUEEN;
            case 5: return BossType.MECH_TITAN;
            case 6: return BossType.CRYSTAL_DRAGON;
            case 7: return BossType.VOID_KING;
            case 8: return BossType.TIME_WARP;
            case 9: return BossType.CHAOS_CUBE;
            case 10: return BossType.CHAOS_CUBE;
            default: return BossType.GUARDIAN_CUBE;
        }
    }

    private ArenaTheme getWorldTheme(int worldNumber) {
        return ArenaTheme.values()[worldNumber - 1];
    }

    public LevelData getLevelData(int levelNumber) {
        return levels.get(levelNumber);
    }

    public LevelData getCurrentLevelData() {
        return levels.get(currentLevel);
    }

    public void setCurrentLevel(int levelNumber) {
        if (levelNumber >= 1 && levelNumber <= MAX_LEVELS) {
            this.currentLevel = levelNumber;
        }
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getHighestUnlockedLevel() {
        return highestUnlockedLevel;
    }

    public boolean isLevelUnlocked(int levelNumber) {
        LevelData data = levels.get(levelNumber);
        return data != null && data.unlocked;
    }

    public boolean canUnlockLevel(int levelNumber) {
        if (levelNumber == 1) return true;
        LevelData previousLevel = levels.get(levelNumber - 1);
        return previousLevel != null && previousLevel.completed;
    }

    public boolean levelComplete(int levelNumber, int score, long completionTime,
                                  boolean noDamage, boolean perfectCombo) {
        LevelData data = levels.get(levelNumber);
        if (data == null) return false;

        int stars = data.calculateStars(score, completionTime, noDamage, perfectCombo);
        data.starsEarned = Math.max(data.starsEarned, stars);
        data.completed = true;
        data.highScore = Math.max(data.highScore, score);

        totalStarsEarned = 0;
        for (int i = 1; i <= MAX_LEVELS; i++) {
            LevelData level = levels.get(i);
            if (level != null) {
                totalStarsEarned += level.starsEarned;
            }
        }

        if (levelNumber + 1 <= MAX_LEVELS) {
            LevelData nextLevel = levels.get(levelNumber + 1);
            if (nextLevel != null && !nextLevel.unlocked) {
                nextLevel.unlocked = true;
                highestUnlockedLevel = levelNumber + 1;
            }
        }

        return stars >= 1;
    }

    public void levelFailed(int levelNumber, int score) {
        LevelData data = levels.get(levelNumber);
        if (data != null) {
            data.highScore = Math.max(data.highScore, score);
        }
    }

    public Array<LevelData> getWorldLevels(int worldNumber) {
        Array<LevelData> worldLevels = new Array<>();
        int startLevel = (worldNumber - 1) * LEVELS_PER_WORLD + 1;
        int endLevel = startLevel + LEVELS_PER_WORLD - 1;

        for (int i = startLevel; i <= endLevel; i++) {
            LevelData data = levels.get(i);
            if (data != null) {
                worldLevels.add(data);
            }
        }
        return worldLevels;
    }

    public int getTotalStarsEarned() {
        return totalStarsEarned;
    }

    public int getMaximumStars() {
        return MAX_LEVELS * 3;
    }

    public float getProgressPercentage() {
        int completed = 0;
        for (int i = 1; i <= MAX_LEVELS; i++) {
            LevelData data = levels.get(i);
            if (data != null && data.completed) {
                completed++;
            }
        }
        return (completed / (float) MAX_LEVELS) * 100f;
    }

    public void resetProgress() {
        for (int i = 1; i <= MAX_LEVELS; i++) {
            LevelData data = levels.get(i);
            if (data != null) {
                data.completed = false;
                data.starsEarned = 0;
                data.highScore = 0;
                data.unlocked = (i == 1);
            }
        }
        currentLevel = 1;
        highestUnlockedLevel = 1;
        totalStarsEarned = 0;
    }

    public void advanceToNextLevel() {
        if (currentLevel < MAX_LEVELS) {
            currentLevel++;
        }
    }

    public boolean hasNextLevel() {
        return currentLevel < MAX_LEVELS;
    }
}