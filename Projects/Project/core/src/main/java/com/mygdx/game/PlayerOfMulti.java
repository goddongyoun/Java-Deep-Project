package com.mygdx.game;

import com.ImportedPackage._Imported_ClientBase;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.Player.PlayerState;
import com.mygdx.game.screens.LobbyScreen;
import com.mygdx.game.util.FontManager;

public class PlayerOfMulti {
    private String nickname;
    private Vector2 position;
    private Vector2 velocity;
    private float speedX = 200f; //160
    private float speedY = 160f; //120
    private Rectangle bounds;
    public float size;
    private Vector2 serverPosition = new Vector2();

    public boolean isUsingSkill = false;

    // 기본 애니메이션 관련 변수
    private TextureAtlas basicAtlas;
    private Animation<TextureRegion> idleLeftAnimation;
    private Animation<TextureRegion> idleRightAnimation;
    private Animation<TextureRegion> runLeftAnimation;
    private Animation<TextureRegion> runRightAnimation;
    private TextureRegion defaultTexture;

    // 구르기 애니메이션 관련
    private TextureAtlas rollingAtlas;
    private Animation<TextureRegion> rollingLeftAnimation;
    private Animation<TextureRegion> rollingRightAnimation;
    private float rollingStartTime;

    // 석화 애니메이션 관련
    private TextureAtlas deadAtlas;
    private Animation<TextureRegion> petrifiedLeftAnimation;
    private Animation<TextureRegion> petrifiedRightAnimation;

    // 보스 애니메이션 관련
    private TextureAtlas bossIdleAtlas;
    private TextureAtlas bossRunAtlas;
    private TextureAtlas bossAttackAtlas;
    private Animation<TextureRegion> bossIdleLeftAnimation;
    private Animation<TextureRegion> bossIdleRightAnimation;
    private Animation<TextureRegion> bossRunLeftAnimation;
    private Animation<TextureRegion> bossRunRightAnimation;
    private Animation<TextureRegion> bossAttackLeftAnimation;
    private Animation<TextureRegion> bossAttackRightAnimation;

    //구르기 관련
    private float rollingCooldown = 3.0f;
    private long lastRollingTime = 0;
    private float rollingDuration = 0.5f;
    private float rollingSpeedMultiplier = 2f;
    private boolean isRolling = false;
    private boolean isInGame = false;
    private static final float ROLL_DISTANCE = 150f;
    private Vector2 rollStartPosition = new Vector2();
    private Vector2 rollDirection = new Vector2();
    float rollTime;

    private float stateTime;
    public boolean facingLeft = false;
    public PlayerState currentState;
    public boolean isBoss = false;
    private boolean isPetrified = false;

    private BitmapFont font;
    public Color nicknameColor;
    private Color outlineColor;
    private GlyphLayout glyphLayout;
    private int fontSize = 19;

    public PlayerState serverPlayerState;

    public enum PlayerState {
        IDLE, RUNNING, ROLLING, PETRIFIED, BOSS_IDLE, BOSS_RUNNING, BOSS_ATTACKING
    }

    public void resetStateTime() {
        stateTime = 0;
    }

    public PlayerOfMulti(String nickname, float x, float y, float size) {
        this.nickname = nickname;
        this.position = new Vector2(x, y);
        this.velocity = new Vector2();
        this.size = size;
        this.bounds = new Rectangle(x, y, size, size);
        this.currentState = PlayerState.IDLE;
        this.nicknameColor = Color.WHITE;
        this.outlineColor = Color.BLACK;
        this.glyphLayout = new GlyphLayout();

        initializeFont();
        loadTextures();
    }

    private void initializeFont() {
        this.font = FontManager.getInstance().getFont(fontSize);
        this.font.getData().markupEnabled = true;
        this.font.getData().setScale(1);
    }

    public void startBossAttack() {
        if (isBoss) {
            currentState = PlayerState.BOSS_ATTACKING;
            stateTime = 0; // 애니메이션 시간 초기화
        }
    }

    public void endBossAttack() {
        if (isBoss) {
            currentState = PlayerState.BOSS_IDLE;
        }
    }

