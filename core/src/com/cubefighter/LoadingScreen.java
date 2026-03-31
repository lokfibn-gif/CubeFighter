package com.cubefighter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LoadingScreen implements Screen {
    private final CubeFighterGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    
    private float loadingTimer;
    
    public LoadingScreen(CubeFighterGame game) {
        this.game = game;
        
        camera = game.getCamera();
        viewport = game.getViewport();
        shapeRenderer = game.getShapeRenderer();
        batch = game.getBatch();
        font = game.getFont();
        
        loadingTimer = 0f;
    }
    
    @Override
    public void show() {
    }
    
    @Override
    public void render(float delta) {
        game.updateLoading();
        
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        if (game.isAssetsLoaded()) {
            loadingTimer += delta;
            if (loadingTimer >= 0.5f) {
                game.onLoadingComplete();
            }
        }
        
        renderLoading();
    }
    
    private void renderLoading() {
        float progress = game.getLoadingProgress();
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(CubeFighterGame.WORLD_WIDTH / 2 - 150, CubeFighterGame.WORLD_HEIGHT / 2 - 15, 300, 30);
        shapeRenderer.end();
        
        shapeRenderer.setColor(new Color(0.2f, 0.6f, 0.9f, 1));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(CubeFighterGame.WORLD_WIDTH / 2 - 148, CubeFighterGame.WORLD_HEIGHT / 2 - 13, 296 * progress, 26);
        shapeRenderer.end();
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        font.draw(batch, "Loading...", CubeFighterGame.WORLD_WIDTH / 2 - 50, CubeFighterGame.WORLD_HEIGHT / 2 + 50);
        font.draw(batch, (int)(progress * 100) + "%", CubeFighterGame.WORLD_WIDTH / 2 - 20, CubeFighterGame.WORLD_HEIGHT / 2 - 50);
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