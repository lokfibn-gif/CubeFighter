package com.cubefighter.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Dialog {
    
    public enum DialogType {
        INFO,
        CONFIRM,
        CUSTOM
    }
    
    private String title;
    private String message;
    private DialogType type;
    
    private Array<Button> buttons;
    private DialogCallback callback;
    
    private Rectangle bounds;
    private Rectangle titleBounds;
    private Rectangle messageBounds;
    
    private float animationTime;
    private float animationDuration;
    private boolean animatingIn;
    private boolean animatingOut;
    private boolean visible;
    private boolean closing;
    
    private Color backgroundColor;
    private Color titleBarColor;
    private Color panelColor;
    
    private BitmapFont titleFont;
    private BitmapFont messageFont;
    private ShapeRenderer shapeRenderer;
    
    private static final float PADDING = 20f;
    private static final float BUTTON_WIDTH = 120f;
    private static final float BUTTON_HEIGHT = 40f;
    private static final float BUTTON_SPACING = 15f;
    
    public interface DialogCallback {
        void onConfirm();
        void onCancel();
        void onButtonClicked(int buttonIndex);
    }
    
    public Dialog() {
        this.buttons = new Array<>();
        this.type = DialogType.INFO;
        
        this.animationTime = 0f;
        this.animationDuration = 0.2f;
        this.animatingIn = false;
        this.animatingOut = false;
        this.visible = false;
        this.closing = false;
        
        this.backgroundColor = new Color(0, 0, 0, 0.5f);
        this.titleBarColor = new Color(0.3f, 0.4f, 0.6f, 1f);
        this.panelColor = new Color(0.15f, 0.15f, 0.2f, 1f);
        
        this.titleFont = new BitmapFont();
        this.titleFont.getData().setScale(2f);
        
        this.messageFont = new BitmapFont();
        this.messageFont.getData().setScale(1.5f);
        
        this.shapeRenderer = new ShapeRenderer();
        
        this.bounds = new Rectangle();
        this.titleBounds = new Rectangle();
        this.messageBounds = new Rectangle();
    }
    
    public void showInfo(String title, String message) {
        this.type = DialogType.INFO;
        this.title = title;
        this.message = message;
        
        buttons.clear();
        createButton("OK", new Button.ClickCallback() {
            @Override
            public void onClick() {
                close();
                if (callback != null) callback.onConfirm();
            }
        });
        
        calculateLayout();
        startAnimation(true);
    }
    
    public void showConfirm(String title, String message) {
        this.type = DialogType.CONFIRM;
        this.title = title;
        this.message = message;
        
        buttons.clear();
        createButton("Yes", new Button.ClickCallback() {
            @Override
            public void onClick() {
                close();
                if (callback != null) callback.onConfirm();
            }
        });
        createButton("No", new Button.ClickCallback() {
            @Override
            public void onClick() {
                close();
                if (callback != null) callback.onCancel();
            }
        });
        
        calculateLayout();
        startAnimation(true);
    }
    
    public void showCustom(String title, String message, String[] buttonLabels) {
        this.type = DialogType.CUSTOM;
        this.title = title;
        this.message = message;
        
        buttons.clear();
        for (int i = 0; i < buttonLabels.length; i++) {
            final int index = i;
            createButton(buttonLabels[i], new Button.ClickCallback() {
                @Override
                public void onClick() {
                    close();
                    if (callback != null) callback.onButtonClicked(index);
                }
            });
        }
        
        calculateLayout();
        startAnimation(true);
    }
    
    private void createButton(String text, Button.ClickCallback clickCallback) {
        Button button = new Button(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT, text);
        button.setCallback(clickCallback);
        buttons.add(button);
    }
    
    private void calculateLayout() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        
        int lines = countLines(message);
        float messageHeight = lines * 25f + 20f;
        
        float dialogWidth = 400f;
        float dialogHeight = 120f + messageHeight + (buttons.size > 0 ? BUTTON_HEIGHT + PADDING : 0);
        
        bounds.set(
            (screenWidth - dialogWidth) / 2,
            (screenHeight - dialogHeight) / 2,
            dialogWidth,
            dialogHeight
        );
        
        titleBounds.set(
            bounds.x,
            bounds.y + bounds.height - 40,
            bounds.width,
            40
        );
        
        messageBounds.set(
            bounds.x + PADDING,
            bounds.y + PADDING + (buttons.size > 0 ? BUTTON_HEIGHT + BUTTON_SPACING : 0),
            bounds.width - PADDING * 2,
            messageHeight
        );
        
        if (buttons.size > 0) {
            float totalButtonWidth = buttons.size * BUTTON_WIDTH + (buttons.size - 1) * BUTTON_SPACING;
            float startX = bounds.x + (bounds.width - totalButtonWidth) / 2;
            
            for (int i = 0; i < buttons.size; i++) {
                Button btn = buttons.get(i);
                btn.setPosition(startX + i * (BUTTON_WIDTH + BUTTON_SPACING), bounds.y + PADDING);
            }
        }
    }
    
    private int countLines(String text) {
        if (text == null || text.isEmpty()) return 1;
        int lines = 1;
        for (char c : text.toCharArray()) {
            if (c == '\n') lines++;
        }
        return lines;
    }
    
    private void startAnimation(boolean show) {
        if (show) {
            visible = true;
            animatingIn = true;
            animatingOut = false;
            closing = false;
            animationTime = 0f;
        } else {
            animatingOut = true;
            animatingIn = false;
            closing = true;
            animationTime = 0f;
        }
    }
    
    public void update(float delta) {
        if (!visible) return;
        
        if (animatingIn || animatingOut) {
            animationTime += delta;
            if (animationTime >= animationDuration) {
                animatingIn = false;
                animatingOut = false;
                if (closing) {
                    visible = false;
                    closing = false;
                }
            }
        }
        
        for (Button btn : buttons) {
            btn.update(delta);
        }
    }
    
    public void render(SpriteBatch batch) {
        if (!visible) return;
        
        float progress = animationTime / animationDuration;
        if (animatingIn) {
            progress = easeOutBack(progress);
        } else if (animatingOut) {
            progress = 1f - progress;
        } else {
            progress = 1f;
        }
        
        batch.end();
        renderOverlay(progress);
        renderPanel(batch, progress);
        batch.begin();
        
        renderTitle(batch, progress);
        renderMessage(batch, progress);
        renderButtons(batch);
    }
    
    private float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        return 1f + c3 * (float) Math.pow(t - 1, 3) + c1 * (float) Math.pow(t - 1, 2);
    }
    
    private void renderOverlay(float progress) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.5f * progress);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
    }
    
    private void renderPanel(SpriteBatch batch, float progress) {
        float scale = progress;
        float scaledWidth = bounds.width * scale;
        float scaledHeight = bounds.height * scale;
        float scaledX = bounds.x + (bounds.width - scaledWidth) / 2;
        float scaledY = bounds.y + (bounds.height - scaledHeight) / 2;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(panelColor.r, panelColor.g, panelColor.b, panelColor.a * progress);
        shapeRenderer.rect(scaledX, scaledY, scaledWidth, scaledHeight);
        
        shapeRenderer.setColor(titleBarColor.r, titleBarColor.g, titleBarColor.b, titleBarColor.a * progress);
        shapeRenderer.rect(scaledX, scaledY + scaledHeight - 40, scaledWidth, 40);
        shapeRenderer.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 0.8f * progress);
        shapeRenderer.rect(scaledX, scaledY, scaledWidth, scaledHeight);
        shapeRenderer.end();
    }
    
    private void renderTitle(SpriteBatch batch, float progress) {
        if (title == null) return;
        
        titleFont.setColor(1f, 1f, 1f, progress);
        float titleWidth = title.length() * 16f;
        titleFont.draw(batch, title,
            bounds.x + bounds.width / 2 - titleWidth / 2,
            bounds.y + bounds.height - 15);
    }
    
    private void renderMessage(SpriteBatch batch, float progress) {
        if (message == null) return;
        
        messageFont.setColor(1f, 1f, 1f, progress);
        String[] lines = message.split("\n");
        float lineHeight = 25f;
        float startY = messageBounds.y + messageBounds.height - 10;
        
        for (int i = 0; i < lines.length; i++) {
            float lineWidth = lines[i].length() * 10f;
            messageFont.draw(batch, lines[i],
                bounds.x + bounds.width / 2 - lineWidth / 2,
                startY - i * lineHeight);
        }
    }
    
    private void renderButtons(SpriteBatch batch) {
        for (Button btn : buttons) {
            btn.render(batch);
        }
    }
    
    public boolean handleClick(float screenX, float screenY) {
        if (!visible || animatingIn || animatingOut) return false;
        
        float y = Gdx.graphics.getHeight() - screenY;
        
        for (Button btn : buttons) {
            if (btn.isInside(screenX, y)) {
                return btn.handleClick(screenX, y);
            }
        }
        
        return true;
    }
    
    public void close() {
        if (visible && !closing) {
            startAnimation(false);
        }
    }
    
    public void setCallback(DialogCallback callback) {
        this.callback = callback;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setBackgroundColor(Color color) {
        this.panelColor = new Color(color);
    }
    
    public void setTitleBarColor(Color color) {
        this.titleBarColor = new Color(color);
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public boolean isAnimating() {
        return animatingIn || animatingOut;
    }
    
    public void showImmediate(String title, String message) {
        visible = true;
        animatingIn = false;
        animatingOut = false;
        closing = false;
    }
    
    public void dispose() {
        titleFont.dispose();
        messageFont.dispose();
        shapeRenderer.dispose();
        for (Button btn : buttons) {
            btn.dispose();
        }
    }
}