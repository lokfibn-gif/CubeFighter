package com.cubefighter.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class MainMenuUI {
    private ShapeRenderer shapeRenderer;
    private BitmapFont titleFont;
    private BitmapFont font;
    
    private Rectangle playButton;
    private Rectangle settingsButton;
    private Rectangle upgradesButton;
    private Rectangle statsArea;
    
    private int highScore = 0;
    private int totalKills = 0;
    private int gamesPlayed = 0;
    private int gold = 0;
    
    private ButtonState playState = ButtonState.NORMAL;
    private ButtonState settingsState = ButtonState.NORMAL;
    private ButtonState upgradesState = ButtonState.NORMAL;
    
    private enum ButtonState { NORMAL, HOVERED, PRESSED }
    
    public interface MenuCallback {
        void onPlay();
        void onSettings();
        void onUpgrades();
    }
    
    private MenuCallback callback;
    
    private static final float BUTTON_WIDTH = 250;
    private static final float BUTTON_HEIGHT = 60;
    private static final float BUTTON_SPACING = 20;
    
    public MainMenuUI() {
        shapeRenderer = new ShapeRenderer();
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(4f);
        
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        
        calculateLayout();
    }
    
    private void calculateLayout() {
        float centerX = Gdx.graphics.getWidth() / 2f;
        float startY = Gdx.graphics.getHeight() / 2f;
        
        playButton = new Rectangle(centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT);
        settingsButton = new Rectangle(centerX - BUTTON_WIDTH / 2, startY - BUTTON_HEIGHT - BUTTON_SPACING, 
                                       BUTTON_WIDTH, BUTTON_HEIGHT);
        upgradesButton = new Rectangle(centerX - BUTTON_WIDTH / 2, 
                                       startY - 2 * (BUTTON_HEIGHT + BUTTON_SPACING),
                                       BUTTON_WIDTH, BUTTON_HEIGHT);
        
        statsArea = new Rectangle(20, 20, 300, 150);
    }
    
    public void setCallback(MenuCallback callback) {
        this.callback = callback;
    }
    
    public void setStats(int highScore, int totalKills, int gamesPlayed, int gold) {
        this.highScore = highScore;
        this.totalKills = totalKills;
        this.gamesPlayed = gamesPlayed;
        this.gold = gold;
    }
    
    public void render(SpriteBatch batch) {
        batch.end();
        
        renderBackground();
        renderButtons();
        renderStatsArea();
        
        batch.begin();
        renderText(batch);
    }
    
    private void renderBackground() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.2f, 1f));
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        for (int i = 0; i < 20; i++) {
            float x = (i * 157f + System.currentTimeMillis() * 0.01f) % Gdx.graphics.getWidth();
            float y = (i * 97f + System.currentTimeMillis() * 0.02f) % Gdx.graphics.getHeight();
            float size = 20 + (i % 5) * 10;
            float alpha = 0.1f + (i % 3) * 0.05f;
            shapeRenderer.setColor(new Color(0.2f, 0.3f, 0.5f, alpha));
            shapeRenderer.rect(x, y, size, size);
        }
        
        shapeRenderer.end();
    }
    
    private void renderButtons() {
        renderButton(playButton, Color.GREEN, playState);
        renderButton(settingsButton, Color.GRAY, settingsState);
        renderButton(upgradesButton, Color.GOLD, upgradesState);
    }
    
    private void renderButton(Rectangle bounds, Color baseColor, ButtonState state) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        Color color;
        if (state == ButtonState.PRESSED) {
            color = baseColor.cpy().mul(0.6f);
        } else if (state == ButtonState.HOVERED) {
            color = baseColor.cpy().mul(1.2f);
        } else {
            color = baseColor;
        }
        
        shapeRenderer.setColor(color);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();
    }
    
    private void renderStatsArea() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.15f, 0.15f, 0.2f, 0.8f));
        shapeRenderer.rect(statsArea.x, statsArea.y, statsArea.width, statsArea.height);
        shapeRenderer.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(0.4f, 0.4f, 0.5f, 1f));
        shapeRenderer.rect(statsArea.x, statsArea.y, statsArea.width, statsArea.height);
        shapeRenderer.end();
    }
    
    private void renderText(SpriteBatch batch) {
        float titleY = Gdx.graphics.getHeight() - 100;
        titleFont.setColor(Color.CYAN);
        
        String title = "CUBE FIGHTER";
        float titleWidth = titleFont.getRegion().getRegionWidth() * 4f;
        float shadowOffset = 3f;
        
        titleFont.setColor(new Color(0f, 0.2f, 0.4f, 1f));
        titleFont.draw(batch, title, Gdx.graphics.getWidth() / 2f - titleWidth / 2 + shadowOffset, 
                       titleY - shadowOffset);
        
        titleFont.setColor(Color.CYAN);
        titleFont.draw(batch, title, Gdx.graphics.getWidth() / 2f - titleWidth / 2, titleY);
        
        font.setColor(Color.WHITE);
        
        drawButtonText(batch, "PLAY", playButton);
        
        font.setColor(Color.LIGHT_GRAY);
        drawButtonText(batch, "SETTINGS", settingsButton);
        
        font.setColor(Color.YELLOW);
        drawButtonText(batch, "UPGRADES", upgradesButton);
        
        font.setColor(Color.WHITE);
        font.draw(batch, "High Score: " + highScore, statsArea.x + 10, statsArea.y + 130);
        font.draw(batch, "Kills: " + totalKills, statsArea.x + 10, statsArea.y + 100);
        font.draw(batch, "Games: " + gamesPlayed, statsArea.x + 10, statsArea.y + 70);
        
        font.setColor(Color.GOLD);
        font.draw(batch, "Gold: " + gold, statsArea.x + 10, statsArea.y + 40);
    }
    
    private void drawButtonText(SpriteBatch batch, String text, Rectangle button) {
        float textWidth = font.getRegion().getRegionWidth();
        float textHeight = font.getRegion().getRegionHeight();
        font.draw(batch, text, 
                 button.x + button.width / 2 - textWidth / 2,
                 button.y + button.height / 2 + textHeight / 2);
    }
    
    public void updateMouse(float screenX, float screenY) {
        float y = Gdx.graphics.getHeight() - screenY;
        float x = screenX;
        
        playState = getButtonState(x, y, playButton, playState);
        settingsState = getButtonState(x, y, settingsButton, settingsState);
        upgradesState = getButtonState(x, y, upgradesButton, upgradesState);
    }
    
    private ButtonState getButtonState(float x, float y, Rectangle button, ButtonState currentState) {
        boolean contained = button.contains(x, y);
        if (contained && currentState == ButtonState.NORMAL) {
            return ButtonState.HOVERED;
        } else if (!contained && currentState == ButtonState.HOVERED) {
            return ButtonState.NORMAL;
        }
        return currentState;
    }
    
    public boolean handleClick(float screenX, float screenY) {
        float y = Gdx.graphics.getHeight() - screenY;
        float x = screenX;
        
        if (playButton.contains(x, y)) {
            if (callback != null) callback.onPlay();
            return true;
        }
        if (settingsButton.contains(x, y)) {
            if (callback != null) callback.onSettings();
            return true;
        }
        if (upgradesButton.contains(x, y)) {
            if (callback != null) callback.onUpgrades();
            return true;
        }
        
        return false;
    }
    
    public void resize(int width, int height) {
        calculateLayout();
    }
    
    public void dispose() {
        shapeRenderer.dispose();
        titleFont.dispose();
        font.dispose();
    }
}