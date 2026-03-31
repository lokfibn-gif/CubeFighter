package com.cubefighter.gamemodes;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class EndlessMode {
    public static final int BOSS_WAVE_INTERVAL = 10;
    public static final int PRESTIGE_WAVE = 100;
    public static final float BASE_DIFFICULTY = 1.0f;
    public static final float DIFFICULTY_INCREMENT = 0.1f;

    private int currentWave;
    private float difficulty;
    private int enemiesPerWave;
    private int totalEnemiesKilled;
    private int totalWavesCompleted;
    private long sessionStartTime;
    private int prestigeLevel;
    private EndlessHighScore highScore;

    public static class EndlessHighScore {
        public int wave;
        public int score;
        public long timestamp;
        public int prestigeLevel;
        public int enemiesKilled;

        public EndlessHighScore() {
            this.wave = 0;
            this.score = 0;
            this.timestamp = 0;
            this.prestigeLevel = 0;
            this.enemiesKilled = 0;
        }

        public void update(int wave, int score, int prestigeLevel, int enemiesKilled) {
            if (score > this.score) {
                this.wave = wave;
                this.score = score;
                this.timestamp = System.currentTimeMillis();
                this.prestigeLevel = prestigeLevel;
                this.enemiesKilled = enemiesKilled;
            }
        }

        public boolean isNewRecord(int score) {
            return score > this.score;
        }
    }

    public EndlessMode() {
        this.currentWave = 1;
        this.difficulty = BASE_DIFFICULTY;
        this.enemiesPerWave = 5;
        this.totalEnemiesKilled = 0;
        this.totalWavesCompleted = 0;
        this.sessionStartTime = System.currentTimeMillis();
        this.prestigeLevel = 0;
        this.highScore = new EndlessHighScore();
    }

    public void startNewSession() {
        currentWave = 1;
        difficulty = BASE_DIFFICULTY;
        enemiesPerWave = 5;
        totalEnemiesKilled = 0;
        totalWavesCompleted = 0;
        sessionStartTime = System.currentTimeMillis();
    }

    public WaveConfig generateWave() {
        WaveConfig config = new WaveConfig();
        config.waveNumber = currentWave;
        config.enemyCount = calculateEnemyCount();
        config.difficulty = difficulty;
        config.isBossWave = isBossWave();
        config.bossType = config.isBossWave ? getBossTypeForWave(currentWave) : LevelManager.BossType.NONE;
        config.spawnDelay = calculateSpawnDelay();
        config.eliteChance = calculateEliteChance();
        config.specialEnemyChance = calculateSpecialEnemyChance();

        return config;
    }

    private int calculateEnemyCount() {
        int baseEnemies = enemiesPerWave;
        int waveBonus = currentWave / 5;
        int prestigeBonus = prestigeLevel * 3;
        return baseEnemies + waveBonus + prestigeBonus;
    }

    private float calculateSpawnDelay() {
        float baseDelay = 1.5f;
        float reduction = (currentWave - 1) * 0.02f;
        float prestigePenalty = prestigeLevel * 0.1f;
        return Math.max(0.5f, baseDelay - reduction + prestigePenalty);
    }

    private float calculateEliteChance() {
        float baseChance = 0.05f;
        float waveIncrease = currentWave * 0.005f;
        float prestigeIncrease = prestigeLevel * 0.02f;
        return Math.min(0.5f, baseChance + waveIncrease + prestigeIncrease);
    }

    private float calculateSpecialEnemyChance() {
        if (currentWave < 10) return 0;
        float baseChance = 0.03f;
        float waveIncrease = (currentWave - 10) * 0.002f;
        return Math.min(0.3f, baseChance + waveIncrease);
    }

    public boolean isBossWave() {
        return currentWave % BOSS_WAVE_INTERVAL == 0;
    }

    private LevelManager.BossType getBossTypeForWave(int wave) {
        int bossIndex = (wave / BOSS_WAVE_INTERVAL - 1) % 10;
        LevelManager.BossType[] bosses = {
                LevelManager.BossType.GUARDIAN_CUBE,
                LevelManager.BossType.SHADOW_MASTER,
                LevelManager.BossType.FIRE_LORD,
                LevelManager.BossType.ICE_QUEEN,
                LevelManager.BossType.MECH_TITAN,
                LevelManager.BossType.VOID_KING,
                LevelManager.BossType.CRYSTAL_DRAGON,
                LevelManager.BossType.TIME_WARP,
                LevelManager.BossType.CHAOS_CUBE,
                LevelManager.BossType.CHAOS_CUBE
        };
        return bosses[bossIndex];
    }

    public void waveCompleted(int enemiesKilled, int score) {
        totalEnemiesKilled += enemiesKilled;
        totalWavesCompleted++;

        boolean canPrestige = currentWave >= PRESTIGE_WAVE;
        if (canPrestige && decisionPrestige()) {
            handlePrestige(score);
        } else {
            currentWave++;
            difficulty += DIFFICULTY_INCREMENT;
            enemiesPerWave += 1;
        }

        updateHighScore(score);
    }

    private boolean decisionPrestige() {
        return currentWave == PRESTIGE_WAVE ||
                (currentWave > PRESTIGE_WAVE && currentWave % 50 == 0);
    }

    private void handlePrestige(int currentScore) {
        prestigeLevel++;
        int savedWave = currentWave;
        currentWave = 1;
        difficulty = BASE_DIFFICULTY + (prestigeLevel * 0.5f);
        enemiesPerWave = 5 + prestigeLevel * 2;
        enemiesPerWave = 5;
    }

    private void updateHighScore(int score) {
        highScore.update(currentWave, score, prestigeLevel, totalEnemiesKilled);
    }

    public void waveFailed(int enemiesKilled, int score) {
        totalEnemiesKilled += enemiesKilled;
        updateHighScore(score);
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public float getDifficulty() {
        return difficulty;
    }

    public int getPrestigeLevel() {
        return prestigeLevel;
    }

    public int getTotalEnemiesKilled() {
        return totalEnemiesKilled;
    }

    public int getTotalWavesCompleted() {
        return totalWavesCompleted;
    }

    public EndlessHighScore getHighScore() {
        return highScore;
    }

    public long getSessionTime() {
        return System.currentTimeMillis() - sessionStartTime;
    }

    public float getScoreMultiplier() {
        float base = 1.0f;
        float waveBonus = currentWave * 0.01f;
        float prestigeBonus = prestigeLevel * 0.5f;
        return base + waveBonus + prestigeBonus;
    }

    public String getDifficultyName() {
        if (difficulty < 1.5f) return "Easy";
        if (difficulty < 2.0f) return "Normal";
        if (difficulty < 3.0f) return "Hard";
        if (difficulty < 4.0f) return "Extreme";
        return "Nightmare";
    }

    public String getPrestigeRank() {
        switch (prestigeLevel) {
            case 0: return "Bronze";
            case 1: return "Silver";
            case 2: return "Gold";
            case 3: return "Platinum";
            case 4: return "Diamond";
            default: return "Master";
        }
    }

    public static class WaveConfig {
        public int waveNumber;
        public int enemyCount;
        public float difficulty;
        public boolean isBossWave;
        public LevelManager.BossType bossType;
        public float spawnDelay;
        public float eliteChance;
        public float specialEnemyChance;

        public int getExpectedScore() {
            int baseScore = enemyCount * 100;
            int difficultyMultiplier = (int) (difficulty * 10);
            int bossBonus = isBossWave ? 1000 : 0;
            return baseScore * difficultyMultiplier + bossBonus;
        }
    }

    public Json toJson() {
        Json json = new Json();
        return json;
    }

    public void loadFromJson(JsonValue data) {
        if (data == null) return;
        highScore.wave = data.getInt("wave", 0);
        highScore.score = data.getInt("score", 0);
        highScore.prestigeLevel = data.getInt("prestigeLevel", 0);
        highScore.enemiesKilled = data.getInt("enemiesKilled", 0);
        highScore.timestamp = data.getLong("timestamp", 0);
    }
}