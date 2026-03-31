package com.cubefighter;

public final class GameConstants {
    
    private GameConstants() {}
    
    public static final float ARENA_WIDTH = 800f;
    public static final float ARENA_HEIGHT = 600f;
    public static final float ARENA_PADDING = 50f;
    
    public static final float PLAYER_BASE_SPEED = 200f;
    public static final float PLAYER_BASE_HEALTH = 100f;
    public static final float PLAYER_BASE_DAMAGE = 10f;
    public static final float PLAYER_BASE_DEFENSE = 0f;
    public static final float PLAYER_SIZE = 32f;
    public static final float PLAYER_ATTACK_RANGE = 60f;
    public static final float PLAYER_ATTACK_COOLDOWN = 0.5f;
    
    public static final float DASH_SPEED = 500f;
    public static final float DASH_DURATION = 0.2f;
    
    public static final float SHIELD_DURATION = 2f;
    public static final float SHIELD_DAMAGE_REDUCTION = 0.75f;
    
    public static final int ENEMY_TYPE_BASIC = 0;
    public static final int ENEMY_TYPE_FAST = 1;
    public static final int ENEMY_TYPE_TANK = 2;
    public static final int ENEMY_TYPE_RANGED = 3;
    public static final int ENEMY_TYPE_EXPLODING = 4;
    
    public static final float[] ENEMY_HEALTH = {
        30f,
        20f,
        80f,
        25f,
        15f
    };
    
    public static final float[] ENEMY_DAMAGE = {
        5f,
        8f,
        15f,
        10f,
        20f
    };
    
    public static final float[] ENEMY_SPEED = {
        80f,
        150f,
        50f,
        60f,
        100f
    };
    
    public static final float[] ENEMY_SIZE = {
        24f,
        18f,
        40f,
        22f,
        20f
    };
    
    public static final float[] ENEMY_SCORE_VALUE = {
        10f,
        15f,
        25f,
        20f,
        30f
    };
    
    public static final int BOSS_TYPE_CUBELORD = 0;
    public static final int BOSS_TYPE_SHADOWCUBE = 1;
    public static final int BOSS_TYPE_MECHACUBE = 2;
    public static final int BOSS_TYPE_ICECUBE = 3;
    
    public static final float[] BOSS_HEALTH = {
        500f,
        400f,
        600f,
        450f
    };
    
    public static final float[] BOSS_DAMAGE = {
        25f,
        20f,
        30f,
        22f
    };
    
    public static final float[] BOSS_SIZE = {
        64f,
        56f,
        72f,
        60f
    };
    
    public static final float[] BOSS_SPEED = {
        100f,
        130f,
        80f,
        110f
    };
    
    public static final int UPGRADE_HEALTH_COST = 100;
    public static final int UPGRADE_DAMAGE_COST = 150;
    public static final int UPGRADE_SPEED_COST = 120;
    public static final int UPGRADE_DEFENSE_COST = 180;
    
    public static final int[] UPGRADE_COSTS = {
        100,
        250,
        500,
        1000,
        2000
    };
    
    public static final float DASH_COOLDOWN = 3f;
    public static final float SHIELD_COOLDOWN = 8f;
    public static final float HEAL_COOLDOWN = 15f;
    public static final float ULTIMATE_COOLDOWN = 60f;
    
    public static final int HEAL_AMOUNT = 30;
    public static final float ULTIMATE_DAMAGE_MULTIPLIER = 5f;
    public static final float ULTIMATE_RANGE = 200f;
    
    public static final int BASE_WAVE_ENEMY_COUNT = 5;
    public static final int MAX_WAVE_ENEMY_COUNT = 50;
    public static final int BOSS_WAVE_INTERVAL = 5;
    
    public static final float WAVE_HEALTH_MULTIPLIER = 1.15f;
    public static final float WAVE_DAMAGE_MULTIPLIER = 1.1f;
    public static final float WAVE_SPEED_MULTIPLIER = 1.05f;
    public static final float WAVE_SPAWN_RATE_MULTIPLIER = 0.95f;
    
    public static final int STARTING_GOLD = 0;
    public static final int GOLD_PER_KILL = 5;
    public static final int GOLD_PER_WAVE = 50;
    public static final int BOSS_GOLD_BONUS = 200;
    
    public static final float PLAYER_MAX_LEVEL = 50f;
    public static final float XP_PER_LEVEL_BASE = 100f;
    public static final float XP_LEVEL_MULTIPLIER = 1.25f;
    
    public static final float POWERUP_DURATION = 10f;
    public static final float POWERUP_SPAWN_CHANCE = 0.1f;
    
    public static final int POWERUP_TYPE_HEALTH = 0;
    public static final int POWERUP_TYPE_DAMAGE = 1;
    public static final int POWERUP_TYPE_SPEED = 2;
    public static final int POWERUP_TYPE_INVINCIBILITY = 3;
    
    public static final float CHARGE_ATTACK_MIN_TIME = 0.5f;
    public static final float CHARGE_ATTACK_MAX_TIME = 1.5f;
    
    public static final float CAMERA_LERP = 0.1f;
    public static final float CAMERA_ZOOM_MIN = 0.5f;
    public static final float CAMERA_ZOOM_MAX = 2f;
    
    public static float getEnemyHealth(int enemyType, int wave) {
        return ENEMY_HEALTH[enemyType] * (float) Math.pow(WAVE_HEALTH_MULTIPLIER, wave - 1);
    }
    
    public static float getEnemyDamage(int enemyType, int wave) {
        return ENEMY_DAMAGE[enemyType] * (float) Math.pow(WAVE_DAMAGE_MULTIPLIER, wave - 1);
    }
    
    public static float getEnemySpeed(int enemyType, int wave) {
        return ENEMY_SPEED[enemyType] * (float) Math.pow(WAVE_SPEED_MULTIPLIER, Math.min(wave - 1, 10));
    }
    
    public static int getWaveEnemyCount(int wave) {
        return (int) Math.min(BASE_WAVE_ENEMY_COUNT + (wave - 1) * 2, MAX_WAVE_ENEMY_COUNT);
    }
    
    public static int getXPRequiredForLevel(int level) {
        return (int) (XP_PER_LEVEL_BASE * Math.pow(XP_LEVEL_MULTIPLIER, level - 1));
    }
    
    public static int getGoldForWave(int wave) {
        return GOLD_PER_WAVE + (wave - 1) * 10;
    }
}