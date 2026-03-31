package com.cubefighter.input;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Circle;

public class VirtualJoystick {
    
    private float x;
    private float y;
    private float radius;
    private float knobRadius;
    private Vector2 knobPosition;
    private Vector2 movement;
    private boolean active;
    
    private Color backgroundColor;
    private Color knobColor;
    private Color borderColor;
    
    private Circle touchArea;
    
    public VirtualJoystick(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.knobRadius = radius * 0.4f;
        this.knobPosition = new Vector2(x, y);
        this.movement = new Vector2();
        this.active = false;
        this.touchArea = new Circle(x, y, radius * 1.5f);
        
        setBackgroundColor(new Color(0.2f, 0.2f, 0.2f, 0.5f));
        setKnobColor(new Color(0.8f, 0.8f, 0.8f, 0.9f));
        setBorderColor(new Color(1f, 1f, 1f, 0.6f));
    }
    
    public void touchDown(float screenX, float screenY) {
        active = true;
        updateKnobPosition(screenX, screenY);
    }
    
    public void touchUp() {
        active = false;
        knobPosition.set(x, y);
        movement.set(0, 0);
    }
    
    public void touchDragged(float screenX, float screenY) {
        if (active) {
            updateKnobPosition(screenX, screenY);
        }
    }
    
    private void updateKnobPosition(float screenX, float screenY) {
        float dx = screenX - x;
        float dy = screenY - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float maxDistance = radius - knobRadius;
        
        if (distance > maxDistance) {
            float ratio = maxDistance / distance;
            dx *= ratio;
            dy *= ratio;
            distance = maxDistance;
        }
        
        knobPosition.set(x + dx, y + dy);
        
        if (maxDistance > 0) {
            movement.set(dx / maxDistance, dy / maxDistance);
        } else {
            movement.set(0, 0);
        }
        
        movement.clamp(-1f, 1f);
    }
    
    public Vector2 getMovement() {
        return movement;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getRadius() {
        return radius;
    }
    
    public float getKnobRadius() {
        return knobRadius;
    }
    
    public Vector2 getKnobPosition() {
        return knobPosition;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public boolean isInJoystickArea(float screenX, float screenY) {
        return touchArea.contains(screenX, screenY);
    }
    
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.touchArea.setPosition(x, y);
        if (!active) {
            knobPosition.set(x, y);
        }
    }
    
    public void setRadius(float radius) {
        this.radius = radius;
        this.knobRadius = radius * 0.4f;
        this.touchArea.setRadius(radius * 1.5f);
    }
    
    public void setBackgroundColor(Color color) {
        this.backgroundColor = new Color(color);
    }
    
    public void setKnobColor(Color color) {
        this.knobColor = new Color(color);
    }
    
    public void setBorderColor(Color color) {
        this.borderColor = new Color(color);
    }
    
    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(backgroundColor);
        shapeRenderer.circle(x, y, radius);
        
        shapeRenderer.setColor(borderColor);
        shapeRenderer.setColor(borderColor.r, borderColor.g, borderColor.b, borderColor.a);
        drawCircleOutline(shapeRenderer, x, y, radius);
        
        shapeRenderer.setColor(knobColor);
        shapeRenderer.circle(knobPosition.x, knobPosition.y, knobRadius);
        
        if (active) {
            shapeRenderer.setColor(borderColor.r, borderColor.g, borderColor.b, 0.8f);
            drawCircleOutline(shapeRenderer, knobPosition.x, knobPosition.y, knobRadius);
        }
    }
    
    private void drawCircleOutline(ShapeRenderer shapeRenderer, float x, float y, float radius) {
        int segments = 32;
        float angleStep = (float) (2 * Math.PI / segments);
        float lineSize = 2f;
        
        for (int i = 0; i < segments; i++) {
            float angle = i * angleStep;
            float nextAngle = (i + 1) * angleStep;
            
            float x1 = x + (float) Math.cos(angle) * radius;
            float y1 = y + (float) Math.sin(angle) * radius;
            float x2 = x + (float) Math.cos(nextAngle) * radius;
            float y2 = y + (float) Math.sin(nextAngle) * radius;
            
            shapeRenderer.rectLine(x1, y1, x2, y2, lineSize);
        }
    }
    
    public void reset() {
        active = false;
        knobPosition.set(x, y);
        movement.set(0, 0);
    }
    
    public float getMovementX() {
        return movement.x;
    }
    
    public float getMovementY() {
        return movement.y;
    }
    
    public float getMovementAngle() {
        return (float) Math.atan2(movement.y, movement.x);
    }
    
    public float getMovementMagnitude() {
        return movement.len();
    }
}