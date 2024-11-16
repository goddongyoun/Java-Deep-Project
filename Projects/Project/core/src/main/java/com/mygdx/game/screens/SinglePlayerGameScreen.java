package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Main;
import com.mygdx.game.Player;
import com.mygdx.game.screens.MainMenuScreen;
import com.mygdx.game.util.FontManager;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SinglePlayerGameScreen implements Screen {
    private Main game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private Player player;

    // 맵 스케일 설정
    private static final float MAP_SCALE = 2.0f;
    // 플레이어 크기 스케일
    private static final float PLAYER_SCALE = 1.2f;

    // 카메라 관련 상수
    private static final float MIN_ZOOM = 0.5f;
    private static final float MAX_ZOOM = 2f;
    private static final float ZOOM_SPEED = 0.1f;

    // 플레이어 크기 관련 변수
    private float playerWidth;
    private float playerHeight;

    // 카메라 관련 변수
    private float currentZoom = 1f;
    private Vector3 cameraPosition = new Vector3();
    private Vector2 tempVec = new Vector2();

    // 충돌 및 상호작용 관련
    private Array<TiledMapTileLayer> collisionLayers;
    private Map<String, TiledMapTileLayer> objOffLayers;
    private Map<String, TiledMapTileLayer> objOnLayers;
    private Map<String, Boolean> objActiveStates;
    private String currentInteractiveObj;
    private Stage stage;
    private Skin skin;

    public SinglePlayerGameScreen(Main game) {
        this.game = game;

        // 카메라 초기화
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT, camera);

        this.stage = new Stage(viewport);
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // 맵 로드
        loadMap();

        // 플레이어 크기 초기화
        initializePlayerSize();

        // 플레이어 초기화 (싱글플레이어용)
        createSinglePlayer();
        resetPlayerPosition();

        findLayers();

        // ESC 키로 메인 메뉴로 돌아가기 위한 입력 프로세서 설정
        Gdx.input.setInputProcessor(stage);
    }

    private void initializePlayerSize() {
        playerWidth = 32 * MAP_SCALE * PLAYER_SCALE;
        playerHeight = playerWidth;
    }

    private void createSinglePlayer() {
        player = new Player("훈련중", 0, 0, playerWidth);
    }

    private void loadMap() {
        try {
            map = new TmxMapLoader().load("maps/game_map.tmx");
            renderer = new OrthogonalTiledMapRenderer(map, MAP_SCALE);

            int mapWidth = map.getProperties().get("width", Integer.class);
            int mapHeight = map.getProperties().get("height", Integer.class);
            int tileWidth = map.getProperties().get("tilewidth", Integer.class);
            int tileHeight = map.getProperties().get("tileheight", Integer.class);

            float actualWidth = mapWidth * tileWidth * MAP_SCALE;
            float actualHeight = mapHeight * tileHeight * MAP_SCALE;

            Gdx.app.log("SinglePlayerGameScreen", String.format(
                "Map loaded - Size: %dx%d tiles, Actual size: %.0fx%.0f pixels, Scale: %.1f",
                mapWidth, mapHeight, actualWidth, actualHeight, MAP_SCALE
            ));
        } catch (Exception e) {
            Gdx.app.error("SinglePlayerGameScreen", "Error loading map", e);
        }
    }

    private void findLayers() {
        collisionLayers = new Array<>();
        objOffLayers = new HashMap<>();
        objOnLayers = new HashMap<>();
        objActiveStates = new HashMap<>();

        MapLayers layers = map.getLayers();
        Pattern blockPattern = Pattern.compile("block\\d*");
        Pattern objOffPattern = Pattern.compile("obj(\\d+)-off");
        Pattern objOnPattern = Pattern.compile("obj(\\d+)-on");

        for (MapLayer layer : layers) {
            String layerName = layer.getName().toLowerCase();

            if (blockPattern.matcher(layerName).matches()) {
                if (layer instanceof TiledMapTileLayer) {
                    collisionLayers.add((TiledMapTileLayer) layer);
                    Gdx.app.log("SinglePlayerGameScreen", "Found collision layer: " + layerName);
                }
            }

            if (objOffPattern.matcher(layerName).matches()) {
                if (layer instanceof TiledMapTileLayer) {
                    String id = layerName.split("-")[0];
                    objOffLayers.put(id, (TiledMapTileLayer) layer);
                    Gdx.app.log("SinglePlayerGameScreen", "Found obj-off layer: " + layerName);
                }
            }

            if (objOnPattern.matcher(layerName).matches()) {
                if (layer instanceof TiledMapTileLayer) {
                    String id = layerName.split("-")[0];
                    objOnLayers.put(id, (TiledMapTileLayer) layer);
                    objActiveStates.put(id, false);
                    ((TiledMapTileLayer) layer).setVisible(false);
                    Gdx.app.log("SinglePlayerGameScreen", "Found obj-on layer: " + layerName);
                }
            }
        }
    }
    private void resetPlayerPosition() {
        if (map != null) {
            int mapWidth = map.getProperties().get("width", Integer.class);
            int mapHeight = map.getProperties().get("height", Integer.class);
            int tileWidth = map.getProperties().get("tilewidth", Integer.class);
            int tileHeight = map.getProperties().get("tileheight", Integer.class);

            float centerX = (mapWidth * tileWidth * MAP_SCALE) / 2f;
            float centerY = (mapHeight * tileHeight * MAP_SCALE) / 2f;

            player.setPosition(new Vector2(
                centerX - playerWidth / 2,
                centerY - playerHeight / 2
            ));
            player.size = playerWidth;
        }
    }

    private boolean isColliding(float x, float y) {
        int tileX = (int) (x / (map.getProperties().get("tilewidth", Integer.class) * MAP_SCALE));
        int tileY = (int) (y / (map.getProperties().get("tileheight", Integer.class) * MAP_SCALE));

        for (TiledMapTileLayer layer : collisionLayers) {
            Cell cell = layer.getCell(tileX, tileY);
            if (cell != null && cell.getTile() != null) {
                return true;
            }
        }
        return false;
    }

    private void checkObjectInteractions() {
        Vector2 playerPos = player.getPosition();
        float playerCenterX = playerPos.x + playerWidth / 2;
        float playerCenterY = playerPos.y + playerHeight / 2;

        int tileX = (int) (playerCenterX / (map.getProperties().get("tilewidth", Integer.class) * MAP_SCALE));
        int tileY = (int) (playerCenterY / (map.getProperties().get("tileheight", Integer.class) * MAP_SCALE));

        boolean foundInteraction = false;

        for (Map.Entry<String, TiledMapTileLayer> entry : objOffLayers.entrySet()) {
            String objId = entry.getKey();
            TiledMapTileLayer offLayer = entry.getValue();

            Cell cell = offLayer.getCell(tileX, tileY);
            if (cell != null && cell.getTile() != null) {
                if (!objActiveStates.get(objId)) {
                    objOnLayers.get(objId).setVisible(true);
                    objActiveStates.put(objId, true);
                }
                currentInteractiveObj = objId;
                foundInteraction = true;
            }
        }

        if (!foundInteraction && currentInteractiveObj != null) {
            if (objActiveStates.get(currentInteractiveObj)) {
                objOnLayers.get(currentInteractiveObj).setVisible(false);
                objActiveStates.put(currentInteractiveObj, false);
            }
            currentInteractiveObj = null;
        }

        if (currentInteractiveObj != null &&
            objActiveStates.get(currentInteractiveObj) &&
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            showInteractionDialog(currentInteractiveObj);
        }

        // ESC 키로 메인 메뉴로 돌아가기
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void handlePlayerMovement(float delta) {
        Vector2 oldPosition = player.getPosition().cpy();
        player.update(delta);

        Vector2 newPosition = player.getPosition();
        if (isColliding(newPosition.x, newPosition.y) ||
            isColliding(newPosition.x + playerWidth, newPosition.y) ||
            isColliding(newPosition.x, newPosition.y + playerHeight) ||
            isColliding(newPosition.x + playerWidth, newPosition.y + playerHeight)) {
            player.setPosition(oldPosition);
        }
    }

    private void showInteractionDialog(String objId) {
        Dialog dialog = new Dialog("상호작용", skin) {
            @Override
            protected void result(Object object) {
                hide();
            }
        };

        Label.LabelStyle labelStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
        labelStyle.font = FontManager.getInstance().getFont(20);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        buttonStyle.font = FontManager.getInstance().getFont(18);

        dialog.text(new Label("오브젝트 " + objId + "와 상호작용했습니다.", labelStyle));
        dialog.button(new TextButton("확인", buttonStyle));
        dialog.show(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handlePlayerMovement(delta);
        checkObjectInteractions();
        updateCamera(delta);

        if (renderer != null) {
            renderer.setView(camera);
            renderer.render();

            renderer.getBatch().begin();
            player.render(renderer.getBatch());
            renderer.getBatch().end();
        }

        stage.act(delta);
        stage.draw();
    }

    private void updateCamera(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            currentZoom = Math.max(MIN_ZOOM, currentZoom - ZOOM_SPEED * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            currentZoom = Math.min(MAX_ZOOM, currentZoom + ZOOM_SPEED * delta);
        }

        camera.zoom = currentZoom;

        Vector2 playerPos = player.getPosition();
        float playerCenterX = playerPos.x + playerWidth / 2;
        float playerCenterY = playerPos.y + playerHeight / 2;

        cameraPosition.x = playerCenterX;
        cameraPosition.y = playerCenterY;

        limitCameraToMapBounds();

        camera.position.set(cameraPosition);
        camera.update();
    }

    private void limitCameraToMapBounds() {
        if (map == null) return;

        float mapWidth = map.getProperties().get("width", Integer.class)
            * map.getProperties().get("tilewidth", Integer.class)
            * MAP_SCALE;
        float mapHeight = map.getProperties().get("height", Integer.class)
            * map.getProperties().get("tileheight", Integer.class)
            * MAP_SCALE;

        float viewportHalfWidth = (camera.viewportWidth * camera.zoom) / 2f;
        float viewportHalfHeight = (camera.viewportHeight * camera.zoom) / 2f;

        cameraPosition.x = Math.max(viewportHalfWidth,
            Math.min(mapWidth - viewportHalfWidth, cameraPosition.x));
        cameraPosition.y = Math.max(viewportHalfHeight,
            Math.min(mapHeight - viewportHalfHeight, cameraPosition.y));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }

    @Override
    public void dispose() {
        if (renderer != null) renderer.dispose();
        if (map != null) map.dispose();
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
