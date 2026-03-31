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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cubefighter.CubeFighterGame;
import com.cubefighter.GameState;
import com.cubefighter.save.SaveManager;

public class VictoryScreen implements Screen {
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
    
    private float[] confettiX;
    private float[] confettiY;
    private float[] confettiSpeed;
    private Color[] confettiColors;
    
    private Rectangle playAgainButton;
    private Rectangle menuButton;

    public VictoryScreen(CubeFighterGame game, GameState gameState) {
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
        
        initConfetti();
        calculateLayout();
    }
    
    public void updateState(GameState state) {
        this.gameState = state;
    }
    
    private void initConfetti() {
        int count = 40;
        confettiX = new float[count];
        confettiY = new float[count];
        confettiSpeed = new float[count];
        confettiColors = new Color[count];
        
        for (int i = 0; i < count; i++) {
            confettiX[i] = MathUtils.random(CubeFighterGame.WORLD_WIDTH);
            confettiY[i] = MathUtils.random(CubeFighterGame.WORLD_HEIGHT);
            confettiSpeed[i] = MathUtils.random(30, 80);
            confettiColors[i] = new Color(
                MathUtils.random(0.5f, 1f),
                MathUtils.random(0.5f, 1f),
                MathUtils.random(0.5f, 1f),
                1f
            );
        }
    }
    
    private void calculateLayout() {
        float centerX = CubeFighterGame.WORLD_WIDTH / 2;
        float buttonY = 80;
        playAgainButton = new Rectangle(centerX - 210, buttonY, 200, 50);
        menuButton = new Rectangle(centerX + 10, buttonY, 200, 50);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        animationTime += delta;
        displayTimer += delta;
        updateConfetti(delta);

        Gdx.gl.glClearColor(0.05f, 0.08f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        drawBackground();
        drawConfetti();
        drawPanel();
        drawStats();
        drawButtons();

        handleInput();
    }
    
    private void updateConfetti(float delta) {
        for (int i = 0; i < confettiX.length; i++) {
            confettiY[i] -= confettiSpeed[i] * delta;
            confettiX[i] += MathUtils.sin(animationTime * 2 + i) * 20 * delta;
            
            if (confettiY[i] < -10) {
                confettiY[i] = CubeFighterGame.WORLD_HEIGHT + 10;
                confettiX[i] = MathUtils.random(CubeFighterGame.WORLD_WIDTH);
            }
        }
    }
    
    private void drawBackground() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(new Color(0.08f, 0.12f, 0.2f, 1f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, CubeFighterGame.WORLD_WIDTH, CubeFighterGame.WORLD_HEIGHT);
        shapeRenderer.end();
    }
    
    private void drawConfetti() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < confettiX.length; i++) {
            shapeRenderer.setColor(confettiColors[i]);
            float size = 4 + MathUtils.sin(animationTime * 3 + i) * 2;
            shapeRenderer.rect(confettiX[i], confettiY[i], size, size);
        }
        shapeRenderer.end();
    }
    
    private void drawPanel() {
        float panelWidth = 520;
        float panelHeight = 380;
        float panelX = CubeFighterGame.WORLD_WIDTH / 2 - panelWidth / 2;
        float panelY = CubeFighterGame.WORLD_HEIGHT / 2 - panelHeight / 2 + 30;
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(new Color(0.08f, 0.1f, 0.18f, 0.95f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        shapeRenderer.end();
        
        shapeRenderer.setColor(new Color(1f, 0.8f, 0.2f, 1f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        shapeRenderer.end();
    }
    
    private void drawStats() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        float pulse = 1f + MathUtils.sin(animationTime * 2) * 0.05f;
        titleFont.getData().setScale(3f * pulse);
        titleFont.setColor(new Color(1f, 0.85f, 0.2f, 1f));
        titleFont.draw(batch, "VICTORY!", CubeFighterGame.WORLD_WIDTH / 2 - 100, CubeFighterGame.WORLD_HEIGHT / 2 + 200);
        
        font.getData().setScale(1.5f);
        
        float statsY = CubeFighterGame.WORLD_HEIGHT / 2 + 120;
        float leftX = CubeFighterGame.WORLD_WIDTH / 2 - 200;
        
        font.setColor(new Color(0.4f, 1f, 0.4f, 1f));
        font.draw(batch, "Congratulations!", CubeFighterGame.WORLD_WIDTH / 2 - 85, statsY);
        
        font.setColor(Color.WHITE);
        font.draw(batch, "Final Score: ", leftX, statsY - 50);
        font.setColor(new Color(1f, 0.8f, 0.2f, 1f));
        font.draw(batch, String.valueOf(gameState.getScore()), leftX + 130, statsY - 50);
        
        font.setColor(Color.WHITE);
        font.draw(batch, "Waves Completed: ", leftX, statsY - 90);
        font.setColor(new Color(0.4f, 0.8f, 1f, 1f));
        font.draw(batch, String.valueOf(gameState.getCurrentWave()), leftX + 180, statsY - 90);
        
        font.setColor(Color.WHITE);
        font.draw(batch, "Levels Completed: ", leftX, statsY - 130);
        font.setColor(new Color(0.6f, 1f, 0.6f, 1f));
        font.draw(batch, gameState.getCurrentLevel() + " / 100", leftX + 180, statsY - 130);
        
        int totalKills = SaveManager.getInstance().getPlayerData().getTotalKills();
        font.setColor(Color.WHITE);
        font.draw(batch, "Enemies Defeated: ", leftX, statsY - 170);
        font.setColor(new Color(1f, 0.5f, 0.5f, 1f));
        font.draw(batch, String.valueOf(totalKills), leftX + 165, statsY - 170);
        
        float time = gameState.getGameTime();
        int hours = (int) (time / 3600);
        int mins = (int) ((time % 3600) / 60);
        int secs = (int) (time % 60);
        font.setColor(Color.WHITE);
        font.draw(batch, "Time Played: ", leftX, statsY - 210);
        font.setColor(new Color(0.8f, 0.6f, 1f, 1f));
        font.draw(batch, String.format("%02d:%02d:%02d", hours, mins, secs), leftX + 120, statsY - 210);
        
        int gemsEarned = gameState.getCurrentWave() / 10 + totalKills / 50;
        font.setColor(new Color(0.5f, 0.8f, 1f, 1f));
        font.getData().setScale(1.3f);
        font.draw(batch, "+" + gemsEarned + " Gems Earned!", CubeFighterGame.WORLD_WIDTH / 2 - 70, statsY - 260);
        font.getData().setScale(1.5f);
        
        batch.end();
    }
    
    private void drawButtons() {
        drawButton(playAgainButton, new Color(0.2f, 0.5f, 0.3f, 1f), new Color(0.4f, 0.8f, 0.5f, 1f));
        drawButton(menuButton, new Color(0.4f, 0.35f, 0.35f, 1f), new Color(0.7f, 0.5f, 0.5f, 1f));
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.getData().setScale(1.3f);
        font.setColor(Color.WHITE);
        font.draw(batch, "Play Again", playAgainButton.x + playAgainButton.width / 2 - 50, playAgainButton.y + 32);
        font.draw(batch, "Main Menu", menuButton.x + menuButton.width / 2 - 50, menuButton.y + 32);
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

            if (playAgainButton.contains(x, y)) {
                game.startGame(gameState.getGameMode());
                return;
            }

            if (menuButton.contains(x, y)) {
                game.returnToMenu();
                return;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.startGame(gameState.getGameMode());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
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