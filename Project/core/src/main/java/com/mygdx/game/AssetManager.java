package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class AssetManager {
    private static AssetManager instance;
    private Texture buttonTexture;
    private Texture backgroundTexture;

    private AssetManager() {
        buttonTexture = new Texture(Gdx.files.internal("ui/buttons.png"));
        backgroundTexture = new Texture(Gdx.files.internal("ui/background.png"));
    }

    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }

    public Texture getButtonTexture() {
        return buttonTexture;
    }

    public Texture getBackgroundTexture() {
        return backgroundTexture;
    }

    public void dispose() {
        buttonTexture.dispose();
        backgroundTexture.dispose();
    }
}
