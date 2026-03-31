package com.cubefighter.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.cubefighter.save.SaveManager;

public class ScoreSystem {
    private static final String SCORE_PREFS = "CubeFighterScores";
    private static final int COMBO_TIMEOUT = 2000;
    private static final int MAX_COMBO_MULTIPLIER = 10;
    private static final float PERFECT_CLEAR_BONUS = 1.5f;
    
    private int totalScore;
    private int currentLevelScore;
    private int killCount;
    private int bossKillCount;
    private int comboCount;
    private float comboTimer;
    private int highestCombo;
    private float totalTime;
    private int waveCount;
    private int perfectClears;
    
    private int[] highScores;
    private String[] highScoreNames;
    
    public ScoreSystem() {
        totalScore = 0;
        currentLevelScore = 0;
        killCount = 0;
        bossKillCount = 0;
        comboCount = 0;
        comboTimer = 0;
        highestCombo = 0;
        totalTime = 0;
        waveCount = 0;
        perfectClears = 0;
        
        highScores = new int[10];
        highScoreNames = new String[10];
        loadHighScores();
    }
    
    public void update(float delta) {
        if (comboTimer > 0) {
            comboTimer -= delta * 1000;
            if (comboTimer <= 0) {
                resetCombo();
            }
        }
    }
    
    public void addKill() {
        killCount++;
        addCombo();
        updateHighCombo();
    }
    
    public void addBossKill() {
        bossKillCount++;
        addCombo();
        addCombo();
        addCombo();
        updateHighCombo();
    }
    
    private void addCombo() {
        comboCount++;
        comboTimer = COMBO_TIMEOUT;
    }
    
    public void resetCombo() {
        if (comboCount > highestCombo) {
            highestCombo = comboCount;
        }
        comboCount = 0;
        comboTimer = 0;
    }
    
    private void updateHighCombo() {
        if (comboCount > highestCombo) {
            highestCombo = comboCount;
        }
    }
    
    public void addScore(int points) {
        int multipliedPoints = (int) (points * getComboMultiplier());
        totalScore += multipliedPoints;
        currentLevelScore += multipliedPoints;
    }
    
    public void addWaveBonus(int wave) {
        int bonus = wave * 100;
        totalScore += bonus;
        currentLevelScore += bonus;
        waveCount++;
    }
    
    public void addPerfectClearBonus() {
        int bonus = (int) (currentLevelScore * PERFECT_CLEAR_BONUS);
        totalScore += bonus;
        perfectClears++;
    }
    
    public void addTimeBonus(float remainingTime) {
        int bonus = (int) (remainingTime * 10);
        totalScore += bonus;
    }
    
    public void calculateFinalScore(int level, float time) {
        totalTime = time;
        
        int timeBonus = calculateTimeBonus(time);
        int levelBonus = level * 500;
        int killBonus = killCount * 10;
        int comboBonus = highestCombo * 50;
        int perfectClearBonus = perfectClears * 1000;
        
        totalScore += timeBonus + levelBonus + killBonus + comboBonus + perfectClearBonus;
        
        SaveManager.getInstance().getPlayerData().setHighScore(Math.max(
            SaveManager.getInstance().getPlayerData().getHighScore(),
            totalScore
        ));
        SaveManager.getInstance().save();
    }
    
    private int calculateTimeBonus(float time) {
        int minutes = (int) (time / 60);
        int seconds = (int) (time % 60);
        
        if (minutes < 5) {
            return 5000 - minutes * 1000 - seconds * 10;
        } else {
            return Math.max(0, 1000 - (minutes - 5) * 100);
        }
    }
    
    public void onGameOver() {
        checkAndSaveHighScore();
    }
    
    private void checkAndSaveHighScore() {
        if (totalScore > highScores[9]) {
            saveHighScore(totalScore, "Player");
        }
    }
    
    public float getComboMultiplier() {
        return Math.min(1f + (comboCount * 0.25f), MAX_COMBO_MULTIPLIER);
    }
    
    public int getScore() {
        return totalScore;
    }
    
    public int getCurrentLevelScore() {
        return currentLevelScore;
    }
    
    public void resetCurrentLevelScore() {
        currentLevelScore = 0;
    }
    
    public int getKillCount() {
        return killCount;
    }
    
    public int getBossKillCount() {
        return bossKillCount;
    }
    
    public int getComboCount() {
        return comboCount;
    }
    
    public float getComboTimer() {
        return comboTimer;
    }
    
    public int getHighestCombo() {
        return highestCombo;
    }
    
    public int getWaveCount() {
        return waveCount;
    }
    
    public float getTotalTime() {
        return totalTime;
    }
    
    public int getPerfectClears() {
        return perfectClears;
    }
    
    public void reset() {
        totalScore = 0;
        currentLevelScore = 0;
        killCount = 0;
        bossKillCount = 0;
        comboCount = 0;
        comboTimer = 0;
        highestCombo = 0;
        totalTime = 0;
        waveCount = 0;
        perfectClears = 0;
    }
    
    public void loadHighScores() {
        Preferences prefs = Gdx.app.getPreferences(SCORE_PREFS);
        
        for (int i = 0; i < 10; i++) {
            highScores[i] = prefs.getInteger("score" + i, 0);
            highScoreNames[i] = prefs.getString("name" + i, "AAA");
        }
    }
    
    public void saveHighScore(int score, String name) {
        for (int i = 0; i < 10; i++) {
            if (score > highScores[i]) {
                for (int j = 9; j > i; j--) {
                    highScores[j] = highScores[j - 1];
                    highScoreNames[j] = highScoreNames[j - 1];
                }
                highScores[i] = score;
                highScoreNames[i] = name;
                break;
            }
        }
        
        Preferences prefs = Gdx.app.getPreferences(SCORE_PREFS);
        for (int i = 0; i < 10; i++) {
            prefs.putInteger("score" + i, highScores[i]);
            prefs.putString("name" + i, highScoreNames[i]);
        }
        prefs.flush();
    }
    
    public int getHighScore(int rank) {
        if (rank >= 0 && rank < 10) {
            return highScores[rank];
        }
        return 0;
    }
    
    public String getHighScoreName(int rank) {
        if (rank >= 0 && rank < 10) {
            return highScoreNames[rank];
        }
        return "AAA";
    }
    
    public int getTopScore() {
        return highScores[0];
    }
    
    public boolean isTopScore(int score) {
        return score > highScores[0];
    }
    
    public int getScoreRank(int score) {
        for (int i = 0; i < 10; i++) {
            if (score > highScores[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public String getFormattedScore() {
        return formatNumber(totalScore);
    }
    
    private String formatNumber(int number) {
        return String.format("%,d", number);
    }
    
    public String getScoreBreakdown() {
        StringBuilder sb = new StringBuilder();
        sb.append("Final Score: ").append(formatNumber(totalScore)).append("\n");
        sb.append("Kills: ").append(killCount).append("\n");
        sb.append("Boss Kills: ").append(bossKillCount).append("\n");
        sb.append("Highest Combo: ").append(highestCombo).append("x\n");
        sb.append("Waves Cleared: ").append(waveCount).append("\n");
        sb.append("Perfect Clears: ").append(perfectClears).append("\n");
        sb.append("Time: ").append(formatTime(totalTime)).append("\n");
        return sb.toString();
    }
    
    private String formatTime(float seconds) {
        int mins = (int) (seconds / 60);
        int secs = (int) (seconds % 60);
        return String.format("%02d:%02d", mins, secs);
    }
}