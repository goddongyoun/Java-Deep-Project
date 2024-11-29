package com.mygdx.game;

import com.ImportedPackage._Imported_ClientBase;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.screens.LobbyScreen;
import com.mygdx.game.util.FontManager;
import org.w3c.dom.ls.LSOutput;

public class Player {
    private String nickname;
    private Vector2 position;
    private Vector2 velocity;
    private float speedX = 160f; //160
    private float speedY = 120f; //120
    private Rectangle bounds;
    public float size;
    private int x, y;

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

    public float stateTime;
    private boolean facingLeft = false;
    private boolean isReady;
    public PlayerState currentState;
    private boolean isBoss = false;

    // 구르기 관련 변수
    private float rollingCooldown = 3.0f;
    private long lastRollingTime = 0;
    private float rollingDuration = 0.5f;
    private float rollingSpeedMultiplier = 1.5f;
    private boolean isRolling = false;
    private boolean isInGame = false;
    private static final float ROLL_DISTANCE = 150f;
    private Vector2 rollStartPosition;
    private Vector2 rollDirection;

    private BitmapFont font;
    private Color nicknameColor;
    private Color outlineColor;
    private GlyphLayout glyphLayout;
    private int fontSize = 19;

    private boolean isPetrified = false;

    //움직임 제한 boolean 변수
    private boolean canMove = true;

    //탈출관련
    private boolean isEscape = false;

    public enum PlayerState {
        IDLE, RUNNING, ROLLING, PETRIFIED, BOSS_IDLE, BOSS_RUNNING, BOSS_ATTACKING
    }

    public void resetStateTime() {
        stateTime = 0;
    }

    public Player(String nickname, float x, float y, float size, boolean isInGame) {
        this.nickname = nickname;
        this.position = new Vector2(x, y);
        this.velocity = new Vector2();
        this.rollDirection = new Vector2();
        this.size = size;
        this.bounds = new Rectangle(x, y, size, size);
        this.isReady = false;
        this.currentState = PlayerState.IDLE;
        this.nicknameColor = Color.WHITE;
        this.outlineColor = Color.BLACK;
        this.glyphLayout = new GlyphLayout();
        this.isInGame = isInGame;
        this.rollStartPosition = new Vector2();

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
            Gdx.app.error("Player", "Error loading textures", e);
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
            Gdx.app.error("Player", type + " atlas file not found: " + path);
            return null;
        }

        Gdx.app.log("Player", "Loading " + type + " atlas: " + path);
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


    public void update(float delta) {
        if (!canMove) return;

        if (isPetrified) {
            currentState = PlayerState.PETRIFIED;
            return;
        }

        if (isEscape) {
            isEscape(delta);
            return;
        }

        stateTime += delta;

        // 게임 상태에서만 구르기 허용
        if (isInGame && !isBoss && Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && canRoll()) {
            startRolling();
        }

        if (currentState == PlayerState.ROLLING) {
            updateRolling(delta);
        } else {
            updateMovement(delta);
        }
    }

    private void updateMovement(float delta) {
        velocity.setZero();
        boolean isMoving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocity.x -= 1;
            facingLeft = true;
            isMoving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocity.x += 1;
            facingLeft = false;
            isMoving = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            velocity.y += 1;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            velocity.y -= 1;
            isMoving = true;
        }

        if (!velocity.isZero()) {
            velocity.nor();
            float currentSpeedX = speedX * (currentState == PlayerState.ROLLING ? rollingSpeedMultiplier : 1);
            float currentSpeedY = speedY * (currentState == PlayerState.ROLLING ? rollingSpeedMultiplier : 1);
            position.x += velocity.x * currentSpeedX * delta;
            position.y += velocity.y * currentSpeedY * delta;
        }

        // 상태 업데이트
        PlayerState newState = isMoving ? (isBoss ? PlayerState.BOSS_RUNNING : PlayerState.RUNNING)
            : (isBoss ? PlayerState.BOSS_IDLE : PlayerState.IDLE);
        if (currentState != newState) {
            currentState = newState;
            if (isInGame) {
                //TODO: erased
            }
        }

