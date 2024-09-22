package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Main;
import com.mygdx.game.ui.LobbyUI;
import com.mygdx.game.util.FontManager;

public class LobbyScreen implements Screen {
    private Main game;
    private Stage stage;
    private Skin skin;
    private LobbyUI lobbyUI;

    public LobbyScreen(final Main game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());

        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        this.skin.add("default-font", FontManager.getInstance().getFont(16));

        Gdx.input.setInputProcessor(stage);

        lobbyUI = new LobbyUI(game, skin);
        stage.addActor(lobbyUI);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        skin.dispose();
    }
}
