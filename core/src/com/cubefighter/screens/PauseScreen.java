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

public class PauseScreen implements Screen {
    private final CubeFighterGame game;
    private GameState gameState;
    private PauseCallback callback;
    
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont titleFont;
    private BitmapFont smallFont;

    private Rectangle resumeButton;
    private Rectangle settingsButton;
    private Rectangle quitButton;

    private float animationTime;

    public interface PauseCallback {
        void onResume();
        void onSettings();
        void onQuitToMenu();
    }

    public PauseScreen(CubeFighterGame game) {
        this(game, null, null);
    }

    public PauseScreen(CubeFighterGame game, GameState gameState) {
        this(game, gameState, null);
    }

    public PauseScreen(CubeFighterGame game, PauseCallback callback) {
        this(game, null, callback);
    }

    public PauseScreen(CubeFighterGame game, GameState gameState, PauseCallback callback) {
        this.game = game;
        this.gameState = gameState;
        this.callback = callback;
        this.animationTime = 0f;

        camera = game.getCamera();
        viewport = game.getViewport();
        shapeRenderer = game.getShapeRenderer();
        batch = game.getBatch();
        font = game.getFont();

        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);

        smallFont = new BitmapFont();
        smallFont.getData().setScale(1.2f);

        calculateLayout();
    }

    public void setGameState(GameState state) {
        this.gameState = state;
    }

    public void setCallback(PauseCallback callback) {
        this.callback = callback;
    }

    private void calculateLayout() {
        float centerX = CubeFighterGame.WORLD_WIDTH / 2;
        float centerY = CubeFighterGame.WORLD_HEIGHT / 2;
        float buttonWidth = 200f;
        float buttonHeight = 50f;

        resumeButton = new Rectangle(centerX - buttonWidth / 2, centerY + 50, buttonWidth, buttonHeight);
        settingsButton = new Rectangle(centerX - buttonWidth / 2, centerY - 10, buttonWidth, buttonHeight);
        quitButton = new Rectangle(centerX - buttonWidth / 2, centerY - 70, buttonWidth, buttonHeight);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        animationTime += delta;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.7f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, CubeFighterGame.WORLD_WIDTH, CubeFighterGame.WORLD_HEIGHT);
        shapeRenderer.end();

        drawPanel();
        drawStats();
        drawButtons();
        drawText();

        handleInput();
    }

    private void drawPanel() {
        float panelWidth = 350;
        float panelHeight = 380;
        float panelX = CubeFighterGame.WORLD_WIDTH / 2 - panelWidth / 2;
        float panelY = CubeFighterGame.WORLD_HEIGHT / 2 - panelHeight / 2;

        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.18f, 0.95f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        shapeRenderer.end();

        shapeRenderer.setColor(new Color(0.3f, 0.5f, 0.8f, 1f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        shapeRenderer.end();
    }

    private void drawStats() {
        if (gameState == null) return;

        float statsX = CubeFighterGame.WORLD_WIDTH / 2 - 80;
        float statsY = CubeFighterGame.WORLD_HEIGHT / 2 + 120;

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        smallFont.setColor(new Color(0.7f, 0.7f, 0.8f, 1f));
        smallFont.draw(batch, "Wave: ", statsX, statsY);
        smallFont.setColor(new Color(0.4f, 0.8f, 1f, 1f));
        smallFont.draw(batch, String.valueOf(gameState.getCurrentWave()), statsX + 70, statsY);

        smallFont.setColor(new Color(0.7f, 0.7f, 0.8f, 1f));
        smallFont.draw(batch, "Score: ", statsX, statsY - 30);
        smallFont.setColor(new Color(1f, 0.8f, 0.2f, 1f));
        smallFont.draw(batch, String.valueOf(gameState.getScore()), statsX + 70, statsY - 30);

        smallFont.setColor(new Color(0.7f, 0.7f, 0.8f, 1f));
        smallFont.draw(batch, "Level: ", statsX, statsY - 60);
        smallFont.setColor(new Color(0.4f, 1f, 0.4f, 1f));
        smallFont.draw(batch, String.valueOf(gameState.getCurrentLevel()), statsX + 70, statsY - 60);

        float time = gameState.getGameTime();
        int mins = (int) (time / 60);
        int secs = (int) (time % 60);
        smallFont.setColor(new Color(0.7f, 0.7f, 0.8f, 1f));
        smallFont.draw(batch, "Time: ", statsX, statsY - 90);
        smallFont.setColor(new Color(0.8f, 0.6f, 1f, 1f));
        smallFont.draw(batch, String.format("%d:%02d", mins, secs), statsX + 70, statsY - 90);

        batch.end();
    }

    private void drawButtons() {
        float pulse = (float) Math.sin(animationTime * 3) * 0.03f;

        drawButton(resumeButton, new Color(0.2f, 0.5f + pulse, 0.3f, 1f), new Color(0.4f, 0.8f, 0.5f, 1f));
        drawButton(settingsButton, new Color(0.35f, 0.35f, 0.45f, 1f), new Color(0.55f, 0.55f, 0.7f, 1f));
        drawButton(quitButton, new Color(0.5f, 0.25f, 0.25f, 1f), new Color(0.8f, 0.4f, 0.4f, 1f));
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

    private void drawText() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        float pulse = (float) Math.sin(animationTime * 4) * 0.08f;
        titleFont.setColor(new Color(1f, 1f, 1f, 0.9f + pulse));
        titleFont.draw(batch, "PAUSED", CubeFighterGame.WORLD_WIDTH / 2 - 70, CubeFighterGame.WORLD_HEIGHT / 2 + 180);

        font.setColor(Color.WHITE);
        font.draw(batch, "Resume", resumeButton.x + resumeButton.width / 2 - 40, resumeButton.y + 32);
        font.draw(batch, "Settings", settingsButton.x + settingsButton.width / 2 - 42, settingsButton.y + 32);
        font.draw(batch, "Quit", quitButton.x + quitButton.width / 2 - 22, quitButton.y + 32);

        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
            float x = viewport.unproject(new com.badlogic.gdx.math.Vector2(Gdx.input.getX(), Gdx.input.getY())).x;
            float y = viewport.unproject(new com.badlogic.gdx.math.Vector2(Gdx.input.getX(), Gdx.input.getY())).y;

            if (resumeButton.contains(x, y)) {
                if (callback != null) callback.onResume();
                return;
            }

            if (settingsButton.contains(x, y)) {
                if (callback != null) callback.onSettings();
                return;
            }

            if (quitButton.contains(x, y)) {
                if (callback != null) callback.onQuitToMenu();
                return;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            if (callback != null) callback.onResume();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            if (callback != null) callback.onQuitToMenu();
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
        smallFont.dispose();
    }
}