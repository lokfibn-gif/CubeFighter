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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cubefighter.save.SaveManager;
import com.cubefighter.systems.ScoreSystem;

public class GameOverScreen implements Screen {
    private final CubeFighterGame game;
    private GameState gameState;
    private ScoreSystem scoreSystem;
    
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    
    private float displayTimer;
    
    public GameOverScreen(CubeFighterGame game, GameState gameState) {
        this.game = game;
        this.gameState = gameState;
        
        camera = game.getCamera();
        viewport = game.getViewport();
        shapeRenderer = game.getShapeRenderer();
        batch = game.getBatch();
        font = game.getFont();
        
        displayTimer = 0f;
    }
    
    public void updateState(GameState state) {
        this.gameState = state;
    }
    
    @Override
    public void show() {
    }
    
    @Override
    public void render(float delta) {
        displayTimer += delta;
        
        Gdx.gl.glClearColor(0.1f, 0.05f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            (Gdx.input.isTouched() && displayTimer > 0.5f)) {
            game.returnToMenu();
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && displayTimer > 0.5f) {
            game.startGame(gameState.getGameMode());
        }
        
        renderGameOver();
    }
    
    private void renderGameOver() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        font.getData().setScale(3f);
        font.setColor(Color.RED);
        font.draw(batch, "GAME OVER", CubeFighterGame.WORLD_WIDTH / 2 - 130, CubeFighterGame.WORLD_HEIGHT / 2 + 150);
        
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        
        font.draw(batch, "Final Score: " + gameState.getScore(), 
            CubeFighterGame.WORLD_WIDTH / 2 - 80, CubeFighterGame.WORLD_HEIGHT / 2 + 80);
        
        font.draw(batch, "Level Reached: " + gameState.getCurrentLevel(),
            CubeFighterGame.WORLD_WIDTH / 2 - 80, CubeFighterGame.WORLD_HEIGHT / 2 + 50);
        
        font.draw(batch, "Wave Reached: " + gameState.getCurrentWave(),
            CubeFighterGame.WORLD_WIDTH / 2 - 80, CubeFighterGame.WORLD_HEIGHT / 2 + 20);
        
        int totalKills = SaveManager.getInstance().getPlayerData().getTotalKills();
        font.draw(batch, "Total Kills: " + totalKills,
            CubeFighterGame.WORLD_WIDTH / 2 - 80, CubeFighterGame.WORLD_HEIGHT / 2 - 10);
        
        float time = gameState.getGameTime();
        int minutes = (int) (time / 60);
        int seconds = (int) (time % 60);
        font.draw(batch, String.format("Time: %02d:%02d", minutes, seconds),
            CubeFighterGame.WORLD_WIDTH / 2 - 80, CubeFighterGame.WORLD_HEIGHT / 2 - 40);
        
        float highScore = SaveManager.getInstance().getPlayerData().getHighScore();
        font.setColor(Color.GOLD);
        font.draw(batch, "High Score: " + (int) highScore,
            CubeFighterGame.WORLD_WIDTH / 2 - 80, CubeFighterGame.WORLD_HEIGHT / 2 - 80);
        
        font.getData().setScale(1.2f);
        font.setColor(Color.WHITE);
        font.draw(batch, "Press ENTER to return to menu",
            CubeFighterGame.WORLD_WIDTH / 2 - 120, CubeFighterGame.WORLD_HEIGHT / 2 - 140);
        font.draw(batch, "Press R to retry",
            CubeFighterGame.WORLD_WIDTH / 2 - 70, CubeFighterGame.WORLD_HEIGHT / 2 - 170);
        
        font.getData().setScale(1.5f);
        batch.end();
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
    
    @Override
    public void pause() {
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
}