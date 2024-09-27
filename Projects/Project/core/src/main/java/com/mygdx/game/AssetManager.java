package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class AssetManager {
    private static AssetManager instance;
    private Texture buttonTexture;
    private Texture backgroundTexture;
    private Texture logoTexture;

    private AssetManager() {
        loadAssets();
    }

    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }

    public void loadAssets() {
        buttonTexture = new Texture(Gdx.files.internal("ui/buttons.png"));
        backgroundTexture = new Texture(Gdx.files.internal("ui/background.png"));
        logoTexture = new Texture(Gdx.files.internal("ui/logo.png"));

        // 배경 이미지의 필터링 설정
        backgroundTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    public void reloadAssets() {
        dispose();
        loadAssets();
    }

    public Texture getButtonTexture() {
        return buttonTexture;
    }

    public Texture getBackgroundTexture() {
        return backgroundTexture;
    }

    public Texture getLogoTexture() {
        return logoTexture;
    }

    public void dispose() {
        if (buttonTexture != null) buttonTexture.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (logoTexture != null) logoTexture.dispose();
    }
}
