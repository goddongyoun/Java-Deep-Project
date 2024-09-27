package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Main;
import com.mygdx.game.Room;
import com.mygdx.game.ui.LobbyMap;

public class LobbyScreen implements Screen {
    private Main game;
    private Stage stage;
    private Skin skin;
    private Table topTable;
    private LobbyMap lobbyMap;

    public LobbyScreen(final Main game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        createUI();

        Gdx.input.setInputProcessor(stage);
    }

    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        createTopTable();
        mainTable.add(topTable).expandX().fillX().pad(10).row();

        lobbyMap = new LobbyMap(game);
        mainTable.add(lobbyMap).expand().fill();
    }

    private void createTopTable() {
        topTable = new Table();

        Room currentRoom = game.getCurrentRoom();
        Label roomInfoLabel = new Label(
            currentRoom.getTitle() + " (" + currentRoom.getPlayers().size() + "/" + currentRoom.getMaxPlayers() + ") Code: " + currentRoom.getCode(),
            skin
        );
        topTable.add(roomInfoLabel).expandX().align(Align.left);

        Table buttonTable = new Table();
        TextButton startButton = new TextButton("Start Game", skin);
        TextButton editButton = new TextButton("Edit Room", skin);
        TextButton leaveButton = new TextButton("Leave Room", skin);

        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO: Implement game start logic
            }
        });

        editButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO: Implement room edit logic
            }
        });

        leaveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        buttonTable.add(startButton).pad(5);
        buttonTable.add(editButton).pad(5);
        buttonTable.add(leaveButton).pad(5);

        topTable.add(buttonTable).align(Align.right);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        lobbyMap.update(delta);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        lobbyMap.resize(width, height);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        lobbyMap.dispose();
    }
}
