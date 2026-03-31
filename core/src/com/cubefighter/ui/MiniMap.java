package com.cubefighter.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class MiniMap {
    
    private float x;
    private float y;
    private float width;
    private float height;
    
    private float arenaWidth;
    private float arenaHeight;
    
    private Vector2 playerPosition;
    private Array<Vector2> enemyPositions;
    private Array<Vector2> bossPositions;
    private Array<Vector2> pickupPositions;
    
    private float playerSize;
    private float enemySize;
    private float bossSize;
    private float pickupSize;
    
    private Color backgroundColor;
    private Color borderColor;
    private Color playerColor;
    private Color enemyColor;
    private Color bossColor;
    private Color pickupColor;
    private Color wallColor;
    
    private boolean visible;
    private boolean showPlayer;
    private boolean showEnemies;
    private boolean showBosses;
    private boolean showPickups;
    
    private MinimapClickListener clickListener;
    
    public interface MinimapClickListener {
        void onClick(float worldX, float worldY);
    }
    
    public MiniMap(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        
        this.arenaWidth = 1000f;
        this.arenaHeight = 1000f;
        
        this.playerPosition = new Vector2();
        this.enemyPositions = new Array<>();
        this.bossPositions = new Array<>();
        this.pickupPositions = new Array<>();
        
        this.playerSize = 6f;
        this.enemySize = 4f;
        this.bossSize = 8f;
        this.pickupSize = 3f;
        
        this.backgroundColor = new Color(0.1f, 0.1f, 0.15f, 0.7f);
        this.borderColor = new Color(1f, 1f, 1f, 0.5f);
        this.playerColor = new Color(0.2f, 0.8f, 0.2f, 1f);
        this.enemyColor = new Color(0.9f, 0.2f, 0.2f, 1f);
        this.bossColor = new Color(0.9f, 0.1f, 0.9f, 1f);
        this.pickupColor = new Color(1f, 1f, 0.2f, 1f);
        this.wallColor = new Color(0.5f, 0.5f, 0.5f, 1f);
        
        this.visible = true;
        this.showPlayer = true;
        this.showEnemies = true;
        this.showBosses = true;
        this.showPickups = true;
    }
    
    public void update(float delta) {
    }
    
    public void render(ShapeRenderer shapeRenderer) {
        if (!visible) return;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(backgroundColor);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
        
        renderWalls(shapeRenderer);
        
        if (showPickups && pickupPositions.size > 0) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(pickupColor);
            for (Vector2 pos : pickupPositions) {
                float mapX = worldToMapX(pos.x);
                float mapY = worldToMapY(pos.y);
                shapeRenderer.circle(mapX, mapY, pickupSize);
            }
            shapeRenderer.end();
        }
        
        if (showEnemies && enemyPositions.size > 0) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(enemyColor);
            for (Vector2 pos : enemyPositions) {
                float mapX = worldToMapX(pos.x);
                float mapY = worldToMapY(pos.y);
                shapeRenderer.rect(mapX - enemySize / 2, mapY - enemySize / 2, enemySize, enemySize);
            }
            shapeRenderer.end();
        }
        
        if (showBosses && bossPositions.size > 0) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(bossColor);
            for (Vector2 pos : bossPositions) {
                float mapX = worldToMapX(pos.x);
                float mapY = worldToMapY(pos.y);
                drawDiamond(shapeRenderer, mapX, mapY, bossSize);
            }
            shapeRenderer.end();
        }
        
        if (showPlayer) {
            float mapX = worldToMapX(playerPosition.x);
            float mapY = worldToMapY(playerPosition.y);
            
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(playerColor);
            drawPlayerTriangle(shapeRenderer, mapX, mapY, playerSize);
            shapeRenderer.end();
        }
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(borderColor);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
    }
    
    private void renderWalls(ShapeRenderer shapeRenderer) {
        float wallthickness = 2f;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(wallColor);
        
        shapeRenderer.rect(x, y, width, wallthickness);
        shapeRenderer.rect(x, y + height - wallthickness, width, wallthickness);
        shapeRenderer.rect(x, y, wallthickness, height);
        shapeRenderer.rect(x + width - wallthickness, y, wallthickness, height);
        
        shapeRenderer.end();
    }
    
    private void drawPlayerTriangle(ShapeRenderer renderer, float cx, float cy, float size) {
        renderer.triangle(
            cx, cy + size,
            cx - size * 0.866f, cy - size / 2,
            cx + size * 0.866f, cy - size / 2
        );
    }
    
    private void drawDiamond(ShapeRenderer renderer, float cx, float cy, float size) {
        renderer.triangle(cx, cy + size, cx - size / 2, cy, cx + size / 2, cy);
        renderer.triangle(cx, cy - size, cx - size / 2, cy, cx + size / 2, cy);
    }
    
    private float worldToMapX(float worldX) {
        return x + (worldX / arenaWidth) * width;
    }
    
    private float worldToMapY(float worldY) {
        return y + (worldY / arenaHeight) * height;
    }
    
    public Vector2 mapToWorld(float mapX, float mapY) {
        float worldX = ((mapX - x) / width) * arenaWidth;
        float worldY = ((mapY - y) / height) * arenaHeight;
        return new Vector2(worldX, worldY);
    }
    
    public void setPlayerPosition(float worldX, float worldY) {
        playerPosition.set(worldX, worldY);
    }
    
    public void setPlayerPosition(Vector2 position) {
        playerPosition.set(position);
    }
    
    public void clearEnemies() {
        enemyPositions.clear();
    }
    
    public void addEnemy(float worldX, float worldY) {
        enemyPositions.add(new Vector2(worldX, worldY));
    }
    
    public void addEnemy(Vector2 position) {
        enemyPositions.add(new Vector2(position));
    }
    
    public void clearBosses() {
        bossPositions.clear();
    }
    
    public void addBoss(float worldX, float worldY) {
        bossPositions.add(new Vector2(worldX, worldY));
    }
    
    public void addBoss(Vector2 position) {
        bossPositions.add(new Vector2(position));
    }
    
    public void clearPickups() {
        pickupPositions.clear();
    }
    
    public void addPickup(float worldX, float worldY) {
        pickupPositions.add(new Vector2(worldX, worldY));
    }
    
    public void addPickup(Vector2 position) {
        pickupPositions.add(new Vector2(position));
    }
    
    public boolean handleClick(float screenX, float screenY, int screenHeight) {
        float y = screenHeight - screenY;
        
        if (screenX >= x && screenX <= x + width && y >= y && y <= y + height) {
            Vector2 worldPos = mapToWorld(screenX, y);
            if (clickListener != null) {
                clickListener.onClick(worldPos.x, worldPos.y);
            }
            return true;
        }
        return false;
    }
    
    public void setClickListener(MinimapClickListener listener) {
        this.clickListener = listener;
    }
    
    public void setArenaSize(float width, float height) {
        this.arenaWidth = width;
        this.arenaHeight = height;
    }
    
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setShowPlayer(boolean show) {
        this.showPlayer = show;
    }
    
    public void setShowEnemies(boolean show) {
        this.showEnemies = show;
    }
    
    public void setShowBosses(boolean show) {
        this.showBosses = show;
    }
    
    public void setShowPickups(boolean show) {
        this.showPickups = show;
    }
    
    public void setPlayerColor(Color color) {
        this.playerColor = new Color(color);
    }
    
    public void setEnemyColor(Color color) {
        this.enemyColor = new Color(color);
    }
    
    public void setBossColor(Color color) {
        this.bossColor = new Color(color);
    }
    
    public void setPickupColor(Color color) {
        this.pickupColor = new Color(color);
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
    
    public Vector2 getPlayerPosition() {
        return playerPosition;
    }
    
    public int getEnemyCount() {
        return enemyPositions.size;
    }
    
    public int getBossCount() {
        return bossPositions.size;
    }
}