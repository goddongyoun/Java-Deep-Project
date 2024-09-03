package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Main;
import com.mygdx.game.ui.MainMenuUI;

public class MainMenuScreen implements Screen {

    private final Main game;
    private Stage stage;
    private Skin skin;
    private MainMenuUI mainMenuUI;

    public MainMenuScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // UI 스킨 로드 (올바른 경로로 수정)
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // MainMenuUI 초기화
        mainMenuUI = new MainMenuUI(game, stage, skin);
    }

    @Override
    public void show() {
        // 화면이 처음 나타날 때 필요한 초기화 작업
    }

    @Override
    public void render(float delta) {
        // 배경을 흰색으로 설정
        Gdx.gl.glClearColor(1, 1, 1, 1);  // 흰색 (RGB: 1, 1, 1)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // 게임이 일시 중지되었을 때의 처리
    }

    @Override
    public void resume() {
        // 게임이 다시 시작되었을 때의 처리
    }

    @Override
    public void hide() {
        // 화면이 숨겨졌을 때 처리
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (mainMenuUI != null) {
            mainMenuUI.dispose();
        }
    }
}
