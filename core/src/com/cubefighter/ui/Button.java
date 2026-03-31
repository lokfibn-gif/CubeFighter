package com.cubefighter.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Button {
    
    public enum State {
        NORMAL,
        PRESSED,
        DISABLED
    }
    
    private Rectangle bounds;
    private String text;
    private State state;
    private ClickCallback clickCallback;
    
    private Color normalColor;
    private Color pressedColor;
    private Color disabledColor;
    private Color borderColor;
    private Color textColor;
    
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    
    private float pressTime;
    private static final float PRESS_DURATION = 0.1f;
    
    public interface ClickCallback {
        void onClick();
    }
    
    public Button(float x, float y, float width, float height, String text) {
        this.bounds = new Rectangle(x, y, width, height);
        this.text = text;
        this.state = State.NORMAL;
        this.clickCallback = null;
        
        this.normalColor = new Color(0.2f, 0.5f, 0.8f, 1f);
        this.pressedColor = new Color(0.1f, 0.3f, 0.6f, 1f);
        this.disabledColor = new Color(0.3f, 0.3f, 0.3f, 1f);
        this.borderColor = new Color(1f, 1f, 1f, 0.8f);
        this.textColor = new Color(Color.WHITE);
        
        this.font = new BitmapFont();
        this.font.getData().setScale(1.5f);
        this.shapeRenderer = new ShapeRenderer();
        this.pressTime = 0f;
    }
    
    public void update(float delta) {
        if (state == State.PRESSED && pressTime > 0) {
            pressTime -= delta;
            if (pressTime <= 0) {
                state = State.NORMAL;
            }
        }
    }
    
    public void render(SpriteBatch batch) {
        batch.end();
        
        Color backgroundColor = getBackgroundColor();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(backgroundColor);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(state == State.DISABLED ? Color.GRAY : borderColor);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();
        
        batch.begin();
        renderText(batch);
    }
    
    private Color getBackgroundColor() {
        switch (state) {
            case NORMAL:
                return normalColor;
            case PRESSED:
                return pressedColor;
            case DISABLED:
                return disabledColor;
            default:
                return normalColor;
        }
    }
    
    private void renderText(SpriteBatch batch) {
        if (state == State.DISABLED) {
            font.setColor(Color.GRAY);
        } else {
            font.setColor(textColor);
        }
        
        float textOffset = state == State.PRESSED ? -2f : 0f;
        
        float textWidth = text.length() * 8f * font.getScaleX();
        float textHeight = 16f * font.getScaleY();
        
        font.draw(batch, text,
            bounds.x + bounds.width / 2 - textWidth / 2,
            bounds.y + bounds.height / 2 + textHeight / 3 + textOffset);
    }
    
    public boolean handleClick(float screenX, float screenY) {
        if (state == State.DISABLED) {
            return false;
        }
        
        if (bounds.contains(screenX, screenY)) {
            state = State.PRESSED;
            pressTime = PRESS_DURATION;
            
            if (clickCallback != null) {
                clickCallback.onClick();
            }
            return true;
        }
        
        return false;
    }
    
    public boolean isInside(float screenX, float screenY) {
        return bounds.contains(screenX, screenY);
    }
    
    public void setCallback(ClickCallback callback) {
        this.clickCallback = callback;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
    
    public void setPosition(float x, float y) {
        bounds.setPosition(x, y);
    }
    
    public void setSize(float width, float height) {
        bounds.setSize(width, height);
    }
    
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
    
    public void setState(State state) {
        this.state = state;
    }
    
    public State getState() {
        return state;
    }
    
    public void setEnabled(boolean enabled) {
        this.state = enabled ? State.NORMAL : State.DISABLED;
    }
    
    public boolean isEnabled() {
        return state != State.DISABLED;
    }
    
    public void setNormalColor(Color color) {
        this.normalColor = new Color(color);
    }
    
    public void setPressedColor(Color color) {
        this.pressedColor = new Color(color);
    }
    
    public void setDisabledColor(Color color) {
        this.disabledColor = new Color(color);
    }
    
    public void setBorderColor(Color color) {
        this.borderColor = new Color(color);
    }
    
    public void setTextColor(Color color) {
        this.textColor = new Color(color);
    }
    
    public void setFont(BitmapFont font) {
        this.font = font;
    }
    
    public float getX() {
        return bounds.x;
    }
    
    public float getY() {
        return bounds.y;
    }
    
    public float getWidth() {
        return bounds.width;
    }
    
    public float getHeight() {
        return bounds.height;
    }
    
    public void centerOn(float x, float y) {
        bounds.setPosition(x - bounds.width / 2, y - bounds.height / 2);
    }
    
    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
    }
}