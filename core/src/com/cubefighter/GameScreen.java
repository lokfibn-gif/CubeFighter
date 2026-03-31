package com.cubefighter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cubefighter.entities.Enemy;
import com.cubefighter.entities.Player;
import com.cubefighter.entities.Boss;
import com.cubefighter.entities.GameObject;
import com.cubefighter.systems.ScoreSystem;
import com.cubefighter.systems.TutorialManager;
import com.cubefighter.systems.WaveManager;
import com.cubefighter.save.SaveManager;

public class GameScreen implements Screen {
    private static final int MAX_LEVELS = 100;
    
    private final CubeFighterGame game;
    private GameWorld gameWorld;
    private GameState gameState;
    private ScoreSystem scoreSystem;
    private TutorialManager tutorialManager;
    private WaveManager waveManager;
    
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    
    private GamePhase currentPhase;
    private float phaseTimer;
    private float gameTimer;
    private float levelStartTime;
    
    private int currentLevel;
    private boolean isPaused;
    private boolean isGameOver;
    private boolean isVictory;
    
    private float playerSpawnX;
    private float playerSpawnY;
    
    private enum GamePhase {
        STARTING,
        PLAYING,
        WAVE_COMPLETE,
        LEVEL_COMPLETE,
        PAUSED,
        GAME_OVER,
        VICTORY
    }
    
    public GameScreen(CubeFighterGame game, GameWorld gameWorld, GameState gameState) {
        this.game = game;
        this.gameWorld = gameWorld;
        this.gameState = gameState;
        
        camera = game.getCamera();
        viewport = game.getViewport();
        shapeRenderer = game.getShapeRenderer();
        batch = game.getBatch();
        font = game.getFont();
        
        scoreSystem = new ScoreSystem();
        waveManager = new WaveManager();
        tutorialManager = new TutorialManager();
        
        currentPhase = GamePhase.STARTING;
        phaseTimer = 2f;
        gameTimer = 0f;
        levelStartTime = 0f;
        currentLevel = 1;
        isPaused = false;
        isGameOver = false;
        isVictory = false;
        
        playerSpawnX = CubeFighterGame.WORLD_WIDTH / 2;
        playerSpawnY = CubeFighterGame.WORLD_HEIGHT / 2;
        
        initializePlayer();
    }
    
    private void initializePlayer() {
        gameWorld.spawnPlayer(playerSpawnX, playerSpawnY);
    }
    
    @Override
    public void show() {
        gameState.setCurrentScreen(GameState.Screen.PLAYING);
    }
    
    @Override
    public void render(float delta) {
        if (isPaused) {
            renderPauseScreen();
            return;
        }
        
        update(delta);
        renderGame();
    }
    
    private void update(float delta) {
        switch (currentPhase) {
            case STARTING:
                updateStarting(delta);
                break;
            case PLAYING:
                updatePlaying(delta);
                break;
            case WAVE_COMPLETE:
                updateWaveComplete(delta);
                break;
            case LEVEL_COMPLETE:
                updateLevelComplete(delta);
                break;
            case GAME_OVER:
                updateGameOver(delta);
                break;
            case VICTORY:
                updateVictory(delta);
                break;
            default:
                break;
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            togglePause();
        }
    }
    
    private void updateStarting(float delta) {
        phaseTimer -= delta;
        if (phaseTimer <= 0) {
            currentPhase = GamePhase.PLAYING;
            phaseTimer = 0f;
            levelStartTime = gameTimer;
            waveManager.startNextWave();
        }
    }
    
    private void updatePlaying(float delta) {
        gameTimer += delta;
        gameState.update(delta);
        
        if (!tutorialManager.isTutorialComplete()) {
            tutorialManager.update(delta);
        }
        
        handleInput(delta);
        updatePlayer(delta);
        updateEnemies(delta);
        updateBullets(delta);
        updateCollisions();
        updateSpawning(delta);
        updateCooldowns(delta);
        checkWaveProgress();
        checkLevelProgress();
        checkGameOver();
        
        gameWorld.update(delta);
        scoreSystem.update(delta);
    }
    
