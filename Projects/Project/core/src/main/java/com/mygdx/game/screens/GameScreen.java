package com.mygdx.game.screens;

import com.ImportedPackage._Imported_ClientBase;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Main;
import com.mygdx.game.Player;
import com.mygdx.game.PlayerOfMulti;
import com.mygdx.game.Room;
import com.mygdx.game.ui.MissionDialog;
import com.mygdx.game.ui.MissionDialogm;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class GameScreen implements Screen {
    private Main game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private Player player;
    private Room currentRoom;
    private Stage uiStage;
    private Skin skin;

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
    private Map<String, Boolean> missionCompletionStatus;  // 미션 완료 상태를 추적

    private Dialog currentDialog; // 현재 표시 중인 다이얼로그

    public GameScreen(Main game) {
        this.game = game;
        this.currentRoom = game.getCurrentRoom();

        // 카메라 초기화
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT, camera);

        // UI Stage 및 Skin 초기화
        this.uiStage = new Stage(viewport);
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // 맵 로드
        loadMap();

        // 플레이어 크기 초기화
        initializePlayerSize();

        // 플레이어 초기화
        player = currentRoom.getme();
        resetPlayerPosition();

        // 다른 플레이어들 크기 조정
        for (int i = 0; i < currentRoom.pCount; i++) {
            if (currentRoom.m_players[i] != null) {
                currentRoom.m_players[i].setSize(playerWidth);
            }
        }

        findLayers();
        missionCompletionStatus = new HashMap<>();

        // 입력 처리를 위한 InputMultiplexer 설정
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void initializePlayerSize() {
        playerWidth = 32 * MAP_SCALE * PLAYER_SCALE;
        playerHeight = playerWidth;
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

            Gdx.app.log("GameScreen", String.format(
                "Map loaded - Size: %dx%d tiles, Actual size: %.0fx%.0f pixels, Scale: %.1f",
                mapWidth, mapHeight, actualWidth, actualHeight, MAP_SCALE
            ));

        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error loading map", e);
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
                    Gdx.app.log("GameScreen", "Found collision layer: " + layerName);
                }
            }

            if (objOffPattern.matcher(layerName).matches()) {
                if (layer instanceof TiledMapTileLayer) {
                    String id = layerName.split("-")[0];
                    objOffLayers.put(id, (TiledMapTileLayer) layer);
                    Gdx.app.log("GameScreen", "Found obj-off layer: " + layerName);
                }
            }

            if (objOnPattern.matcher(layerName).matches()) {
                if (layer instanceof TiledMapTileLayer) {
                    String id = layerName.split("-")[0];
                    objOnLayers.put(id, (TiledMapTileLayer) layer);
                    objActiveStates.put(id, false);
                    ((TiledMapTileLayer) layer).setVisible(false);
                    Gdx.app.log("GameScreen", "Found obj-on layer: " + layerName);
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

        // 스페이스바로 상호작용 및 미니게임 실행
        if (currentInteractiveObj != null &&
            objActiveStates.get(currentInteractiveObj) &&
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            showInteractionDialog(currentInteractiveObj);
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
        if (objId.matches("obj\\d+")) {
            // 미션이 이미 완료되었는지 확인
            if (!missionCompletionStatus.getOrDefault(objId, false)) {
                MissionDialogm missionDialog = new MissionDialogm("미션", skin, uiStage);
                missionDialog.showMission(uiStage);

                // 미션 완료 시 서버에 알림
                missionDialog.setMissionCompleteCallback(() -> {
                    missionCompletionStatus.put(objId, true);
                    // TODO: 서버에 미션 완료 상태 전송
                    // _Imported_ClientBase.sendMissionComplete(objId);
                    checkAllMissionsComplete();
                });
            }
        }
    }

    private void checkAllMissionsComplete() {
        boolean allComplete = true;
        for (Map.Entry<String, Boolean> entry : missionCompletionStatus.entrySet()) {
            if (!entry.getValue()) {
                allComplete = false;
                break;
            }
        }

        if (allComplete) {
            // TODO: 게임 클리어 처리
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handlePlayerMovement(delta);
        updateMultiplayerPositions(delta);
        checkObjectInteractions();
        updateCamera(delta);

        if (renderer != null) {
            renderer.setView(camera);
            renderer.render();

            renderer.getBatch().begin();
            // 다른 플레이어들 렌더링
            for (int i = 0; i < currentRoom.pCount; i++) {
                if (currentRoom.m_players[i] != null) {
                    currentRoom.m_players[i].render(renderer.getBatch());
                }
            }
            player.render(renderer.getBatch());
            renderer.getBatch().end();
        }

        // UI 렌더링
        uiStage.act(delta);
        uiStage.draw();
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

    private void updateMultiplayerPositions(float delta) {
        Vector2 playerPos = player.getPosition();
        float playerCenterX = playerPos.x + playerWidth / 2;
        float playerCenterY = playerPos.y + playerHeight / 2;

        _Imported_ClientBase.updateLoc(
            Math.round(playerCenterX),
            Math.round(playerCenterY)
        );

        _Imported_ClientBase.getLocation();
        currentRoom.pCount = _Imported_ClientBase.playerCount - 1;

        int temp = 0;
        for (int i = 0; i < 5; i++) {
            if (_Imported_ClientBase.players[i] != null) {
                if (!(_Imported_ClientBase.players[i].name.equals(currentRoom.getme().getNickname()))) {
                    if (currentRoom.m_players[temp] == null) {
                        currentRoom.m_players[temp] = new PlayerOfMulti(
                            _Imported_ClientBase.players[i].name,
                            _Imported_ClientBase.players[i].x - playerWidth / 2,
                            _Imported_ClientBase.players[i].y - playerHeight / 2,
                            playerWidth
                        );
                    }
                    currentRoom.m_players[temp].update(
                        _Imported_ClientBase.players[i].x - playerWidth / 2,
                        _Imported_ClientBase.players[i].y - playerHeight / 2,
                        delta
                    );
                    temp++;
                }
            }
        }
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
        if (uiStage != null) uiStage.dispose();
        if (skin != null) skin.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