    private void loadTextures() {
        try {
            loadBasicAnimations();
            loadRollingAnimations();
            loadPetrifiedAnimations();
            loadBossAnimations();
        } catch (Exception e) {
            Gdx.app.error("PlayerOfMulti", "Error loading textures", e);
            e.printStackTrace();
            createDefaultTexture();
        }
    }

    private void loadBasicAnimations() {
        String atlasPath = "player/Frogs.atlas";
        basicAtlas = loadAtlas(atlasPath, "Basic");

        float frameDuration = 0.1f;
        idleLeftAnimation = createAnimation(basicAtlas, "FrogIdleL", 12, frameDuration, Animation.PlayMode.LOOP);
        idleRightAnimation = createAnimation(basicAtlas, "FrogIdleR", 12, frameDuration, Animation.PlayMode.LOOP);
        runLeftAnimation = createAnimation(basicAtlas, "FrogRunL", 12, frameDuration, Animation.PlayMode.LOOP);
        runRightAnimation = createAnimation(basicAtlas, "FrogRunR", 12, frameDuration, Animation.PlayMode.LOOP);

        defaultTexture = basicAtlas.findRegion("FrogIdleR1");
        if (defaultTexture == null) {
            createDefaultTexture();
        }
    }

    private void loadRollingAnimations() {
        String rollingPath = "player/FrogRolling.atlas";
        rollingAtlas = loadAtlas(rollingPath, "Rolling");

        if (rollingAtlas != null) {
            float frameDuration = 0.1f;
            rollingLeftAnimation = createAnimation(rollingAtlas, "FrogRollingL", 5, frameDuration, Animation.PlayMode.NORMAL);
            rollingRightAnimation = createAnimation(rollingAtlas, "FrogRollingR", 5, frameDuration, Animation.PlayMode.NORMAL);
        }
    }

    private void loadPetrifiedAnimations() {
        String deadPath = "player/FrogDead.atlas";
        deadAtlas = loadAtlas(deadPath, "Dead");

        if (deadAtlas != null) {
            Array<TextureRegion> framesLeft = new Array<>();
            Array<TextureRegion> framesRight = new Array<>();
            for (int i = 1; i <= 8; i++) {
                framesLeft.add(deadAtlas.findRegion("FrogDeadL" + i));
                framesRight.add(deadAtlas.findRegion("FrogDeadR" + i));
            }
            petrifiedLeftAnimation = new Animation<>(0.1f, framesLeft, Animation.PlayMode.NORMAL);
            petrifiedRightAnimation = new Animation<>(0.1f, framesRight, Animation.PlayMode.NORMAL);
        }
    }

    private void loadBossAnimations() {
        // 보스 Idle 애니메이션
        String bossIdlePath = "boss/BossIdle.atlas";
        bossIdleAtlas = loadAtlas(bossIdlePath, "BossIdle");
        if (bossIdleAtlas != null) {
            float frameDuration = 0.1f;
            bossIdleLeftAnimation = createAnimation(bossIdleAtlas, "BossIdleL", 8, frameDuration, Animation.PlayMode.LOOP);
            bossIdleRightAnimation = createAnimation(bossIdleAtlas, "BossIdleR", 8, frameDuration, Animation.PlayMode.LOOP);
        }

        // 보스 Run 애니메이션
        String bossRunPath = "boss/BossRun.atlas";
        bossRunAtlas = loadAtlas(bossRunPath, "BossRun");
        if (bossRunAtlas != null) {
            float frameDuration = 0.1f;
            bossRunLeftAnimation = createAnimation(bossRunAtlas, "BossRunL", 6, frameDuration, Animation.PlayMode.LOOP);
            bossRunRightAnimation = createAnimation(bossRunAtlas, "BossRunR", 6, frameDuration, Animation.PlayMode.LOOP);
        }

        // 보스 Attack 애니메이션
        String bossAttackPath = "boss/BossAttack.atlas";
        bossAttackAtlas = loadAtlas(bossAttackPath, "BossAttack");
        if (bossAttackAtlas != null) {
            float frameDuration = 0.1f;
            bossAttackLeftAnimation = createAnimation(bossAttackAtlas, "BossAttackL", 6, frameDuration, Animation.PlayMode.NORMAL);
            bossAttackRightAnimation = createAnimation(bossAttackAtlas, "BossAttackR", 6, frameDuration, Animation.PlayMode.NORMAL);
        }
    }
    private TextureAtlas loadAtlas(String path, String type) {
        FileHandle atlasFile = Gdx.files.internal(path);
        if (!atlasFile.exists()) {
            Gdx.app.error("PlayerOfMulti", type + " atlas file not found: " + path);
            return null;
        }

        Gdx.app.log("PlayerOfMulti", "Loading " + type + " atlas: " + path);
        return new TextureAtlas(atlasFile);
    }

