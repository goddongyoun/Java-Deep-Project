package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Main;
import com.mygdx.game.ui.ChatSystem;

public class LobbyScreen implements Screen {

    private final Main game;
    private Stage stage;
    private Skin skin;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;

    private Image playerImage;
    private float playerSpeed = 100f;
    private String roomName;
    private int maxPlayers;
    private String roomCode;
    private ChatSystem chatSystem;
    private Table rootTable;

    public LobbyScreen(Main game, Skin skin, String roomName, int maxPlayers, String roomCode) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.skin = skin;
        this.roomName = roomName;
        this.maxPlayers = maxPlayers;
        this.roomCode = roomCode;
        Gdx.input.setInputProcessor(stage);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        map = new TmxMapLoader().load("maps/lobby_map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1.5f);

        rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        initializeUI();
        initializePlayer();
        initializeChatSystem();
    }

    private void initializeUI() {
        Table topTable = new Table();

        Label roomInfoLabel = new Label(roomName + " (1/" + maxPlayers + ") - Code: " + roomCode, skin);
        roomInfoLabel.setFontScale(1.0f);
        topTable.add(roomInfoLabel).expandX().left().padLeft(10);

        Table buttonTable = new Table();
        ImageButton startGameButton = createImageButton("start_game.png");
        ImageButton modifyRoomButton = createImageButton("edit_room.png");
        ImageButton leaveRoomButton = createImageButton("leave_room.png");

        buttonTable.add(startGameButton).padBottom(5).row();
        buttonTable.add(modifyRoomButton).padBottom(5).row();
        buttonTable.add(leaveRoomButton);

        topTable.add(buttonTable).right().padRight(10);

        rootTable.add(topTable).expandX().fillX().top().padTop(10).row();
    }

    private void initializePlayer() {
        Texture playerTexture = new Texture(Gdx.files.internal("player_default.png"));
        playerImage = new Image(playerTexture);

        float scale = 0.15f;
        playerImage.setScale(scale);

        System.out.println("Original size: " + playerTexture.getWidth() + "x" + playerTexture.getHeight());
        System.out.println("Scaled size: " + playerImage.getWidth() + "x" + playerImage.getHeight());
        System.out.println("Scale: " + playerImage.getScaleX() + "x" + playerImage.getScaleY());

        rootTable.add(playerImage).expand().center().row();
    }

    private void initializeChatSystem() {
        chatSystem = new ChatSystem(skin);
        rootTable.add(chatSystem).left().bottom().size(250, 220).pad(10);
    }

    private ImageButton createImageButton(String imagePath) {
        Texture texture = new Texture(Gdx.files.internal(imagePath));
        TextureRegionDrawable drawable = new TextureRegionDrawable(texture);
        return new ImageButton(drawable);
    }

    @Override
    public void show() {
        // 화면이 보여질 때 필요한 초기화 작업
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();

        handleInput(delta);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    private void handleInput(float delta) {
        float moveAmount = playerSpeed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerImage.moveBy(-moveAmount, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerImage.moveBy(moveAmount, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            playerImage.moveBy(0, moveAmount);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            playerImage.moveBy(0, -moveAmount);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        camera.setToOrtho(false, width, height);
        camera.position.set(map.getProperties().get("width", Integer.class) * 16 * 1.5f / 2,
            map.getProperties().get("height", Integer.class) * 16 * 1.5f / 2, 0);
        camera.update();
    }

    @Override
    public void pause() {
        // 게임이 일시정지될 때 호출됨
    }

    @Override
    public void resume() {
        // 게임이 재개될 때 호출됨
    }

    @Override
    public void hide() {
        // 화면이 숨겨질 때 호출됨
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        map.dispose();
        mapRenderer.dispose();
    }
}
