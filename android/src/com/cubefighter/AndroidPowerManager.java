package com.cubefighter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Window;
import android.view.WindowManager;

public class AndroidPowerManager {
    
    private Activity activity;
    private Vibrator vibrator;
    private boolean keepScreenOn;
    private boolean isPaused;
    private boolean vibrationEnabled;
    
    public AndroidPowerManager(Activity activity) {
        this.activity = activity;
        this.vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        this.keepScreenOn = false;
        this.isPaused = false;
        this.vibrationEnabled = true;
    }
    
    public void setKeepScreenOn(boolean keepOn) {
        this.keepScreenOn = keepOn;
        applyKeepScreenOn();
    }
    
    private void applyKeepScreenOn() {
        if (activity == null || activity.getWindow() == null) return;
        
        Window window = activity.getWindow();
        if (keepScreenOn) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
    
    public void onResume() {
        this.isPaused = false;
        applyKeepScreenOn();
    }
    
    public void onPause() {
        this.isPaused = true;
    }
    
    public boolean isPaused() {
        return isPaused;
    }
    
    public void setVibrationEnabled(boolean enabled) {
        this.vibrationEnabled = enabled;
    }
    
    public boolean isVibrationEnabled() {
        return vibrationEnabled;
    }
    
    public void vibrate(long milliseconds) {
        if (!vibrationEnabled || vibrator == null || !vibrator.hasVibrator()) {
            return;
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect effect = VibrationEffect.createOneShot(
                milliseconds,
                VibrationEffect.DEFAULT_AMPLITUDE
            );
            vibrator.vibrate(effect);
        } else {
            vibrator.vibrate(milliseconds);
        }
    }
    
    public void vibrate(long[] pattern, int repeat) {
        if (!vibrationEnabled || vibrator == null || !vibrator.hasVibrator()) {
            return;
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect effect = VibrationEffect.createWaveform(pattern, repeat);
            vibrator.vibrate(effect);
        } else {
            vibrator.vibrate(pattern, repeat);
        }
    }
    
    public void vibrateHit() {
        vibrate(50);
    }
    
    public void vibrateDamage() {
        vibrate(100);
    }
    
    public void vibrateCriticalHit() {
        long[] pattern = {0, 100, 50, 100};
        vibrate(pattern, -1);
    }
    
    public void vibrateDeath() {
        long[] pattern = {0, 200, 100, 200, 100, 200};
        vibrate(pattern, -1);
    }
    
    public void cancelVibration() {
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
    
    public boolean hasVibrator() {
        return vibrator != null && vibrator.hasVibrator();
    }
    
    public boolean checkAvailableStorage() {
        try {
            android.os.StatFs stat = new android.os.StatFs(
                android.os.Environment.getDataDirectory().getPath()
            );
            long availableBytes;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                availableBytes = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
            } else {
                availableBytes = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
            }
            long minRequiredBytes = 10 * 1024 * 1024;
            return availableBytes >= minRequiredBytes;
        } catch (Exception e) {
            return true;
        }
    }
    
    public long getAvailableStorageMB() {
        try {
            android.os.StatFs stat = new android.os.StatFs(
                android.os.Environment.getDataDirectory().getPath()
            );
            long availableBytes;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                availableBytes = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
            } else {
                availableBytes = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
            }
            return availableBytes / (1024 * 1024);
        } catch (Exception e) {
            return -1;
        }
    }
}