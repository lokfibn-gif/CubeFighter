package com.cubefighter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cubefighter.CubeFighterGame;
import com.cubefighter.GameState;
import com.cubefighter.save.SaveManager;

public class GameOverScreen implements Screen {
    private final CubeFighterGame game;
    private GameState gameState;
    
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont titleFont;
    
    private float animationTime;
    private float displayTimer;
    
    private Rectangle retryButton;
    private Rectangle menuButton;
    
    public GameOverScreen(CubeFighterGame game, GameState gameState) {
        this.game = game;
        this.gameState = gameState;
        
        camera = game.getCamera();
        viewport = game.getViewport();
        shapeRenderer = game.getShapeRenderer();
        batch = game.getBatch();
        font = game.getFont();
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2f);
        
        animationTime = 0f;
        displayTimer = 0f;
        
        calculateLayout();
    }
    
    public void updateState(GameState state) {
        this.gameState = state;
    }
    
    private void calculateLayout() {
        float centerX = CubeFighterGame.WORLD_WIDTH / 2;
        float buttonY = 100;
        retryButton = new Rectangle(centerX - 210, buttonY, 200, 50);
        menuButton = new Rectangle(centerX + 10, buttonY, 200, 50);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        animationTime += delta;
        displayTimer += delta;

        Gdx.gl.glClearColor(0.1f, 0.05f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        drawBackground();
        drawPanel();
        drawStats();
        drawButtons();

        handleInput();
    }
    
    private void drawBackground() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        for (int i = 0; i < 10; i++) {
            float offset = (animationTime * 20 + i * 80) % CubeFighterGame.WORLD_WIDTH;
            shapeRenderer.setColor(new Color(0.2f, 0.1f, 0.1f, 0.3f));
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.line(offset, 0, offset - 60, CubeFighterGame.WORLD_HEIGHT);
            shapeRenderer.end();
        }
    }
    
    private void drawPanel() {
        float panelWidth = 500;
        float panelHeight = 350;
        float panelX = CubeFighterGame.WORLD_WIDTH / 2 - panelWidth / 2;
        float panelY = CubeFighterGame.WORLD_HEIGHT / 2 - panelHeight / 2 + 50;
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(new Color(0.08f, 0.05f, 0.08f, 0.95f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        shapeRenderer.end();
        
        shapeRenderer.setColor(new Color(0.8f, 0.2f, 0.2f, 1f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        shapeRenderer.end();
    }
    
    private void drawStats() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        float pulse = 1f + (float) Math.sin(animationTime * 3) * 0.05f;
        titleFont.getData().setScale(2.5f * pulse);
        titleFont.setColor(new Color(0.9f, 0.2f, 0.2f, 1f));
        titleFont.draw(batch, "GAME OVER", CubeFighterGame.WORLD_WIDTH / 2 - 100, CubeFighterGame.WORLD_HEIGHT / 2 + 180);
        
        font.getData().setScale(1.5f);
        
        float statsY = CubeFighterGame.WORLD_HEIGHT / 2 + 100;
        float leftX = CubeFighterGame.WORLD_WIDTH / 2 - 180;
        
        font.setColor(Color.WHITE);
        font.draw(batch, "Score: ", leftX, statsY);
        font.setColor(new Color(1f, 0.8f, 0.2f, 1f));
        font.draw(batch, String.valueOf(gameState.getScore()), leftX + 100, statsY);
        
        font.setColor(Color.WHITE);
        font.draw(batch, "Wave: ", leftX, statsY - 35);
        font.setColor(new Color(0.4f, 0.8f, 1f, 1f));
        font.draw(batch, String.valueOf(gameState.getCurrentWave()), leftX + 100, statsY - 35);
        
        font.setColor(Color.WHITE);
        font.draw(batch, "Level: ", leftX, statsY - 70);
        font.setColor(new Color(0.4f, 1f, 0.4f, 1f));
        font.draw(batch, String.valueOf(gameState.getCurrentLevel()), leftX + 100, statsY - 70);
        
        float time = gameState.getGameTime();
        int mins = (int) (time / 60);
        int secs = (int) (time % 60);
        font.setColor(Color.WHITE);
        font.draw(batch, "Time: ", leftX, statsY - 105);
        font.setColor(new Color(0.8f, 0.6f, 0.9f, 1f));
        font.draw(batch, String.format("%d:%02d", mins, secs), leftX + 100, statsY - 105);
        
        boolean isNewHighScore = gameState.getScore() > SaveManager.getInstance().getPlayerData().getHighScore();
        if (isNewHighScore) {
            SaveManager.getInstance().getPlayerData().setHighScore(gameState.getScore());
            SaveManager.getInstance().save();
            font.setColor(new Color(1f, 0.8f, 0.2f, 1f));
            font.getData().setScale(1.8f);
            font.draw(batch, "NEW HIGH SCORE!", CubeFighterGame.WORLD_WIDTH / 2 - 90, statsY - 150);
            font.getData().setScale(1.5f);
        } else {
            font.setColor(new Color(0.7f, 0.7f, 0.8f, 1f));
            font.draw(batch, "High Score: " + (int) SaveManager.getInstance().getPlayerData().getHighScore(), leftX, statsY - 150);
        }
        
        batch.end();
    }
    
    private void drawButtons() {
        drawButton(retryButton, new Color(0.2f, 0.5f, 0.3f, 1f), new Color(0.4f, 0.8f, 0.5f, 1f));
        drawButton(menuButton, new Color(0.4f, 0.35f, 0.35f, 1f), new Color(0.7f, 0.5f, 0.5f, 1f));
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.getData().setScale(1.3f);
        font.setColor(Color.WHITE);
        font.draw(batch, "Retry", retryButton.x + retryButton.width / 2 - 30, retryButton.y + 32);
        font.draw(batch, "Menu", menuButton.x + menuButton.width / 2 - 28, menuButton.y + 32);
        batch.end();
    }
    
    private void drawButton(Rectangle bounds, Color fillColor, Color borderColor) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(fillColor);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();
        
        shapeRenderer.setColor(borderColor);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();
    }

    private void handleInput() {
        if (displayTimer < 0.5f) return;
        
        if (Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
            float x = viewport.unproject(new com.badlogic.gdx.math.Vector2(Gdx.input.getX(), Gdx.input.getY())).x;
            float y = viewport.unproject(new com.badlogic.gdx.math.Vector2(Gdx.input.getX(), Gdx.input.getY())).y;

            if (retryButton.contains(x, y)) {
                game.startGame(gameState.getGameMode());
                return;
            }

            if (menuButton.contains(x, y)) {
                game.returnToMenu();
                return;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            game.startGame(gameState.getGameMode());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.returnToMenu();
        }
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
        titleFont.dispose();
    }
}