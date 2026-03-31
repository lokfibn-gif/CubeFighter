package com.cubefighter.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.cubefighter.GameConfig;

public class InputHandler implements InputProcessor {
    
    private VirtualJoystick joystick;
    private Vector2 movement;
    private boolean attackPressed;
    private boolean attackHeld;
    private float attackHoldTime;
    private boolean dashPressed;
    private boolean shieldPressed;
    private boolean healPressed;
    private boolean ultimatePressed;
    private boolean pausePressed;
    private boolean paused;
    
    private final float maxChargeTime = 1.5f;
    
    private Array<InputListener> listeners;
    
    public InputHandler() {
        movement = new Vector2();
        listeners = new Array<>();
        attackPressed = false;
        attackHeld = false;
        attackHoldTime = 0f;
        dashPressed = false;
        shieldPressed = false;
        healPressed = false;
        ultimatePressed = false;
        pausePressed = false;
        paused = false;
    }
    
    public InputHandler(boolean useJoystick) {
        this();
        if (useJoystick) {
            joystick = new VirtualJoystick(
                GameConfig.JOYSTICK_X, 
                GameConfig.JOYSTICK_Y,
                GameConfig.JOYSTICK_RADIUS
            );
        }
    }
    
    public void update(float delta) {
        updateMovement();
        updateAttack(delta);
        updateAbilityButtons();
        
        if (attackHeld) {
            attackHoldTime += delta;
        }
    }
    
    private void updateMovement() {
        movement.set(0, 0);
        
        if (isMobile() && joystick != null) {
            movement.set(joystick.getMovement());
        }
        
        if (!isMobile() || GameConfig.ENABLE_KEYBOARD_ON_MOBILE) {
            if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
                movement.y = 1;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                movement.y = -1;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                movement.x = -1;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                movement.x = 1;
            }
        }
        
        if (movement.len() > 0) {
            movement.nor();
        }
    }
    
    private void updateAttack(float delta) {
        if (isMobile()) {
            if (Gdx.input.isTouched(0) && !isTouchingJoystickArea()) {
                attackHeld = true;
            } else {
                if (attackHeld && attackHoldTime < maxChargeTime) {
                    notifyAttack();
                } else if (attackHeld) {
                    notifyChargeAttack(attackHoldTime / maxChargeTime);
                }
                attackHeld = false;
                attackHoldTime = 0f;
            }
        } else {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                attackHeld = true;
            } else {
                if (attackHeld && attackHoldTime < maxChargeTime) {
                    notifyAttack();
                } else if (attackHeld) {
                    notifyChargeAttack(attackHoldTime / maxChargeTime);
                }
                attackHeld = false;
                attackHoldTime = 0f;
            }
        }
    }
    
    private void updateAbilityButtons() {
        if (!isMobile()) {
            dashPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
            shieldPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || 
                           Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
            healPressed = Gdx.input.isKeyJustPressed(Input.Keys.Q);
            ultimatePressed = Gdx.input.isKeyJustPressed(Input.Keys.R);
        }
        
        pausePressed = Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);
        
        if (pausePressed && !paused) {
            paused = true;
            notifyPause();
        }
    }
    
    private boolean isMobile() {
        return Gdx.app.getType().name().contains("Android") || 
               Gdx.app.getType().name().contains("iOS");
    }
    
    private boolean isTouchingJoystickArea() {
        if (joystick == null) return false;
        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
        return joystick.isInJoystickArea(touchX, touchY);
    }
    
    public void setDashPressed(boolean pressed) {
        this.dashPressed = pressed;
        if (pressed) notifyDash();
    }
    
    public void setShieldPressed(boolean pressed) {
        this.shieldPressed = pressed;
        if (pressed) {
            notifyShieldStart();
        } else {
            notifyShieldEnd();
        }
    }
    
    public void setHealPressed(boolean pressed) {
        this.healPressed = pressed;
        if (pressed) notifyHeal();
    }
    
    public void setUltimatePressed(boolean pressed) {
        this.ultimatePressed = pressed;
        if (pressed) notifyUltimate();
    }
    
    public void setAttackPressed() {
        this.attackPressed = true;
        notifyAttack();
    }
    
    public Vector2 getMovement() {
        return movement;
    }
    
    public boolean isAttackHeld() {
        return attackHeld;
    }
    
    public float getChargeProgress() {
        return Math.min(attackHoldTime / maxChargeTime, 1f);
    }
    
    public boolean isChargeReady() {
        return attackHoldTime >= maxChargeTime;
    }
    
    public boolean isPaused() {
        return paused;
    }
    
    public void setPaused(boolean paused) {
        this.paused = paused;
    }
    
    public void resume() {
        this.paused = false;
    }
    
    public VirtualJoystick getJoystick() {
        return joystick;
    }
    
    public void setJoystick(VirtualJoystick joystick) {
        this.joystick = joystick;
    }
    
    public void addListener(InputListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(InputListener listener) {
        listeners.removeValue(listener, true);
    }
    
    private void notifyAttack() {
        for (InputListener listener : listeners) {
            listener.onAttack();
        }
    }
    
    private void notifyChargeAttack(float chargeLevel) {
        for (InputListener listener : listeners) {
            listener.onChargeAttack(chargeLevel);
        }
    }
    
    private void notifyDash() {
        for (InputListener listener : listeners) {
            listener.onDash();
        }
    }
    
    private void notifyShieldStart() {
        for (InputListener listener : listeners) {
            listener.onShieldStart();
        }
    }
    
    private void notifyShieldEnd() {
        for (InputListener listener : listeners) {
            listener.onShieldEnd();
        }
    }
    
    private void notifyHeal() {
        for (InputListener listener : listeners) {
            listener.onHeal();
        }
    }
    
    private void notifyUltimate() {
        for (InputListener listener : listeners) {
            listener.onUltimate();
        }
    }
    
    private void notifyPause() {
        for (InputListener listener : listeners) {
            listener.onPause();
        }
    }
    
    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.SPACE:
                dashPressed = true;
                notifyDash();
                break;
            case Input.Keys.SHIFT_LEFT:
            case Input.Keys.SHIFT_RIGHT:
                shieldPressed = true;
                notifyShieldStart();
                break;
            case Input.Keys.Q:
                healPressed = true;
                notifyHeal();
                break;
            case Input.Keys.R:
                ultimatePressed = true;
                notifyUltimate();
                break;
            case Input.Keys.ESCAPE:
                pausePressed = true;
                paused = !paused;
                notifyPause();
                break;
        }
        return true;
    }
    
    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.SHIFT_LEFT:
            case Input.Keys.SHIFT_RIGHT:
                shieldPressed = false;
                notifyShieldEnd();
                break;
        }
        return true;
    }
    
    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (joystick != null && pointer == 0) {
            float worldY = Gdx.graphics.getHeight() - screenY;
            if (joystick.isInJoystickArea(screenX, worldY)) {
                joystick.touchDown(screenX, worldY);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (joystick != null) {
            joystick.touchUp();
        }
        return true;
    }
    
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (joystick != null) {
            joystick.touchDragged(screenX, Gdx.graphics.getHeight() - screenY);
        }
        return true;
    }
    
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
    
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    
    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
    
    public interface InputListener {
        void onAttack();
        void onChargeAttack(float chargeLevel);
        void onDash();
        void onShieldStart();
        void onShieldEnd();
        void onHeal();
        void onUltimate();
        void onPause();
    }
}