package com.cubefighter;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.cubefighter.entities.Boss;
import com.cubefighter.entities.Enemy;
import com.cubefighter.entities.GameObject;
import com.cubefighter.entities.Player;

public class GameWorld {
    private static final float WORLD_WIDTH = CubeFighterGame.WORLD_WIDTH;
    private static final float WORLD_HEIGHT = CubeFighterGame.WORLD_HEIGHT;
    private static final float SPAWN_MARGIN = 64f;
    
    private Player player;
    private Array<Enemy> enemies;
    private Array<Boss> bosses;
    private Array<Bullet> bullets;
    private Array<Pickup> pickups;
    
    private Rectangle worldBounds;
    private Rectangle safeZone;
    
    private float[] spawnPointsX;
    private float[] spawnPointsY;
    
    private int lastSpawnIndex;
    
    public GameWorld() {
        enemies = new Array<>();
        bosses = new Array<>();
        bullets = new Array<>();
        pickups = new Array<>();
        
        worldBounds = new Rectangle(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        safeZone = new Rectangle(WORLD_WIDTH / 2 - 100, WORLD_HEIGHT / 2 - 100, 200, 200);
        
        initializeSpawnPoints();
        lastSpawnIndex = 0;
    }
    
    private void initializeSpawnPoints() {
        int numPoints = 8;
        spawnPointsX = new float[numPoints];
        spawnPointsY = new float[numPoints];
        
        spawnPointsX[0] = SPAWN_MARGIN;
        spawnPointsY[0] = SPAWN_MARGIN;
        
        spawnPointsX[1] = WORLD_WIDTH / 2;
        spawnPointsY[1] = SPAWN_MARGIN;
        
        spawnPointsX[2] = WORLD_WIDTH - SPAWN_MARGIN;
        spawnPointsY[2] = SPAWN_MARGIN;
        
        spawnPointsX[3] = WORLD_WIDTH - SPAWN_MARGIN;
        spawnPointsY[3] = WORLD_HEIGHT / 2;
        
        spawnPointsX[4] = WORLD_WIDTH - SPAWN_MARGIN;
        spawnPointsY[4] = WORLD_HEIGHT - SPAWN_MARGIN;
        
        spawnPointsX[5] = WORLD_WIDTH / 2;
        spawnPointsY[5] = WORLD_HEIGHT - SPAWN_MARGIN;
        
        spawnPointsX[6] = SPAWN_MARGIN;
        spawnPointsY[6] = WORLD_HEIGHT - SPAWN_MARGIN;
        
        spawnPointsX[7] = SPAWN_MARGIN;
        spawnPointsY[7] = WORLD_HEIGHT / 2;
    }
    
    public void spawnPlayer(float x, float y) {
        player = new Player(x, y);
    }
    
    public void spawnEnemy(Enemy enemy) {
        if (enemy != null) {
            enemies.add(enemy);
        }
    }
    
    public void spawnEnemyAtRandomLocation(Enemy enemy) {
        if (enemy == null) return;
        
        float x, y;
        int attempts = 0;
        boolean validPosition = false;
        
        do {
            int side = MathUtils.random(3);
            switch (side) {
                case 0:
                    x = MathUtils.random(0, WORLD_WIDTH);
                    y = WORLD_HEIGHT + 10;
                    break;
                case 1:
                    x = MathUtils.random(0, WORLD_WIDTH);
                    y = -10;
                    break;
                case 2:
                    x = WORLD_WIDTH + 10;
                    y = MathUtils.random(0, WORLD_HEIGHT);
                    break;
                default:
                    x = -10;
                    y = MathUtils.random(0, WORLD_HEIGHT);
            }
            
            if (player != null) {
                float distToPlayer = Vector2.dst(x, y, player.getCenterX(), player.getCenterY());
                validPosition = distToPlayer > 100;
            } else {
                validPosition = true;
            }
            
            attempts++;
        } while (!validPosition && attempts < 10);
        
        enemy.setPosition(x, y);
        enemies.add(enemy);
    }
    
    public void spawnEnemyAtSpawnPoint(Enemy enemy) {
        if (enemy == null) return;
        
        int index = (lastSpawnIndex + 1) % spawnPointsX.length;
        lastSpawnIndex = index;
        
        float x = spawnPointsX[index];
        float y = spawnPointsY[index];
        
        enemy.setPosition(x, y);
        enemies.add(enemy);
    }
    
    public void spawnBoss(Boss boss) {
        if (boss != null) {
            boss.setPosition(WORLD_WIDTH / 2 - boss.getWidth() / 2, WORLD_HEIGHT - 100);
            bosses.add(boss);
        }
    }
    
    public Bullet spawnBullet(float x, float y, Vector2 direction, float damage) {
        Bullet bullet = new Bullet(x, y, direction, damage);
        bullets.add(bullet);
        return bullet;
    }
    
    public void spawnPickup(Pickup pickup) {
        if (pickup != null) {
            pickups.add(pickup);
        }
    }
    
    public void update(float delta) {
        updateEntities(delta);
        checkCollisions();
        checkBounds();
        cleanupInactive();
    }
    
    private void updateEntities(float delta) {
        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                enemy.update(delta);
            }
        }
        
