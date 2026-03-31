package com.cubefighter.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.utils.Array;

public class ModelRenderer {
    
    private ModelBatch modelBatch;
    private Array<ModelInstance> modelInstances;
    
    private Model playerModel;
    private Model enemyModel;
    private Model bossModel;
    
    public ModelRenderer() {
        modelInstances = new Array<>();
        modelBatch = new ModelBatch();
        loadModels();
    }
    
    private void loadModels() {
        playerModel = ModelLoader.loadObj("models/player_cube.obj");
        enemyModel = ModelLoader.loadObj("models/enemy_cube.obj");
        bossModel = ModelLoader.loadObj("models/boss_cube.obj");
    }
    
    public void begin(Camera camera) {
        modelBatch.begin(camera);
    }
    
    public void end() {
        modelBatch.end();
    }
    
    public void renderPlayer(Vector3 position, Quaternion rotation, Vector3 scale) {
        ModelInstance instance = new ModelInstance(playerModel);
        instance.transform.set(position, rotation, scale);
        modelBatch.render(instance);
    }
    
    public void renderEnemy(Vector3 position, int enemyType, Quaternion rotation) {
        ModelInstance instance = new ModelInstance(enemyModel);
        
        float sizeScale = getEnemySizeScale(enemyType);
        instance.transform.set(position, rotation, new Vector3(sizeScale, sizeScale, sizeScale));
        
        modelBatch.render(instance);
    }
    
    public void renderBoss(Vector3 position, int bossType, Quaternion rotation) {
        ModelInstance instance = new ModelInstance(bossModel);
        
        float sizeScale = getBossSizeScale(bossType);
        instance.transform.set(position, rotation, new Vector3(sizeScale, sizeScale, sizeScale));
        
        modelBatch.render(instance);
    }
    
    private float getEnemySizeScale(int type) {
        switch (type) {
            case 0: return 0.5f;
            case 1: return 0.75f;
            case 2: return 1.0f;
            case 3: return 2.0f;
            case 4: return 4.0f;
            default: return 1.0f;
        }
    }
    
    private float getBossSizeScale(int type) {
        switch (type) {
            case 0: return 3.0f;
            case 1: return 4.0f;
            case 2: return 4.0f;
            case 3: return 5.0f;
            case 4: return 6.0f;
            case 5: return 5.0f;
            case 6: return 7.0f;
            default: return 4.0f;
        }
    }
    
    public void dispose() {
        modelBatch.dispose();
        ModelLoader.dispose();
    }
}