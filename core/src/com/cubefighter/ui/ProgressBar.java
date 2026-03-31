package com.cubefighter.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ProgressBar {
    
    private float x;
    private float y;
    private float width;
    private float height;
    
    private float currentValue;
    private float maxValue;
    private float displayValue;
    
    private Color backgroundColor;
    private Color fillColor;
    private Color borderColor;
    
    private boolean showText;
    private String textPrefix;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    
    private float animationSpeed;
    private boolean animateFill;
    
    private static final float DEFAULT_ANIMATION_SPEED = 100f;
    
    public ProgressBar(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        
        this.currentValue = 0;
        this.maxValue = 100;
        this.displayValue = 0;
        
        this.backgroundColor = new Color(0.2f, 0.2f, 0.2f, 1f);
        this.fillColor = new Color(0f, 0.8f, 0f, 1f);
        this.borderColor = new Color(1f, 1f, 1f, 0.8f);
        
        this.showText = true;
        this.textPrefix = "";
        this.font = new BitmapFont();
        this.font.getData().setScale(1.2f);
        this.shapeRenderer = new ShapeRenderer();
        
        this.animationSpeed = DEFAULT_ANIMATION_SPEED;
        this.animateFill = true;
    }
    
    public void update(float delta) {
        if (animateFill) {
            if (Math.abs(displayValue - currentValue) > 0.5f) {
                if (displayValue < currentValue) {
                    displayValue += animationSpeed * delta;
                    if (displayValue > currentValue) {
                        displayValue = currentValue;
                    }
                } else {
                    displayValue -= animationSpeed * delta;
                    if (displayValue < currentValue) {
                        displayValue = currentValue;
                    }
                }
            }
        } else {
            displayValue = currentValue;
        }
    }
    
    public void render(SpriteBatch batch) {
        batch.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(backgroundColor);
        shapeRenderer.rect(x, y, width, height);
        
        float fillPercent = getDisplayPercent();
        if (fillPercent > 0) {
            shapeRenderer.setColor(fillColor);
            shapeRenderer.rect(x, y, width * fillPercent, height);
        }
        shapeRenderer.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(borderColor);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
        
        batch.begin();
        renderText(batch);
    }
    
    private void renderText(SpriteBatch batch) {
        if (!showText) {
            return;
        }
        
        font.setColor(Color.WHITE);
        
        String text;
        if (textPrefix != null && !textPrefix.isEmpty()) {
            text = String.format("%s%d/%d", textPrefix, (int) currentValue, (int) maxValue);
        } else {
            text = String.format("%d/%d", (int) currentValue, (int) maxValue);
        }
        
        float textWidth = text.length() * 7f;
        float textHeight = 14f;
        
        font.draw(batch, text,
            x + width / 2 - textWidth / 2,
            y + height / 2 + textHeight / 2);
    }
    
    public void setValue(float current, float max) {
        this.currentValue = Math.min(current, max);
        this.maxValue = Math.max(max, 1);
        if (!animateFill) {
            displayValue = this.currentValue;
        }
    }
    
    public void setValue(float current) {
        setValue(current, this.maxValue);
    }
    
    public void setValueDirect(float current, float max) {
        this.currentValue = Math.min(current, max);
        this.maxValue = Math.max(max, 1);
        this.displayValue = this.currentValue;
    }
    
    public float getPercent() {
        return maxValue > 0 ? currentValue / maxValue : 0;
    }
    
    public float getDisplayPercent() {
        return maxValue > 0 ? displayValue / maxValue : 0;
    }
    
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }
    
    public void setBackgroundColor(Color color) {
        this.backgroundColor = new Color(color);
    }
    
    public void setFillColor(Color color) {
        this.fillColor = new Color(color);
    }
    
    public void setHealthColors() {
        this.backgroundColor = new Color(0.3f, 0.1f, 0.1f, 1f);
        this.fillColor = new Color(0.8f, 0.2f, 0.2f, 1f);
    }
    
    public void setHealthColorsDynamic() {
        float percent = getPercent();
        if (percent > 0.6f) {
            fillColor = new Color(0.2f, 0.8f, 0.2f, 1f);
        } else if (percent > 0.3f) {
            fillColor = new Color(0.9f, 0.6f, 0.1f, 1f);
        } else {
            fillColor = new Color(0.9f, 0.2f, 0.2f, 1f);
        }
    }
    
    public void setXPColors() {
        this.backgroundColor = new Color(0.1f, 0.1f, 0.2f, 1f);
        this.fillColor = new Color(0.3f, 0.5f, 0.9f, 1f);
    }
    
    public void setManaColors() {
        this.backgroundColor = new Color(0.1f, 0.1f, 0.3f, 1f);
        this.fillColor = new Color(0.2f, 0.4f, 0.9f, 1f);
    }
    
    public void setBorderColor(Color color) {
        this.borderColor = new Color(color);
    }
    
    public void setShowText(boolean show) {
        this.showText = show;
    }
    
    public void setTextPrefix(String prefix) {
        this.textPrefix = prefix;
    }
    
    public void setFont(BitmapFont font) {
        this.font = font;
    }
    
    public void setAnimationSpeed(float speed) {
        this.animationSpeed = speed;
    }
    
    public void setAnimateFill(boolean animate) {
        this.animateFill = animate;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getWidth() {
        return width;
    }
    
    public float getHeight() {
        return height;
    }
    
    public float getCurrentValue() {
        return currentValue;
    }
    
    public float getMaxValue() {
        return maxValue;
    }
    
    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
    }
}