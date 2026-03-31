package com.cubefighter.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class TimerDisplay {
    
    private float x;
    private float y;
    private float width;
    private float height;
    
    private float currentTime;
    private float timeLimit;
    private float warningThreshold;
    
    private boolean running;
    private boolean paused;
    private boolean countdown;
    
    private Color backgroundColor;
    private Color normalColor;
    private Color warningColor;
    private Color criticalColor;
    private Color borderColor;
    
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    
    private String timeFormat;
    private boolean showMilliseconds;
    private boolean showBackground;
    
    private TimerCallback callback;
    
    public interface TimerCallback {
        void onTimeUp();
        void onWarning();
    }
    
    private boolean warningTriggered;
    
    public TimerDisplay(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        
        this.currentTime = 0f;
        this.timeLimit = 0f;
        this.warningThreshold = 30f;
        
        this.running = false;
        this.paused = false;
        this.countdown = true;
        
        this.backgroundColor = new Color(0.1f, 0.1f, 0.15f, 0.8f);
        this.normalColor = new Color(0.3f, 0.8f, 0.3f, 1f);
        this.warningColor = new Color(0.9f, 0.7f, 0.2f, 1f);
        this.criticalColor = new Color(0.9f, 0.2f, 0.2f, 1f);
        this.borderColor = new Color(1f, 1f, 1f, 0.5f);
        
        this.font = new BitmapFont();
        this.font.getData().setScale(2f);
        this.shapeRenderer = new ShapeRenderer();
        
        this.timeFormat = "%02d:%02d";
        this.showMilliseconds = false;
        this.showBackground = true;
        
        this.warningTriggered = false;
    }
    
    public void update(float delta) {
        if (!running || paused) {
            return;
        }
        
        if (countdown) {
            currentTime -= delta;
            
            if (currentTime <= warningThreshold && !warningTriggered) {
                warningTriggered = true;
                if (callback != null) {
                    callback.onWarning();
                }
            }
            
            if (currentTime <= 0) {
                currentTime = 0;
                running = false;
                if (callback != null) {
                    callback.onTimeUp();
                }
            }
        } else {
            currentTime += delta;
        }
    }
    
    public void render(SpriteBatch batch) {
        if (showBackground) {
            batch.end();
            renderBackground();
            batch.begin();
        }
        
        renderTime(batch);
    }
    
    private void renderBackground() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(backgroundColor);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(borderColor);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
    }
    
    private void renderTime(SpriteBatch batch) {
        String timeText = formatTime();
        
        Color timeColor = getTimeColor();
        font.setColor(timeColor);
        
        float textWidth = timeText.length() * 12f;
        float textHeight = 24f;
        
        float textX = x + width / 2 - textWidth / 2;
        float textY = y + height / 2 + textHeight / 2;
        
        if (isCritical() && running) {
            float pulse = (float) Math.sin(System.currentTimeMillis() * 0.01) * 0.3f + 0.7f;
            font.setColor(timeColor.r * pulse, timeColor.g * pulse, timeColor.b * pulse, 1f);
        }
        
        font.draw(batch, timeText, textX, textY);
    }
    
    private String formatTime() {
        int totalSeconds;
        if (countdown) {
            totalSeconds = Math.max(0, (int) Math.ceil(currentTime));
        } else {
            totalSeconds = (int) currentTime;
        }
        
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        
        if (showMilliseconds) {
            int millis = (int) ((currentTime % 1) * 100);
            return String.format("%02d:%02d.%02d", minutes, seconds, millis);
        }
        
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    private Color getTimeColor() {
        if (!countdown) {
            return normalColor;
        }
        
        if (timeLimit > 0) {
            float percent = currentTime / timeLimit;
            if (percent <= 0.1f) {
                return criticalColor;
            } else if (percent <= 0.25f) {
                return warningColor;
            }
        } else {
            if (currentTime <= 10f) {
                return criticalColor;
            } else if (currentTime <= warningThreshold) {
                return warningColor;
            }
        }
        
        return normalColor;
    }
    
    private boolean isCritical() {
        if (!countdown) return false;
        return currentTime <= 10f;
    }
    
    public void start() {
        running = true;
        paused = false;
    }
    
    public void pause() {
        if (running) {
            paused = true;
        }
    }
    
    public void resume() {
        if (running && paused) {
            paused = false;
        }
    }
    
    public void stop() {
        running = false;
        paused = false;
    }
    
    public void reset() {
        currentTime = timeLimit;
        warningTriggered = false;
        running = false;
        paused = false;
    }
    
    public void setTime(float seconds) {
        this.currentTime = seconds;
        if (countdown && timeLimit <= 0) {
            this.timeLimit = seconds;
        }
    }
    
    public void setTimeLimit(float seconds) {
        this.timeLimit = seconds;
        if (currentTime > seconds) {
            currentTime = seconds;
        }
    }
    
    public void setCountdown(float seconds) {
        this.countdown = true;
        this.timeLimit = seconds;
        this.currentTime = seconds;
    }
    
    public void setStopwatch() {
        this.countdown = false;
        this.timeLimit = 0;
        this.currentTime = 0;
    }
    
    public void addTime(float seconds) {
        currentTime += seconds;
        if (countdown && timeLimit > 0 && currentTime > timeLimit) {
            currentTime = timeLimit;
        }
    }
    
    public void subtractTime(float seconds) {
        currentTime -= seconds;
        if (currentTime < 0) {
            currentTime = 0;
        }
    }
    
    public void setCallback(TimerCallback callback) {
        this.callback = callback;
    }
    
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }
    
    public void setWarningThreshold(float seconds) {
        this.warningThreshold = seconds;
    }
    
    public void setShowMilliseconds(boolean show) {
        this.showMilliseconds = show;
    }
    
    public void setShowBackground(boolean show) {
        this.showBackground = show;
    }
    
    public void setNormalColor(Color color) {
        this.normalColor = new Color(color);
    }
    
    public void setWarningColor(Color color) {
        this.warningColor = new Color(color);
    }
    
    public void setCriticalColor(Color color) {
        this.criticalColor = new Color(color);
    }
    
    public void setBackgroundColor(Color color) {
        this.backgroundColor = new Color(color);
    }
    
    public void setFont(BitmapFont font) {
        this.font = font;
    }
    
    public float getTime() {
        return currentTime;
    }
    
    public float getTimeLimit() {
        return timeLimit;
    }
    
    public int getMinutes() {
        return (int) currentTime / 60;
    }
    
    public int getSeconds() {
        return (int) currentTime % 60;
    }
    
    public boolean isRunning() {
        return running && !paused;
    }
    
    public boolean isPaused() {
        return paused;
    }
    
    public boolean isFinished() {
        return countdown && currentTime <= 0;
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
    
    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
    }
}