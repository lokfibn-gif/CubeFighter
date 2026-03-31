package com.cubefighter.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Queue;

public class AssetLoader implements Disposable {
    private AssetManager assetManager;
    private LoadProgressCallback progressCallback;
    private Queue<AssetTask> loadQueue;
    private boolean loaded;
    private float totalAssets;
    private float loadedAssets;

    public interface LoadProgressCallback {
        void onProgress(float progress);
        void onComplete();
        void onError(String error);
    }

    private static class AssetTask {
        String path;
        Class type;
        String tag;

        AssetTask(String path, Class type, String tag) {
            this.path = path;
            this.type = type;
            this.tag = tag;
        }
    }

    public AssetLoader() {
        this.assetManager = new AssetManager();
        this.loadQueue = new Queue<>();
        this.loaded = false;
        this.totalAssets = 0;
        this.loadedAssets = 0;
        setupFontLoaders();
    }

    private void setupFontLoaders() {
    }

    public void setProgressCallback(LoadProgressCallback callback) {
        this.progressCallback = callback;
    }

    public void queueAllAssets() {
        queueTextures();
        queueTextureAtlas();
        queueSounds();
        queueMusic();
        queueFonts();
        queueParticles();

        totalAssets = loadQueue.size;
    }

    private void queueTextures() {
        TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;

        queueTexture("textures/player/cube_hero.png", "player", param);
        queueTexture("textures/player/cube_hero_dash.png", "player", param);
        queueTexture("textures/player/cube_hero_attack.png", "player", param);

        for (int i = 1; i <= 5; i++) {
            queueTexture("textures/enemies/enemy_" + i + ".png", "enemies", param);
            queueTexture("textures/enemies/enemy_" + i + "_elite.png", "enemies", param);
        }

        for (int i = 1; i <= 10; i++) {
            queueTexture("textures/bosses/boss_" + i + ".png", "bosses", param);
            queueTexture("textures/bosses/boss_" + i + "_phase2.png", "bosses", param);
        }

        String[] themes = {"city", "desert", "ice", "volcano", "crystal", "sky", "shadow", "cyber", "tomb", "void"};
        for (String theme : themes) {
            queueTexture("textures/arenas/arena_" + theme + ".png", "arenas", param);
            queueTexture("textures/arenas/arena_" + theme + "_bg.png", "arenas", param);
        }

        for (int i = 1; i <= 8; i++) {
            queueTexture("textures/particles/particle_" + i + ".png", "particles", param);
        }

        queueTexture("textures/ui/menu_background.png", "ui", param);
        queueTexture("textures/ui/button_normal.png", "ui", param);
        queueTexture("textures/ui/button_pressed.png", "ui", param);
        queueTexture("textures/ui/button_disabled.png", "ui", param);
        queueTexture("textures/ui/panel_background.png", "ui", param);
        queueTexture("textures/ui/health_bar.png", "ui", param);
        queueTexture("textures/ui/health_bar_fill.png", "ui", param);
        queueTexture("textures/ui/score_display.png", "ui", param);
    }

    private void queueTextureAtlas() {
        assetManager.load("atlases/game_atlas.atlas", TextureAtlas.class);
        loadQueue.addLast(new AssetTask("atlases/game_atlas.atlas", TextureAtlas.class, "atlas"));
    }

    private void queueSounds() {
        String[] soundNames = {
                "hit", "attack", "dash", "jump", "land",
                "enemy_hurt", "enemy_death", "boss_hurt", "boss_death",
                "pickup", "powerup", "level_complete", "level_failed",
                "menu_click", "menu_hover", "star_earned",
                "wave_start", "wave_complete", "combo_break"
        };

        for (String sound : soundNames) {
            queueSound("sounds/" + sound + ".wav", "sfx");
        }
    }

    private void queueMusic() {
        String[] tracks = {
                "main_menu", "level_ambient", "boss_fight",
                "victory", "game_over", "endless"
        };

        for (String track : tracks) {
            queueMusic("music/" + track + ".mp3", "music");
        }
    }

