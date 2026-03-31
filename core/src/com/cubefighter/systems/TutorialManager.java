package com.cubefighter.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.cubefighter.save.PlayerData;
import com.cubefighter.save.SaveManager;

public class TutorialManager {
    private static final String TUTORIAL_PREFS = "CubeFighterTutorial";
    private static final int TUTORIAL_REWARD_POINTS = 500;
    private static final int TUTORIAL_REWARD_GEMS = 10;
    
    private boolean tutorialCompleted;
    private boolean tutorialSkipped;
    private int currentStep;
    private float stepTimer;
    private boolean stepCompleted;
    
    private String[] tutorialHints;
    private String[] tutorialControls;
    private boolean[] stepRequires;
    
    private Preferences preferences;
    private boolean hintsEnabled;
    private int totalKillsAtStart;
    private int wavesAtStart;
    
    public TutorialManager() {
        preferences = Gdx.app.getPreferences(TUTORIAL_PREFS);
        loadTutorialState();
        initializeTutorialSteps();
        
        PlayerData playerData = SaveManager.getInstance().getPlayerData();
        totalKillsAtStart = playerData.getTotalKills();
        wavesAtStart = playerData.getBestWave();
        
        hintsEnabled = true;
        stepTimer = 0f;
        stepCompleted = false;
    }
    
    private void loadTutorialState() {
        tutorialCompleted = preferences.getBoolean("tutorialCompleted", false);
        tutorialSkipped = preferences.getBoolean("tutorialSkipped", false);
        currentStep = preferences.getInteger("currentStep", 0);
        
        if (isFirstTimePlayer()) {
            tutorialCompleted = false;
            currentStep = 0;
        }
    }
    
    private void initializeTutorialSteps() {
        tutorialHints = new String[] {
            "Welcome to Cube Fighter! Use WASD or Arrow Keys to move.",
            "Click or tap to shoot in that direction.",
            "Press SPACE to dash quickly and avoid enemies.",
            "Press Q to activate your shield and reduce damage.",
            "Press E to heal yourself once available.",
            "Press R to unleash your ultimate attack when charged.",
            "Defeat enemies to level up and grow stronger!",
            "Every 10 waves you'll face tougher enemies.",
            "Combine attacks for combos and higher scores!",
            "Press ESC to pause. Good luck!"
        };
        
        tutorialControls = new String[] {
            "Try moving using WASD or Arrow Keys",
            "Try shooting by clicking or tapping",
            "Try dashing with SPACE",
            "Try your shield with Q",
            "Try healing with E (when available)",
            "Try ultimate with R (when charged)",
            "Defeat enemies to continue",
            "Clear waves to progress",
            "Chain kills for combos",
            "Tutorial complete!"
        };
        
        stepRequires = new boolean[] {
            true,
            true,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        };
    }
    
    private boolean isFirstTimePlayer() {
        PlayerData playerData = SaveManager.getInstance().getPlayerData();
        return playerData.getTotalKills() == 0 && playerData.getBestWave() == 0;
    }
    
    public void update(float delta) {
        if (tutorialCompleted || tutorialSkipped) {
            return;
        }
        
        stepTimer += delta;
        
        if (stepTimer > 0.5f) {
            checkStepCompletion();
        }
    }
    
    private void checkStepCompletion() {
        switch (currentStep) {
            case 0:
                if (hasPlayerMoved()) {
                    advanceStep();
                }
                break;
            case 1:
                if (hasPlayerShot()) {
                    advanceStep();
                }
                break;
            case 2:
                if (hasPlayerDashed()) {
                    advanceStep();
                }
                break;
            case 3:
            case 4:
            case 5:
                advanceStep();
                break;
            case 6:
                PlayerData data = SaveManager.getInstance().getPlayerData();
                if (data.getTotalKills() > totalKillsAtStart) {
                    advanceStep();
                }
                break;
            case 7:
                if (SaveManager.getInstance().getPlayerData().getBestWave() > wavesAtStart) {
                    advanceStep();
                }
                break;
            case 8:
            case 9:
                advanceStep();
                break;
        }
    }
    
    private boolean hasPlayerMoved() {
        return Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W) ||
               Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A) ||
               Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S) ||
               Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D) ||
               Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP) ||
               Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN) ||
               Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT) ||
               Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT);
    }
    
    private boolean hasPlayerShot() {
        return Gdx.input.isTouched() || Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.LEFT);
    }
    
    private boolean hasPlayerDashed() {
        return Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.SPACE);
    }
    
    private void advanceStep() {
        currentStep++;
        stepTimer = 0f;
        
        if (currentStep >= tutorialHints.length) {
            completeTutorial();
        } else {
            saveProgress();
        }
    }
    
    public void skipTutorial() {
        tutorialSkipped = true;
        currentStep = tutorialHints.length;
        saveProgress();
    }
    
    private void completeTutorial() {
        tutorialCompleted = true;
        grantReward();
        saveProgress();
    }
    
    private void grantReward() {
        PlayerData playerData = SaveManager.getInstance().getPlayerData();
        playerData.addPoints(TUTORIAL_REWARD_POINTS);
        playerData.addGems(TUTORIAL_REWARD_GEMS);
        SaveManager.getInstance().save();
    }
    
    private void saveProgress() {
        preferences.putBoolean("tutorialCompleted", tutorialCompleted);
        preferences.putBoolean("tutorialSkipped", tutorialSkipped);
        preferences.putInteger("currentStep", currentStep);
        preferences.flush();
    }
    
    public boolean isTutorialComplete() {
        return tutorialCompleted || tutorialSkipped;
    }
    
    public boolean shouldShowTutorial() {
        return !tutorialCompleted && !tutorialSkipped && isFirstTimePlayer() && hintsEnabled;
    }
    
    public String getCurrentHint() {
        if (currentStep < tutorialHints.length) {
            return tutorialHints[currentStep];
        }
        return null;
    }
    
    public String getCurrentInstruction() {
        if (currentStep < tutorialControls.length) {
            return tutorialControls[currentStep];
        }
        return null;
    }
    
    public int getCurrentStep() {
        return currentStep;
    }
    
    public int getTotalSteps() {
        return tutorialHints.length;
    }
    
    public float getProgress() {
        return (float) currentStep / tutorialHints.length;
    }
    
    public boolean isStepRequiresAction(int step) {
        if (step >= 0 && step < stepRequires.length) {
            return stepRequires[step];
        }
        return false;
    }
    
    public void setHintsEnabled(boolean enabled) {
        hintsEnabled = enabled;
    }
    
    public boolean isHintsEnabled() {
        return hintsEnabled;
    }
    
    public void resetTutorial() {
        tutorialCompleted = false;
        tutorialSkipped = false;
        currentStep = 0;
        stepTimer = 0f;
        saveProgress();
    }
    
    public int getRewardPoints() {
        return TUTORIAL_REWARD_POINTS;
    }
    
    public int getRewardGems() {
        return TUTORIAL_REWARD_GEMS;
    }
}