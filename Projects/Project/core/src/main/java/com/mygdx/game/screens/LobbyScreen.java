package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Main;
import com.mygdx.game.Player;
import com.mygdx.game.Room;
import com.mygdx.game.ui.*;
import com.mygdx.game.util.FontManager;

public class LobbyScreen implements Screen {
    private Main game;
    private Stage stage;
    private Skin skin;
    private LobbyMap lobbyMap;
    private SpriteBatch batch;
    private Texture roomInfoBackground;
    private BitmapFont roomInfoFont;
    private OrthographicCamera camera;
    private Viewport viewport;
    private GlyphLayout layout;
    private TextureAtlas buttonAtlas;
    private Table buttonTable;
    private MissionDialog missionDialog;
    private MissionDialog2 missionDialog2;
    private MissionDialog3 missionDialog3;
    private MissionDialog4 missionDialog4;
    private MissionDialog5 missionDialog5;
    private Player player;

    public LobbyScreen(final Main game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT, camera);
        this.batch = new SpriteBatch();
        this.stage = new Stage(viewport, this.batch);
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        this.layout = new GlyphLayout();
        this.buttonAtlas = new TextureAtlas(Gdx.files.internal("ui/button.atlas"));
        this.player = player;

        // 방 정보 배경 이미지 로드
        this.roomInfoBackground = new Texture(Gdx.files.internal("ui/room_info.png"));

        // 방 정보 폰트 생성
        this.roomInfoFont = FontManager.getInstance().getFont(19);
        this.roomInfoFont.setColor(Color.BLACK);

        // 미션 다이얼로그 생성

        createUI();

        Gdx.input.setInputProcessor(stage);
    }

    private void createUI() {
        lobbyMap = new LobbyMap(game);
        stage.addActor(lobbyMap);

        createButtons();
    }

    private void createButtons() {
        buttonTable = new Table();
        buttonTable.top().right().padRight(20).padTop(20);
        buttonTable.setFillParent(true);

        Texture buttonTexture = buttonAtlas.findRegion("createLobbyBtn").getTexture();

        AnimatedImageButton startButton = new AnimatedImageButton(buttonTexture,
            buttonAtlas.findRegion("createLobbyBtn").getRegionX(),
            buttonAtlas.findRegion("createLobbyBtn").getRegionY(),
            buttonAtlas.findRegion("createLobbyBtn").getRegionWidth(),
            buttonAtlas.findRegion("createLobbyBtn").getRegionHeight());

        AnimatedImageButton editButton = new AnimatedImageButton(buttonTexture,
            buttonAtlas.findRegion("settingBtn").getRegionX(),
            buttonAtlas.findRegion("settingBtn").getRegionY(),
            buttonAtlas.findRegion("settingBtn").getRegionWidth(),
            buttonAtlas.findRegion("settingBtn").getRegionHeight());

        TextButton mission3Btn = new TextButton("미션3 어몽구스",skin);
        TextButton mission4Btn = new TextButton("미션4 스타",skin);
        TextButton mission5Btn = new TextButton("미션5 팩맨",skin);


        AnimatedImageButton leaveButton = new AnimatedImageButton(buttonTexture,
            buttonAtlas.findRegion("closeGameBtn").getRegionX(),
            buttonAtlas.findRegion("closeGameBtn").getRegionY(),
            buttonAtlas.findRegion("closeGameBtn").getRegionWidth(),
            buttonAtlas.findRegion("closeGameBtn").getRegionHeight());

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                Gdx.app.log("LobbyScreen", "Start Game button clicked");
                missionDialog2 = new MissionDialog2("", skin, stage);
                missionDialog2.showMission(stage);
            }
        });

        editButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                missionDialog = new MissionDialog("", skin, stage);
                missionDialog.showMission(stage);  // 미션 팝업을 화면에 띄움
            }
        });

        mission3Btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                missionDialog3 = new MissionDialog3("", skin, stage);
                missionDialog3.showMission(stage);  // 미션 팝업을 화면에 띄움
            }
        });

        mission4Btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                missionDialog4 = new MissionDialog4("", skin, stage);
                missionDialog4.showMission(stage);  // 미션 팝업을 화면에 띄움
            }
        });

        mission5Btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                missionDialog5 = new MissionDialog5("", skin, stage);
                missionDialog5.showMission(stage);  // 미션 팝업을 화면에 띄움
            }
        });

        leaveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("LobbyScreen", "Leave Room button clicked");
                game.setScreen(new MainMenuScreen(game));
            }
        });

        buttonTable.add(startButton).padBottom(10).row();
        buttonTable.add(editButton).padBottom(10).row();
        buttonTable.add(mission3Btn).padBottom(10).row();
        buttonTable.add(mission4Btn).padBottom(10).row();
        buttonTable.add(mission5Btn).padBottom(10).row();
        buttonTable.add(leaveButton);

        stage.addActor(buttonTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        // 맵 업데이트 및 그리기
        lobbyMap.update(delta);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        lobbyMap.draw(batch, 1);
        batch.end();

        // UI 요소 (버튼 등) 그리기
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        // 방 정보 그리기
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawRoomInfo();
        batch.end();
    }

    private void drawRoomInfo() {
        Room currentRoom = game.getCurrentRoom();
        if (currentRoom == null) return;

        float backgroundWidth = roomInfoBackground.getWidth();
        float backgroundHeight = roomInfoBackground.getHeight();
        float scale = 1.0f;

        float x = (camera.viewportWidth - backgroundWidth * scale) / 2;
        float y = camera.viewportHeight - backgroundHeight * scale - 20;

//        batch.draw(roomInfoBackground, x, y, backgroundWidth * scale, backgroundHeight * scale);

        String roomTitle = "방 제목: " + currentRoom.getTitle();
        String playerInfo = String.format("플레이어: %d / %d", currentRoom.getPlayers().size(), currentRoom.getMaxPlayers());
        String roomCode = "방 코드: " + currentRoom.getCode();

        float textY = y + backgroundHeight * scale - 40;
        float centerX = x + (backgroundWidth * scale) / 2;

//        roomInfoFont.setColor(Color.BLACK);
//        layout.setText(roomInfoFont, roomTitle);
//        roomInfoFont.draw(batch, layout, centerX - layout.width / 2, textY);
//
//        layout.setText(roomInfoFont, playerInfo);
//        roomInfoFont.draw(batch, layout, centerX - layout.width / 2, textY - 30);
//
//        layout.setText(roomInfoFont, roomCode);
//        roomInfoFont.draw(batch, layout, centerX - layout.width / 2, textY - 60);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(Main.WINDOW_WIDTH / 2, Main.WINDOW_HEIGHT / 2, 0);
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
        batch.dispose();
        roomInfoBackground.dispose();
        buttonAtlas.dispose();
    }
}
