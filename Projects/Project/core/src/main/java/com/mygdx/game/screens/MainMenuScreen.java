package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Main;
import com.mygdx.game.ui.MainMenuUI;
import com.mygdx.game.AssetManager;

public class MainMenuScreen implements Screen {
    private Main game;
    private Stage stage;
    private Texture backgroundTexture;
    private Image backgroundImage;
    private MainMenuUI mainMenuUI;

    public MainMenuScreen(final Main game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        createUI();
    }

    private void createUI() {
        // 배경 설정
        this.backgroundTexture = AssetManager.getInstance().getBackgroundTexture();
        backgroundImage = new Image(backgroundTexture);
        updateBackgroundSize();
        stage.addActor(backgroundImage);

        // MainMenuUI 초기화
        this.mainMenuUI = new MainMenuUI(game);
        stage.addActor(mainMenuUI);

        // UI 요소 위치 및 크기 설정
        mainMenuUI.setLogoPositionPercentX(0.51f);
        mainMenuUI.setLogoPositionPercentY(0.7f);
        mainMenuUI.setButtonsPositionPercentX(0.487f);
        mainMenuUI.setButtonsPositionPercentY(0.28f);
        mainMenuUI.setLogoSizePercent(0.4f);
        mainMenuUI.setButtonSizePercent(0.13f);
        mainMenuUI.setButtonSpacing(15f);

        // 레이아웃 업데이트
        mainMenuUI.updateLayout();
    }

    private void updateBackgroundSize() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float textureWidth = backgroundTexture.getWidth();
        float textureHeight = backgroundTexture.getHeight();

        float scaleX = screenWidth / textureWidth;
        float scaleY = screenHeight / textureHeight;
        float scale = Math.max(scaleX, scaleY);

        float newWidth = textureWidth * scale;
        float newHeight = textureHeight * scale;

        backgroundImage.setSize(newWidth, newHeight);
        backgroundImage.setPosition((screenWidth - newWidth) / 2, (screenHeight - newHeight) / 2);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        AssetManager.getInstance().reloadAssets();
        createUI();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        updateBackgroundSize();
        mainMenuUI.updateLayout();
    }

    @Override
    public void pause() {
        // 게임이 일시 정지될 때 호출됩니다.
    }

    @Override
    public void resume() {
        // 게임이 재개될 때 호출됩니다.
        AssetManager.getInstance().reloadAssets();
        createUI();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        mainMenuUI.dispose();
    }
}
