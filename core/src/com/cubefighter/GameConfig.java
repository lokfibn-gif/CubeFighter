package com.cubefighter;

public final class GameConfig {
    
    private GameConfig() {}
    
    public static boolean DEBUG_MODE = false;
    public static boolean SHOW_FPS = true;
    public static boolean SHOW_HITBOXES = false;
    public static boolean SHOW_GRID = false;
    
    public static float MASTER_VOLUME = 1.0f;
    public static float MUSIC_VOLUME = 0.7f;
    public static float SOUND_VOLUME = 0.8f;
    public static float AMBIENT_VOLUME = 0.5f;
    
    public static int TARGET_FPS = 60;
    public static boolean VSYNC_ENABLED = true;
    public static boolean FULLSCREEN = false;
    
    public static int PARTICLE_LIMIT = 500;
    public static int MAX_PARTICLES_PER_FRAME = 50;
    
    public static int WINDOW_WIDTH = 800;
    public static int WINDOW_HEIGHT = 600;
    public static boolean RESIZABLE = true;
    
    public static float JOYSTICK_X = 150f;
    public static float JOYSTICK_Y = 150f;
    public static float JOYSTICK_RADIUS = 80f;
    public static float BUTTON_SIZE = 80f;
    
    public static boolean ENABLE_KEYBOARD_ON_MOBILE = false;
    public static boolean ENABLE_TOUCH_ON_DESKTOP = false;
    public static boolean AUTO_PAUSE_ON_LOSS_FOCUS = true;
    
    public static String SAVE_FILE_NAME = "cubefighter_save.dat";
    public static String PREFERENCES_NAME = "com.cubefighter.prefs";
    
    public static float VIBRATION_DURATION_SHORT = 50f;
    public static float VIBRATION_DURATION_LONG = 150f;
    
    public static boolean PARTICLE_EFFECTS_ENABLED = true;
    public static boolean SCREEN_SHAKE_ENABLED = true;
    public static float SCREEN_SHAKE_INTENSITY = 5f;
    
    public static int HIGH_SCORE_SLOT_COUNT = 10;
    public static String PLAYER_NAME_DEFAULT = "Player";
    
    public static float TUTORIAL_DELAY_SECONDS = 2f;
    public static boolean SKIP_TUTORIAL = false;
    
    public static float MENU_TRANSITION_TIME = 0.3f;
    public static float FADE_DURATION = 0.5f;
    
    public static float EXPLOSION_SHAKE_DURATION = 0.3f;
    public static float BOSS_APPEAR_SHAKE_DURATION = 0.5f;
    
    public static float POWERUP_BLINK_RATE = 0.25f;
    public static float LOW_HEALTH_BLINK_RATE = 0.5f;
    public static float LOW_HEALTH_THRESHOLD = 0.25f;
    
    public static String DEFAULT_LANGUAGE = "en";
    public static String[] SUPPORTED_LANGUAGES = {"en", "es", "fr", "de", "ja", "zh"};
    
    public static boolean ANALYTICS_ENABLED = true;
    public static boolean SEND_ERROR_REPORTS = true;
    
    public static String GOOGLE_PLAY_APP_ID = "";
    public static String GAME_CENTER_ID = "";
    
    public static boolean LEADERBOARD_ENABLED = true;
    public static boolean ACHIEVEMENTS_ENABLED = true;
    
    public static String ACHIEVEMENT_FIRST_BLOOD = "first_blood";
    public static String ACHIEVEMENT_WAVE_MASTER = "wave_master";
    public static String ACHIEVEMENT_BOSS_SLAYER = "boss_slayer";
    public static String ACHIEVEMENT_PERFECT_WAVE = "perfect_wave";
    
    public static String getButtonDashTexture() {
        return "ui/button_dash.png";
    }
    
    public static String getButtonShieldTexture() {
        return "ui/button_shield.png";
    }
    
    public static String getButtonHealTexture() {
        return "ui/button_heal.png";
    }
    
    public static String getButtonUltimateTexture() {
        return "ui/button_ultimate.png";
    }
    
    public static float getButtonDashX() {
        return WINDOW_WIDTH - 250f;
    }
    
    public static float getButtonDashY() {
        return 150f;
    }
    
    public static float getButtonShieldX() {
        return WINDOW_WIDTH - 150f;
    }
    
    public static float getButtonShieldY() {
        return 150f;
    }
    
    public static float getButtonHealX() {
        return WINDOW_WIDTH - 250f;
    }
    
    public static float getButtonHealY() {
        return 60f;
    }
    
    public static float getButtonUltimateX() {
        return WINDOW_WIDTH - 150f;
    }
    
    public static float getButtonUltimateY() {
        return 60f;
    }
    
    public static void setDebugMode(boolean enabled) {
        DEBUG_MODE = enabled;
        if (enabled) {
            SHOW_HITBOXES = true;
            SHOW_FPS = true;
        }
    }
    
    public static void setVolume(float master, float music, float sound) {
        MASTER_VOLUME = Math.max(0f, Math.min(1f, master));
        MUSIC_VOLUME = Math.max(0f, Math.min(1f, music));
        SOUND_VOLUME = Math.max(0f, Math.min(1f, sound));
    }
    
    public static void setGraphics(int fps, boolean vsync, boolean fullscreen) {
        TARGET_FPS = Math.max(30, Math.min(120, fps));
        VSYNC_ENABLED = vsync;
        FULLSCREEN = fullscreen;
    }
    
    public static void setPerformance(int particleLimit, boolean particlesEnabled) {
        PARTICLE_LIMIT = Math.max(100, Math.min(1000, particleLimit));
        PARTICLE_EFFECTS_ENABLED = particlesEnabled;
    }
    
    public static void resetToDefaults() {
        DEBUG_MODE = false;
        SHOW_FPS = true;
        SHOW_HITBOXES = false;
        MASTER_VOLUME = 1.0f;
        MUSIC_VOLUME = 0.7f;
        SOUND_VOLUME = 0.8f;
        TARGET_FPS = 60;
        VSYNC_ENABLED = true;
        PARTICLE_LIMIT = 500;
        PARTICLE_EFFECTS_ENABLED = true;
        SCREEN_SHAKE_ENABLED = true;
    }
}