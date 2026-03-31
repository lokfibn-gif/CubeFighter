package com.cubefighter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cubefighter.CubeFighterGame;
import com.cubefighter.save.PlayerData;
import com.cubefighter.save.SaveManager;

public class UpgradeShopScreen implements Screen {
    private final CubeFighterGame game;
    
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont titleFont;
    private BitmapFont smallFont;

    private int selectedCategory;
    private int currentPage;
    private static final int ITEMS_PER_PAGE = 4;

    private String[] categories = {"Stats", "Weapons", "Abilities", "Special"};

    private Rectangle[] categoryButtons;
    private Rectangle[] upgradeButtons;
    private Rectangle backButton;
    private Rectangle prevPageButton;
    private Rectangle nextPageButton;

    private float animationTime;

    private static final String[][] STAT_UPGRADES = {
        {"HP", "Increase max health"},
        {"Damage", "Increase attack power"},
        {"Speed", "Move faster"},
        {"Critical", "Critical hit chance"},
        {"Armor", "Damage reduction"}
    };

    private static final String[][] WEAPON_UPGRADES = {
        {"Pistol", "Basic reliable weapon"},
        {"Shotgun", "Multiple projectiles"},
        {"Rifle", "Fast firing rate"},
        {"Sniper", "High damage, slow"},
        {"Laser", "Continuous beam"}
    };

    private static final String[][] ABILITY_UPGRADES = {
        {"Dash", "Quick dodge move"},
        {"Shield", "Block damage"},
        {"Regeneration", "Heal over time"},
        {"Ultimate", "Devastating attack"}
    };

    private static final String[][] SPECIAL_UPGRADES = {
        {"Size+", "Larger cube"},
        {"XP Boost", "More experience"},
        {"Gold Boost", "More currency"},
        {"Lucky", "Better drops"}
    };

    private static final int[][] UPGRADE_COSTS = {
        {50, 100, 200, 400, 800},
        {100, 200, 400, 800, 1600},
        {75, 150, 300, 600, 1200},
        {100, 250, 500, 1000, 2000},
        {150, 300, 600, 1200, 2400}
    };

    public UpgradeShopScreen(CubeFighterGame game) {
        this.game = game;
        selectedCategory = 0;
        currentPage = 0;
        animationTime = 0f;

        camera = game.getCamera();
        viewport = game.getViewport();
        shapeRenderer = game.getShapeRenderer();
        batch = game.getBatch();
        font = game.getFont();

        titleFont = new BitmapFont();
        titleFont.getData().setScale(2f);

        smallFont = new BitmapFont();
        smallFont.getData().setScale(1f);

        calculateLayout();
    }

    private void calculateLayout() {
        float centerX = CubeFighterGame.WORLD_WIDTH / 2;
        
        categoryButtons = new Rectangle[4];
        for (int i = 0; i < 4; i++) {
            categoryButtons[i] = new Rectangle(50 + i * 130, CubeFighterGame.WORLD_HEIGHT - 80, 120, 40);
        }

        upgradeButtons = new Rectangle[ITEMS_PER_PAGE];
        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            upgradeButtons[i] = new Rectangle(50, CubeFighterGame.WORLD_HEIGHT - 160 - i * 70, CubeFighterGame.WORLD_WIDTH - 100, 60);
        }

        backButton = new Rectangle(CubeFighterGame.WORLD_WIDTH - 110, 10, 100, 40);
        prevPageButton = new Rectangle(50, 20, 80, 30);
        nextPageButton = new Rectangle(CubeFighterGame.WORLD_WIDTH - 130, 20, 80, 30);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        animationTime += delta;

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawBackground();
        drawCategoryTabs();
        drawUpgradeList();
        drawNavigation();
        drawPointsDisplay();
        drawBackButton();

