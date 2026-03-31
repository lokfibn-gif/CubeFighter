package com.cubefighter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CubeFighterGame extends Game {
    private MenuScreen menuScreen;
    private GameScreen gameScreen;

    @Override
    public void create() {
        menuScreen = new MenuScreen(this);
        setScreen(menuScreen);
    }

    public void startGame() {
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        gameScreen = new GameScreen(this);
        setScreen(gameScreen);
    }

    public void showMenu() {
        if (gameScreen != null) {
            gameScreen.dispose();
            gameScreen = null;
        }
        setScreen(menuScreen);
    }

    @Override
    public void dispose() {
        if (menuScreen != null) {
            menuScreen.dispose();
        }
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        super.dispose();
    }
}