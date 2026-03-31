package com.cubefighter.systems;

import java.util.ArrayList;
import java.util.List;

public class ComboSystem {
    
    private int comboCount;
    private float comboTimer;
    private final float comboTimeout;
    private int maxCombo;
    private List<ComboListener> listeners;
    private boolean comboBroken;
    
    public interface ComboListener {
        void onComboHit(int comboCount);
        void onComboBreak(int finalCombo);
        void onMaxComboReached();
    }
    
    public ComboSystem() {
        this.comboTimeout = 2.0f;
        this.comboCount = 0;
        this.comboTimer = 0f;
        this.maxCombo = 10;
        this.listeners = new ArrayList<ComboListener>();
        this.comboBroken = false;
    }
    
    public void addListener(ComboListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(ComboListener listener) {
        listeners.remove(listener);
    }
    
    public void registerHit() {
        comboCount++;
        comboTimer = comboTimeout;
        comboBroken = false;
        
        for (ComboListener listener : listeners) {
            listener.onComboHit(comboCount);
        }
        
        if (comboCount >= maxCombo) {
            for (ComboListener listener : listeners) {
                listener.onMaxComboReached();
            }
        }
    }
    
    public void update(float deltaTime) {
        if (comboCount > 0) {
            comboTimer -= deltaTime;
            if (comboTimer <= 0) {
                breakCombo();
            }
        }
    }
    
    private void breakCombo() {
        if (comboCount > 0 && !comboBroken) {
            comboBroken = true;
            for (ComboListener listener : listeners) {
                listener.onComboBreak(comboCount);
            }
            comboCount = 0;
        }
    }
    
    public void reset() {
        comboCount = 0;
        comboTimer = 0f;
        comboBroken = false;
    }
    
    public float getComboMultiplier() {
        if (comboCount <= 1) return 1.0f;
        if (comboCount >= maxCombo) return maxCombo;
        return (float) comboCount;
    }
    
    public String getMultiplierString() {
        int mult = (int) getComboMultiplier();
        return "x" + mult;
    }
    
    public int calculateBonusPoints(int basePoints) {
        float multiplier = getComboMultiplier();
        int bonus = (int) (basePoints * (multiplier - 1));
        
        if (comboCount >= 5) {
            bonus += 50;
        }
        if (comboCount >= 8) {
            bonus += 100;
        }
        if (comboCount >= maxCombo) {
            bonus += 200;
        }
        
        return bonus;
    }
    
    public int getComboCount() {
        return comboCount;
    }
    
    public float getRemainingTime() {
        return comboTimer;
    }
    
    public int getMaxCombo() {
        return maxCombo;
    }
    
    public boolean isActive() {
        return comboCount > 0;
    }
    
    public boolean isAtMax() {
        return comboCount >= maxCombo;
    }
    
    public float getComboProgress() {
        return (float) comboCount / maxCombo;
    }
}