package com.cubefighter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cubefighter.assets.Assets;
import com.cubefighter.audio.AudioManager;
import com.cubefighter.save.SaveManager;
import com.cubefighter.screens.GameOverScreen;
import com.cubefighter.screens.PauseScreen;
import com.cubefighter.screens.TutorialScreen;
import com.cubefighter.screens.UpgradeShopScreen;
import com.cubefighter.screens.VictoryScreen;

public class CubeFighterGame extends Game {
    public static final float WORLD_WIDTH = 800f;
    public static final float WORLD_HEIGHT = 600f;
    public static final float MIN_WORLD_WIDTH = 400f;
    public static final float MIN_WORLD_HEIGHT = 300f;
    
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private OrthographicCamera camera;
    private Viewport viewport;
    
    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private LoadingScreen loadingScreen;
    private GameOverScreen gameOverScreen;
    private VictoryScreen victoryScreen;
    private UpgradeShopScreen upgradeShopScreen;
    private PauseScreen pauseScreen;
    private TutorialScreen tutorialScreen;
    
    private GameState gameState;
    private GameWorld gameWorld;
    private boolean assetsLoaded;
    private float loadingProgress;
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        
        Assets.initialize();
        gameState = new GameState();
        
        SaveManager.getInstance().load();
        
        loadingScreen = new LoadingScreen(this);
        setScreen(loadingScreen);
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        super.resize(width, height);
    }
    
    @Override
    public void render() {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        super.render();
    }
    
    public void updateLoading() {
        loadingProgress += 0.02f;
        if (loadingProgress >= 1f) {
            assetsLoaded = true;
            loadingProgress = 1f;
        }
    }
    
    public boolean isAssetsLoaded() {
        return assetsLoaded;
    }
    
    public float getLoadingProgress() {
        return loadingProgress;
    }
    
    public void onLoadingComplete() {
        showMenu();
    }
    
    public void startGame() {
        startGame(GameState.GameMode.ENDLESS);
    }
    
    public void startGame(GameState.GameMode mode) {
        gameState.startGame(mode);
        
        if (gameWorld != null) {
            gameWorld.dispose();
        }
        gameWorld = new GameWorld();
        
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        gameScreen = new GameScreen(this, gameWorld, gameState);
        setScreen(gameScreen);
    }
    
    public void showMenu() {
        if (menuScreen == null) {
            menuScreen = new MenuScreen(this);
        }
        setScreen(menuScreen);
    }
    
    public void pauseGame() {
        gameState.pauseGame();
    }
    
    public void resumeGame() {
        gameState.resumeGame();
    }
    
    public void onGameOver() {
        gameState.endGame();
        
        if (gameOverScreen == null) {
            gameOverScreen = new GameOverScreen(this, gameState);
        } else {
            gameOverScreen.updateState(gameState);
        }
        setScreen(gameOverScreen);
    }
    
    public void onVictory() {
        if (victoryScreen == null) {
            victoryScreen = new VictoryScreen(this, gameState);
        } else {
            victoryScreen.updateState(gameState);
        }
        setScreen(victoryScreen);
    }
    
    public void showUpgradeShop() {
        if (upgradeShopScreen != null) {
            upgradeShopScreen.dispose();
        }
        upgradeShopScreen = new UpgradeShopScreen(this);
        setScreen(upgradeShopScreen);
    }
    
    public void showPause(PauseScreen.PauseCallback callback) {
        if (pauseScreen != null) {
            pauseScreen.dispose();
        }
        pauseScreen = new PauseScreen(this, gameState, callback);
        setScreen(pauseScreen);
    }
    
    public void showTutorial() {
        if (tutorialScreen != null) {
            tutorialScreen.dispose();
        }
        tutorialScreen = new TutorialScreen(this);
        setScreen(tutorialScreen);
    }
    
    public void returnToMenu() {
        if (gameScreen != null) {
            gameScreen.dispose();
            gameScreen = null;
        }
        if (gameWorld != null) {
            gameWorld.dispose();
            gameWorld = null;
        }
        showMenu();
    }
    
    @Override
    public void dispose() {
        if (menuScreen != null) {
            menuScreen.dispose();
        }
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        if (loadingScreen != null) {
            loadingScreen.dispose();
        }
        if (gameOverScreen != null) {
            gameOverScreen.dispose();
        }
        if (victoryScreen != null) {
            victoryScreen.dispose();
        }
        if (upgradeShopScreen != null) {
            upgradeShopScreen.dispose();
        }
        if (pauseScreen != null) {
            pauseScreen.dispose();
        }
        if (tutorialScreen != null) {
            tutorialScreen.dispose();
        }
        if (gameWorld != null) {
            gameWorld.dispose();
        }
        
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        Assets.dispose();
        AudioManager.disposeInstance();
        SaveManager.dispose();
    }
    
    @Override
    public void pause() {
        if (gameScreen != null && gameState.getCurrentScreen() == GameState.Screen.PLAYING) {
            gameState.pauseGame();
        }
        SaveManager.getInstance().save();
    }
    
    @Override
    public void resume() {
        SaveManager.getInstance().load();
    }
    
    public SpriteBatch getBatch() { return batch; }
    public ShapeRenderer getShapeRenderer() { return shapeRenderer; }
    public BitmapFont getFont() { return font; }
    public OrthographicCamera getCamera() { return camera; }
    public Viewport getViewport() { return viewport; }
    public GameState getGameState() { return gameState; }
    public GameWorld getGameWorld() { return gameWorld; }
    public GameScreen getGameScreen() { return gameScreen; }
}