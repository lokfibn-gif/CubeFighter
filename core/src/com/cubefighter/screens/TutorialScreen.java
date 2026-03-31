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
import com.cubefighter.save.SaveManager;

public class TutorialScreen implements Screen {
    private final CubeFighterGame game;
    
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont titleFont;
    private BitmapFont smallFont;

    private int currentPage;
    private static final int TOTAL_PAGES = 6;

    private Rectangle prevButton;
    private Rectangle nextButton;
    private Rectangle skipButton;

    private float animationTime;

    private static final String[] PAGE_TITLES = {
        "Welcome", "Movement", "Combat", "Upgrades", "Abilities", "Tips"
    };

    private static final String[][] PAGE_CONTENT = {
        {"Welcome to Cube Fighter!", "Defeat waves of enemies", "Survive as long as possible", "Collect points and gems"},
        {"Use WASD or Arrow Keys", "Move your cube around", "Avoid enemy attacks", "Dodge projectiles"},
        {"Click or Tap to shoot", "Aim with mouse/touch", "Defeat enemies for points", "Boss waves every 5 levels"},
        {"Spend points on upgrades", "Increase HP and Damage", "Improve Speed and Crit", "Unlock new weapons"},
        {"Dash (D): Quick dodge", "Shield (S): Block damage", "Heal (H): Restore HP", "Ultimate (U): Devastating"},
        {"Watch for powerups", "Save ultimates for bosses", "Upgrade wisely", "Have fun!"}
    };

    private static final Color[] PAGE_COLORS = {
        new Color(0.3f, 0.5f, 0.8f, 1f),
        new Color(0.4f, 0.7f, 0.4f, 1f),
        new Color(0.8f, 0.4f, 0.4f, 1f),
        new Color(0.8f, 0.7f, 0.3f, 1f),
        new Color(0.6f, 0.4f, 0.8f, 1f),
        new Color(0.4f, 0.8f, 0.8f, 1f)
    };

    public TutorialScreen(CubeFighterGame game) {
        this.game = game;
        currentPage = 0;
        animationTime = 0f;

        camera = game.getCamera();
        viewport = game.getViewport();
        shapeRenderer = game.getShapeRenderer();
        batch = game.getBatch();
        font = game.getFont();

        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);

        smallFont = new BitmapFont();
        smallFont.getData().setScale(1.2f);

        calculateLayout();
    }

    private void calculateLayout() {
        prevButton = new Rectangle(50, 50, 100, 40);
        nextButton = new Rectangle(CubeFighterGame.WORLD_WIDTH - 150, 50, 100, 40);
        skipButton = new Rectangle(CubeFighterGame.WORLD_WIDTH - 110, CubeFighterGame.WORLD_HEIGHT - 50, 100, 40);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        animationTime += delta;

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawBackground();
        drawPageIndicator();
        drawContent();
        drawButtons();

        handleInput();
    }

    private void drawBackground() {
        Color bgColor = PAGE_COLORS[currentPage];
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(new Color(bgColor.r * 0.25f, bgColor.g * 0.25f, bgColor.b * 0.25f, 1f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, CubeFighterGame.WORLD_WIDTH, CubeFighterGame.WORLD_HEIGHT);
        shapeRenderer.end();

        for (int i = 0; i < 8; i++) {
            float offset = (animationTime * 15 + i * 80) % CubeFighterGame.WORLD_WIDTH;
            shapeRenderer.setColor(new Color(bgColor.r * 0.15f, bgColor.g * 0.15f, bgColor.b * 0.15f, 0.3f));
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.line(offset, 0, offset - 60, CubeFighterGame.WORLD_HEIGHT);
            shapeRenderer.end();
        }
    }

    private void drawPageIndicator() {
        float dotSpacing = 20;
        float startX = CubeFighterGame.WORLD_WIDTH / 2 - (TOTAL_PAGES - 1) * dotSpacing / 2;
        float y = CubeFighterGame.WORLD_HEIGHT - 35;

        for (int i = 0; i < TOTAL_PAGES; i++) {
            boolean isActive = (i == currentPage);
            float size = isActive ? 12 : 8;
            Color color = isActive ? PAGE_COLORS[i] : new Color(0.4f, 0.4f, 0.5f, 1f);

            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.setColor(color);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.rect(startX + i * dotSpacing - size / 2, y - size / 2, size, size);
            shapeRenderer.end();
        }
    }

    private void drawContent() {
        float panelX = CubeFighterGame.WORLD_WIDTH / 2 - 350;
        float panelY = 80;
        float panelWidth = 700;
        float panelHeight = 350;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(new Color(0.08f, 0.08f, 0.12f, 0.95f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        shapeRenderer.end();

        shapeRenderer.setColor(PAGE_COLORS[currentPage]);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        titleFont.setColor(PAGE_COLORS[currentPage]);
        titleFont.draw(batch, PAGE_TITLES[currentPage], CubeFighterGame.WORLD_WIDTH / 2 - PAGE_TITLES[currentPage].length() * 15, CubeFighterGame.WORLD_HEIGHT - 70);

        font.setColor(Color.WHITE);
        String[] content = PAGE_CONTENT[currentPage];
        float startY = CubeFighterGame.WORLD_HEIGHT / 2 + 30;
        for (int i = 0; i < content.length; i++) {
            font.draw(batch, content[i], CubeFighterGame.WORLD_WIDTH / 2 - content[i].length() * 8, startY - i * 45);
        }

        batch.end();
    }

    private void drawButtons() {
        if (currentPage > 0) {
            drawButton(prevButton, new Color(0.25f, 0.25f, 0.35f, 1f), new Color(0.45f, 0.45f, 0.55f, 1f));
        }

        Color nextColor = currentPage < TOTAL_PAGES - 1 ? new Color(0.25f, 0.45f, 0.3f, 1f) : new Color(0.25f, 0.55f, 0.3f, 1f);
        Color nextBorder = currentPage < TOTAL_PAGES - 1 ? new Color(0.45f, 0.75f, 0.5f, 1f) : new Color(0.5f, 0.9f, 0.5f, 1f);
        drawButton(nextButton, nextColor, nextBorder);

        drawButton(skipButton, new Color(0.35f, 0.25f, 0.25f, 1f), new Color(0.55f, 0.45f, 0.45f, 1f));

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        smallFont.setColor(Color.WHITE);
        if (currentPage > 0) {
            smallFont.draw(batch, "< Prev", prevButton.x + 25, prevButton.y + 25);
        }

        smallFont.draw(batch, currentPage < TOTAL_PAGES - 1 ? "Next >" : "Start!", nextButton.x + 20, nextButton.y + 25);
        smallFont.draw(batch, "Skip", skipButton.x + 30, skipButton.y + 25);

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

            if (prevButton.contains(x, y) && currentPage > 0) {
                currentPage--;
                return;
            }

            if (nextButton.contains(x, y)) {
                if (currentPage < TOTAL_PAGES - 1) {
                    currentPage++;
                } else {
                    SaveManager.getInstance().load();
                    game.startGame();
                }
                return;
            }

            if (skipButton.contains(x, y)) {
                SaveManager.getInstance().load();
                game.showMenu();
                return;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && currentPage > 0) {
            currentPage--;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (currentPage < TOTAL_PAGES - 1) {
                currentPage++;
            } else {
                SaveManager.getInstance().load();
                game.startGame();
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            SaveManager.getInstance().load();
            game.showMenu();
        }
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