    private void queueFonts() {
    }

    private void queueParticles() {
        String[] effects = {
                "explosion", "hit_spark", "heal", "powerup",
                "smoke", "fire", "ice", "electricity"
        };

        for (String effect : effects) {
            loadQueue.addLast(new AssetTask("particles/" + effect + ".p", com.badlogic.gdx.files.FileHandle.class, "particles"));
        }
    }

    private void queueTexture(String path, String tag, TextureLoader.TextureParameter param) {
        assetManager.load(path, Texture.class, param);
        loadQueue.addLast(new AssetTask(path, Texture.class, tag));
    }

    private void queueSound(String path, String tag) {
        assetManager.load(path, Sound.class);
        loadQueue.addLast(new AssetTask(path, Sound.class, tag));
    }

    private void queueMusic(String path, String tag) {
        assetManager.load(path, Music.class);
        loadQueue.addLast(new AssetTask(path, Music.class, tag));
    }

    private void queueFont(String path, int size, String tag) {
    }

    public boolean update() {
        boolean complete = assetManager.update();
        float progress = assetManager.getProgress();

        if (progressCallback != null) {
            progressCallback.onProgress(progress);
        }

        if (complete && !loaded) {
            loaded = true;
            assignLoadedAssets();
            if (progressCallback != null) {
                progressCallback.onComplete();
            }
        }

        return complete;
    }

    public boolean loadAsync() {
        queueAllAssets();
        assetManager.finishLoading();
        assignLoadedAssets();
        loaded = true;
        return true;
    }

    private void assignLoadedAssets() {
        assignTextures();
        assignTextureAtlas();
        assignSounds();
        assignMusic();
        assignFonts();
    }

    private void assignTextures() {
        Assets.playerTexture = assetManager.get("textures/player/cube_hero.png", Texture.class);
        Assets.playerDashTexture = assetManager.get("textures/player/cube_hero_dash.png", Texture.class);
        Assets.playerAttackTexture = assetManager.get("textures/player/cube_hero_attack.png", Texture.class);

        Assets.enemyTextures = new Texture[5];
        Assets.enemyEliteTextures = new Texture[5];
        for (int i = 0; i < 5; i++) {
            Assets.enemyTextures[i] = assetManager.get("textures/enemies/enemy_" + (i + 1) + ".png", Texture.class);
            Assets.enemyEliteTextures[i] = assetManager.get("textures/enemies/enemy_" + (i + 1) + "_elite.png", Texture.class);
        }

        Assets.bossTextures = new Texture[10];
        Assets.bossPhase2Textures = new Texture[10];
        for (int i = 0; i < 10; i++) {
            Assets.bossTextures[i] = assetManager.get("textures/bosses/boss_" + (i + 1) + ".png", Texture.class);
            Assets.bossPhase2Textures[i] = assetManager.get("textures/bosses/boss_" + (i + 1) + "_phase2.png", Texture.class);
        }

        Assets.arenaBackgrounds = new Texture[10];
        Assets.arenaForegrounds = new Texture[10];
        String[] themes = {"city", "desert", "ice", "volcano", "crystal", "sky", "shadow", "cyber", "tomb", "void"};
        for (int i = 0; i < themes.length; i++) {
            Assets.arenaBackgrounds[i] = assetManager.get("textures/arenas/arena_" + themes[i] + "_bg.png", Texture.class);
            Assets.arenaForegrounds[i] = assetManager.get("textures/arenas/arena_" + themes[i] + ".png", Texture.class);
        }

        Assets.particleTextures = new Texture[8];
        for (int i = 0; i < 8; i++) {
            Assets.particleTextures[i] = assetManager.get("textures/particles/particle_" + (i + 1) + ".png", Texture.class);
        }

        Assets.menuBackground = assetManager.get("textures/ui/menu_background.png", Texture.class);
        Assets.buttonNormal = assetManager.get("textures/ui/button_normal.png", Texture.class);
        Assets.buttonPressed = assetManager.get("textures/ui/button_pressed.png", Texture.class);
        Assets.buttonDisabled = assetManager.get("textures/ui/button_disabled.png", Texture.class);
        Assets.panelBackground = assetManager.get("textures/ui/panel_background.png", Texture.class);
        Assets.healthBarEmpty = assetManager.get("textures/ui/health_bar.png", Texture.class);
        Assets.healthBarFill = assetManager.get("textures/ui/health_bar_fill.png", Texture.class);
        Assets.scoreDisplay = assetManager.get("textures/ui/score_display.png", Texture.class);
    }