    private void updateWaveComplete(float delta) {
        phaseTimer -= delta;
        if (phaseTimer <= 0) {
            startNextWave();
        }
    }
    
    private void updateLevelComplete(float delta) {
        phaseTimer -= delta;
        if (phaseTimer <= 0) {
            advanceLevel();
        }
    }
    
    private void updateGameOver(float delta) {
        phaseTimer -= delta;
        if (phaseTimer <= 0) {
            game.onGameOver();
        }
    }
    
    private void updateVictory(float delta) {
        phaseTimer -= delta;
        if (phaseTimer <= 0) {
            game.onVictory();
        }
    }
    
    private void handleInput(float delta) {
        Player player = gameWorld.getPlayer();
        if (player == null || !player.isActive()) return;
        
        float dx = 0, dy = 0;
        float speed = player.getSpeed();
        
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            dx = -speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            dx = speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            dy = speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            dy = -speed * delta;
        }
        
        float newX = MathUtils.clamp(player.getX() + dx, 0, CubeFighterGame.WORLD_WIDTH - player.getWidth());
        float newY = MathUtils.clamp(player.getY() + dy, 0, CubeFighterGame.WORLD_HEIGHT - player.getHeight());
        player.setPosition(newX, newY);
        
        if (Gdx.input.isTouched() || Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            shoot(player);
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && player.dashAvailable()) {
            performDash(player, dx, dy);
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) && player.shieldAvailable()) {
            player.activateShield();
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) && player.healAvailable()) {
            player.heal();
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && player.ultimateAvailable()) {
            useUltimate(player);
        }
    }
    
    private void shoot(Player player) {
        float screenX = Gdx.input.getX();
        float screenY = Gdx.input.getY();
        Vector2 worldCoords = viewport.unproject(new Vector2(screenX, screenY));
        Vector2 playerCenter = new Vector2(player.getCenterX(), player.getCenterY());
        Vector2 direction = worldCoords.sub(playerCenter).nor();
        
        gameWorld.spawnBullet(
            player.getCenterX() - 4,
            player.getCenterY() - 4,
            direction,
            player.attack()
        );
    }
    
    private void performDash(Player player, float dx, float dy) {
        if (dx == 0 && dy == 0) {
            dx = player.getVelocity().x != 0 ? Math.signum(player.getVelocity().x) : 0;
            dy = player.getVelocity().y != 0 ? Math.signum(player.getVelocity().y) : 1;
        }
        Vector2 normalized = new Vector2(dx, dy).nor();
        player.dash(normalized.x, normalized.y);
    }
    
    private void useUltimate(Player player) {
        int damage = player.attackWithUltimate();
        if (damage > 0) {
            float range = GameConstants.ULTIMATE_RANGE;
            for (Enemy enemy : gameWorld.getEnemies()) {
                float dist = distanceBetween(player, enemy);
                if (dist <= range) {
                    enemy.takeDamage(damage);
                    scoreSystem.addKill();
                }
            }
            for (Boss boss : gameWorld.getBosses()) {
                float dist = distanceBetween(player, boss);
                if (dist <= range) {
                    boss.takeDamage(damage);
                }
            }
        }
    }
    
    private float distanceBetween(GameObject obj1, GameObject obj2) {
        float dx = obj1.getCenterX() - obj2.getCenterX();
        float dy = obj1.getCenterY() - obj2.getCenterY();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    
    private void updatePlayer(float delta) {
        Player player = gameWorld.getPlayer();
        if (player != null) {
            player.update(delta);
            player.updateCooldowns(delta);
            
            if (player.getHp() <= 0 && !isGameOver) {
                onPlayerDeath();
            }
        }
    }
    
    private void updateEnemies(float delta) {
        Player player = gameWorld.getPlayer();
        
        for (Enemy enemy : gameWorld.getEnemies()) {
            if (enemy.isActive()) {
                enemy.moveTowardPlayer(player, delta);
                enemy.update(delta);
                
                if (enemy.canAttack() && enemy.isInRange(player)) {
                    int damage = enemy.attack(player);
                    if (damage > 0) {
                        player.takeDamage(damage);
                        scoreSystem.resetCombo();
                    }
                }
            }
        }
        
        for (Boss boss : gameWorld.getBosses()) {
            if (boss.isActive()) {
                boss.moveTowardPlayer(player, delta);
                boss.update(delta);
                
                if (boss.canUseAbility()) {
                    boss.useAbility();
                }
                
                if (boss.isInRange(player)) {
                    int damage = boss.getDamage();
                    player.takeDamage(damage);
                    scoreSystem.resetCombo();
                }
            }
        }
    }
    
    private void updateBullets(float delta) {
        gameWorld.updateBullets(delta);
    }
    
    private void updateCollisions() {
        Player player = gameWorld.getPlayer();
        if (player == null) return;
        
        Rectangle playerBounds = player.getBounds();
        
        for (int i = gameWorld.getBullets().size - 1; i >= 0; i--) {
            GameWorld.Bullet bullet = gameWorld.getBullets().get(i);
            
            for (int j = gameWorld.getEnemies().size - 1; j >= 0; j--) {
                Enemy enemy = gameWorld.getEnemies().get(j);
                if (enemy.isActive() && bullet.bounds.overlaps(enemy.getBounds())) {
                    enemy.takeDamage((int) bullet.damage);
                    gameWorld.getBullets().removeIndex(i);
                    
                    if (!enemy.isActive()) {
                        onEnemyKilled(enemy);
                    }
                    break;
                }
            }
            
            for (Boss boss : gameWorld.getBosses()) {
                if (boss.isActive() && bullet.bounds.overlaps(boss.getBounds())) {
                    boss.takeDamage((int) bullet.damage);
                    gameWorld.getBullets().removeIndex(i);
                    
                    if (!boss.isActive()) {
                        onBossKilled(boss);
                    }
                    break;
                }
            }
        }
        
        for (Enemy enemy : gameWorld.getEnemies()) {
            if (enemy.isActive() && playerBounds.overlaps(enemy.getBounds())) {
                player.takeDamage(enemy.getDamage());
                scoreSystem.resetCombo();
            }
        }
        
        for (Boss boss : gameWorld.getBosses()) {
            if (boss.isActive() && playerBounds.overlaps(boss.getBounds())) {
                player.takeDamage(boss.getDamage());
                scoreSystem.resetCombo();
            }
        }
    }
    
    private void updateSpawning(float delta) {
        Enemy newEnemy = waveManager.spawnEnemy(delta);
        if (newEnemy != null) {
            gameWorld.spawnEnemyAtRandomLocation(newEnemy);
        }
        
        if (waveManager.isBossWave()) {
            Boss boss = waveManager.spawnBoss();
            if (boss != null) {
                gameWorld.spawnBoss(boss);
            }
        }
    }
    
    private void updateCooldowns(float delta) {
    }
    
    private void checkWaveProgress() {
        if (currentPhase == GamePhase.PLAYING) {
            if (waveManager.checkWaveComplete()) {
                int waveReward = waveManager.calculateWaveCompletionReward();
                scoreSystem.addScore(waveReward);
                scoreSystem.addPerfectClearBonus();
                
                waveManager.enemyDefeated();
                
                if (waveManager.getCurrentWave() % 10 == 0) {
                    currentPhase = GamePhase.LEVEL_COMPLETE;
                    phaseTimer = 3f;
                } else {
                    currentPhase = GamePhase.WAVE_COMPLETE;
                    phaseTimer = 2f;
                }
            }
        }
    }
    
    private void checkLevelProgress() {
        if (currentLevel >= MAX_LEVELS && waveManager.checkWaveComplete()) {
            isVictory = true;
            currentPhase = GamePhase.VICTORY;
            phaseTimer = 3f;
            calculateFinalScore();
        }
    }
    
    private void checkGameOver() {
        Player player = gameWorld.getPlayer();
        if (player != null && player.getHp() <= 0 && !isGameOver) {
            isGameOver = true;
            currentPhase = GamePhase.GAME_OVER;
            phaseTimer = 2f;
            calculateFinalScore();
        }
    }
    
    private void onPlayerDeath() {
        isGameOver = true;
        currentPhase = GamePhase.GAME_OVER;
        phaseTimer = 2f;
        
        gameState.endGame();
        scoreSystem.onGameOver();
        
        SaveManager.getInstance().getPlayerData().setBestWave(
            Math.max(SaveManager.getInstance().getPlayerData().getBestWave(), waveManager.getCurrentWave())
        );
        SaveManager.getInstance().save();
    }
    
    private void onEnemyKilled(Enemy enemy) {
        scoreSystem.addKill();
        scoreSystem.addScore(enemy.getType().baseHp);
        
        SaveManager.getInstance().getPlayerData().addKill();
    }
    
    private void onBossKilled(Boss boss) {
        int bossScore = waveManager.calculateBossReward();
        scoreSystem.addScore(bossScore);
        scoreSystem.addBossKill();
        
        waveManager.enemyDefeated();
    }
    
    private void startNextWave() {
        waveManager.startNextWave();
        currentPhase = GamePhase.PLAYING;
    }
    
    private void advanceLevel() {
        currentLevel++;
        waveManager.startNextWave();
        currentPhase = GamePhase.PLAYING;
    }
    
    private void calculateFinalScore() {
        scoreSystem.calculateFinalScore(currentLevel, gameTimer);
    }
    
    private void togglePause() {
        if (currentPhase == GamePhase.PLAYING) {
            isPaused = true;
            gameState.pauseGame();
            currentPhase = GamePhase.PAUSED;
        } else if (currentPhase == GamePhase.PAUSED) {
            isPaused = false;
            gameState.resumeGame();
            currentPhase = GamePhase.PLAYING;
        }
    }
    
    private void renderGame() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
        
        renderArena();
        renderPlayer();
        renderEnemies();
        renderBullets();
        renderUI();
        
        if (currentPhase == GamePhase.STARTING) {
            renderStartMessage();
        } else if (currentPhase == GamePhase.WAVE_COMPLETE) {
            renderWaveCompleteMessage();
        } else if (currentPhase == GamePhase.LEVEL_COMPLETE) {
            renderLevelCompleteMessage();
        }
        
        if (!tutorialManager.isTutorialComplete()) {
            renderTutorial();
        }
    }
    
    private void renderArena() {
        shapeRenderer.setColor(new Color(0.15f, 0.15f, 0.2f, 1));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, CubeFighterGame.WORLD_WIDTH, CubeFighterGame.WORLD_HEIGHT);
        shapeRenderer.end();
        
        shapeRenderer.setColor(new Color(0.3f, 0.3f, 0.4f, 0.5f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < CubeFighterGame.WORLD_WIDTH; i += 50) {
            shapeRenderer.line(i, 0, i, CubeFighterGame.WORLD_HEIGHT);
        }
        for (int i = 0; i < CubeFighterGame.WORLD_HEIGHT; i += 50) {
            shapeRenderer.line(0, i, CubeFighterGame.WORLD_WIDTH, i);
        }
        shapeRenderer.end();
    }
    
    private void renderPlayer() {
        Player player = gameWorld.getPlayer();
        if (player == null || !player.isActive()) return;
        
        Color playerColor = player.isShieldActive() ? new Color(0.3f, 0.7f, 1f, 1) : new Color(0.2f, 0.6f, 0.9f, 1);
        
        shapeRenderer.setColor(playerColor);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        shapeRenderer.end();
        
        if (player.isDashing()) {
            shapeRenderer.setColor(new Color(0.5f, 0.8f, 1f, 0.5f));
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.rect(player.getX() - 5, player.getY() - 5, player.getWidth() + 10, player.getHeight() + 10);
            shapeRenderer.end();
        }
    }
    
    private void renderEnemies() {
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Enemy enemy : gameWorld.getEnemies()) {
            if (enemy.isActive()) {
                shapeRenderer.rect(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
            }
        }
        shapeRenderer.end();
        
        shapeRenderer.setColor(new Color(0.8f, 0.2f, 0.2f, 1));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Boss boss : gameWorld.getBosses()) {
            if (boss.isActive()) {
                float size = boss.getWidth();
                shapeRenderer.rect(boss.getX(), boss.getY(), size, size);
            }
        }
        shapeRenderer.end();
    }
    
    private void renderBullets() {
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (GameWorld.Bullet bullet : gameWorld.getBullets()) {
            shapeRenderer.rect(bullet.bounds.x, bullet.bounds.y, bullet.bounds.width, bullet.bounds.height);
        }
        shapeRenderer.end();
    }
    
