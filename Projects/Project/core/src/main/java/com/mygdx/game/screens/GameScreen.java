package com.mygdx.game.screens;

import com.ImportedPackage._Imported_ClientBase;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Main;
import com.mygdx.game.Player;
import com.mygdx.game.PlayerOfMulti;
import com.mygdx.game.PlayerOfMulti.PlayerState;
import com.mygdx.game.Room;
import com.mygdx.game.ui.*;

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
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    private GameUI gameUI;

    // 맵 스케일 설정
    private static final float MAP_SCALE = 2.0f;
    // 플레이어 크기 스케일
    private static final float PLAYER_SCALE = 1.2f;

    // 카메라 관련 상수
    private static final float MIN_ZOOM = 0.5f;
    private static final float MAX_ZOOM = 2f;
    private static final float ZOOM_SPEED = 0.1f;

    private float gameStartTimer = 0;
    private static final float BOSS_ACTIVATION_TIME = 20f;
    private boolean bossActivated = false;
    private boolean isBossPlayer = false;
    private float bossSkillCooldown = 5f;
    private float bossSkillDuration = 1f;

    // 플레이어 크기 관련 변수
    private float playerWidth;
    private float playerHeight;

    // 카메라 관련 변수
    private float currentZoom = 1f;
    private Vector3 cameraPosition = new Vector3();

    // 충돌 및 상호작용 관련
    private Array<TiledMapTileLayer> collisionLayers;
    private Map<String, TiledMapTileLayer> objOffLayers;
    private Map<String, TiledMapTileLayer> objOnLayers;
    private Map<String, Boolean> objActiveStates;
    private String currentInteractiveObj;
    private String objId;
    private Map<String, Boolean> missionCompletionStatus;  // 미션 완료 상태를 추적
    private Dialog currentDialog;

    // 보스 스킬 관련 상수
    private static final float BOSS_SKILL_RANGE = 100f; // 스킬 기본 크기 조정
    private static final float BOSS_SKILL_DURATION = 0.5f; // 스킬 지속 시간
    private static final float BOSS_SKILL_COOLDOWN = 3f; // 스킬 쿨다운

    // 보스 스킬 관련 필드
    private float currentBossSkillTime = 0;
    private float lastBossSkillTime = 0;
    private boolean isUsingBossSkill = false;
    private boolean showSkillHitbox = false;
    private Rectangle skillHitbox;
    private TextureAtlas bossSkillAtlas;

    // 보스 공격 애니메이션
    private Animation<TextureRegion> bossAttackLeftAnimation;
    private Animation<TextureRegion> bossAttackRightAnimation;

    //미니게임 관련
    public MissionDialog missionDialog;
    public MissionDialog2 missionDialog2;
    public MissionDialog3 missionDialog3;
    public MissionDialog4 missionDialog4;
    public MissionDialog5 missionDialog5;
    private boolean isMissionActivated=false;
    private boolean allComplete=false;

    //탈출 관련
    private boolean isEscape=false;

    private Pattern doorClosePattern;

    public static boolean everybodyEnd = false;

    public int totalPlayer;
    public int deadPlayer;

    InputMultiplexer multiplexer;

    public GameScreen(Main game) {
    	_Imported_ClientBase.endRoll();
    	endShoted = false;
        everybodyEnd = false;
        allComplete = false;
        this.game = game;
        this.currentRoom = game.getCurrentRoom();

        // 스킬 히트박스 초기화
        skillHitbox = new Rectangle(0, 0, BOSS_SKILL_RANGE, BOSS_SKILL_RANGE);

        // 스킬 아틀라스 로드
        bossSkillAtlas = new TextureAtlas(Gdx.files.internal("boss/BossSkill.atlas"));

        // 보스 공격 애니메이션 로드
        TextureAtlas bossAttackAtlas = new TextureAtlas(Gdx.files.internal("boss/BossAttack.atlas"));

        // 왼쪽 공격 애니메이션 생성
        Array<TextureRegion> attackLeftFrames = new Array<>();
        for (int i = 1; i <= 6; i++) {
            TextureRegion region = bossAttackAtlas.findRegion("BossAttackL" + i);
            if (region != null) {
                attackLeftFrames.add(region);
            }
        }
        bossAttackLeftAnimation = new Animation<>(0.1f, attackLeftFrames, Animation.PlayMode.NORMAL);

        // 오른쪽 공격 애니메이션 생성
        Array<TextureRegion> attackRightFrames = new Array<>();
        for (int i = 1; i <= 6; i++) {
            TextureRegion region = bossAttackAtlas.findRegion("BossAttackR" + i);
            if (region != null) {
                attackRightFrames.add(region);
            }
        }
        bossAttackRightAnimation = new Animation<>(0.1f, attackRightFrames, Animation.PlayMode.NORMAL);

        if (this.currentRoom == null) {
            Gdx.app.error("GameScreen", "Current room is null!");
            return;
        }

        player = currentRoom.getme();
        if (player == null) {
            Gdx.app.error("GameScreen", "Player is null!");
            return;
        }

        // 카메라 초기화
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT, camera);

        // UI Stage 및 Skin 초기화
        this.batch = new SpriteBatch();
        this.stage = new Stage(viewport, this.batch);
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        //미션 초기화
        missionDialog = new MissionDialog("",skin, stage);
        missionDialog2 = new MissionDialog2("",skin, stage);
        missionDialog3 = new MissionDialog3("",skin, stage);
        missionDialog4 = new MissionDialog4("",skin, stage);
        missionDialog5 = new MissionDialog5("",skin, stage);

        // 맵 로드
        loadMap();

        // 플레이어 크기 초기화
        initializePlayerSize();

        // 플레이어 초기화
        player.setInGame(true);

        if (currentRoom.pCount > 0) {
            for (int i = 0; i < currentRoom.pCount; i++) {
                if (currentRoom.m_players[i] != null) {
                    currentRoom.m_players[i].setSize(playerWidth);
                }
            }
        }

        skillHitbox = new Rectangle(0, 0, BOSS_SKILL_RANGE * 2, BOSS_SKILL_RANGE * 2);
        bossSkillAtlas = new TextureAtlas(Gdx.files.internal("boss/BossSkill.atlas"));

        findLayers();
        missionCompletionStatus = new HashMap<>();
        this.gameUI = new GameUI(batch, player);

        // 입력 처리를 위한 InputMultiplexer 설정
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer); // -> show 메소드 = 사용자가 최종적으로 화면에 보여질 때 실행됨, 즉 맨 마지막에 이 코드를 실행시켜야 모든 이벤트 리스너가 작동함
    }

    private void initializePlayerSize() {
        if (player == null) {
            Gdx.app.error("GameScreen", "Cannot initialize player size - player is null");
            return;
        }
        resetPlayerPosition();
        playerWidth = 32 * MAP_SCALE * PLAYER_SCALE;
        playerHeight = playerWidth;
        player.size = playerWidth;
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
        Pattern objPattern = Pattern.compile("obj(\\d+)");
        doorClosePattern = Pattern.compile("door-close\\d*");

        for (MapLayer layer : layers) {
            String layerName = layer.getName().toLowerCase();

            if (blockPattern.matcher(layerName).matches()
                ||objPattern.matcher(layerName).matches()
                ||doorClosePattern.matcher(layerName).matches()) {
                if (layer instanceof TiledMapTileLayer) {
                    collisionLayers.add((TiledMapTileLayer) layer);
                    Gdx.app.log("GameScreen", "Found collision layer: " + layerName);
                }
            }

            if (objOffPattern.matcher(layerName).matches()) {
                if (layer instanceof TiledMapTileLayer) {
                    String id = layerName.split("-")[0];
                    objOffLayers.put(id, (TiledMapTileLayer) layer);
                    objOffLayers.get(id).setVisible(false);
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

            System.out.println(objOffLayers);
        }
    }

    private void updateGameState(float delta) {
        if (!bossActivated) {
            //gameStartTimer += delta;
            if (gameStartTimer >= BOSS_ACTIVATION_TIME) {
            	System.out.println("Boss came " + gameStartTimer);
                bossActivated = true;
                String bossName = _Imported_ClientBase.getBossName();
                isBossPlayer = bossName != null && bossName.equals(player.getNickname());

                if (isBossPlayer) {
                    player.transformToBoss();
                }
                else {
                    for(int i = 0; i<currentRoom.pCount; i++) {
                        if(currentRoom.m_players[i] != null) {
                            if(currentRoom.m_players[i].getNickname().equals(bossName)) {
                                currentRoom.m_players[i].isBoss = true;
                                currentRoom.m_players[i].transformToBoss();
                            }
                        }
                    }
                }
            }
        }

        if (bossActivated && isBossPlayer) {
            updateBossSkills(delta);
        }
        else if(bossActivated) {
        	updateBossSkillsM(delta);
        }

        gameUI.update(delta);
        gameUI.setBossActivated(bossActivated);
        gameUI.setIsBossPlayer(isBossPlayer);
        if (isBossPlayer) {
            gameUI.setLastBossSkillTime(lastBossSkillTime);
        }
    }

    private void updateBossSkills(float delta) {
        if (isUsingBossSkill) {
            currentBossSkillTime += delta;

            if (showSkillHitbox) {
                for (int i = 0; i < currentRoom.pCount; i++) {
                    PlayerOfMulti target = currentRoom.m_players[i];
                    if (target != null && !target.isPetrified() && checkSkillCollision(target)) {
                        System.out.println("hit");
                        deadPlayer++; //죽은 플레이어 수 카운트
                        currentRoom.m_players[i].setPetrified(true);
                        _Imported_ClientBase.setIsDead(target.getNickname());
                    }
                }
            }

            if (currentBossSkillTime >= bossSkillDuration) {
                endBossSkill();
            }
        }

        if (!isUsingBossSkill && Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            float timeSinceLastUse = gameStartTimer - lastBossSkillTime;
            if (timeSinceLastUse >= BOSS_SKILL_COOLDOWN) {
                // isBossPlayer 여부와 관계없이 startRoll 호출, 서버에서 보스 여부 확인
                _Imported_ClientBase.startRoll(player.isFacingLeft());

                if (isBossPlayer) {
                    useBossSkill();
                }
            }
        }
    }

    private void updateBossSkillsM(float delta) {
    	if (isUsingBossSkill) {
            currentBossSkillTime += delta;

            if (currentBossSkillTime >= bossSkillDuration) {
                endBossSkill();
            }
    	}
    }

    private void updateSkillHitbox() {
        if (showSkillHitbox) {
            Vector2 playerPos = player.getPosition();
            skillHitbox.setCenter(
                playerPos.x + player.size/2,
                playerPos.y + player.size/2
            );

            for (int i = 0; i < currentRoom.pCount; i++) {
                PlayerOfMulti target = currentRoom.m_players[i];
                if (target != null && !target.isPetrified() && checkSkillCollision(target)) {
                    _Imported_ClientBase.setIsDead(target.getNickname());
                }
            }
        }
    }

    private boolean checkSkillCollision(PlayerOfMulti target) {
        Rectangle targetBounds = new Rectangle(
            target.getPosition().x,
            target.getPosition().y,
            target.getSize(),
            target.getSize()
        );
        return skillHitbox.overlaps(targetBounds);
    }

    private void useBossSkill() {
        isUsingBossSkill = true;
        currentBossSkillTime = 0;
        lastBossSkillTime = gameStartTimer;
        showSkillHitbox = true;

        // 보스 공격 상태로 전환하고 stateTime 초기화
        player.currentState = Player.PlayerState.BOSS_ATTACKING;
        player.resetStateTime();

        Gdx.app.log("GameScreen", "Boss skill activated, state: " + player.getCurrentState());
    }

    private void useBossSkillM(int pnum) {
        isUsingBossSkill = true;
        currentBossSkillTime = 0;
        lastBossSkillTime = gameStartTimer;
        showSkillHitbox = true;

        // 보스 공격 상태로 전환하고 stateTime 초기화
        currentRoom.m_players[pnum].currentState = PlayerOfMulti.PlayerState.BOSS_ATTACKING;
        currentRoom.m_players[pnum].resetStateTime();

        Gdx.app.log("GameScreen", "Boss skill activated, state: " + currentRoom.m_players[pnum].currentState);
    }

    private void endBossSkill() {
        isUsingBossSkill = false;
        showSkillHitbox = false;
        // 다시 IDLE 상태로 돌아가기
        player.currentState = Player.PlayerState.BOSS_IDLE;
        _Imported_ClientBase.endRoll();
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

    private boolean isCollidingWithShadow(float x, float y) {
        int tileX = (int) (x / (map.getProperties().get("tilewidth", Integer.class) * MAP_SCALE));
        int tileY = (int) (y / (map.getProperties().get("tileheight", Integer.class) * MAP_SCALE));

        TiledMapTileLayer shadowLayer = (TiledMapTileLayer) map.getLayers().get("door_shadow");
        if (shadowLayer == null) {
            return false;
        }

        TiledMapTileLayer.Cell cell = shadowLayer.getCell(tileX, tileY);
        return cell != null && cell.getTile() != null;
    }

    private void checkObjectInteractions() {
        Vector2 playerPos = player.getPosition();
        float playerCenterX = playerPos.x + playerWidth / 2;
        float playerCenterY = playerPos.y;

        int tileX = (int) (playerCenterX / (map.getProperties().get("tilewidth", Integer.class) * MAP_SCALE));
        int tileY = (int) (playerCenterY / (map.getProperties().get("tileheight", Integer.class) * MAP_SCALE));

        boolean foundInteraction = false;

        for (Map.Entry<String, TiledMapTileLayer> entry : objOffLayers.entrySet()) {
            objId = entry.getKey();
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

        //미션 클리어하면 objonlayer가 보이던 것을 안보이게 설정함.
        for (Map.Entry<String, Boolean> entry : missionCompletionStatus.entrySet()) {
            String checkObjId = entry.getKey();
            boolean isMissionComplete = entry.getValue();

            if (isMissionComplete) {
                objOnLayers.get(checkObjId).setVisible(false);
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
            !isMissionActivated &&
            Gdx.input.isKeyJustPressed(Input.Keys.Z) &&
            player.isBoss() == false && player.isPetrified() == false) {
            	showInteractionDialog(currentInteractiveObj);
        }
    }

    private void handlePlayerMovement(float delta) {
        Vector2 oldPosition = player.getPosition().cpy();
        player.update(delta);

        Vector2 newPosition = player.getPosition();
        float tempPlayerWidth = playerWidth / 3;
        float tempPlayerHeight = playerHeight / 3;

        // 캐릭터 충돌 경계선 설정
        float leftBorder = newPosition.x + tempPlayerWidth;
        float rightBorder = newPosition.x + tempPlayerWidth * 2;
        float topBorder = newPosition.y + tempPlayerHeight;
        float bottomBorder = newPosition.y;

        boolean collidedLeft = isColliding(leftBorder, topBorder) || isColliding(leftBorder, bottomBorder);
        boolean collidedRight = isColliding(rightBorder, topBorder) || isColliding(rightBorder, bottomBorder);
        boolean collidedTop = isColliding(leftBorder, topBorder) || isColliding(rightBorder, topBorder);
        boolean collidedBottom = isColliding(leftBorder, bottomBorder) || isColliding(rightBorder, bottomBorder);

        if (collidedLeft || collidedRight) {
            player.setPosition(oldPosition.x, newPosition.y);
        }
        if (collidedTop || collidedBottom) {
            player.setPosition(newPosition.x, oldPosition.y);
        }
    }

    private void showInteractionDialog(String objId) {
        if (objId.matches("obj\\d+")) {
            for (Map.Entry<String, Boolean> entry : missionCompletionStatus.entrySet()) {
                Gdx.app.log("Mission Load", entry.getKey() + " status: " + entry.getValue());
            }
            System.out.println(missionCompletionStatus.getOrDefault(objId, false));
            // 미션이 이미 완료되었는지 확인
            if (!missionCompletionStatus.getOrDefault(objId, false)) {
                player.setCanMove(false);
                switch (objId){
                    case "obj1":
                        missionDialog = new MissionDialog("",skin, stage);
                        missionDialog.showMission(stage);

                        // 미션 완료 시 서버에 알림
                        missionDialog.setMissionCompleteCallback(() -> {
                            missionCompletionStatus.put(objId, true);
                            _Imported_ClientBase.setMission(0,true);
                        });
                        break;
                    case "obj2":
                        //객체를 다시 초기화하지 않음으로써 기존에 진행하던 과정 보존
//                        missionDialog2 = new MissionDialog2("미션2",skin,uiStage);
                        missionDialog2.showMission(stage);

                        // 미션 완료 시 서버에 알림
                        missionDialog2.setMissionCompleteCallback(() -> {
                            missionCompletionStatus.put(objId, true);
                            _Imported_ClientBase.setMission(1,true);
                        });
                        break;
                    case "obj3":
                        missionDialog3 = new MissionDialog3("미션3",skin, stage);
                        missionDialog3.showMission(stage);

                        // 미션 완료 시 서버에 알림
                        missionDialog3.setMissionCompleteCallback(() -> {
                            missionCompletionStatus.put(objId, true);
                            _Imported_ClientBase.setMission(2,true);
                        });
                        break;
                    case "obj4":
                        missionDialog4 = new MissionDialog4("미션4",skin, stage);
                        missionDialog4.showMission(stage);

                        // 미션 완료 시 서버에 알림
                        missionDialog4.setMissionCompleteCallback(() -> {
                            missionCompletionStatus.put(objId, true);
                            _Imported_ClientBase.setMission(3,true);
                        });
                        break;
                    case "obj5":
                        missionDialog5 = new MissionDialog5("미션5",skin, stage);
                        missionDialog5.showMission(stage);

                        // 미션 완료 시 서버에 알림
                        missionDialog5.setMissionCompleteCallback(() -> {
                            missionCompletionStatus.put(objId, true);
                            _Imported_ClientBase.setMission(4,true);
                        });
                        break;
                }
            }
        }
    }

    // 서버에서 미션 완료 상태를 받아와 missionCompletionStatus에 저장하는 메서드
    private void loadMissionCompletionStatusFromServer() {
        try {
            // 서버의 missionState 배열 값을 missionCompletionStatus에 반영
            for (int i = 0; i < _Imported_ClientBase.missionState.length; i++) {
                missionCompletionStatus.put("obj" + (i + 1), _Imported_ClientBase.missionState[i]);
            }

        } catch (Exception e) {
            Gdx.app.log("Mission Load", "Error loading mission completion status from server", e);
        }
    }

    private void setMissionState(){
        if (currentInteractiveObj != null) {
//            System.out.println(missionCompletionStatus.getOrDefault(currentInteractiveObj, false));
            switch (currentInteractiveObj) {
                case "obj1":
                    if (missionDialog != null) {
                        isMissionActivated = missionDialog.isShowingMission();
                    }
                    break;
                case "obj2":
                    if (missionDialog2 != null) {
                        isMissionActivated = missionDialog2.isShowingMission2();
                    }
                    break;
                case "obj3":
                    if (missionDialog3 != null) {
                        isMissionActivated = missionDialog3.isShowingMission3();
                    }
                    break;
                case "obj4":
                    if (missionDialog4 != null) {
                        isMissionActivated = missionDialog4.isShowingMission4();
                    }
                    break;
                case "obj5":
                    if (missionDialog5 != null) {
                        isMissionActivated = missionDialog5.isShowingMission5();
                    }
                    break;
            }
        }
    }

    private void setMissionPosition(){
        if (isMissionActivated) {
            missionDialog.setPosition(
                camera.position.x - (missionDialog.getWidth() / 2),
                camera.position.y - (missionDialog.getHeight() / 2)
            );
            missionDialog2.setPosition(
                camera.position.x - (missionDialog2.getWidth() / 2),
                camera.position.y - (missionDialog2.getHeight() / 2)
            );
            missionDialog3.setPosition(
                camera.position.x - (missionDialog3.getWidth() / 2),
                camera.position.y - (missionDialog3.getHeight() / 2)
            );
            missionDialog4.setPosition(
                camera.position.x - (missionDialog4.getWidth() / 2),
                camera.position.y - (missionDialog4.getHeight() / 2)
            );
            missionDialog5.setPosition(
                camera.position.x - (missionDialog5.getWidth() / 2),
                camera.position.y - (missionDialog5.getHeight() / 2)
            );
        }else{
            player.setCanMove(true);
        }
    }

    private void checkAllMissionsComplete() {
        allComplete = true;
        for (Map.Entry<String, Boolean> entry : missionCompletionStatus.entrySet()) {
            if (!entry.getValue()) {
                allComplete = false;
                break;
            }
        }

        if (allComplete) {
            //System.out.println("all mission clear");
            // TODO: 게임 클리어 처리
        }else{
            //System.out.println("some missions are left");
        }
    }

    private void deleteDoorClose() {
        // door-close, door-close1, ... door-close15 레이어들을 모두 제거
        Array<String> layerNamesToRemove = new Array<>();

        if (layerNamesToRemove.size < 16) {
            // 먼저 삭제할 레이어의 이름을 수집합니다.
            for (MapLayer layer : map.getLayers()) {
                if (layer.getName().startsWith("door-close")) {
                    layerNamesToRemove.add(layer.getName());
                }
            }
        }

        // 레이어의 이름을 수집한 후, 순차적으로 타이머로 제거합니다.
        removeDoorCloseLayerSequentially(layerNamesToRemove, layerNamesToRemove.size-1);
    }

    private void removeDoorCloseLayerSequentially(Array<String> layerNamesToRemove, int index) {
        // 모든 레이어가 제거되었으면 종료
        if (index < 0) {
            return;
        }

        // 0.5초 후에 현재 레이어를 제거하고, 다음 레이어 제거를 예약합니다.
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                // 현재 레이어 이름 가져오기
                String layerName = layerNamesToRemove.get(index);

                // 레이어 제거
                MapLayer layer = map.getLayers().get(layerName);
                if (layer != null) {
                    map.getLayers().remove(layer);
                }

                // collisionLayers에서도 제거
                for (int i = 0; i < collisionLayers.size; i++) {
                    TiledMapTileLayer collisionLayer = collisionLayers.get(i);
                    if (layerName.equals(collisionLayer.getName())) {
                        collisionLayers.removeIndex(i);
                        break; // 더 이상의 반복은 필요하지 않으므로 중단
                    }
                }

                // 다음 레이어를 제거하도록 재귀 호출 (0.5초 후)
                removeDoorCloseLayerSequentially(layerNamesToRemove, index - 1);
            }
        }, 0.1f); // 0.5초 간격으로 실행
    }


    private void ifColliedShadow() {
        Vector2 oldPosition = player.getPosition().cpy();

        Vector2 newPosition = player.getPosition();
        float tempPlayerWidth = playerWidth / 3;
        float tempPlayerHeight = playerHeight/2 + playerHeight;

        // 캐릭터 충돌 경계선 설정
        float leftBorder = newPosition.x + tempPlayerWidth;
        float rightBorder = newPosition.x + tempPlayerWidth * 2;
        float topBorder = newPosition.y + tempPlayerHeight;
        float bottomBorder = newPosition.y - tempPlayerHeight;

        boolean collidedLeft = isCollidingWithShadow(leftBorder, topBorder) || isCollidingWithShadow(leftBorder, bottomBorder);
        boolean collidedRight = isCollidingWithShadow(rightBorder, topBorder) || isCollidingWithShadow(rightBorder, bottomBorder);
        boolean collidedTop = isCollidingWithShadow(leftBorder, topBorder) || isCollidingWithShadow(rightBorder, topBorder);
        boolean collidedBottom = isCollidingWithShadow(leftBorder, bottomBorder) || isCollidingWithShadow(rightBorder, bottomBorder);

        if (collidedLeft || collidedRight || collidedTop || collidedBottom) {
        	if(player.isBoss() == false) {
        		isEscape=true;
        	}
        }
    }

    private float escapeAnimationDuration = 5.0f; // 탈출 애니메이션 지속 시간
    private float escapeAnimationTimer = 0f;
    private float petrifiedDuration = 3.0f;  // 석화 지속 시간
    private float petrifiedTimer = 0f;
    private boolean isDefeatScreenTriggered = false;

    public static boolean endShoted = false;
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 게임 시간 업데이트
        gameStartTimer += delta;  // 이 부분을 updateGameState에서 여기로 이동

        updateGameState(delta);
        handlePlayerMovement(delta);
        updateMultiplayerPositions(delta);
        checkObjectInteractions();
        updateCamera(delta);
        setMissionState();
        setMissionPosition();
        loadMissionCompletionStatusFromServer();
        checkAllMissionsComplete();

        if(player.isPetrified()) {
        	if(petrifiedTimer < petrifiedDuration) {
        		petrifiedTimer+=delta;
        	}
        	else {
        		if(endShoted == false) {
        			_Imported_ClientBase.setEnd();
                	endShoted = true;
        		}
        	}
        	
        	if(missionDialog.isShowingMission()) {
            	System.out.println("came1");
        		missionDialog.stopShowMission();
        	}
        	if(missionDialog2.isShowingMission()) {
            	System.out.println("came2");
        		missionDialog2.stopShowMission();
        	}
        	if(missionDialog3.isShowingMission()) {
            	System.out.println("came3");
        		missionDialog3.stopShowMission();
        	}
        	if(missionDialog4.isShowingMission()) {
            	System.out.println("came4");
        		missionDialog4.stopShowMission();
        	}
        	if(missionDialog5.isShowingMission()) {
            	System.out.println("came5");
        		missionDialog5.stopShowMission();
        	}
        }
        
        if (player.isPetrified() && !isDefeatScreenTriggered && everybodyEnd == true) {
            isDefeatScreenTriggered = true;
            game.setScreen(new EscapeResultScreen(game, false, deadPlayer, totalPlayer)); //탈출 실패
            initialAllPlayerStatus();
            //dispose();
        }

        if(everybodyEnd == true && player.isBoss()) {
        	game.setScreen(new EscapeResultScreen(game, false, deadPlayer, totalPlayer)); //탈출 성공
            initialAllPlayerStatus();
        }

        if (renderer != null) {
            renderer.setView(camera);

            MapLayer floorLayer = map.getLayers().get("floor");
            if (floorLayer != null && floorLayer.isVisible()) {

                renderer.getBatch().begin();
                renderer.renderTileLayer((TiledMapTileLayer) floorLayer);
                renderer.getBatch().end();
            }

            MapLayer onfloorLayer = map.getLayers().get("on_floor");
            if (onfloorLayer != null && onfloorLayer.isVisible()) {

                renderer.getBatch().begin();
                renderer.renderTileLayer((TiledMapTileLayer) onfloorLayer);
                renderer.getBatch().end();
            }

            //모든 미션 클리어시
            if (allComplete) {
                //문 닫힌거 제거
                deleteDoorClose();

                //탈출구 츨입 확인
                ifColliedShadow();
            }

            //탈출구에 출입할시
            if (isEscape){
            	if(endShoted == false) {
            		_Imported_ClientBase.setEnd();
                	endShoted = true;
            	}
                renderer.getBatch().begin();
                player.render(renderer.getBatch());
                renderer.getBatch().end();

                player.setIsEscapeState(true);

                // 플레이어의 위치 가져오기
                Vector2 playerPos = player.getPosition();

                // 플레이어가 y 좌표로 계속 이동하도록 설정
                if (playerPos.y < 2800f) {
                    player.setPosition(playerPos.x, playerPos.y += 120 * delta);
                }

                escapeAnimationTimer += delta;

                if (escapeAnimationTimer >= escapeAnimationDuration && everybodyEnd == true) {
                    game.setScreen(new EscapeResultScreen(game, true, deadPlayer, totalPlayer)); //탈출 성공
                    initialAllPlayerStatus();
                    //dispose(); // 현재 화면의 리소스 정리
                }
            }

            // fake_wall1 레이어 이전의 모든 레이어를 렌더링 (플레이어보다 아래에 있음)
            renderer.getBatch().begin();
            for (int i = 0; i < map.getLayers().getCount(); i++) {
                if (i == map.getLayers().getIndex("fake_wall1")) {
                    break;
                }
                MapLayer layer = map.getLayers().get(i);
                if (layer.isVisible() && !layer.getName().equals("on_floor") && !layer.getName().equals("floor")) {
                    renderer.renderTileLayer((TiledMapTileLayer) layer);
                }
            }
            renderer.getBatch().end();

            // 다른 플레이어들 렌더링 (fake_wall1 레이어보다 아래)
            renderer.getBatch().begin();
            for (int i = 0; i < currentRoom.pCount; i++) {
                if (currentRoom.m_players[i] != null) {
                    currentRoom.m_players[i].render(renderer.getBatch());
                }
            }

            if (isUsingBossSkill && showSkillHitbox) {
            	if(player.isBoss()) {
            		renderBossSkill(renderer.getBatch());
            	}
            	else {
            		renderBossSkillM(renderer.getBatch(), _Imported_ClientBase.getBossName());
            	}
            }

            renderer.getBatch().end();

            if (!isEscape){
                // 본인 캐릭터 렌더링 (다른 플레이어와 분리하여 따로 렌더링)
                renderer.getBatch().begin();
                player.render(renderer.getBatch());
                renderer.getBatch().end();
            }

            // fake_wall1 레이어 렌더링 (플레이어 위에 위치하도록 렌더링)
            renderer.getBatch().begin();
            MapLayer fakeWallLayer = map.getLayers().get(map.getLayers().getIndex("fake_wall1"));
            if (fakeWallLayer.isVisible()) {
                renderer.renderTileLayer((TiledMapTileLayer) fakeWallLayer);
            }
            renderer.getBatch().end();

            // fake_wall1 이후의 모든 레이어 렌더링 (플레이어보다 아래에 있음)
            renderer.getBatch().begin();
            for (int i = map.getLayers().getIndex("fake_wall1") + 1; i < map.getLayers().getCount(); i++) {
                MapLayer layer = map.getLayers().get(i);
                if (layer.isVisible()) {
                    renderer.renderTileLayer((TiledMapTileLayer) layer);
                }
            }
            renderer.getBatch().end();
        }

        // 디버그 정보 출력 - 실제 필요할 때만 출력하도록 수정
        if (bossActivated && isBossPlayer && isUsingBossSkill) {
            float cooldownRemaining = Math.max(0, BOSS_SKILL_COOLDOWN - (gameStartTimer - lastBossSkillTime));
            Gdx.app.debug("GameScreen", String.format(
                "Skill State: active=%b, cooldown=%.1f, time=%.1f",
                isUsingBossSkill,
                cooldownRemaining,
                currentBossSkillTime
            ));
        }

        gameUI.render();

        // UI 렌더링
        stage.act(delta);
        stage.draw();
    }

    private void renderBossSkill(Batch batch) {
        if (bossSkillAtlas != null) {
            float frameTime = currentBossSkillTime / bossSkillDuration;
            int frameIndex = Math.min((int)(frameTime * 20) + 1, 20);
            TextureRegion skillFrame = bossSkillAtlas.findRegion("BossSkill" + frameIndex);

            if (skillFrame != null) {
                Vector2 playerPos = player.getPosition();
                float playerCenterX = playerPos.x + player.size/2;
                float playerCenterY = playerPos.y + player.size/2;

                // 스킬 크기 조정
                float skillWidth = 240f;
                float skillHeight = 80f;

                // 스킬 오프셋 설정 (캐릭터로부터의 거리)
                float skillOffsetX = 40f;

                // 스킬 위치 계산
                float skillX;
                if (player.isFacingLeft()) {
                    // 왼쪽을 볼 때 (<O)
                    skillX = playerCenterX - skillWidth - skillOffsetX;
                    batch.draw(skillFrame,
                        skillX + skillWidth,
                        playerCenterY - skillHeight/2,
                        -skillWidth,
                        skillHeight
                    );
                } else {
                    // 오른쪽을 볼 때 (O>)
                    skillX = playerCenterX + skillOffsetX;
                    batch.draw(skillFrame,
                        skillX,
                        playerCenterY - skillHeight/2,
                        skillWidth,
                        skillHeight
                    );
                }

                // 스킬 히트박스 업데이트
                skillHitbox.x = skillX;
                skillHitbox.y = playerCenterY - skillHeight/2;
                skillHitbox.width = skillWidth;
                skillHitbox.height = skillHeight;
            }
        }
    }

    int bossInd = -1;

    private void renderBossSkillM(Batch batch, String bossName) {
        if (bossSkillAtlas != null) {
            float frameTime = currentBossSkillTime / bossSkillDuration;
            int frameIndex = Math.min((int)(frameTime * 20) + 1, 20);
            TextureRegion skillFrame = bossSkillAtlas.findRegion("BossSkill" + frameIndex);

            if (skillFrame != null) {
            	for(int i = 0; i<currentRoom.pCount; i++) {
            		if(currentRoom.m_players[i].getNickname().equals(bossName)) {
            			bossInd = i;
            			break;
            		}
            	}
                Vector2 playerPos = currentRoom.m_players[bossInd].getPosition();
                float playerCenterX = playerPos.x + player.size/2;
                float playerCenterY = playerPos.y + player.size/2;

                // 스킬 크기 조정
                float skillWidth = 240f;
                float skillHeight = 80f;

                // 스킬 오프셋 설정 (캐릭터로부터의 거리)
                float skillOffsetX = 40f;

                // 스킬 위치 계산
                float skillX;
                if (currentRoom.m_players[bossInd].facingLeft) {
                    // 왼쪽을 볼 때 (<O)
                    skillX = playerCenterX - skillWidth - skillOffsetX;
                    batch.draw(skillFrame,
                        skillX + skillWidth,
                        playerCenterY - skillHeight/2,
                        -skillWidth,
                        skillHeight
                    );
                } else {
                    // 오른쪽을 볼 때 (O>)
                    skillX = playerCenterX + skillOffsetX;
                    batch.draw(skillFrame,
                        skillX,
                        playerCenterY - skillHeight/2,
                        skillWidth,
                        skillHeight
                    );
                }

                // 스킬 히트박스 업데이트
                skillHitbox.x = skillX;
                skillHitbox.y = playerCenterY - skillHeight/2;
                skillHitbox.width = skillWidth;
                skillHitbox.height = skillHeight;
            }
        }
    }

    private void initialAllPlayerStatus(){
        player.initialPlayerStatus();
        for (int i = 0; i < currentRoom.pCount; i++) {
            if (currentRoom.m_players[i] != null && currentRoom.m_players[i].isPetrified()) {
                currentRoom.m_players[i].setPetrified(false);
                _Imported_ClientBase.players[i].isDead=false;
            }
        }
    }

    //이미지 겹칠시 투명부분 체크
    private boolean checkCollisionWithTransparency(
        TextureRegion region1, float x1, float y1, float width1, float height1,
        TextureRegion region2, float x2, float y2, float width2, float height2
    ) {
        // 겹치는 영역 계산 (화면 좌표 기준)
        int overlapXStart = Math.max((int) x1, (int) x2);
        int overlapYStart = Math.max((int) y1, (int) y2);
        int overlapXEnd = Math.min((int) (x1 + width1), (int) (x2 + width2));
        int overlapYEnd = Math.min((int) (y1 + height1), (int) (y2 + height2));

        // 겹치는 영역이 없으면 충돌 아님
        if (overlapXStart >= overlapXEnd || overlapYStart >= overlapYEnd) {
            return false;
        }else{
            // Pixmap 생성
            Pixmap pixmap1 = getPixmapFromTextureRegion(region1);
            Pixmap pixmap2 = getPixmapFromTextureRegion(region2);
            // 겹치는 영역의 픽셀 투명도 확인
            for (int x = overlapXStart; x < overlapXEnd; x++) {
                for (int y = overlapYStart; y < overlapYEnd; y++) {
                    // 실제 화면 좌표를 TextureRegion의 좌표로 변환
                    float scaleX1 = region1.getRegionWidth() / width1;
                    float scaleY1 = region1.getRegionHeight() / height1;
                    float scaleX2 = region2.getRegionWidth() / width2;
                    float scaleY2 = region2.getRegionHeight() / height2;

                    int localX1 = (int) ((x - x1) * scaleX1) + region1.getRegionX();
                    int localY1 = (int) ((y - y1) * scaleY1) + region1.getRegionY();
                    int localX2 = (int) ((x - x2) * scaleX2) + region2.getRegionX();
                    int localY2 = (int) ((y - y2) * scaleY2) + region2.getRegionY();

                    // Pixmap 좌표계 변환 (Y축 뒤집기)
                    int pixmapY1 = pixmap1.getHeight() - localY1 - 1;
                    int pixmapY2 = pixmap2.getHeight() - localY2 - 1;

                    // 픽셀의 알파값 확인
                    int alpha1 = (pixmap1.getPixel(localX1, pixmapY1) >>> 24) & 0xff;
                    int alpha2 = (pixmap2.getPixel(localX2, pixmapY2) >>> 24) & 0xff;

                    // 두 픽셀이 모두 투명하지 않다면 충돌 발생
                    if (alpha1 > 0 && alpha2 > 0) {
                        pixmap1.dispose();
                        pixmap2.dispose();
                        return true;
                    }
                }
            }

            // Pixmap 메모리 해제
            pixmap1.dispose();
            pixmap2.dispose();
            return false;
        }
    }

    private Pixmap getPixmapFromTextureRegion(TextureRegion region) {
        Texture texture = region.getTexture();
        texture.getTextureData().prepare();
        return texture.getTextureData().consumePixmap();
    }

    private void updateCamera(float delta) {
        if (isEscape){
            return;
        }
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

    private boolean skillAnimating = false;

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
        totalPlayer = currentRoom.pCount; //현재 총 플레이어 수 (보스 제외)

        int temp = 0;
        for (int i = 0; i < 5; i++) {
            if (_Imported_ClientBase.players[i] != null) {
                if (!(_Imported_ClientBase.players[i].name.equals(player.getNickname()))) {
                    if (currentRoom.m_players[temp] == null) {
                        currentRoom.m_players[temp] = new PlayerOfMulti(
                            _Imported_ClientBase.players[i].name,
                            _Imported_ClientBase.players[i].x - playerWidth / 2,
                            _Imported_ClientBase.players[i].y - playerHeight / 2,
                            playerWidth
                        );
                    }
                    currentRoom.m_players[temp].setPetrified(_Imported_ClientBase.players[i].isDead);
                    if(_Imported_ClientBase.players[i].isUsingSkill == true) {
                    	if(_Imported_ClientBase.players[i].name.equals(_Imported_ClientBase.getBossName()) && bossActivated == true) {
                    		if (!skillAnimating) {  // 애니메이션이 진행중이 아닐 때만 실행
                                useBossSkillM(temp);
                                skillAnimating = true;
                            }
                    	}
                    	else {
                    		currentRoom.m_players[temp].serverPlayerState = PlayerState.ROLLING;
                    	}
                    }
                    else if(_Imported_ClientBase.players[i].name.equals(_Imported_ClientBase.getBossName())) {
                    	skillAnimating = false;
                    }
                    currentRoom.m_players[temp].update(
                        _Imported_ClientBase.players[i].x - playerWidth / 2,
                        _Imported_ClientBase.players[i].y - playerHeight / 2,
                        delta
                    );
                    temp++;
                }
                else {
                	player.setPetrified(_Imported_ClientBase.players[i].isDead);
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
    public void show() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void dispose() {
        if (renderer != null) renderer.dispose();
        if (map != null) map.dispose();
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
        if (batch != null) batch.dispose();
        if (bossSkillAtlas != null) bossSkillAtlas.dispose();
        if (gameUI != null) gameUI.dispose();
        if (bossSkillAtlas != null) {
            bossSkillAtlas.dispose();
        }
        if (bossAttackLeftAnimation != null && bossAttackLeftAnimation.getKeyFrame(0) != null) {
            bossAttackLeftAnimation.getKeyFrame(0).getTexture().dispose();
        }
        if (bossAttackRightAnimation != null && bossAttackRightAnimation.getKeyFrame(0) != null) {
            bossAttackRightAnimation.getKeyFrame(0).getTexture().dispose();
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
