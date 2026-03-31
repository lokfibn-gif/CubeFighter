package com.cubefighter.systems;

import com.cubefighter.entities.Enemy;
import com.cubefighter.entities.Boss;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class WaveManager {
    
    private int currentWave;
    private int enemiesRemaining;
    private float spawnTimer;
    private float spawnInterval;
    private int enemiesPerWave;
    private boolean waveActive;
    private Random random;
    
    private static final int BOSS_WAVE_INTERVAL = 10;
    private static final int BASE_ENEMIES_PER_WAVE = 5;
    private static final float BASE_SPAWN_INTERVAL = 2.0f;
    private static final int BASE_POINTS_PER_WAVE = 100;
    private static final float DIFFICULTY_SCALE = 1.15f;
    
    public WaveManager() {
        this.currentWave = 0;
        this.enemiesRemaining = 0;
        this.spawnTimer = 0f;
        this.spawnInterval = BASE_SPAWN_INTERVAL;
        this.enemiesPerWave = BASE_ENEMIES_PER_WAVE;
        this.waveActive = false;
        this.random = new Random();
    }
    
    public void startNextWave() {
        currentWave++;
        waveActive = true;
        
        if (isBossWave()) {
            enemiesRemaining = 1;
            spawnInterval = 0f;
        } else {
            enemiesRemaining = calculateEnemiesForWave();
            spawnInterval = calculateSpawnInterval();
        }
        
        spawnTimer = 0f;
    }
    
    public boolean isBossWave() {
        return currentWave > 0 && currentWave % BOSS_WAVE_INTERVAL == 0;
    }
    
    private int calculateEnemiesForWave() {
        int baseEnemies = BASE_ENEMIES_PER_WAVE + (currentWave / 2);
        return (int)(baseEnemies * Math.pow(DIFFICULTY_SCALE, currentWave / 10.0));
    }
    
    private float calculateSpawnInterval() {
        return Math.max(0.3f, BASE_SPAWN_INTERVAL - (currentWave * 0.05f));
    }
    
    public Enemy spawnEnemy(float deltaTime) {
        if (!waveActive || enemiesRemaining <= 0) {
            return null;
        }
        
        spawnTimer += deltaTime;
        
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f;
            enemiesRemaining--;
            return createEnemy();
        }
        
        return null;
    }
    
    public Boss spawnBoss() {
        if (!isBossWave() || enemiesRemaining <= 0) {
            return null;
        }
        
        enemiesRemaining--;
        return createBoss();
    }
    
    private Enemy createEnemy() {
        float difficulty = getDifficultyMultiplier();
        int hp = (int)(50 * difficulty);
        int damage = (int)(10 * difficulty);
        float speed = 1.0f + (difficulty * 0.1f);
        
        float spawnX = random.nextFloat() * 800;
        float spawnY = random.nextFloat() * 600;
        
        Enemy.EnemyType type = Enemy.EnemyType.values()[random.nextInt(Enemy.EnemyType.values().length)];
        Enemy enemy = new Enemy(type, spawnX, spawnY);
        
        return enemy;
    }
    
    private Boss createBoss() {
        int bossLevel = currentWave / BOSS_WAVE_INTERVAL;
        float difficulty = getDifficultyMultiplier();
        
        float spawnX = 400;
        float spawnY = 300;
        
        Boss.BossType bossType = Boss.BossType.values()[Math.min(bossLevel -1, Boss.BossType.values().length - 1)];
        Boss boss = new Boss(bossType, spawnX, spawnY);
        
        return boss;
    }
    
    public float getDifficultyMultiplier() {
        return (float)Math.pow(DIFFICULTY_SCALE, currentWave);
    }
    
    public int calculateEnemyReward() {
        return BASE_POINTS_PER_WAVE + (currentWave * 10);
    }
    
    public int calculateBossReward() {
        int bossLevel = currentWave / BOSS_WAVE_INTERVAL;
        return BASE_POINTS_PER_WAVE * bossLevel * 5;
    }
    
    public int calculateWaveCompletionReward() {
        return BASE_POINTS_PER_WAVE * currentWave;
    }
    
    public void enemyDefeated() {
        checkWaveComplete();
    }
    
    public boolean checkWaveComplete() {
        if (waveActive && enemiesRemaining <= 0) {
            waveActive = false;
            return true;
        }
        return false;
    }
    
    public boolean isWaveActive() {
        return waveActive;
    }
    
    public int getCurrentWave() {
        return currentWave;
    }
    
    public int getEnemiesRemaining() {
        return enemiesRemaining;
    }
    
    public void resetWaves() {
        currentWave = 0;
        enemiesRemaining = 0;
        waveActive = false;
        spawnTimer = 0f;
    }
    
    public int getWavePhase() {
        if (isBossWave()) return 2;
        if (currentWave % BOSS_WAVE_INTERVAL >= BOSS_WAVE_INTERVAL - 2) return 1;
        return 0;
    }
    
    public float getSpawnProgress() {
        if (!waveActive) return 0f;
        int totalEnemies = isBossWave() ? 1 : calculateEnemiesForWave();
        return 1.0f - ((float)enemiesRemaining / totalEnemies);
    }
}