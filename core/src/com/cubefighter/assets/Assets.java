package com.cubefighter.assets;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {
    public static Texture playerTexture;
    public static Texture playerDashTexture;
    public static Texture playerAttackTexture;

    public static Texture[] enemyTextures;
    public static Texture[] enemyEliteTextures;

    public static Texture[] bossTextures;
    public static Texture[] bossPhase2Textures;

    public static Texture[] arenaBackgrounds;
    public static Texture[] arenaForegrounds;

    public static Texture[] particleTextures;

    public static Texture menuBackground;
    public static Texture buttonNormal;
    public static Texture buttonPressed;
    public static Texture buttonDisabled;
    public static Texture panelBackground;
    public static Texture healthBarEmpty;
    public static Texture healthBarFill;
    public static Texture scoreDisplay;

    public static TextureAtlas gameAtlas;

    public static Sound hitSound;
    public static Sound attackSound;
    public static Sound dashSound;
    public static Sound jumpSound;
    public static Sound landSound;
    public static Sound enemyHurtSound;
    public static Sound enemyDeathSound;
    public static Sound bossHurtSound;
    public static Sound bossDeathSound;
    public static Sound pickupSound;
    public static Sound powerupSound;
    public static Sound levelCompleteSound;
    public static Sound levelFailedSound;
    public static Sound menuClickSound;
    public static Sound menuHoverSound;
    public static Sound starEarnedSound;
    public static Sound waveStartSound;
    public static Sound waveCompleteSound;
    public static Sound comboBreakSound;

    public static Music menuMusic;
    public static Music levelMusic;
    public static Music bossMusic;
    public static Music victoryMusic;
    public static Music gameOverMusic;
    public static Music endlessMusic;

    public static BitmapFont mainFont;
    public static BitmapFont largeFont;
    public static BitmapFont smallFont;
    public static BitmapFont titleFont;

    public static TextureRegion playerRegion;
    public static TextureRegion playerDashRegion;
    public static TextureRegion playerAttackRegion;
    public static TextureRegion[] playerWalkFrames;
    public static TextureRegion[] playerIdleFrames;

    public static TextureRegion[][] enemyRegions;
    public static TextureRegion[][] bossRegions;

    public static void initialize() {
        enemyTextures = new Texture[5];
        enemyEliteTextures = new Texture[5];
        bossTextures = new Texture[10];
        bossPhase2Textures = new Texture[10];
        arenaBackgrounds = new Texture[10];
        arenaForegrounds = new Texture[10];
        particleTextures = new Texture[8];

        enemyRegions = new TextureRegion[5][];
        bossRegions = new TextureRegion[10][];
        playerWalkFrames = new TextureRegion[4];
        playerIdleFrames = new TextureRegion[2];
    }

    public static void createRegions() {
        if (playerTexture != null) {
            playerRegion = new TextureRegion(playerTexture);
        }
        if (playerDashTexture != null) {
            playerDashRegion = new TextureRegion(playerDashTexture);
        }
        if (playerAttackTexture != null) {
            playerAttackRegion = new TextureRegion(playerAttackTexture);
        }

        if (playerTexture != null) {
            int frameWidth = playerTexture.getWidth() / 4;
            int frameHeight = playerTexture.getHeight();
            for (int i = 0; i < 4; i++) {
                playerWalkFrames[i] = new TextureRegion(playerTexture, i * frameWidth, 0, frameWidth, frameHeight);
            }
        }

        for (int i = 0; i < enemyTextures.length; i++) {
            if (enemyTextures[i] != null) {
                enemyRegions[i] = splitTexture(enemyTextures[i], 4, 1);
            }
        }

        for (int i = 0; i < bossTextures.length; i++) {
            if (bossTextures[i] != null) {
                bossRegions[i] = splitTexture(bossTextures[i], 2, 2);
            }
        }
    }

    private static TextureRegion[] splitTexture(Texture texture, int cols, int rows) {
        TextureRegion[][] tmp = TextureRegion.split(texture,
                texture.getWidth() / cols,
                texture.getHeight() / rows);
        TextureRegion[] frames = new TextureRegion[cols * rows];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                frames[index++] = tmp[i][j];
            }
        }
        return frames;
    }

    public static TextureRegion getEnemyRegion(int enemyType, int frame) {
        if (enemyType < 0 || enemyType >= enemyRegions.length) {
            return null;
        }
        if (enemyRegions[enemyType] == null || frame >= enemyRegions[enemyType].length) {
            return null;
        }
        return enemyRegions[enemyType][frame];
    }

    public static TextureRegion getBossRegion(int bossType, int frame) {
        int bossIndex = bossType - 1;
        if (bossIndex < 0 || bossIndex >= bossRegions.length) {
            return null;
        }
        if (bossRegions[bossIndex] == null || frame >= bossRegions[bossIndex].length) {
            return null;
        }
        return bossRegions[bossIndex][frame];
    }

    public static TextureRegion getAtlasRegion(String name) {
        if (gameAtlas == null) {
            return null;
        }
        return gameAtlas.findRegion(name);
    }

    public static TextureRegion getArenaBackground(int themeIndex) {
        if (themeIndex < 0 || themeIndex >= arenaBackgrounds.length) {
            return null;
        }
        return arenaBackgrounds[themeIndex] != null ?
                new TextureRegion(arenaBackgrounds[themeIndex]) : null;
    }

    public static TextureRegion getArenaForeground(int themeIndex) {
        if (themeIndex < 0 || themeIndex >= arenaForegrounds.length) {
            return null;
        }
        return arenaForegrounds[themeIndex] != null ?
                new TextureRegion(arenaForegrounds[themeIndex]) : null;
    }

    public static TextureRegion getParticleTexture(int index) {
        if (index < 0 || index >= particleTextures.length) {
            return null;
        }
        return particleTextures[index] != null ?
                new TextureRegion(particleTextures[index]) : null;
    }

    public static int getEnemyTextureCount() {
        return enemyTextures != null ? enemyTextures.length : 0;
    }

    public static int getBossTextureCount() {
        return bossTextures != null ? bossTextures.length : 0;
    }

    public static int getArenaTextureCount() {
        return arenaBackgrounds != null ? arenaBackgrounds.length : 0;
    }

    public static Sound getSound(String name) {
        switch (name) {
            case "hit": return hitSound;
            case "attack": return attackSound;
            case "dash": return dashSound;
            case "jump": return jumpSound;
            case "land": return landSound;
            case "enemy_hurt": return enemyHurtSound;
            case "enemy_death": return enemyDeathSound;
            case "boss_hurt": return bossHurtSound;
            case "boss_death": return bossDeathSound;
            case "pickup": return pickupSound;
            case "powerup": return powerupSound;
            case "level_complete": return levelCompleteSound;
            case "level_failed": return levelFailedSound;
            case "menu_click": return menuClickSound;
            case "menu_hover": return menuHoverSound;
            case "star_earned": return starEarnedSound;
            case "wave_start": return waveStartSound;
            case "wave_complete": return waveCompleteSound;
            case "combo_break": return comboBreakSound;
            default: return null;
        }
    }

    public static Music getMusic(String name) {
        switch (name) {
            case "menu": return menuMusic;
            case "level": return levelMusic;
            case "boss": return bossMusic;
            case "victory": return victoryMusic;
            case "game_over": return gameOverMusic;
            case "endless": return endlessMusic;
            default: return null;
        }
    }

    public static void dispose() {
        playerTexture = null;
        playerDashTexture = null;
        playerAttackTexture = null;

        enemyTextures = null;
        enemyEliteTextures = null;
        bossTextures = null;
        bossPhase2Textures = null;
        arenaBackgrounds = null;
        arenaForegrounds = null;
        particleTextures = null;

        menuBackground = null;
        buttonNormal = null;
        buttonPressed = null;
        buttonDisabled = null;
        panelBackground = null;
        healthBarEmpty = null;
        healthBarFill = null;
        scoreDisplay = null;

        gameAtlas = null;

        hitSound = null;
        attackSound = null;
        dashSound = null;
        jumpSound = null;
        landSound = null;
        enemyHurtSound = null;
        enemyDeathSound = null;
        bossHurtSound = null;
        bossDeathSound = null;
        pickupSound = null;
        powerupSound = null;
        levelCompleteSound = null;
        levelFailedSound = null;
        menuClickSound = null;
        menuHoverSound = null;
        starEarnedSound = null;
        waveStartSound = null;
        waveCompleteSound = null;
        comboBreakSound = null;

        menuMusic = null;
        levelMusic = null;
        bossMusic = null;
        victoryMusic = null;
        gameOverMusic = null;
        endlessMusic = null;

        mainFont = null;
        largeFont = null;
        smallFont = null;
        titleFont = null;

        playerRegion = null;
        playerDashRegion = null;
        playerAttackRegion = null;
        playerWalkFrames = null;
        playerIdleFrames = null;
        enemyRegions = null;
        bossRegions = null;
    }
}