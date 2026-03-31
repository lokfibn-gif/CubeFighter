package com.cubefighter.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class PauseMenu {
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private BitmapFont titleFont;
    
    private Rectangle resumeButton;
    private Rectangle settingsButton;
    private Rectangle quitButton;
    
    private static final float BUTTON_WIDTH = 200f;
    private static final float BUTTON_HEIGHT = 50f;
    private static final float BUTTON_SPACING = 15f;
    
    private PauseCallback callback;
    
    public interface PauseCallback {
        void onResume();
        void onSettings();
        void onQuitToMenu();
    }
    
    public PauseMenu() {
        shapeRenderer = new ShapeRenderer();
        
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);
        
        calculateLayout();
    }
    
    private void calculateLayout() {
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        
        resumeButton = new Rectangle(
            centerX - BUTTON_WIDTH / 2,
            centerY + BUTTON_HEIGHT + BUTTON_SPACING,
            BUTTON_WIDTH, BUTTON_HEIGHT
        );
        
        settingsButton = new Rectangle(
            centerX - BUTTON_WIDTH / 2,
            centerY,
            BUTTON_WIDTH, BUTTON_HEIGHT
        );
        
        quitButton = new Rectangle(
            centerX - BUTTON_WIDTH / 2,
            centerY - BUTTON_HEIGHT - BUTTON_SPACING,
            BUTTON_WIDTH, BUTTON_HEIGHT
        );
    }
    
    public void setCallback(PauseCallback callback) {
        this.callback = callback;
    }
    
    public void render(SpriteBatch batch) {
        batch.end();
        
        renderOverlay();
        renderButtons();
        
        batch.begin();
        renderText(batch);
    }
    
    private void renderOverlay() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.7f));
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
    }
    
    private void renderButtons() {
        float centerX = Gdx.graphics.getWidth() / 2f;
        float panelWidth = BUTTON_WIDTH + 60;
        float panelHeight = BUTTON_HEIGHT * 3 + BUTTON_SPACING * 2 + 100;
        float panelX = centerX - panelWidth / 2;
        float panelY = quitButton.y - 40;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.15f, 0.15f, 0.2f, 1f));
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        shapeRenderer.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(0.4f, 0.4f, 0.5f, 1f));
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        shapeRenderer.end();
        
        renderButton(resumeButton, new Color(0.2f, 0.6f, 0.2f, 1f));
        renderButton(settingsButton, new Color(0.4f, 0.4f, 0.5f, 1f));
        renderButton(quitButton, new Color(0.6f, 0.2f, 0.2f, 1f));
    }
    
    private void renderButton(Rectangle bounds, Color color) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();
    }
    
    private void renderText(SpriteBatch batch) {
        float titleY = resumeButton.y + resumeButton.height + 60;
        titleFont.setColor(Color.WHITE);
        titleFont.draw(batch, "PAUSED", 
                       Gdx.graphics.getWidth() / 2f - 60, titleY);
        
        font.setColor(Color.WHITE);
        
        drawCenteredText(batch, "RESUME", resumeButton);
        drawCenteredText(batch, "SETTINGS", settingsButton);
        drawCenteredText(batch, "QUIT TO MENU", quitButton);
    }
    
    private void drawCenteredText(SpriteBatch batch, String text, Rectangle bounds) {
        float textWidth = text.length() * 8f;
        font.draw(batch, text,
                 bounds.x + bounds.width / 2 - textWidth / 2,
                 bounds.y + bounds.height / 2 + 8);
    }
    
    public boolean handleClick(float screenX, float screenY) {
        float y = Gdx.graphics.getHeight() - screenY;
        float x = screenX;
        
        if (resumeButton.contains(x, y)) {
            if (callback != null) callback.onResume();
            return true;
        }
        
        if (settingsButton.contains(x, y)) {
            if (callback != null) callback.onSettings();
            return true;
        }
        
        if (quitButton.contains(x, y)) {
            if (callback != null) callback.onQuitToMenu();
            return true;
        }
        
        return false;
    }
    
    public void resize(int width, int height) {
        calculateLayout();
    }
    
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
        titleFont.dispose();
    }
}