    private void assignTextureAtlas() {
        if (assetManager.isLoaded("atlases/game_atlas.atlas")) {
            Assets.gameAtlas = assetManager.get("atlases/game_atlas.atlas", TextureAtlas.class);
        }
    }

    private void assignSounds() {
        Assets.hitSound = assetManager.get("sounds/hit.wav", Sound.class);
        Assets.attackSound = assetManager.get("sounds/attack.wav", Sound.class);
        Assets.dashSound = assetManager.get("sounds/dash.wav", Sound.class);
        Assets.jumpSound = assetManager.get("sounds/jump.wav", Sound.class);
        Assets.landSound = assetManager.get("sounds/land.wav", Sound.class);
        Assets.enemyHurtSound = assetManager.get("sounds/enemy_hurt.wav", Sound.class);
        Assets.enemyDeathSound = assetManager.get("sounds/enemy_death.wav", Sound.class);
        Assets.bossHurtSound = assetManager.get("sounds/boss_hurt.wav", Sound.class);
        Assets.bossDeathSound = assetManager.get("sounds/boss_death.wav", Sound.class);
        Assets.pickupSound = assetManager.get("sounds/pickup.wav", Sound.class);
        Assets.powerupSound = assetManager.get("sounds/powerup.wav", Sound.class);
        Assets.levelCompleteSound = assetManager.get("sounds/level_complete.wav", Sound.class);
        Assets.levelFailedSound = assetManager.get("sounds/level_failed.wav", Sound.class);
        Assets.menuClickSound = assetManager.get("sounds/menu_click.wav", Sound.class);
        Assets.menuHoverSound = assetManager.get("sounds/menu_hover.wav", Sound.class);
        Assets.starEarnedSound = assetManager.get("sounds/star_earned.wav", Sound.class);
        Assets.waveStartSound = assetManager.get("sounds/wave_start.wav", Sound.class);
        Assets.waveCompleteSound = assetManager.get("sounds/wave_complete.wav", Sound.class);
        Assets.comboBreakSound = assetManager.get("sounds/combo_break.wav", Sound.class);
    }

    private void assignMusic() {
        Assets.menuMusic = assetManager.get("music/main_menu.mp3", Music.class);
        Assets.levelMusic = assetManager.get("music/level_ambient.mp3", Music.class);
        Assets.bossMusic = assetManager.get("music/boss_fight.mp3", Music.class);
        Assets.victoryMusic = assetManager.get("music/victory.mp3", Music.class);
        Assets.gameOverMusic = assetManager.get("music/game_over.mp3", Music.class);
        Assets.endlessMusic = assetManager.get("music/endless.mp3", Music.class);
    }

    private void assignFonts() {
    }

    public float getProgress() {
        return assetManager.getProgress() * 100f;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public <T> T getAsset(String path, Class<T> type) {
        return assetManager.get(path, type);
    }

    public boolean isAssetLoaded(String path) {
        return assetManager.isLoaded(path);
    }

    public void unloadAsset(String path) {
        assetManager.unload(path);
    }

    public void unloadAssetsByTag(String tag) {
        Queue<AssetTask> keepQueue = new Queue<>();
        for (AssetTask task : loadQueue) {
            if (!task.tag.equals(tag)) {
                keepQueue.addLast(task);
            } else {
                assetManager.unload(task.path);
            }
        }
        loadQueue = keepQueue;
    }

    public void clearLoadQueue() {
        loadQueue.clear();
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        Assets.dispose();
        loaded = false;
    }
}