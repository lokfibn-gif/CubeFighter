package com.cubefighter;

import com.cubefighter.entities.Player;

public class GameState {
    
    public enum Screen {
        MENU,
        PLAYING,
        PAUSED,
        UPGRADE,
        GAME_OVER
    }
    
    public enum GameMode {
        ENDLESS,
        LEVEL,
        BOSS_RUSH,
        DAILY
    }
    
    private Screen currentScreen;
    private GameMode gameMode;
    private int currentWave;
    private int currentLevel;
    private PlayerState playerState;
    private WaveState waveState;
    private boolean isPaused;
    private boolean isGameOver;
    private float gameTime;
    private int score;
    
    public static class PlayerState {
        public float x;
        public float y;
        public float health;
        public float maxHealth;
        public int lives;
        public int bombs;
        public float speed;
        public float damage;
        public boolean isInvincible;
        public float invincibilityTimer;
        
        public PlayerState() {
            health = 100;
            maxHealth = 100;
            lives = 3;
            bombs = 3;
            speed = 200f;
            damage = 10f;
            isInvincible = false;
            invincibilityTimer = 0f;
        }
    }
    
    public static class WaveState {
        public int enemiesRemaining;
        public int totalEnemies;
        public float waveTimer;
        public boolean isBossWave;
        public int waveNumber;
        
        public WaveState() {
            enemiesRemaining = 0;
            totalEnemies = 0;
            waveTimer = 0f;
            isBossWave = false;
            waveNumber = 1;
        }
    }
    
    public GameState() {
        currentScreen = Screen.MENU;
        gameMode = GameMode.ENDLESS;
        currentWave = 1;
        currentLevel = 1;
        playerState = new PlayerState();
        waveState = new WaveState();
        isPaused = false;
        isGameOver = false;
        gameTime = 0f;
        score = 0;
    }
    
    public void startGame() {
        currentScreen = Screen.PLAYING;
        currentWave = 1;
        currentLevel = 1;
        isPaused = false;
        isGameOver = false;
        gameTime = 0f;
        score = 0;
        playerState = new PlayerState();
        waveState = new WaveState();
        waveState.waveNumber = 1;
    }
    
    public void startGame(GameMode mode) {
        this.gameMode = mode;
        startGame();
    }
    
    public void pauseGame() {
        if (currentScreen == Screen.PLAYING) {
            currentScreen = Screen.PAUSED;
            isPaused = true;
        }
    }
    
    public void resumeGame() {
        if (currentScreen == Screen.PAUSED) {
            currentScreen = Screen.PLAYING;
            isPaused = false;
        }
    }
    
    public void endGame() {
        currentScreen = Screen.GAME_OVER;
        isGameOver = true;
    }
    
    public void nextWave() {
        currentWave++;
        waveState.waveNumber = currentWave;
        waveState.enemiesRemaining = calculateEnemiesForWave(currentWave);
        waveState.totalEnemies = waveState.enemiesRemaining;
        waveState.isBossWave = (currentWave % 5 == 0);
    }
    
    public void nextLevel() {
        currentLevel++;
    }
    
    public void addScore(int points) {
        score += points;
    }
    
    public void update(float delta) {
        if (!isPaused && currentScreen == Screen.PLAYING) {
            gameTime += delta;
        }
    }
    
    private int calculateEnemiesForWave(int wave) {
        int baseEnemies = 5;
        int enemiesPerWave = 3;
        return baseEnemies + (wave - 1) * enemiesPerWave;
    }
    
    public Screen getCurrentScreen() {
        return currentScreen;
    }
    
    public void setCurrentScreen(Screen screen) {
        this.currentScreen = screen;
    }
    
    public GameMode getGameMode() {
        return gameMode;
    }
    
    public void setGameMode(GameMode mode) {
        this.gameMode = mode;
    }
    
    public int getCurrentWave() {
        return currentWave;
    }
    
    public void setCurrentWave(int wave) {
        this.currentWave = wave;
    }
    
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    public void setCurrentLevel(int level) {
        this.currentLevel = level;
    }
    
    public PlayerState getPlayerState() {
        return playerState;
    }
    
    public WaveState getWaveState() {
        return waveState;
    }
    
    public boolean isPaused() {
        return isPaused;
    }
    
    public boolean isGameOver() {
        return isGameOver;
    }
    
    public float getGameTime() {
        return gameTime;
    }
    
    public int getScore() {
        return score;
    }
}