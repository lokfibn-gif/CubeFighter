package com.cubefighter.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class UpgradeUI {
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private BitmapFont titleFont;
    
    private UpgradeCategory[] categories;
    private int selectedIndex = 0;
    private int gold = 0;
    private Runnable onBackCallback;
    
    private static final float CATEGORY_WIDTH = 200f;
    private static final float CATEGORY_HEIGHT = 280f;
    private static final float PREVIEW_SIZE = 80f;
    private static final float BUTTON_WIDTH = 160f;
    private static final float BUTTON_HEIGHT = 40f;
    
    private enum UpgradeType {
        SIZE("Size", "+10% cube size", Color.BLUE),
        WEAPON("Weapon", "+15% damage", Color.RED),
        STATS("Stats", "+20 HP", Color.GREEN),
        ABILITIES("Abilities", "-1s cooldown", Color.PURPLE);
        
        String name;
        String description;
        Color color;
        
        UpgradeType(String name, String desc, Color color) {
            this.name = name;
            this.description = desc;
            this.color = color;
        }
    }
    
    private static class UpgradeCategory {
        UpgradeType type;
        int level;
        int maxLevel;
        int baseCost;
        Rectangle bounds;
        Rectangle buttonBounds;
        Rectangle previewBounds;
        
        UpgradeCategory(UpgradeType type, int maxLevel, int baseCost) {
            this.type = type;
            this.level = 0;
            this.maxLevel = maxLevel;
            this.baseCost = baseCost;
        }
        
        int getCost() {
            return baseCost + (level * baseCost / 2);
        }
        
        boolean canUpgrade() {
            return level < maxLevel;
        }
        
        float getNextBonus() {
            switch (type) {
                case SIZE: return 0.1f;
                case WEAPON: return 0.15f;
                case STATS: return 20f;
                case ABILITIES: return 1f;
                default: return 0f;
            }
        }
    }
    
    public UpgradeUI() {
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(1.2f);
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2f);
        
        categories = new UpgradeCategory[4];
        categories[0] = new UpgradeCategory(UpgradeType.SIZE, 10, 100);
        categories[1] = new UpgradeCategory(UpgradeType.WEAPON, 10, 150);
        categories[2] = new UpgradeCategory(UpgradeType.STATS, 20, 80);
        categories[3] = new UpgradeCategory(UpgradeType.ABILITIES, 5, 200);
        
        calculateBounds();
    }
    
    private void calculateBounds() {
        float totalWidth = categories.length * CATEGORY_WIDTH + (categories.length - 1) * 30;
        float startX = (Gdx.graphics.getWidth() - totalWidth) / 2;
        float startY = (Gdx.graphics.getHeight() - CATEGORY_HEIGHT) / 2;
        
        for (int i = 0; i < categories.length; i++) {
            UpgradeCategory cat = categories[i];
            float x = startX + i * (CATEGORY_WIDTH + 30);
            
            cat.bounds = new Rectangle(x, startY, CATEGORY_WIDTH, CATEGORY_HEIGHT);
            cat.previewBounds = new Rectangle(
                x + (CATEGORY_WIDTH - PREVIEW_SIZE) / 2,
                startY + CATEGORY_HEIGHT - PREVIEW_SIZE - 20,
                PREVIEW_SIZE, PREVIEW_SIZE
            );
            cat.buttonBounds = new Rectangle(
                x + (CATEGORY_WIDTH - BUTTON_WIDTH) / 2,
                startY + 80,
                BUTTON_WIDTH, BUTTON_HEIGHT
            );
        }
    }
    
    public void setGold(int gold) {
        this.gold = gold;
    }
    
    public void setOnBackCallback(Runnable callback) {
        this.onBackCallback = callback;
    }
    
    public void render(SpriteBatch batch) {
        batch.end();
        
        renderBackground();
        renderCategories();
        
        batch.begin();
        renderText(batch);
    }
    
    private void renderBackground() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.15f, 0.9f));
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
    }
    
    private void renderCategories() {
        for (int i = 0; i < categories.length; i++) {
            UpgradeCategory cat = categories[i];
            
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            
            if (i == selectedIndex) {
                shapeRenderer.setColor(new Color(0.3f, 0.3f, 0.4f, 1f));
            } else {
                shapeRenderer.setColor(new Color(0.15f, 0.15f, 0.2f, 1f));
            }
            shapeRenderer.rect(cat.bounds.x, cat.bounds.y, cat.bounds.width, cat.bounds.height);
            
            shapeRenderer.setColor(cat.type.color);
            shapeRenderer.rect(cat.previewBounds.x, cat.previewBounds.y, 
                              cat.previewBounds.width, cat.previewBounds.height);
            
            boolean canAfford = gold >= cat.getCost() && cat.canUpgrade();
            if (canAfford) {
                shapeRenderer.setColor(new Color(0.2f, 0.6f, 0.2f, 1f));
            } else {
                shapeRenderer.setColor(new Color(0.4f, 0.4f, 0.4f, 1f));
            }
            shapeRenderer.rect(cat.buttonBounds.x, cat.buttonBounds.y,
                              cat.buttonBounds.width, cat.buttonBounds.height);
            
            shapeRenderer.end();
            
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.rect(cat.bounds.x, cat.bounds.y, cat.bounds.width, cat.bounds.height);
            shapeRenderer.end();
        }
        
        renderBackButton();
    }
    
    private void renderBackButton() {
        float backX = Gdx.graphics.getWidth() / 2f - 75;
        float backY = 30;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.5f, 0.2f, 0.2f, 1f));
        shapeRenderer.rect(backX, backY, 150, 45);
        shapeRenderer.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(backX, backY, 150, 45);
        shapeRenderer.end();
    }
    
    private void renderText(SpriteBatch batch) {
        float titleY = Gdx.graphics.getHeight() - 50;
        titleFont.setColor(Color.GOLD);
        titleFont.draw(batch, "UPGRADES", Gdx.graphics.getWidth() / 2f - 90, titleY);
        
        font.setColor(Color.WHITE);
        font.draw(batch, "Gold: " + gold, Gdx.graphics.getWidth() - 150, titleY);
        
        for (UpgradeCategory cat : categories) {
            float centerX = cat.bounds.x + CATEGORY_WIDTH / 2;
            
            font.setColor(cat.type.color);
            font.draw(batch, cat.type.name, centerX - 30, cat.bounds.y + CATEGORY_HEIGHT - 10);
            
            font.setColor(Color.WHITE);
            String levelText = "Lv. " + cat.level + "/" + cat.maxLevel;
            font.draw(batch, levelText, centerX - 30, cat.bounds.y + CATEGORY_HEIGHT - 110);
            
            String costText = cat.canUpgrade() ? "Cost: " + cat.getCost() : "MAX";
            boolean canAfford = gold >= cat.getCost() && cat.canUpgrade();
            font.setColor(canAfford ? Color.GREEN : Color.RED);
            font.draw(batch, costText, centerX - 35, cat.bounds.y + CATEGORY_HEIGHT - 135);
            
            font.setColor(Color.LIGHT_GRAY);
            font.getData().setScale(0.9f);
            String bonus = cat.canUpgrade() ? "+" + cat.getNextBonus() : "MAX";
            font.draw(batch, bonus, centerX - 25, cat.bounds.y + 110);
            font.getData().setScale(1.2f);
            
            String purchaseText = cat.canUpgrade() ? "UPGRADE" : "MAXED";
            font.setColor(Color.WHITE);
            font.draw(batch, purchaseText, centerX - 35, cat.bounds.y + 108);
        }
        
        font.setColor(Color.WHITE);
        font.draw(batch, "BACK", Gdx.graphics.getWidth() / 2f - 25, 62);
    }
    
    public boolean handleClick(float screenX, float screenY) {
        float y = Gdx.graphics.getHeight() - screenY;
        float x = screenX;
        
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].bounds.contains(x, y)) {
                selectedIndex = i;
            }
            if (categories[i].buttonBounds.contains(x, y)) {
                return tryUpgrade(i);
            }
        }
        
        float backX = Gdx.graphics.getWidth() / 2f - 75;
        float backY = 30;
        if (x >= backX && x <= backX + 150 && y >= backY && y <= backY + 45) {
            if (onBackCallback != null) {
                onBackCallback.run();
            }
            return true;
        }
        
        return false;
    }
    
    private boolean tryUpgrade(int index) {
        UpgradeCategory cat = categories[index];
        if (cat.canUpgrade() && gold >= cat.getCost()) {
            gold -= cat.getCost();
            cat.level++;
            return true;
        }
        return false;
    }
    
    public int getSizeLevel() { return categories[0].level; }
    public int getWeaponLevel() { return categories[1].level; }
    public int getStatsLevel() { return categories[2].level; }
    public int getAbilitiesLevel() { return categories[3].level; }
    
    public void setLevels(int size, int weapon, int stats, int abilities) {
        categories[0].level = size;
        categories[1].level = weapon;
        categories[2].level = stats;
        categories[3].level = abilities;
    }
    
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
        titleFont.dispose();
    }
}