package com.cubefighter.gamemodes;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

public class BossRushMode {
    public static final int HEAL_BETWEEN_BOSSES = 25;
    public static final int BASE_LIVES = 5;

    private Array<BossFight> bossFightQueue;
    private int currentBossIndex;
    private int lives;
    private int totalScore;
    private int bossesDefeated;
    private int maxCombo;
    private float difficultyScaling;
    private boolean healBetweenBosses;
    private BossRushHighScore highScore;

    public static class BossFight {
        public LevelManager.BossType bossType;
        public float baseHealth;
        public float baseDamage;
        public float speed;
        public int scoreValue;
        public String name;
        public String arenaTheme;

        public BossFight(LevelManager.BossType bossType, int wave) {
            this.bossType = bossType;
            this.baseHealth = calculateHealth(wave);
            this.baseDamage = calculateDamage(wave);
            this.speed = calculateSpeed(wave);
            this.scoreValue = calculateScore(wave);
            this.name = getBossName(bossType);
            this.arenaTheme = getArenaTheme(bossType);
        }

        private float calculateHealth(int wave) {
            float baseHealth = 500f;
            float scaling = 1.0f + (wave * 0.15f);
            return baseHealth * scaling;
        }

        private float calculateDamage(int wave) {
            float baseDamage = 10f;
            float scaling = 1.0f + (wave * 0.05f);
            return baseDamage * scaling;
        }

        private float calculateSpeed(int wave) {
            float baseSpeed = 1.0f;
            float scaling = 1.0f + (wave * 0.02f);
            return baseSpeed * scaling;
        }

        private int calculateScore(int wave) {
            int baseScore = 1000;
            int scaling = (wave / 5) * 500;
            return baseScore + scaling;
        }

        private String getBossName(LevelManager.BossType type) {
            switch (type) {
                case GUARDIAN_CUBE: return "Guardian Cube";
                case SHADOW_MASTER: return "Shadow Master";
                case FIRE_LORD: return "Fire Lord";
                case ICE_QUEEN: return "Ice Queen";
                case MECH_TITAN: return "Mech Titan";
                case VOID_KING: return "Void King";
                case CRYSTAL_DRAGON: return "Crystal Dragon";
                case TIME_WARP: return "Time Warp";
                case CHAOS_CUBE: return "Chaos Cube";
                default: return "Unknown Boss";
            }
        }

        private String getArenaTheme(LevelManager.BossType type) {
            switch (type) {
                case GUARDIAN_CUBE: return "city";
                case SHADOW_MASTER: return "shadow";
                case FIRE_LORD: return "volcano";
                case ICE_QUEEN: return "ice";
                case MECH_TITAN: return "cyber";
                case VOID_KING: return "void";
                case CRYSTAL_DRAGON: return "crystal";
                case TIME_WARP: return "sky";
                case CHAOS_CUBE: return "chaos";
                default: return "default";
            }
        }
    }

    public static class BossRushHighScore {
        public int bossesDefeated;
        public int score;
        public int maxCombo;
        public long timestamp;

        public BossRushHighScore() {
            this.bossesDefeated = 0;
            this.score = 0;
            this.maxCombo = 0;
            this.timestamp = 0;
        }

        public void update(int bossesDefeated, int score, int combo) {
            if (score > this.score) {
                this.bossesDefeated = bossesDefeated;
                this.score = score;
                this.maxCombo = combo;
                this.timestamp = System.currentTimeMillis();
            }
        }
    }

    public BossRushMode() {
        this.bossFightQueue = new Array<>();
        this.currentBossIndex = 0;
        this.lives = BASE_LIVES;
        this.totalScore = 0;
        this.bossesDefeated = 0;
        this.maxCombo = 0;
        this.difficultyScaling = 1.0f;
        this.healBetweenBosses = true;
        this.highScore = new BossRushHighScore();
        initializeBossQueue();
    }

    private void initializeBossQueue() {
        LevelManager.BossType[] bossTypes = LevelManager.BossType.values();
        for (int i = 1; i < bossTypes.length; i++) {
            BossFight fight = new BossFight(bossTypes[i], i);
            bossFightQueue.add(fight);
        }
    }

