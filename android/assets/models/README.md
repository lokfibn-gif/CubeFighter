# 3D Models for Cube Fighter

This directory contains 3D models in OBJ format for use in the game.

## Models Included

### player_cube.obj
- Basic cube model for the player character
- Size: 1x1x1 units (scaled in-game)
- Ready for blue player color

### enemy_cube.obj
- Cube model for all enemy types
- Includes spike geometry for aggressive look
- Scaled by enemy type (Tiny → Huge)

### boss_cube.obj
- Larger cube with beveled edges
- Includes horn/crown geometry
- Size: 2x2x2 base (scaled by boss type)

### sword.obj
- Weapon model (sword)
- Can be applied to player

### materials.mtl
- Color definitions for all entity types
- Diffuse (Kd), Specular (Ks), Shininess (Ns)
- Player: Blue
- Enemies: Red → Orange (by size)
- Bosses: Green, Orange, Cyan, Red variations

## How to Use

```java
// Load model
Model playerModel = ModelLoader.loadObj("models/player_cube.obj");

// Create instance
ModelInstance instance = new ModelInstance(playerModel);

// Position and scale
instance.transform.set(position, rotation, scale);

// Render
modelBatch.render(instance);
```

## Integration

1. Add to AssetLoader:
```java
models.put("player", ModelLoader.loadObj("models/player_cube.obj"));
models.put("enemy", ModelLoader.loadObj("models/enemy_cube.obj"));
models.put("boss", ModelLoader.loadObj("models/boss_cube.obj"));
```

2. Use ModelRenderer in GameScreen:
```java
modelRenderer.begin(camera);
modelRenderer.renderPlayer(playerPos, rotation, scale);
modelRenderer.renderEnemy(enemyPos, enemyType, rotation);
modelRenderer.end();
```

## Future Improvements

- Add animations (rotation, scale pulse)
- Create more weapon models
- Add particle effects on models
- Create 3D UI elements