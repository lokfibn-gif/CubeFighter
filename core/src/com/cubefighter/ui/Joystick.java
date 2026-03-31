package com.cubefighter.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class Joystick {
    
    private float baseX;
    private float baseY;
    private float baseRadius;
    private float knobRadius;
    
    private Vector2 knobPosition;
    private Vector2 movementVector;
    
    private boolean active;
    private int activePointer;
    
    private Circle touchArea;
    
    private Color baseColor;
    private Color knobColor;
    private Color borderColor;
    
    private float deadzone;
    private boolean visible;
    
    public Joystick(float x, float y, float radius) {
        this.baseX = x;
        this.baseY = y;
        this.baseRadius = radius;
        this.knobRadius = radius * 0.4f;
        
        this.knobPosition = new Vector2(x, y);
        this.movementVector = new Vector2(0, 0);
        
        this.active = false;
        this.activePointer = -1;
        
        this.touchArea = new Circle(x, y, radius * 2f);
        
        this.baseColor = new Color(0.2f, 0.2f, 0.2f, 0.6f);
        this.knobColor = new Color(0.8f, 0.8f, 0.8f, 0.9f);
        this.borderColor = new Color(1f, 1f, 1f, 0.5f);
        
        this.deadzone = 0.1f;
        this.visible = true;
    }
    
    public void update(float delta) {
    }
    
    public void touchDown(float screenX, float screenY, int pointer) {
        if (!visible) return;
        
        float y = Gdx.graphics.getHeight() - screenY;
        if (touchArea.contains(screenX, y)) {
            active = true;
            activePointer = pointer;
            updateKnobPosition(screenX, y);
        }
    }
    
    public void touchUp(int pointer) {
        if (active && pointer == activePointer) {
            resetJoystick();
        }
    }
    
    public void touchDragged(float screenX, float screenY, int pointer) {
        if (!visible || !active) return;
        
        if (pointer == activePointer) {
            float y = Gdx.graphics.getHeight() - screenY;
            updateKnobPosition(screenX, y);
        }
    }
    
    private void updateKnobPosition(float screenX, float screenY) {
        float dx = screenX - baseX;
        float dy = screenY - baseY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float maxDistance = baseRadius - knobRadius;
        
        if (distance > maxDistance && distance > 0) {
            float ratio = maxDistance / distance;
            dx *= ratio;
            dy *= ratio;
        }
        
        knobPosition.set(baseX + dx, baseY + dy);
        
        if (maxDistance > 0) {
            movementVector.set(dx / maxDistance, dy / maxDistance);
        } else {
            movementVector.set(0, 0);
        }
        
        if (movementVector.len() < deadzone) {
            movementVector.set(0, 0);
        } else {
            movementVector.nor();
        }
    }
    
    private void resetJoystick() {
        active = false;
        activePointer = -1;
        knobPosition.set(baseX, baseY);
        movementVector.set(0, 0);
    }
    
    public void render(ShapeRenderer shapeRenderer) {
        if (!visible) return;
        
        shapeRenderer.setColor(baseColor);
        shapeRenderer.circle(baseX, baseY, baseRadius);
        
        drawCircleOutline(shapeRenderer, baseX, baseY, baseRadius, borderColor);
        
        Color activeKnobColor = active ? 
            new Color(knobColor.r * 1.2f, knobColor.g * 1.2f, knobColor.b * 1.2f, knobColor.a) : 
            knobColor;
        
        shapeRenderer.setColor(activeKnobColor);
        shapeRenderer.circle(knobPosition.x, knobPosition.y, knobRadius);
        
        if (active) {
            drawCircleOutline(shapeRenderer, knobPosition.x, knobPosition.y, knobRadius, 
                new Color(1f, 1f, 1f, 0.8f));
        }
    }
    
    private void drawCircleOutline(ShapeRenderer renderer, float x, float y, float radius, Color color) {
        renderer.setColor(color);
        int segments = 32;
        float angleStep = (float) (2 * Math.PI / segments);
        
        for (int i = 0; i < segments; i++) {
            float angle1 = i * angleStep;
            float angle2 = (i + 1) * angleStep;
            
            float x1 = x + (float) Math.cos(angle1) * radius;
            float y1 = y + (float) Math.sin(angle1) * radius;
            float x2 = x + (float) Math.cos(angle2) * radius;
            float y2 = y + (float) Math.sin(angle2) * radius;
            
            renderer.rectLine(x1, y1, x2, y2, 2f);
        }
    }
    
    public Vector2 getMovementVector() {
        return movementVector;
    }
    
    public float getMovementX() {
        return movementVector.x;
    }
    
    public float getMovementY() {
        return movementVector.y;
    }
    
    public float getAngle() {
        return (float) Math.atan2(movementVector.y, movementVector.x);
    }
    
    public float getAngleDegrees() {
        return (float) Math.toDegrees(Math.atan2(movementVector.y, movementVector.x));
    }
    
    public float getMagnitude() {
        return movementVector.len();
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setPosition(float x, float y) {
        this.baseX = x;
        this.baseY = y;
        this.touchArea.setPosition(x, y);
        if (!active) {
            knobPosition.set(x, y);
        }
    }
    
    public void setSize(float radius) {
        this.baseRadius = radius;
        this.knobRadius = radius * 0.4f;
        this.touchArea.setRadius(radius * 2f);
    }
    
    public void setBaseColor(Color color) {
        this.baseColor = new Color(color);
    }
    
    public void setKnobColor(Color color) {
        this.knobColor = new Color(color);
    }
    
    public void setBorderColor(Color color) {
        this.borderColor = new Color(color);
    }
    
    public void setDeadzone(float deadzone) {
        this.deadzone = Math.max(0, Math.min(1, deadzone));
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
        if (!visible) {
            resetJoystick();
        }
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public float getX() {
        return baseX;
    }
    
    public float getY() {
        return baseY;
    }
    
    public float getRadius() {
        return baseRadius;
    }
    
    public Vector2 getKnobPosition() {
        return knobPosition;
    }
    
    public int getActivePointer() {
        return activePointer;
    }
    
    public void reset() {
        resetJoystick();
    }
}