private void renderUI() {
        batch.begin();
        
        // MOBILE FRIENDLY: Larger fonts for touchscreens
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f);
        font.draw(batch, "Level: " + currentLevel + "/" + MAX_LEVELS, 15, CubeFighterGame.WORLD_HEIGHT - 25);
        font.draw(batch, "Wave: " + waveManager.getCurrentWave(), 15, CubeFighterGame.WORLD_HEIGHT - 55);
        font.draw(batch, "Score: " + scoreSystem.getScore(), 15, CubeFighterGame.WORLD_HEIGHT - 85);
        
        // Combo with color
        float combo = scoreSystem.getComboMultiplier();
        if (combo >= 5.0f) {
            font.setColor(Color.GOLD);
        } else if (combo >= 2.0f) {
            font.setColor(Color.YELLOW);
        } else {
            font.setColor(Color.WHITE);
        }
        font.draw(batch, "Combo: x" + String.format("%.1f", combo), 15, CubeFighterGame.WORLD_HEIGHT - 115);
        
        // Timer - top right
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f);
        float timeSeconds = gameTimer;
        int minutes = (int) (timeSeconds / 60);
        int seconds = (int) (timeSeconds % 60);
        font.draw(batch, String.format("%02d:%02d", minutes, seconds), 
            CubeFighterGame.WORLD_WIDTH - 80, CubeFighterGame.WORLD_HEIGHT - 25);
        
        Player player = gameWorld.getPlayer();
        if (player != null) {
            // MOBILE: Large HP bar at bottom
            int hpPercent = (int)((float) player.getHp() / player.getMaxHp() * 100);
            
            // HP Text
            font.getData().setScale(1.0f);
            font.setColor(Color.WHITE);
            font.draw(batch, "HP: " + player.getHp() + "/" + player.getMaxHp(), 15, 90);
            
            // HP Bar background
            batch.end();
            shapeRenderer.setColor(Color.DARK_GRAY);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.rect(15, 60, 200, 20);
            shapeRenderer.end();
            
            // HP Bar fill - color based on health
            if (hpPercent > 50) {
                shapeRenderer.setColor(Color.GREEN);
            } else if (hpPercent > 25) {
                shapeRenderer.setColor(Color.ORANGE);
            } else {
                shapeRenderer.setColor(Color.RED);
            }
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.rect(15, 60, 200 * hpPercent / 100, 20);
            shapeRenderer.end();
            
            // HP Bar border
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.rect(15, 60, 200, 20);
            shapeRenderer.end();
            
            batch.begin();
            
            // MOBILE: Large ability buttons indicator
            font.getData().setScale(0.8f);
            int abilityY = 35;
            
            // Dash
            font.setColor(player.dashAvailable() ? Color.GREEN : Color.GRAY);
            font.draw(batch, player.dashAvailable() ? "[SPACE]" : "[CD...]", 250, abilityY);
            
            // Shield
            font.setColor(player.shieldAvailable() ? Color.CYAN : Color.GRAY);
            font.draw(batch, player.shieldAvailable() ? "[Q]" : "[CD]", 340, abilityY);
            
            // Heal
            font.setColor(player.healAvailable() ? Color.LIME : Color.GRAY);
            font.draw(batch, player.healAvailable() ? "[E]" : "[CD]", 380, abilityY);
            
            // Ultimate
            font.setColor(player.ultimateAvailable() ? Color.YELLOW : Color.GRAY);
            font.draw(batch, player.ultimateAvailable() ? "[R]" : "[CD]", 420, abilityY);
        }
        
        // MOBILE: Simple controls hint at bottom
        font.setColor(Color.LIGHT_GRAY);
        font.getData().setScale(0.7f);
        font.draw(batch, "Move: WASD/Touch | Attack: Click/Touch", 
            CubeFighterGame.WORLD_WIDTH / 2 - 130, 15);
        
        // Pause indicator - visible on mobile
        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f);
        font.draw(batch, "ESC=Pause", CubeFighterGame.WORLD_WIDTH - 100, 50);
        
        font.getData().setScale(1.0f);
        batch.end();
    }
    
    private void renderStartMessage() {
        batch.begin();
        font.getData().setScale(2f);
        font.setColor(Color.WHITE);
        font.draw(batch, "Level " + currentLevel, CubeFighterGame.WORLD_WIDTH / 2 - 60, CubeFighterGame.WORLD_HEIGHT / 2);
        font.getData().setScale(1.5f);
        batch.end();
    }
    
    private void renderWaveCompleteMessage() {
        batch.begin();
        font.getData().setScale(1.5f);
        font.setColor(Color.GREEN);
        font.draw(batch, "Wave " + waveManager.getCurrentWave() + " Complete!", 
            CubeFighterGame.WORLD_WIDTH / 2 - 120, CubeFighterGame.WORLD_HEIGHT / 2);
        font.draw(batch, "+" + waveManager.calculateWaveCompletionReward() + " points!",
            CubeFighterGame.WORLD_WIDTH / 2 - 80, CubeFighterGame.WORLD_HEIGHT / 2 - 30);
        font.getData().setScale(1.5f);
        batch.end();
    }
    
    private void renderLevelCompleteMessage() {
        batch.begin();
        font.getData().setScale(2f);
        font.setColor(Color.GOLD);
        font.draw(batch, "Level " + currentLevel + " Complete!", 
            CubeFighterGame.WORLD_WIDTH / 2 - 130, CubeFighterGame.WORLD_HEIGHT / 2 + 20);
        font.getData().setScale(1.2f);
        font.setColor(Color.WHITE);
        font.draw(batch, "Preparing next level...", 
            CubeFighterGame.WORLD_WIDTH / 2 - 100, CubeFighterGame.WORLD_HEIGHT / 2 - 20);
        font.getData().setScale(1.5f);
        batch.end();
    }
    
    private void renderPauseScreen() {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        batch.begin();
        font.getData().setScale(2f);
        font.setColor(Color.WHITE);
        font.draw(batch, "PAUSED", CubeFighterGame.WORLD_WIDTH / 2 - 60, CubeFighterGame.WORLD_HEIGHT / 2);
        font.getData().setScale(1.2f);
        font.draw(batch, "Press ESC to Resume", CubeFighterGame.WORLD_WIDTH / 2 - 100, CubeFighterGame.WORLD_HEIGHT / 2 - 40);
        font.draw(batch, "Press Q to Quit", CubeFighterGame.WORLD_WIDTH / 2 - 80, CubeFighterGame.WORLD_HEIGHT / 2 - 70);
        font.getData().setScale(1.5f);
        batch.end();
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            game.returnToMenu();
        }
    }
    
    private void renderTutorial() {
        if (tutorialManager.shouldShowTutorial()) {
            String hint = tutorialManager.getCurrentHint();
            if (hint != null) {
                batch.begin();
                font.getData().setScale(1.2f);
                font.setColor(Color.CYAN);
                font.draw(batch, hint, CubeFighterGame.WORLD_WIDTH / 2 - hint.length() * 3, 80);
                font.getData().setScale(1.5f);
                font.draw(batch, "[Tab] Skip Tutorial", CubeFighterGame.WORLD_WIDTH / 2 - 80, 55);
                batch.end();
                
                if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
                    tutorialManager.skipTutorial();
                }
            }
        }
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
    
    @Override
    public void pause() {
        if (currentPhase == GamePhase.PLAYING) {
            togglePause();
        }
    }
    
    @Override
    public void resume() {
    }
    
    @Override
    public void hide() {
    }
    
    @Override
    public void dispose() {
    }
    
    public ScoreSystem getScoreSystem() { return scoreSystem; }
    public GamePhase getCurrentPhase() { return currentPhase; }
    public int getCurrentLevel() { return currentLevel; }
    public float getGameTimer() { return gameTimer; }
    public boolean isVictory() { return isVictory; }
}