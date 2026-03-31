package com.cubefighter;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
    
    private AndroidPowerManager powerManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;
        config.useGyroscope = false;
        config.useImmersiveMode = true;
        config.useWakelock = true;
        
        powerManager = new AndroidPowerManager(this);
        powerManager.setKeepScreenOn(true);
        
        initialize(new CubeFighterGame(), config);
        
        setupImmersiveMode();
    }
    
    private void setupImmersiveMode() {
        Window window = getWindow();
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (powerManager != null) {
            powerManager.onResume();
        }
        setupImmersiveMode();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (powerManager != null) {
            powerManager.onPause();
        }
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setupImmersiveMode();
        }
    }
}