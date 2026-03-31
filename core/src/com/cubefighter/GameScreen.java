package com.cubefighter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 480;
    private static final float PLAYER_SPEED = 200f;
    private static final float PLAYER_SIZE = 32f;

    private final CubeFighterGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;

    private Rectangle player;
    private int playerLevel;
    private float playerSize;
    private int score;
    private int enemiesDefeated;

    private Array<Enemy> enemies;
    private Array<Bullet> bullets;
    private float shootCooldown;
    private static final float SHOOT_DELAY = 0.2f;

    private int weaponLevel;
    private float weaponDamage;

    public GameScreen(CubeFighterGame game) {
        this.game = game;
        playerLevel = 1;
        playerSize = PLAYER_SIZE;
        score = 0;
        enemiesDefeated = 0;
        weaponLevel = 1;
        weaponDamage = 10f;
        shootCooldown = 0f;

        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);

        shapeRenderer = new ShapeRenderer();

        player = new Rectangle(WORLD_WIDTH / 2 - PLAYER_SIZE / 2, WORLD_HEIGHT / 2 - PLAYER_SIZE / 2, playerSize, playerSize);

        enemies = new Array<>();
        bullets = new Array<>();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(player.x, player.y, player.width, player.height);
        shapeRenderer.end();

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Enemy enemy : enemies) {
            shapeRenderer.rect(enemy.bounds.x, enemy.bounds.y, enemy.bounds.width, enemy.bounds.height);
        }
        shapeRenderer.end();

        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Bullet bullet : bullets) {
            shapeRenderer.rect(bullet.bounds.x, bullet.bounds.y, bullet.bounds.width, bullet.bounds.height);
        }
        shapeRenderer.end();
    }

    private void update(float delta) {
        handleInput(delta);
        updateBullets(delta);
        updateEnemies(delta);
        checkCollisions();
        spawnEnemies(delta);
        updateCooldowns(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.showMenu();
        }
    }

    private void handleInput(float delta) {
        float dx = 0, dy = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            dx = -PLAYER_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            dx = PLAYER_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            dy = PLAYER_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            dy = -PLAYER_SPEED * delta;
        }

        player.x = MathUtils.clamp(player.x + dx, 0, WORLD_WIDTH - player.width);
        player.y = MathUtils.clamp(player.y + dy, 0, WORLD_HEIGHT - player.height);

        if (Gdx.input.isTouched() || Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            shoot();
        }
    }

    private void shoot() {
        if (shootCooldown <= 0 && bullets.size < 20) {
            Vector2 direction = getShootDirection();
            Bullet bullet = new Bullet(
                player.x + player.width / 2 - 4,
                player.y + player.height / 2 - 4,
                direction,
                weaponDamage
            );
            bullets.add(bullet);
            shootCooldown = Math.max(0.1f, SHOOT_DELAY - weaponLevel * 0.02f);
        }
    }

    private Vector2 getShootDirection() {
        float screenX = Gdx.input.getX();
        float screenY = Gdx.input.getY();
        Vector2 worldCoords = viewport.unproject(new Vector2(screenX, screenY));
        Vector2 playerCenter = new Vector2(player.x + player.width / 2, player.y + player.height / 2);
        Vector2 direction = worldCoords.sub(playerCenter).nor();
        return direction;
    }

    private void updateBullets(float delta) {
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.bounds.x += bullet.direction.x * bullet.speed * delta;
            bullet.bounds.y += bullet.direction.y * bullet.speed * delta;

            if (bullet.bounds.x < -10 || bullet.bounds.x > WORLD_WIDTH + 10 ||
                bullet.bounds.y < -10 || bullet.bounds.y > WORLD_HEIGHT + 10) {
                bullets.removeIndex(i);
            }
        }
    }

    private void updateEnemies(float delta) {
        for (Enemy enemy : enemies) {
            Vector2 direction = new Vector2(
                player.x - enemy.bounds.x,
                player.y - enemy.bounds.y
            ).nor();

            enemy.bounds.x += direction.x * enemy.speed * delta;
            enemy.bounds.y += direction.y * enemy.speed * delta;
        }
    }

    private void checkCollisions() {
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);

            for (int j = bullets.size - 1; j >= 0; j--) {
                Bullet bullet = bullets.get(j);
                if (enemy.bounds.overlaps(bullet.bounds)) {
                    enemy.health -= bullet.damage;
                    bullets.removeIndex(j);

                    if (enemy.health <= 0) {
                        enemies.removeIndex(i);
                        score += enemy.points;
                        enemiesDefeated++;
                        checkLevelUp();
                        break;
                    }
                }
            }
        }

        for (Enemy enemy : enemies) {
            if (player.overlaps(enemy.bounds)) {
                game.showMenu();
                return;
            }
        }
    }

    private void checkLevelUp() {
        int newLevel = enemiesDefeated / 10 + 1;
        if (newLevel > playerLevel) {
            playerLevel = newLevel;
            playerSize = PLAYER_SIZE + (playerLevel - 1) * 4;
            player.width = playerSize;
            player.height = playerSize;
            weaponLevel = Math.min(10, playerLevel);
            weaponDamage = 10f + weaponLevel * 5f;
        }
    }

    private float spawnTimer = 0f;

    private void spawnEnemies(float delta) {
        spawnTimer += delta;
        float spawnRate = Math.max(0.5f, 2f - playerLevel * 0.1f);

        if (spawnTimer >= spawnRate && enemies.size < 20) {
            spawnTimer = 0f;

            float x, y;
            int side = MathUtils.random(3);
            switch (side) {
                case 0:
                    x = MathUtils.random(WORLD_WIDTH);
                    y = WORLD_HEIGHT;
                    break;
                case 1:
                    x = MathUtils.random(WORLD_WIDTH);
                    y = -32;
                    break;
                case 2:
                    x = WORLD_WIDTH;
                    y = MathUtils.random(WORLD_HEIGHT);
                    break;
                default:
                    x = -32;
                    y = MathUtils.random(WORLD_HEIGHT);
            }

            float enemySize = MathUtils.random(16f, 40f + playerLevel * 2f);
            float enemyHealth = 20f + playerLevel * 5f + enemySize;
            float enemySpeed = MathUtils.random(30f, 80f + playerLevel * 5f);
            int enemyPoints = (int) (enemySize + enemySpeed / 2);

            enemies.add(new Enemy(x, y, enemySize, enemyHealth, enemySpeed, enemyPoints));
        }
    }

    private void updateCooldowns(float delta) {
        if (shootCooldown > 0) {
            shootCooldown -= delta;
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
        shapeRenderer.dispose();
    }

    private static class Enemy {
        Rectangle bounds;
        float health;
        float speed;
        int points;

        Enemy(float x, float y, float size, float health, float speed, int points) {
            this.bounds = new Rectangle(x, y, size, size);
            this.health = health;
            this.speed = speed;
            this.points = points;
        }
    }

    private static class Bullet {
        Rectangle bounds;
        Vector2 direction;
        float damage;
        float speed = 500f;

        Bullet(float x, float y, Vector2 direction, float damage) {
            this.bounds = new Rectangle(x, y, 8, 8);
            this.direction = direction;
            this.damage = damage;
        }
    }
}