package com.cubefighter.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;
import java.util.Map;

public class AudioManager implements Disposable {
    public static AudioManager instance;
    private Map<SoundType, Sound> sounds;
    private Map<String, Music> musicTracks;
    private Music currentMusic;
    private String currentTrack;
    private float masterVolume;
    private float musicVolume;
    private float sfxVolume;
    private boolean soundEnabled;
    private boolean musicEnabled;

    private AudioManager() {
        sounds = new HashMap<>();
        musicTracks = new HashMap<>();
        masterVolume = 1.0f;
        musicVolume = 0.5f;
        sfxVolume = 0.7f;
        soundEnabled = true;
        musicEnabled = true;
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void loadSounds() {
        sounds.put(SoundType.ATTACK, Gdx.audio.newSound(Gdx.files.internal("sounds/attack.wav")));
        sounds.put(SoundType.HIT, Gdx.audio.newSound(Gdx.files.internal("sounds/hit.wav")));
        sounds.put(SoundType.UPGRADE, Gdx.audio.newSound(Gdx.files.internal("sounds/upgrade.wav")));
        sounds.put(SoundType.LEVELUP, Gdx.audio.newSound(Gdx.files.internal("sounds/levelup.wav")));
        sounds.put(SoundType.BOSS_APPEAR, Gdx.audio.newSound(Gdx.files.internal("sounds/boss_appear.wav")));
        sounds.put(SoundType.DEATH, Gdx.audio.newSound(Gdx.files.internal("sounds/death.wav")));
        sounds.put(SoundType.VICTORY, Gdx.audio.newSound(Gdx.files.internal("sounds/victory.wav")));
        sounds.put(SoundType.BUTTON, Gdx.audio.newSound(Gdx.files.internal("sounds/button.wav")));
    }

    public void loadMusic() {
        Music menuMusic = Gdx.audio.newMusic(Gdx.files.internal("music/menu.mp3"));
        menuMusic.setLooping(true);
        musicTracks.put("menu", menuMusic);

        Music gameMusic = Gdx.audio.newMusic(Gdx.files.internal("music/game.mp3"));
        gameMusic.setLooping(true);
        musicTracks.put("game", gameMusic);

        Music bossMusic = Gdx.audio.newMusic(Gdx.files.internal("music/boss.mp3"));
        bossMusic.setLooping(true);
        musicTracks.put("boss", bossMusic);
    }

    public void play(SoundType type) {
        if (!soundEnabled) return;
        Sound sound = sounds.get(type);
        if (sound != null) {
            sound.play(masterVolume * sfxVolume);
        }
    }

    public void play(SoundType type, float volume) {
        if (!soundEnabled) return;
        Sound sound = sounds.get(type);
        if (sound != null) {
            sound.play(masterVolume * sfxVolume * volume);
        }
    }

    public void play(SoundType type, float volume, float pitch, float pan) {
        if (!soundEnabled) return;
        Sound sound = sounds.get(type);
        if (sound != null) {
            long id = sound.play(masterVolume * sfxVolume * volume);
            sound.setPitch(id, pitch);
            sound.setPan(id, pan, volume);
        }
    }

    public void stop(SoundType type) {
        Sound sound = sounds.get(type);
        if (sound != null) {
            sound.stop();
        }
    }

    public void stopAllSounds() {
        for (Sound sound : sounds.values()) {
            sound.stop();
        }
    }

    public void playMusic(String trackName) {
        if (!musicEnabled) return;
        Music music = musicTracks.get(trackName);
        if (music != null) {
            if (currentMusic != null && currentMusic.isPlaying()) {
                currentMusic.stop();
            }
            currentMusic = music;
            currentTrack = trackName;
            music.setVolume(masterVolume * musicVolume);
            music.play();
        }
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    public void pauseMusic() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }

    public void resumeMusic() {
        if (currentMusic != null && musicEnabled) {
            currentMusic.play();
        }
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0f, Math.min(1f, volume));
        updateMusicVolume();
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0f, Math.min(1f, volume));
        updateMusicVolume();
    }

    public void setSfxVolume(float volume) {
        this.sfxVolume = Math.max(0f, Math.min(1f, volume));
    }

    private void updateMusicVolume() {
        if (currentMusic != null) {
            currentMusic.setVolume(masterVolume * musicVolume);
        }
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled && currentMusic != null) {
            currentMusic.pause();
        } else if (enabled && currentMusic != null) {
            currentMusic.play();
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public String getCurrentTrack() {
        return currentTrack;
    }

    public boolean isMusicPlaying() {
        return currentMusic != null && currentMusic.isPlaying();
    }

    public static void disposeInstance() {
        if (instance != null) {
            instance.dispose();
            instance = null;
        }
    }

    @Override
    public void dispose() {
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
        sounds.clear();
        for (Music music : musicTracks.values()) {
            music.dispose();
        }
        musicTracks.clear();
        currentMusic = null;
    }
}