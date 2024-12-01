package com.mygdx.game.screens;

import com.ImportedPackage._Imported_ClientBase;
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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Main;
import com.mygdx.game.Room;
import com.mygdx.game.ui.AnimatedImageButton;
import com.mygdx.game.ui.LobbyMap;
import com.mygdx.game.util.FontManager;

public class LobbyScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final Skin skin;
    private LobbyMap lobbyMap;
    private final SpriteBatch batch;
    private final Texture roomInfoBackground;
    private final BitmapFont roomInfoFont;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final GlyphLayout layout;
    private final TextureAtlas buttonAtlas;
    private final TextureAtlas startLeaveButtonAtlas;
    private Table buttonTable;
    private final Room room;
    public static boolean isJoined = false;

    public static boolean shouldStart = false;

    private Dialog currentDialog; // 현재 표시 중인 다이얼로그

    public LobbyScreen(final Main game, boolean isJoined) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT, camera);
        this.batch = new SpriteBatch();
        this.stage = new Stage(viewport, this.batch);
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        this.layout = new GlyphLayout();
        this.buttonAtlas = new TextureAtlas(Gdx.files.internal("ui/button.atlas"));
        this.startLeaveButtonAtlas = new TextureAtlas(Gdx.files.internal("ui/startLeaveButton.atlas"));

        this.room = game.getCurrentRoom();
        LobbyScreen.isJoined = isJoined;
        shouldStart = false;

        // 방 정보 배경 이미지 로드
        this.roomInfoBackground = new Texture(Gdx.files.internal("ui/Room_info.png"));

        // 방 정보 폰트 생성 및 설정
        this.roomInfoFont = FontManager.getInstance().getFont(19);
        this.roomInfoFont.setColor(Color.BLACK);

        createUI();
        Gdx.input.setInputProcessor(stage);
    }

    private void createUI() {
        // 로비 맵 생성 및 추가
        lobbyMap = new LobbyMap(game);
        stage.addActor(lobbyMap);

        createButtons();
    }

    private void createButtons() {
        buttonTable = new Table();
        buttonTable.top().right().padRight(20).padTop(20);
        buttonTable.setFillParent(true);

        Texture buttonTexture = buttonAtlas.findRegion("createLobbyBtn").getTexture();
        Texture startButtonTexture = startLeaveButtonAtlas.findRegion("leaveLobbyButton").getTexture();

        if(isJoined == false) {
            // 게임 시작 버튼
            AnimatedImageButton startButton = createAnimatedButtonForStartLeave(startButtonTexture, "startButton",
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                    	if(isJoined == false) {
                    		shouldStart = true;
                           	_Imported_ClientBase.startPls();
                            handleGameStart();
                    	}
                    }
                });

            buttonTable.add(startButton).width(112).height(34).padBottom(10).row();
        }

        // 설정 버튼
        AnimatedImageButton editButton = createAnimatedButton(buttonTexture, "settingBtn",
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("LobbyScreen", "Edit Room button clicked");
                    // TODO: 방 설정 다이얼로그 표시
                }
            });

        // 나가기 버튼
        AnimatedImageButton leaveButton = createAnimatedButtonForStartLeave(startButtonTexture, "leaveLobbyButton",
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleRoomExit();
                }
            });

        buttonTable.add(editButton).width(112).height(34).padBottom(10).row();
        buttonTable.add(leaveButton).width(112).height(34).padBottom(10).row();

        stage.addActor(buttonTable);
    }

    private AnimatedImageButton createAnimatedButton(Texture buttonTexture, String regionName,
                                                     ClickListener listener) {
        TextureAtlas.AtlasRegion region = buttonAtlas.findRegion(regionName);
        AnimatedImageButton button = new AnimatedImageButton(buttonTexture,
            region.getRegionX(), region.getRegionY(),
            region.getRegionWidth(), region.getRegionHeight());
        button.addListener(listener);
        return button;
    }

    private AnimatedImageButton createAnimatedButtonForStartLeave(Texture buttonTexture, String regionName,
                                                     ClickListener listener) {
        TextureAtlas.AtlasRegion region = startLeaveButtonAtlas.findRegion(regionName);
        AnimatedImageButton button = new AnimatedImageButton(buttonTexture,
            region.getRegionX(), region.getRegionY(),
            region.getRegionWidth(), region.getRegionHeight());
        button.addListener(listener);
        return button;
    }

    private void handleGameStart() {
        Room currentRoom = game.getCurrentRoom();
        int totalPlayers = currentRoom.pCount + 1; // 현재 플레이어 수 (자신 포함)

        // 플레이어 수 체크
        if (totalPlayers < 1) {
            showDialog("게임 시작 불가", "게임을 시작하려면 최소 2명의 플레이어가 필요합니다.");
            return;
        }

        // TODO: 서버에 게임 시작 요청
        // 서버 응답 대기 및 처리
        try {
            // 게임 시작 전환 효과 (페이드 아웃 등) 구현 가능
            game.setScreen(new GameScreen(game));
        } catch (Exception e) {
            Gdx.app.error("LobbyScreen", "Error starting game", e);
            showDialog("오류", "게임을 시작하는 중 오류가 발생했습니다.");
        }
    }

    private void handleRoomExit() {
        _Imported_ClientBase.outGame();
        game.setScreen(new MainMenuScreen(game));
    }

    private void showDialog(String title, String message) {
        // 이전 다이얼로그가 있다면 제거
        if (currentDialog != null) {
            currentDialog.hide();
        }

        Dialog dialog = new Dialog("", skin) {
            @Override
            protected void result(Object object) {
                hide();
                currentDialog = null;
            }
        };

        // 스타일 설정
        Label.LabelStyle labelStyle = new Label.LabelStyle(
            FontManager.getInstance().getFont(20), Color.WHITE
        );
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(
            skin.get(TextButton.TextButtonStyle.class)
        );
        buttonStyle.font = FontManager.getInstance().getFont(18);

        // 다이얼로그 구성
        dialog.pad(20);
        dialog.getTitleLabel().setStyle(labelStyle);
        dialog.text(new Label(message, labelStyle));
        dialog.button(new TextButton("확인", buttonStyle));

        // 표시
        dialog.show(stage);
        currentDialog = dialog;
    }

    private void drawRoomInfo() {
        Room currentRoom = game.getCurrentRoom();
        if (currentRoom == null) return;

        float backgroundWidth = roomInfoBackground.getWidth();
        float backgroundHeight = roomInfoBackground.getHeight();
        float scale = 1.0f;

        float x = (camera.viewportWidth - backgroundWidth * scale) / 2;
        float y = camera.viewportHeight - backgroundHeight * scale - 20;

        // 배경 그리기
        batch.draw(roomInfoBackground, x, y, backgroundWidth * scale, backgroundHeight * scale);

        // 방 정보 텍스트
        String roomTitle = "방 제목: " + currentRoom.getTitle();
        String playerInfo = String.format("플레이어: %d / %d", currentRoom.pCount + 1,
            currentRoom.getMaxPlayers());
        String roomCode = "방 코드: " + currentRoom.getCode();

        float textY = y + backgroundHeight * scale - 40;
        float centerX = x + (backgroundWidth * scale) / 2;

        // 텍스트 그리기
        roomInfoFont.setColor(Color.BLACK);
        drawCenteredText(roomTitle, centerX, textY);
        drawCenteredText(playerInfo, centerX, textY - 30);
        drawCenteredText(roomCode, centerX, textY - 60);
    }

    private void drawCenteredText(String text, float x, float y) {
        layout.setText(roomInfoFont, text);
        roomInfoFont.draw(batch, layout, x - layout.width / 2, y);
    }

    @Override
    public void render(float delta) {
    	if(shouldStart == true) {
    		handleGameStart();
    	}

    	//System.out.println(room.getme().size);

        // 화면 클리어
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        // 맵 업데이트 및 그리기
        lobbyMap.update(delta);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        lobbyMap.draw(batch, 1);
        batch.end();

        // UI 요소 업데이트 및 그리기
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        // 방 정보 그리기
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawRoomInfo();
        batch.end();
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
        if (currentDialog != null) {
            currentDialog.hide();
        }
    }
}