        handleInput();
    }

    private void drawBackground() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(new Color(0.06f, 0.06f, 0.12f, 1f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, CubeFighterGame.WORLD_WIDTH, CubeFighterGame.WORLD_HEIGHT);
        shapeRenderer.end();

        for (int i = 0; i < 6; i++) {
            float offset = (animationTime * 15 + i * 100) % CubeFighterGame.WORLD_WIDTH;
            shapeRenderer.setColor(new Color(0.12f, 0.12f, 0.2f, 0.3f));
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.line(offset, 0, offset - 50, CubeFighterGame.WORLD_HEIGHT);
            shapeRenderer.end();
        }
    }

    private void drawCategoryTabs() {
        for (int i = 0; i < categories.length; i++) {
            boolean selected = (i == selectedCategory);
            Color fillColor = selected ? new Color(0.25f, 0.45f, 0.7f, 1f) : new Color(0.15f, 0.15f, 0.2f, 1f);
            Color borderColor = selected ? new Color(0.4f, 0.6f, 0.9f, 1f) : new Color(0.3f, 0.3f, 0.4f, 1f);
            drawButton(categoryButtons[i], fillColor, borderColor);
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        smallFont.setColor(Color.WHITE);
        for (int i = 0; i < categories.length; i++) {
            String text = categories[i];
            float textWidth = text.length() * 6f;
            smallFont.draw(batch, text, categoryButtons[i].x + categoryButtons[i].width / 2 - textWidth, categoryButtons[i].y + 25);
        }
        batch.end();
    }

    private void drawUpgradeList() {
        String[][] items = getUpgradesForCategory(selectedCategory);
        int startIdx = currentPage * ITEMS_PER_PAGE;

        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            int idx = startIdx + i;
            if (idx < items.length) {
                drawUpgradeItem(i, items[idx], idx);
            }
        }
    }

    private void drawUpgradeItem(int displayIndex, String[] itemData, int itemIndex) {
        Rectangle bounds = upgradeButtons[displayIndex];
        PlayerData pd = SaveManager.getInstance().getPlayerData();
        int currentLevel = getItemLevel(selectedCategory, itemIndex);
        int cost = getUpgradeCost(selectedCategory, itemIndex, currentLevel);
        boolean canAfford = pd.getPoints() >= cost;
        boolean maxed = currentLevel >= getMaxLevel(selectedCategory, itemIndex);

        Color bgColor = new Color(0.12f, 0.12f, 0.18f, 1f);
        Color borderColor = maxed ? new Color(0.6f, 0.5f, 0.2f, 1f) : canAfford ? new Color(0.3f, 0.6f, 0.3f, 1f) : new Color(0.25f, 0.25f, 0.35f, 1f);

        drawButton(bounds, bgColor, borderColor);

        shapeRenderer.setProjectionMatrix(camera.combined);
        Color previewColor = getCategoryColor(selectedCategory);
        shapeRenderer.setColor(previewColor);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(bounds.x + 15, bounds.y + 15, 30, 30);
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        font.setColor(Color.WHITE);
        font.draw(batch, itemData[0], bounds.x + 60, bounds.y + 42);

        smallFont.setColor(new Color(0.6f, 0.6f, 0.7f, 1f));
        smallFont.draw(batch, itemData[1], bounds.x + 60, bounds.y + 22);

        String levelText = maxed ? "MAX" : "Lv." + currentLevel;
        smallFont.setColor(new Color(0.8f, 0.65f, 0.2f, 1f));
        smallFont.draw(batch, levelText, bounds.x + bounds.width - 80, bounds.y + 42);

        if (!maxed) {
            smallFont.setColor(canAfford ? new Color(0.3f, 0.9f, 0.3f, 1f) : new Color(0.9f, 0.3f, 0.3f, 1f));
            smallFont.draw(batch, cost + " pts", bounds.x + bounds.width - 80, bounds.y + 22);
        }

        batch.end();
    }

    private Color getCategoryColor(int category) {
        switch (category) {
            case 0: return new Color(0.3f, 0.7f, 0.5f, 1f);
            case 1: return new Color(0.7f, 0.5f, 0.3f, 1f);
            case 2: return new Color(0.5f, 0.4f, 0.7f, 1f);
            case 3: return new Color(0.7f, 0.6f, 0.3f, 1f);
            default: return new Color(0.4f, 0.5f, 0.7f, 1f);
        }
    }

    private void drawNavigation() {
        String[][] items = getUpgradesForCategory(selectedCategory);
        int totalPages = (int) Math.ceil(items.length / (float) ITEMS_PER_PAGE);

        if (totalPages > 1) {
            drawButton(prevPageButton, new Color(0.25f, 0.25f, 0.35f, 1f), new Color(0.4f, 0.4f, 0.55f, 1f));
            drawButton(nextPageButton, new Color(0.25f, 0.25f, 0.35f, 1f), new Color(0.4f, 0.4f, 0.55f, 1f));

            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            smallFont.setColor(Color.WHITE);
            smallFont.draw(batch, "<", prevPageButton.x + 35, prevPageButton.y + 22);
            smallFont.draw(batch, ">", nextPageButton.x + 35, nextPageButton.y + 22);
            smallFont.draw(batch, (currentPage + 1) + "/" + totalPages, CubeFighterGame.WORLD_WIDTH / 2 - 15, 40);
            batch.end();
        }
    }

    private void drawPointsDisplay() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        int points = SaveManager.getInstance().getPlayerData().getPoints();
        int gems = SaveManager.getInstance().getPlayerData().getGems();

        titleFont.setColor(new Color(1f, 0.8f, 0.2f, 1f));
        titleFont.draw(batch, "UPGRADE SHOP", CubeFighterGame.WORLD_WIDTH / 2 - 100, CubeFighterGame.WORLD_HEIGHT - 20);

        font.setColor(new Color(1f, 0.8f, 0.2f, 1f));
        font.draw(batch, "Points: " + points, CubeFighterGame.WORLD_WIDTH - 200, CubeFighterGame.WORLD_HEIGHT - 20);

        smallFont.setColor(new Color(0.5f, 0.7f, 1f, 1f));
        smallFont.draw(batch, "Gems: " + gems, CubeFighterGame.WORLD_WIDTH - 100, CubeFighterGame.WORLD_HEIGHT - 45);

        batch.end();
    }

    private void drawBackButton() {
        drawButton(backButton, new Color(0.35f, 0.25f, 0.25f, 1f), new Color(0.55f, 0.45f, 0.45f, 1f));

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Back", backButton.x + 30, backButton.y + 27);
        batch.end();
    }

    private void drawButton(Rectangle bounds, Color fillColor, Color borderColor) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(fillColor);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();

        shapeRenderer.setColor(borderColor);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();
    }

    private void handleInput() {
        if (Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
            float x = viewport.unproject(new com.badlogic.gdx.math.Vector2(Gdx.input.getX(), Gdx.input.getY())).x;
            float y = viewport.unproject(new com.badlogic.gdx.math.Vector2(Gdx.input.getX(), Gdx.input.getY())).y;

            for (int i = 0; i < categoryButtons.length; i++) {
                if (categoryButtons[i].contains(x, y)) {
                    selectedCategory = i;
                    currentPage = 0;
                    return;
                }
            }

            String[][] items = getUpgradesForCategory(selectedCategory);
            int startIdx = currentPage * ITEMS_PER_PAGE;
            for (int i = 0; i < ITEMS_PER_PAGE; i++) {
                int idx = startIdx + i;
                if (idx < items.length && upgradeButtons[i].contains(x, y)) {
                    purchaseUpgrade(selectedCategory, idx);
                    return;
                }
            }

            if (prevPageButton.contains(x, y) && currentPage > 0) {
                currentPage--;
            }
            if (nextPageButton.contains(x, y) && (currentPage + 1) * ITEMS_PER_PAGE < items.length) {
                currentPage++;
            }

            if (backButton.contains(x, y)) {
                game.showMenu();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.showMenu();
        }
    }

    private String[][] getUpgradesForCategory(int category) {
        switch (category) {
            case 0: return STAT_UPGRADES;
            case 1: return WEAPON_UPGRADES;
            case 2: return ABILITY_UPGRADES;
            case 3: return SPECIAL_UPGRADES;
            default: return STAT_UPGRADES;
        }
    }

    private int getItemLevel(int category, int index) {
        PlayerData pd = SaveManager.getInstance().getPlayerData();
        switch (category) {
            case 0: return pd.getStatLevel(STAT_UPGRADES[index][0].toLowerCase());
            case 1: return pd.getWeaponLevel();
            case 2: return pd.getStatLevel(ABILITY_UPGRADES[index][0].toLowerCase());
            case 3: return index == 0 ? pd.getSizeLevel() : pd.getStatLevel(SPECIAL_UPGRADES[index][0].toLowerCase().replace("+", "").replace(" ", ""));
            default: return 0;
        }
    }

    private int getMaxLevel(int category, int index) {
        if (category == 0) return 5;
        if (category == 1) return 10;
        if (category == 2) return 10;
        if (category == 3) return 5;
        return 5;
    }

    private int getUpgradeCost(int category, int index, int currentLevel) {
        if (category == 0 && index < UPGRADE_COSTS.length && currentLevel < UPGRADE_COSTS[index].length) {
            return UPGRADE_COSTS[index][currentLevel];
        }
        int baseCost = category == 2 ? 50 : 100;
        return baseCost + currentLevel * 50;
    }

    private void purchaseUpgrade(int category, int index) {
        PlayerData pd = SaveManager.getInstance().getPlayerData();
        int currentLevel = getItemLevel(category, index);
        int cost = getUpgradeCost(category, index, currentLevel);

        if (currentLevel >= getMaxLevel(category, index)) return;
        if (pd.getPoints() < cost) return;

        pd.addPoints(-cost);

        switch (category) {
            case 0: pd.setStatLevel(STAT_UPGRADES[index][0].toLowerCase(), currentLevel + 1); break;
            case 1: pd.setWeaponLevel(pd.getWeaponLevel() + 1); break;
            case 2: pd.setStatLevel(ABILITY_UPGRADES[index][0].toLowerCase(), currentLevel + 1); break;
            case 3:
                if (index == 0) pd.setSizeLevel(pd.getSizeLevel() + 1);
                else pd.setStatLevel(SPECIAL_UPGRADES[index][0].toLowerCase().replace("+", "").replace(" ", ""), currentLevel + 1);
                break;
        }

        SaveManager.getInstance().save();
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
        titleFont.dispose();
        smallFont.dispose();
    }
}