    private Animation<TextureRegion> createAnimation(TextureAtlas atlas, String regionName, int frameCount, float frameDuration, Animation.PlayMode playMode) {
        if (atlas == null) {
            Gdx.app.error("Player", "Atlas is null for " + regionName);
            return null;
        }

        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= frameCount; i++) {
            TextureAtlas.AtlasRegion region = atlas.findRegion(regionName + i);
            if (region != null) {
                frames.add(region);
            }
        }

        if (frames.size == 0) {
            Gdx.app.error("Player", "No frames found for animation: " + regionName);
            return null;
        }

        return new Animation<>(frameDuration, frames, playMode);
    }

    private void createDefaultTexture() {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.MAGENTA);
        pixmap.fill();
        defaultTexture = new TextureRegion(new Texture(pixmap));
        pixmap.dispose();
    }

    public void update(float x, float y, float delta) {
        stateTime += delta;
        serverPosition.set(x, y);

        float dx = serverPosition.x - position.x;
        float dy = serverPosition.y - position.y;

        // 구르기 상태일 때는 보간 처리로 부드럽게 이동
        if (currentState == PlayerState.ROLLING) {
            updateRolling(delta);

            // 구르기 중에는 서버 위치로 부드럽게 보간
            float lerpFactor = 5f * delta; // 보간 속도 조절
            position.x += dx * lerpFactor;
            position.y += dy * lerpFactor;
        } else {
            // 텔레포트가 필요한 거리 체크
            float teleportThreshold = 100f;
            if (Math.abs(dx) > teleportThreshold || Math.abs(dy) > teleportThreshold) {
                position.set(serverPosition);
            } else {
                updateMovement(dx, dy, delta);
            }
        }

        //System.out.println(serverPlayerState + " " + currentState);
        if (serverPlayerState == PlayerState.ROLLING && currentState != PlayerState.ROLLING) {
            startRolling();
        }

        bounds.setPosition(position);
    }

    private void updateRolling(float delta) {
        float rollTime = stateTime - rollingStartTime;

        if (rollTime >= rollingDuration ||
            position.dst(rollStartPosition) >= ROLL_DISTANCE) {
            endRolling();
            return;
        }

        // 구르기 방향으로 이동
        float currentSpeedX = speedX * rollingSpeedMultiplier;
        float currentSpeedY = speedY * rollingSpeedMultiplier;
        position.x += rollDirection.x * currentSpeedX * delta;
        position.y += rollDirection.y * currentSpeedY * delta;
        bounds.setPosition(position);
    }

    private void endRolling() {
        isRolling = false;
        // 구르기 끝날 때 IDLE 상태로 전환하고 서버에 전송
        if (isInGame) {
            String state = isBoss ? "BOSS_IDLE" : "IDLE";
            currentState = isBoss ? PlayerState.BOSS_IDLE : PlayerState.IDLE;
            serverPlayerState = currentState;
            _Imported_ClientBase.endRoll();
        }
    }

    private void updateMovement(float dx, float dy, float delta) {
        velocity.setZero();
        float moveThreshold = 1f;

        if (Math.abs(dx) > moveThreshold || Math.abs(dy) > moveThreshold) {
            velocity.x = dx;
            velocity.y = dy;
            velocity.nor();

            if (Math.abs(dx) > moveThreshold) {
                facingLeft = velocity.x < 0;
            }

            if (!isRolling) {
                currentState = isBoss ? PlayerState.BOSS_RUNNING : PlayerState.RUNNING;
            }
        } else {
            if (!isRolling) {
                currentState = isBoss ? PlayerState.BOSS_IDLE : PlayerState.IDLE;
            }
        }

        float speedMultiplier = isRolling ? 1.5f : 1.0f;
        position.x += velocity.x * speedX * speedMultiplier * delta;
        position.y += velocity.y * speedY * speedMultiplier * delta;
    }

    public void handleStateUpdate(String state) {
        // TODO: 상태 업데이트 처리
        // - 서버로부터 받은 상태(구르기/보스변신/보스스킬)를 처리
        // - 각 상태에 맞는 애니메이션 재생
        // - 특히 구르기의 경우 방향 정보도 함께 처리
    	try {
            if (state.contains("/")) {
                String[] parts = state.split("/");
                PlayerState newState = PlayerState.valueOf(parts[0]);
                if (newState == PlayerState.ROLLING) {
                    facingLeft = Boolean.parseBoolean(parts[1]);
                    startRolling();
                }
                setState(newState);
            } else {
                PlayerState newState = PlayerState.valueOf(state);
                if (newState == PlayerState.BOSS_ATTACKING) {
                    stateTime = 0;  // 보스 공격 애니메이션 시작 시 시간 초기화
                }
                setState(newState);
            }
        } catch (IllegalArgumentException e) {
            Gdx.app.error("PlayerOfMulti", "Invalid state received: " + state);
        }
    }

    private void startRolling() {
    	System.out.println("multi Start Roll Came");
        isRolling = true;
        rollingStartTime = stateTime;
        currentState = PlayerState.ROLLING;
        serverPlayerState = currentState;
        lastRollingTime = TimeUtils.millis();
        rollStartPosition.set(position);
    }

    private void setState(PlayerState state) {
        this.currentState = state;
        if (state != PlayerState.ROLLING) {
            isRolling = false;
        }
    }

    public void render(Batch batch) {
        TextureRegion currentFrame = getCurrentFrame();
        if (currentFrame != null && currentFrame.getTexture() != null) {
            batch.draw(currentFrame, position.x, position.y, size, size);
        }

        renderNickname(batch);
    }

    public void transformToBoss() {
        // TODO: 보스 변신 관련
        // - 보스 변신 상태를 서버에 전송
        // - 다른 플레이어의 화면에서도 보스 모습으로 변하도록 처리
        // - 크기 변경 및 보스 상태로 전환
        isBoss = true;
        currentState = PlayerState.BOSS_IDLE;
        size *= 1.2f;
        bounds.setSize(size, size);

        updateBossSpeed();
        // 보스 변신 상태를 서버에 전송
        //_Imported_ClientBase.sendBossTransform(true); TODO: erased
    }

    private void updateBossSpeed(){
        if(isBoss){
            System.out.println("speed changed");
            speedX=220f;
            speedY=175f;
        }
    }

    public void transformToFlog() {
    	isBoss = false;
        currentState = PlayerState.IDLE;
    }

    private TextureRegion getCurrentFrame() {
        try {
            if (isPetrified) {
                return facingLeft ?
                    petrifiedLeftAnimation.getKeyFrame(stateTime, false) :
                    petrifiedRightAnimation.getKeyFrame(stateTime, false);
            }

            if (isBoss) {
            	// 상태 전환 디버그 로그 추가
                //Gdx.app.log("Player", "Current state: " + currentState + ", stateTime: " + stateTime);

                switch (currentState) {
                    case BOSS_RUNNING:
                        return facingLeft ?
                            bossRunLeftAnimation.getKeyFrame(stateTime, true) :
                            bossRunRightAnimation.getKeyFrame(stateTime, true);
                    case BOSS_ATTACKING:
                        Gdx.app.log("Player", "Playing boss attack animation");
                        return facingLeft ?
                            bossAttackLeftAnimation.getKeyFrame(stateTime, false) :
                            bossAttackRightAnimation.getKeyFrame(stateTime, false);
                    case BOSS_IDLE:
                    default:
                        return facingLeft ?
                            bossIdleLeftAnimation.getKeyFrame(stateTime, true) :
                            bossIdleRightAnimation.getKeyFrame(stateTime, true);
                }
            }

            switch (currentState) {
                case RUNNING:
                    return facingLeft ?
                        runLeftAnimation.getKeyFrame(stateTime, true) :
                        runRightAnimation.getKeyFrame(stateTime, true);
                case ROLLING:
                    float rollTime = stateTime - rollingStartTime;
                    if (rollTime <= rollingDuration) {
                        Animation<TextureRegion> currentAnim = facingLeft ? rollingLeftAnimation : rollingRightAnimation;
                        if (currentAnim != null) {
                            return currentAnim.getKeyFrame(rollTime, false);
                        }
                    }
                    isRolling = false;
                    currentState = isBoss ? PlayerState.BOSS_IDLE : PlayerState.IDLE;
                    return facingLeft ?
                        idleLeftAnimation.getKeyFrame(stateTime, true) :
                        idleRightAnimation.getKeyFrame(stateTime, true);
                case IDLE:
                default:
                    return facingLeft ?
                        idleLeftAnimation.getKeyFrame(stateTime, true) :
                        idleRightAnimation.getKeyFrame(stateTime, true);
            }
        } catch (Exception e) {
            Gdx.app.error("PlayerOfMulti", "Error getting animation frame", e);
        }
        return defaultTexture;
    }

    private void renderNickname(Batch batch) {
        String tempBName = null;
        if (LobbyScreen.shouldStart == true) {
            if (nickname.equals(_Imported_ClientBase.getBossName())) {
                if (nicknameColor != Color.RED) {
                    nicknameColor = Color.RED;
                }
                tempBName = "*BOSS* " + nickname;
                glyphLayout.setText(font, tempBName);
            }
        } else {
            glyphLayout.setText(font, nickname);
        }

        float nicknameX = position.x + size / 2 - glyphLayout.width / 2;
        float nicknameY = position.y + size + glyphLayout.height + 5;

        font.setColor(outlineColor);
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    font.draw(batch, tempBName != null ? tempBName : nickname,
                        nicknameX + i, nicknameY + j);
                }
            }
        }

        font.setColor(nicknameColor);
        font.draw(batch, tempBName != null ? tempBName : nickname, nicknameX, nicknameY);
    }

    // Getters and Setters
    public Vector2 getPosition() { return position; }
    public void setPosition(Vector2 position) {
        this.position.set(position);
        this.bounds.setPosition(position);
    }
    public Rectangle getBounds() { return bounds; }
    public float getSize() { return size; }
    public void setSize(float size) { this.size = size; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setPetrified(boolean petrified) {
        if (petrified && !this.isPetrified) {
            stateTime = 0; // 석화 시작 시 애니메이션 시간 초기화
            velocity.setZero();
        }
        this.isPetrified = petrified;
        if (petrified) {
            currentState = PlayerState.PETRIFIED;
        }
    }
    public boolean isPetrified() { return isPetrified; }
    public void setBoss(boolean boss) {
        this.isBoss = boss;
        if (boss) {
            size *= 1.2f;
            bounds.setSize(size, size);
            currentState = PlayerState.BOSS_IDLE;
        }
    }

    public void dispose() {
        if (basicAtlas != null) basicAtlas.dispose();
        if (rollingAtlas != null) rollingAtlas.dispose();
        if (deadAtlas != null) deadAtlas.dispose();
        if (bossIdleAtlas != null) bossIdleAtlas.dispose();
        if (bossRunAtlas != null) bossRunAtlas.dispose();
        if (bossAttackAtlas != null) bossAttackAtlas.dispose();
        if (defaultTexture != null && defaultTexture.getTexture() != null) {
            defaultTexture.getTexture().dispose();
        }
    }
}