        for (Boss boss : bosses) {
            if (boss.isActive()) {
                boss.update(delta);
            }
        }
        
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(delta);
            
            if (bullet.isOutOfBounds()) {
                bullets.removeIndex(i);
            }
        }
        
        for (int i = pickups.size - 1; i >= 0; i--) {
            Pickup pickup = pickups.get(i);
            pickup.update(delta);
            
            if (!pickup.isActive()) {
                pickups.removeIndex(i);
            }
        }
    }
    
    private void checkCollisions() {
        if (player == null || !player.isActive()) return;
        
        Rectangle playerBounds = player.getBounds();
        
        for (Pickup pickup : pickups) {
            if (pickup.isActive() && playerBounds.overlaps(pickup.getBounds())) {
                pickup.applyTo(player);
                pickup.setActive(false);
            }
        }
    }
    
    private void checkBounds() {
        if (player != null) {
            float px = MathUtils.clamp(player.getX(), 0, WORLD_WIDTH - player.getWidth());
            float py = MathUtils.clamp(player.getY(), 0, WORLD_HEIGHT - player.getHeight());
            player.setPosition(px, py);
        }
        
        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                float ex = MathUtils.clamp(enemy.getX(), -enemy.getWidth(), WORLD_WIDTH);
                float ey = MathUtils.clamp(enemy.getY(), -enemy.getHeight(), WORLD_HEIGHT);
                enemy.setPosition(ex, ey);
            }
        }
        
        for (Boss boss : bosses) {
            if (boss.isActive()) {
                float bx = MathUtils.clamp(boss.getX(), 0, WORLD_WIDTH - boss.getWidth());
                float by = MathUtils.clamp(boss.getY(), 0, WORLD_HEIGHT - boss.getHeight());
                boss.setPosition(bx, by);
            }
        }
    }
    
    private void cleanupInactive() {
        for (int i = enemies.size - 1; i >= 0; i--) {
            if (!enemies.get(i).isActive()) {
                enemies.removeIndex(i);
            }
        }
        
        for (int i = bosses.size - 1; i >= 0; i--) {
            if (!bosses.get(i).isActive()) {
                bosses.removeIndex(i);
            }
        }
        
        for (int i = pickups.size - 1; i >= 0; i--) {
            if (!pickups.get(i).isActive()) {
                pickups.removeIndex(i);
            }
        }
    }
    
    public void updateBullets(float delta) {
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
    
    public boolean isInBounds(float x, float y, float width, float height) {
        return worldBounds.contains(x, y) || worldBounds.contains(x + width, y) ||
               worldBounds.contains(x, y + height) || worldBounds.contains(x + width, y + height);
    }
    
    public boolean isInSafeZone(float x, float y) {
        return safeZone.contains(x, y);
    }
    
    public Vector2 getRandomSpawnPosition() {
        int side = MathUtils.random(3);
        float x, y;
        
        switch (side) {
            case 0:
                x = MathUtils.random(0, WORLD_WIDTH);
                y = WORLD_HEIGHT + SPAWN_MARGIN;
                break;
            case 1:
                x = MathUtils.random(0, WORLD_WIDTH);
                y = -SPAWN_MARGIN;
                break;
            case 2:
                x = WORLD_WIDTH + SPAWN_MARGIN;
                y = MathUtils.random(0, WORLD_HEIGHT);
                break;
            default:
                x = -SPAWN_MARGIN;
                y = MathUtils.random(0, WORLD_HEIGHT);
        }
        
        return new Vector2(x, y);
    }
    
    public Player getPlayer() { return player; }
    public Array<Enemy> getEnemies() { return enemies; }
    public Array<Boss> getBosses() { return bosses; }
    public Array<Bullet> getBullets() { return bullets; }
    public Array<Pickup> getPickups() { return pickups; }
    public Rectangle getWorldBounds() { return worldBounds; }
    public Rectangle getSafeZone() { return safeZone; }
    
    public int getEnemyCount() { return enemies.size + bosses.size; }
    public int getBulletCount() { return bullets.size; }
    
    public void clearAllEntities() {
        enemies.clear();
        bosses.clear();
        bullets.clear();
        pickups.clear();
    }
    
    public void dispose() {
        clearAllEntities();
        player = null;
    }
    
    public static class Bullet {
        public Rectangle bounds;
        public Vector2 direction;
        public float damage;
        public float speed;
        public boolean isPlayerBullet;
        
        public Bullet(float x, float y, Vector2 direction, float damage) {
            this.bounds = new Rectangle(x, y, 8, 8);
            this.direction = direction.cpy().nor();
            this.damage = damage;
            this.speed = 500f;
            this.isPlayerBullet = true;
        }
        
        public void update(float delta) {
        }
        
        public boolean isOutOfBounds() {
            return bounds.x < -10 || bounds.x > WORLD_WIDTH + 10 ||
                   bounds.y < -10 || bounds.y > WORLD_HEIGHT + 10;
        }
    }
    
    public static class Pickup extends GameObject {
        public enum PickupType {
            HEALTH,
            DAMAGE_BOOST,
            SPEED_BOOST,
            INVINCIBILITY,
            SCORE_BONUS
        }
        
        private PickupType type;
        private float duration;
        private int value;
        private float lifetime;
        
        public Pickup(PickupType type, float x, float y) {
            super(x, y, 20, 20);
            this.type = type;
            this.duration = 10f;
            this.lifetime = 30f;
            
            switch (type) {
                case HEALTH:
                    value = 25;
                    break;
                case DAMAGE_BOOST:
                    value = 50;
                    break;
                case SPEED_BOOST:
                    value = 30;
                    break;
                case INVINCIBILITY:
                    value = 100;
                    break;
                case SCORE_BONUS:
                    value = 500;
                    break;
            }
        }
        
        @Override
        public void update(float deltaTime) {
            lifetime -= deltaTime;
            if (lifetime <= 0) {
                active = false;
            }
        }
        
        @Override
        public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        }
        
        public void applyTo(Player player) {
            switch (type) {
                case HEALTH:
                    player.setHp(player.getHp() + value);
                    break;
                case DAMAGE_BOOST:
                    player.setDamage(player.getDamage() + value);
                    break;
                case SPEED_BOOST:
                    player.setSpeed(player.getSpeed() + value);
                    break;
                case INVINCIBILITY:
                    break;
                case SCORE_BONUS:
                    break;
            }
        }
        
        public PickupType getType() { return type; }
        public float getDuration() { return duration; }
        public int getValue() { return value; }
        public float getLifetime() { return lifetime; }
    }
}