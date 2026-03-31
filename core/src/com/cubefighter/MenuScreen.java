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

public class MenuScreen implements Screen {
    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 480;

    private final CubeFighterGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    private float rotation;
    private float pulseTime;

    public MenuScreen(CubeFighterGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(2f);

        rotation = 0f;
        pulseTime = 0f;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawAnimatedBackground();
        drawTitle();
        drawMenuOptions();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "CUBE FIGHTER", 280, 350);
        font.getData().setScale(1.2f);
        font.draw(batch, "TAP OR CLICK TO START", 270, 200);
        font.draw(batch, "Use WASD or Arrow Keys to move", 230, 130);
        font.draw(batch, "Click or Tap to shoot", 275, 100);
        font.getData().setScale(2f);
        batch.end();
    }

    private void update(float delta) {
        rotation += delta * 45f;
        pulseTime += delta * 3f;

        if (Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.startGame();
        }
    }

    private void drawAnimatedBackground() {
        shapeRenderer.setProjectionMatrix(camera.combined);

        for (int i = 0; i < 5; i++) {
            float offset = i * 160 + (rotation * 0.5f) % 160;
            float alpha = 0.1f + 0.02f * i;
            shapeRenderer.setColor(new Color(0.2f, 0.3f, 0.5f, alpha));
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            for (int j = 0; j < 10; j++) {
                shapeRenderer.line(offset + j * 160, 0, offset + j * 160, WORLD_HEIGHT);
            }
            shapeRenderer.end();
        }
    }

    private void drawTitle() {
        float baseSize = 60;
        float pulse = 1 + (float) Math.sin(pulseTime) * 0.05f;
        float size = baseSize * pulse;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(new Color(0.3f, 0.6f, 0.9f, 0.8f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float centerX = WORLD_WIDTH / 2 - size * 1.5f;
        float centerY = WORLD_HEIGHT / 2 + 50;

        shapeRenderer.rect(centerX - size / 2, centerY - size / 2, size, size);

        shapeRenderer.setColor(new Color(0.5f, 0.8f, 1f, 0.5f));
        float innerSize = size * 0.6f;
        shapeRenderer.rect(centerX - innerSize / 2, centerY - innerSize / 2 + (float) Math.sin(pulseTime * 2) * 5, innerSize, innerSize);

        shapeRenderer.end();
    }

    private void drawMenuOptions() {
        shapeRenderer.setProjectionMatrix(camera.combined);

        float buttonWidth = 200;
        float buttonHeight = 50;
        float buttonX = WORLD_WIDTH / 2 - buttonWidth / 2;
        float buttonY = 180;

        float hover = (float) Math.sin(pulseTime * 2) * 3;

        shapeRenderer.setColor(new Color(0.2f, 0.4f, 0.7f, 1f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(buttonX, buttonY + hover, buttonWidth, buttonHeight);
        shapeRenderer.end();

        shapeRenderer.setColor(new Color(0.4f, 0.6f, 0.9f, 1f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(buttonX, buttonY + hover, buttonWidth, buttonHeight);
        shapeRenderer.end();
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
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }
}