    public void startNewRun() {
        currentBossIndex = 0;
        lives = BASE_LIVES;
        totalScore = 0;
        bossesDefeated = 0;
        maxCombo = 0;
        difficultyScaling = 1.0f;
        generateAdditionalBosses();
    }

    private void generateAdditionalBosses() {
        int endlessBaseCount = 10;

        for (int i = 0; i < endlessBaseCount; i++) {
            int cycleIndex = i % (LevelManager.BossType.values().length - 1);
            LevelManager.BossType bossType = LevelManager.BossType.values()[cycleIndex + 1];
            BossFight fight = new BossFight(bossType, bossFightQueue.size + i + 1);
            fight.baseHealth *= (1.0f + i * 0.1f);
            fight.baseDamage *= (1.0f + i * 0.05f);
            bossFightQueue.add(fight);
        }
    }

    public BossFight getCurrentBoss() {
        if (currentBossIndex < bossFightQueue.size) {
            return bossFightQueue.get(currentBossIndex);
        }
        return null;
    }

    public BossFight getNextBoss() {
        int nextIndex = currentBossIndex + 1;
        if (nextIndex < bossFightQueue.size) {
            return bossFightQueue.get(nextIndex);
        }
        return generateEndlessBoss();
    }

    private BossFight generateEndlessBoss() {
        int endlessIndex = currentBossIndex - bossFightQueue.size + 1;
        int cycleIndex = endlessIndex % (LevelManager.BossType.values().length - 1);
        LevelManager.BossType bossType = LevelManager.BossType.values()[cycleIndex + 1];
        BossFight endlessBoss = new BossFight(bossType, currentBossIndex + 1);

        endlessBoss.baseHealth *= (1.0f + endlessIndex * 0.2f);
        endlessBoss.baseDamage *= (1.0f + endlessIndex * 0.1f);
        endlessBoss.speed *= (1.0f + endlessIndex * 0.05f);
        endlessBoss.scoreValue += endlessIndex * 200;

        return endlessBoss;
    }

    public int calculateHealAmount() {
        int baseHeal = HEAL_BETWEEN_BOSSES;
        int comboBonus = Math.min(maxCombo, 10) * 2;
        return baseHeal + comboBonus;
    }

    public void bossDefeated(int bossScore, int combo) {
        totalScore += bossScore * difficultyScaling;
        bossesDefeated++;
        maxCombo = Math.max(maxCombo, combo);

        difficultyScaling += 0.05f;

        if (healBetweenBosses && lives > 0) {
        }

        currentBossIndex++;
        highScore.update(bossesDefeated, totalScore, maxCombo);
    }

    public void bossFailed() {
        lives--;
        if (lives <= 0) {
            gameOver();
        }
    }

    private void gameOver() {
        highScore.update(bossesDefeated, totalScore, maxCombo);
    }

    public void revive(int healthRestore) {
        if (lives < BASE_LIVES) {
            lives = Math.min(lives + healthRestore, BASE_LIVES);
        }
    }

    public void setHealBetweenBosses(boolean heal) {
        this.healBetweenBosses = heal;
    }

    public boolean hasLives() {
        return lives > 0;
    }

    public int getLives() {
        return lives;
    }

    public int getBossesDefeated() {
        return bossesDefeated;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public float getDifficultyScaling() {
        return difficultyScaling;
    }

    public BossRushHighScore getHighScore() {
        return highScore;
    }

    public String getDifficultyTier() {
        if (difficultyScaling < 1.3f) return "Normal";
        if (difficultyScaling < 1.6f) return "Hard";
        if (difficultyScaling < 2.0f) return "Extreme";
        if (difficultyScaling < 2.5f) return "Nightmare";
        return "Hell";
    }

    public int getEstimatedBossHealth() {
        BossFight current = getCurrentBoss();
        if (current != null) {
            return (int) (current.baseHealth * difficultyScaling);
        }
        return 0;
    }

    public void generateMoreBosses(int count) {
        for (int i = 0; i < count; i++) {
            int index = bossFightQueue.size + i;
            int cycleIndex = index % (LevelManager.BossType.values().length - 1);
            LevelManager.BossType bossType = LevelManager.BossType.values()[cycleIndex + 1];
            BossFight fight = new BossFight(bossType, index + 1);
            bossFightQueue.add(fight);
        }
    }
}