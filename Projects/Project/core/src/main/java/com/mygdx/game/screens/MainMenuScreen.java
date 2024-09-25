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
    private MainMenuUI mainMenuUI;

    public MainMenuScreen(final Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // 배경 이미지 로드
        backgroundTexture = AssetManager.getInstance().getBackgroundTexture();
        Image background = new Image(backgroundTexture);
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(background);

        // MainMenuUI 생성 및 추가
        mainMenuUI = new MainMenuUI(game);
        stage.addActor(mainMenuUI);

        // 메뉴 위치 설정 (화면의 35% 위치)
        mainMenuUI.setYPositionPercent(0.25f);
    }

    @Override
    public void show() {}

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
        mainMenuUI.updatePosition();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        mainMenuUI.dispose();
    }
}