        bounds.setPosition(position);
    }

    private void updateRolling(float delta) {
        float rollTime = (TimeUtils.millis() - lastRollingTime) / 1000f;

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

    private boolean canRoll() {
        return !isPetrified && currentState != PlayerState.ROLLING &&
            TimeUtils.millis() - lastRollingTime > rollingCooldown * 1000;
    }

    public void startRolling() {
        // TODO: 구르기 모션 관련
        // - 구르기 상태를 서버에 전송 (상태 + 방향)
        // - updateMovement()에서 구르기 상태일 때 속도 증가
        // - PlayerOfMulti에서도 보이도록 처리
        currentState = PlayerState.ROLLING;
        lastRollingTime = TimeUtils.millis();
        isRolling = true;
        rollStartPosition.set(position);

        // 구르기 방향 설정
        rollDirection.setZero();
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            rollDirection.x = -1;
            facingLeft = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            rollDirection.x = 1;
            facingLeft = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            rollDirection.y = 1;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            rollDirection.y = -1;
        }

        // 방향키 입력이 없으면 현재 바라보는 방향으로
        if (rollDirection.isZero()) {
            rollDirection.x = facingLeft ? -1 : 1;
        }
        rollDirection.nor();

        // 서버에 구르기 상태 전송
        if (isInGame) {
            String state = "ROLLING/" + facingLeft;
            _Imported_ClientBase.startRoll(facingLeft);
        }
    }

    private void endRolling() {
        isRolling = false;
        // 구르기 끝날 때 IDLE 상태로 전환하고 서버에 전송
        if (isInGame) {
            String state = isBoss ? "BOSS_IDLE" : "IDLE";
            _Imported_ClientBase.endRoll();
        }
        currentState = isBoss ? PlayerState.BOSS_IDLE : PlayerState.IDLE;
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
        // 보스 변신 상태를 서버에 전송
        //_Imported_ClientBase.sendBossTransform(true); TODO: erased
    }

    public void render(Batch batch) {
        TextureRegion currentFrame = getCurrentFrame();
        if (currentFrame != null && currentFrame.getTexture() != null) {
            batch.draw(currentFrame, position.x, position.y, size, size);
        }

        renderNickname(batch);
    }

    public void setIsEscapeState(boolean isEscape) {
        this.isEscape=isEscape;
    }

    //탈출시 뛰기 애니메이션 활성화
    public void isEscape(float delta){
        // 플레이어의 이동 속도 설정 (UP 키가 눌린 것처럼 설정)
        velocity.y += 100 * delta; // 플레이어의 y 방향 속도를 증가시켜 위로 이동

        // 플레이어 애니메이션 상태를 '뛰기' 상태로 설정
        currentState = PlayerState.RUNNING; // 'RUNNING'을 플레이어가 뛰는 상태로 가정

        stateTime += delta; // stateTime 업데이트
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
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
                Gdx.app.log("Player", "Current state: " + currentState + ", stateTime: " + stateTime);

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
                        runLeftAnimation.getKeyFrame(stateTime, true):
                        runRightAnimation.getKeyFrame(stateTime, true);
                case ROLLING:
                    Animation<TextureRegion> currentAnim = facingLeft ? rollingLeftAnimation : rollingRightAnimation;
                    float rollTime = (TimeUtils.millis() - lastRollingTime) / 1000f;
                    if (rollTime <= rollingDuration) {
                        return currentAnim.getKeyFrame(rollTime, false);
                    }
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
            Gdx.app.error("Player", "Error getting animation frame", e);
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
    public void setPosition(float x, float y) {
        this.position.set(x,y);
        this.bounds.setPosition(x,y);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public boolean isReady() { return isReady; }
    public void setReady(boolean ready) { this.isReady = ready; }
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
    public boolean isBoss() { return isBoss; }
    public PlayerState getCurrentState() { return currentState; }
    public boolean isFacingLeft() { return facingLeft; }
    public float getStateTime() { return stateTime; }
    public void setInGame(boolean inGame) { this.isInGame = inGame; }
    public boolean isInGame() { return isInGame; }
    public float getRollingCooldown() { return rollingCooldown; }
    public long getLastRollingTime() { return lastRollingTime; }

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
