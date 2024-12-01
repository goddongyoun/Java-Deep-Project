package com.mygdx.game.screens;

import com.ImportedPackage._Imported_ClientBase;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Main;
import com.mygdx.game.Player;
import com.mygdx.game.Room;

public class EscapeResultScreen implements Screen {
    private Main game;
    private Stage stage;
    private SpriteBatch batch;
    private Texture resultTexture;
    private Room currentRoom;
    private Player player;
    
    private float screenDuration = 5.0f;
    private float screenTimer = 0f;

    public EscapeResultScreen(Main game , boolean winOrDead) {
        this.game = game;
        this.stage = new Stage(new FitViewport(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT));
        this.batch = new SpriteBatch();
        this.currentRoom = game.getCurrentRoom();
        this.player = currentRoom.getme();
        
        if(!player.isBoss()) {
        	_Imported_ClientBase.setEnd();
        }
        
        if(winOrDead) {
        	resultTexture = new Texture(Gdx.files.internal("ui/win.png"));
        }
        else {
        	resultTexture = new Texture(Gdx.files.internal("ui/defeat.png"));
        }
        
        
        // UI 구성
        setupUI();
    }

    private void setupUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        
        Image resultImage = new Image(resultTexture);
        mainTable.add(resultImage).expand().center();
        
        stage.addActor(mainTable);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
    	Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 타이머 업데이트
        screenTimer += delta;        
        
        // 5초가 지나면 로비로 전환
        if (screenTimer >= screenDuration) {
        	player.setPosition(Main.WINDOW_WIDTH/2, Main.WINDOW_HEIGHT/2);
        	player.size = 32;
        	player.transformToFlog();
        	player.nicknameColor = Color.WHITE;
        	player.setCanMove(true);
        	for(int i = 0; i<currentRoom.pCount; i++) {
        		if(currentRoom.m_players[i] != null) {
        			currentRoom.m_players[i].size = 64;
        			currentRoom.m_players[i].transformToFlog();
        			currentRoom.m_players[i].nicknameColor = Color.WHITE;
        		}
        	}
            game.setScreen(new LobbyScreen(game, LobbyScreen.isJoined));
            //dispose();
            return;
        }

        stage.act(delta);
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
        batch.dispose();
        resultTexture.dispose();
    }
}