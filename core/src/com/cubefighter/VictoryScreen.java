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

public class VictoryScreen implements Screen {
    private final CubeFighterGame game;
    private GameState gameState;
    
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    
    private float displayTimer;
    private float animationTime;
    
    public VictoryScreen(CubeFighterGame game, GameState gameState) {
        this.game = game;
        this.gameState = gameState;
        
        camera = game.getCamera();
        viewport = game.getViewport();
        shapeRenderer = game.getShapeRenderer();
        batch = game.getBatch();
        font = game.getFont();
        
        displayTimer = 0f;
        animationTime = 0f;
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
        animationTime += delta;
        
        Gdx.gl.glClearColor(0.05f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        renderVictoryAnimation();
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            (Gdx.input.isTouched() && displayTimer > 0.5f)) {
            game.returnToMenu();
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.P) && displayTimer > 0.5f) {
            continueToNextPlay();
        }
    }
    
    private void continueToNextPlay() {
        game.startGame(gameState.getGameMode());
    }
    
    private void renderVictoryAnimation() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        float pulse = (float) Math.sin(animationTime * 3) * 0.1f + 1;
        
        for (int i = 0; i < 20; i++) {
            float x = (float) ((animationTime * 50 + i * 100) % CubeFighterGame.WORLD_WIDTH);
            float y = (float) ((i * 47 + animationTime * 30) % CubeFighterGame.WORLD_HEIGHT);
            
            shapeRenderer.setColor(new Color(0.3f, 0.6f, 0.9f, 0.3f));
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.rect(x, y, 4, 4);
            shapeRenderer.end();
        }
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        font.getData().setScale(3f * pulse);
        font.setColor(Color.GOLD);
        font.draw(batch, "VICTORY!", CubeFighterGame.WORLD_WIDTH / 2 - 100, CubeFighterGame.WORLD_HEIGHT / 2 + 200);
        
        font.getData().setScale(1.8f);
        font.setColor(Color.WHITE);
        font.draw(batch, "Congratulations! You completed all 100 levels!",
            CubeFighterGame.WORLD_WIDTH / 2 - 230, CubeFighterGame.WORLD_HEIGHT / 2 + 130);
        
        font.getData().setScale(1.5f);
        
        font.setColor(new Color(0.9f, 0.9f, 0.5f, 1));
        font.draw(batch, "Final Score: " + gameState.getScore(),
            CubeFighterGame.WORLD_WIDTH / 2 - 80, CubeFighterGame.WORLD_HEIGHT / 2 + 60);
        
        font.setColor(Color.WHITE);
        
        int totalKills = SaveManager.getInstance().getPlayerData().getTotalKills();
        font.draw(batch, "Total Enemies Defeated: " + totalKills,
            CubeFighterGame.WORLD_WIDTH / 2 - 110, CubeFighterGame.WORLD_HEIGHT / 2 + 20);
        
        float time = gameState.getGameTime();
        int hours = (int) (time / 3600);
        int minutes = (int) ((time % 3600) / 60);
        int seconds = (int) (time % 60);
        font.draw(batch, String.format("Total Time: %02d:%02d:%02d", hours, minutes, seconds),
            CubeFighterGame.WORLD_WIDTH / 2 - 90, CubeFighterGame.WORLD_HEIGHT / 2 - 20);
        
        font.draw(batch, "Levels Completed: 100 / 100",
            CubeFighterGame.WORLD_WIDTH / 2 - 100, CubeFighterGame.WORLD_HEIGHT / 2 - 60);
        
        float highScore = SaveManager.getInstance().getPlayerData().getHighScore();
        font.setColor(Color.CYAN);
        font.draw(batch, "High Score: " + (int) highScore,
            CubeFighterGame.WORLD_WIDTH / 2 - 80, CubeFighterGame.WORLD_HEIGHT / 2 - 100);
        
        font.getData().setScale(1.2f);
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "You are a true Cube Warrior!",
            CubeFighterGame.WORLD_WIDTH / 2 - 100, CubeFighterGame.WORLD_HEIGHT / 2 - 150);
        
        font.getData().setScale(1.5f);
        font.setColor(Color.GREEN);
        font.draw(batch, "Press ENTER to return to menu",
            CubeFighterGame.WORLD_WIDTH / 2 - 130, CubeFighterGame.WORLD_HEIGHT / 2 - 200);
        font.draw(batch, "Press P to play again",
            CubeFighterGame.WORLD_WIDTH / 2 - 90, CubeFighterGame.WORLD_HEIGHT / 2 - 230);
        
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