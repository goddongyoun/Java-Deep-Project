package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.util.FontManager;
import com.badlogic.gdx.utils.Disposable;

public class MissionDialog extends Dialog implements Disposable {
    // 미션 완료 콜백 인터페이스
    public interface MissionCompleteCallback {
        void onMissionComplete();
    }

    private MissionCompleteCallback completeCallback;
    private Image currentMonster;
    private float currentMonsterSpeedX = 0f;
    private boolean currentMonsterMoving = false;
    private int currentRound = 1;
    private Image aimImage;
    private TextButton closeButton;
    private Image closeButtonImage;
    private Image fieldImage;
    private Image borderImage;
    private Image shooterImage;
    private Image monsterBallImage;
    private float monsterBallSpeedX;
    private float monsterBallSpeedY;
    private boolean isMonsterBallMoving = false;
    private float monsterBallInitialX;
    private float monsterBallInitialY = 0;
    private float shooterInitialX;
    private float shooterInitialY = 0;
    private float aimInitialX;
    private float aimInitialY = 0;
    private float aimingAngle = 90;
    private float aimingAngleSpeed = 1.0f;
    private float monsterBallSpeed = 500;
    private boolean isNewRound = true;
    private int remainingBalls = 2;
    private float stateTime = 0f;
    private boolean isShowingMission = false;

    // 애니메이션 관련
    private TextureAtlas successAtals = new TextureAtlas("publicImages/successes.atlas");
    private TextureRegionDrawable successDrawable;
    private Array<TextureRegion> successArray = new Array<>();
    private Animation<TextureRegion> successAnimation;
    private Image success = new Image();
    private float successStateTime = 0f;

    private TextureAtlas failAtals = new TextureAtlas("publicImages/failes.atlas");
    private TextureRegionDrawable failDrawable;
    private Array<TextureRegion> failArray = new Array<>();
    private Animation<TextureRegion> failAnimation;
    private Image fail;
    private float failStateTime = 0f;

    private boolean roundClear = false;
    private boolean roundFail = false;
    private boolean missionComplete = false;  // 미션 전체 완료 상태

    // 텍스처와 이미지 리소스
    Texture aim = new Texture(Gdx.files.internal("images/aim.png"));
    Texture closeButtonTexture = new Texture(Gdx.files.internal("images/mission_button1.png"));
    Texture closeButtonTextureHover = new Texture(Gdx.files.internal("images/mission_button2.png"));
    Texture field = new Texture(Gdx.files.internal("images/mission-field.png"));
    Texture border = new Texture(Gdx.files.internal("images/mission_box.png"));
    Texture shooter1 = new Texture(Gdx.files.internal("images/mssn1Shooter1.png"));
    Texture shooter2 = new Texture(Gdx.files.internal("images/mssn1Shooter2.png"));
    TextureAtlas monsters = new TextureAtlas(Gdx.files.internal("images/m1monsters.atlas"));
    Array<TextureRegion> monsterAArray;
    private Animation<TextureRegion> monsterAAnimation;
    TextureRegionDrawable monsterADrawable;
    private TextureAtlas roundsAtlas = new TextureAtlas("publicImages/rounds.atlas");
    private TextureRegionDrawable stage1Drawable = new TextureRegionDrawable(roundsAtlas.findRegion("mission_round1"));
    private TextureRegionDrawable stage2Drawable = new TextureRegionDrawable(roundsAtlas.findRegion("mission_round2"));
    private TextureRegionDrawable stage3Drawable = new TextureRegionDrawable(roundsAtlas.findRegion("mission_round3"));
    private TextureRegionDrawable stage4Drawable = new TextureRegionDrawable(roundsAtlas.findRegion("mission_round4"));
    private Image card;
    private Stage stage;

    public MissionDialog(String title, Skin skin, Stage stage) {
        super(title, skin);
        this.stage = stage;
        this.setModal(true);
        this.setMovable(true);
        this.setResizable(false);

        // ESC 키 리스너 추가
        this.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    isShowingMission = false;
                    MissionDialog.this.hide();
                    return true;
                }
                return super.keyDown(event, keycode);
            }
        });

        initializeAnimations();
        setupUI();
    }

    public void setMissionCompleteCallback(MissionCompleteCallback callback) {
        this.completeCallback = callback;
    }

    private void initializeAnimations() {
        // 성공 애니메이션 초기화
        for(int i=1; i <= 6; i++) {
            successArray.add(successAtals.findRegion("mission_success" + i));
        }
        successAnimation = new Animation<>(0.15f, successArray, Animation.PlayMode.NORMAL);
        successDrawable = new TextureRegionDrawable(successAnimation.getKeyFrame(0));
        success = new Image(successDrawable);

        // 실패 애니메이션 초기화
        for(int i=1; i <= 6; i++) {
            failArray.add(failAtals.findRegion("mission_false" + i));
        }
        failAnimation = new Animation<>(0.15f, failArray, Animation.PlayMode.NORMAL);
        failDrawable = new TextureRegionDrawable(failAnimation.getKeyFrame(0));
        fail = new Image(failDrawable);
    }

    private void setupUI() {
        Table contentTable = getContentTable();

        // 몬스터볼 이미지 초기화
        Texture monsterBall = new Texture(Gdx.files.internal("images/ball.png"));
        monsterBallImage = new Image(monsterBall);
        monsterBallImage.setSize(50, 50);

        // 플레이어 이미지 초기화
        shooterImage = new Image(shooter1);
        shooterImage.setSize(50, 50);

        // 테두리 및 배경 이미지 초기화
        borderImage = new Image(border);
        borderImage.setSize(640, 360);
        fieldImage = new Image(field);

        // 조준선 초기화
        aimImage = new Image(aim);
        aimImage.setSize(150, 150);

        // 버튼 스타일 설정
        setupCloseButton();

        // 스테이지 카드 초기화
        card = new Image(stage1Drawable);

        // UI 요소들 추가
        addUIComponents(contentTable);
    }

    private void setupCloseButton() {
        Drawable closeButtonDrawable = new TextureRegionDrawable(new TextureRegion(closeButtonTexture));
        Drawable closeButtonHoverDrawable = new TextureRegionDrawable(new TextureRegion(closeButtonTextureHover));
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = closeButtonDrawable;
        buttonStyle.down = closeButtonHoverDrawable;
        buttonStyle.over = closeButtonHoverDrawable;
        buttonStyle.font = FontManager.getInstance().getFont(16);

        closeButton = new TextButton("", buttonStyle);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isShowingMission = false;
                MissionDialog.this.hide();
            }
        });
    }

    //GameScreen에서 미니게임이 열려있는지 확인 용도
    public boolean isShowingMission(){
        return isShowingMission;
    }

    private void addUIComponents(Table contentTable) {
        contentTable.add(fieldImage).width(640).height(365).expand().fill().pad(30);
        contentTable.add(monsterBallImage).width(30).height(30).expand().fill().pad(10);
        contentTable.add(shooterImage).width(80).height(80).expand().fill().pad(10);
        contentTable.add(borderImage).width(640).height(365).expand().fill().pad(10);
        contentTable.add(closeButton).width(32).height(32).expand().fill().pad(10);
        contentTable.add(aimImage).width(150).height(150).expand().fill().pad(10);
        contentTable.add(card).width(250).height(80).expand().fill();
        contentTable.add(fail).width(250).height(100).expand().fill();
        contentTable.add(success).width(250).height(100).expand().fill();

        // 초기 상태 설정
        card.setVisible(false);
        fail.setVisible(false);
        success.setVisible(false);

        this.getCell(contentTable).width(640).height(360);
        this.setBackground((Drawable) null);
    }
    public void showMission(Stage stage) {
        this.setSize(640, 360);
        stage.addActor(this);
        isShowingMission = true;
        GameInitialization();
        this.invalidate();
        this.layout();
        initializePositions();
        this.show(stage);
    }

    private void initializePositions() {
        // 몬스터볼 초기 위치
        monsterBallInitialX = (this.getWidth() - monsterBallImage.getWidth()) / 2;
        monsterBallInitialY = 25;
        monsterBallImage.setPosition(monsterBallInitialX, monsterBallInitialY);

        // 조준선 초기 위치
        aimInitialX = (this.getWidth() - aimImage.getWidth()) / 2;
        aimInitialY = 15;
        aimImage.setPosition(aimInitialX, aimInitialY);

        // 플레이어 초기 위치
        shooterInitialX = ((this.getWidth() - shooterImage.getWidth()) / 2) - (shooterImage.getWidth() / 2.5f);
        shooterInitialY = 15;
        shooterImage.setPosition(shooterInitialX, shooterInitialY);

        // 닫기 버튼 위치
        closeButton.setPosition(this.getWidth() - (closeButton.getWidth()+2), this.getHeight() - closeButton.getHeight());

        // 테두리 및 배경 위치
        borderImage.setPosition(0, -5);
        fieldImage.setPosition(0, -5);

        // 다이얼로그 위치
        this.setPosition(
            (stage.getWidth() - this.getWidth()) / 2,
            (stage.getHeight() - this.getHeight()) / 2
        );
    }

    private void nextRound() {
        Table contentTable = getContentTable();
        changeStageCard();

        if (currentRound != 5) {
            card.setVisible(true);
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    card.setVisible(false);
                }
            }, 1f);
        }

        shooterImage.setDrawable(new TextureRegionDrawable(new TextureRegion(shooter1)));

        if (currentMonster != null) {
            contentTable.removeActor(currentMonster);
            currentMonster.remove();
        }

        setupMonsterForRound();

        if (currentRound <= 4) {
            roundClear = false;
            remainingBalls = 2;
            isNewRound = true;
        } else {
            success.setVisible(true);
            missionComplete = true;
            notifyMissionComplete();
        }

        // 몬스터볼 이미지의 회전 중심을 이미지의 중앙으로 설정
        monsterBallImage.setOrigin(monsterBallImage.getWidth() / 2, monsterBallImage.getHeight() / 2);

        // 조준선 회전 중심 설정
        aimImage.setOrigin(aimImage.getWidth() / 2, monsterBallImage.getHeight() / 2);
        aimImage.setRotation(aimingAngle-90);
    }

    private void setupMonsterForRound() {
        monsterAArray = new Array<>();
        String monsterPrefix = "mssn1MstrA";
        float speed = 200f;

        switch (currentRound) {
            case 1: speed = 200f; monsterPrefix = "mssn1MstrA"; break;
            case 2: speed = 300f; monsterPrefix = "mssn1MstrB"; break;
            case 3: speed = 400f; monsterPrefix = "mssn1MstrC"; break;
            case 4: speed = 500f; monsterPrefix = "mssn1MstrD"; break;
            default: return;
        }

        monsterAArray.add(monsters.findRegion(monsterPrefix + "1"));
        monsterAArray.add(monsters.findRegion(monsterPrefix + "2"));

        monsterAAnimation = new Animation<>(0.1f, monsterAArray, Animation.PlayMode.LOOP);
        monsterADrawable = new TextureRegionDrawable(monsterAAnimation.getKeyFrame(0));
        currentMonsterSpeedX = speed;

        currentMonster = new Image(monsterADrawable);
        currentMonster.setSize(91, 91);

        getContentTable().add(currentMonster).width(91).height(91).expand().fill().pad(10).row();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        updateUIPositions();
        updateAnimations(delta);

        if (!roundFail && !card.isVisible() && currentRound != 5) {
            handleGameLogic(delta);
        }
    }

    private void updateUIPositions() {
        float centerX = (this.getWidth() - card.getWidth()) / 2;
        float centerY = (this.getHeight() - card.getHeight()) / 2;

        card.setPosition(centerX, centerY);
        fail.setPosition(centerX, centerY);
        success.setPosition(centerX, centerY);

        shooterImage.setPosition(shooterInitialX, shooterInitialY);
        closeButton.setPosition(this.getWidth() - closeButton.getWidth(), this.getHeight() - (closeButton.getHeight() + 5));
        borderImage.setPosition(0, -5);
        fieldImage.setPosition(0, -5);
        aimImage.setPosition(aimInitialX, aimInitialY);
    }

    private void updateAnimations(float delta) {
        stateTime += delta;

        if (failArray != null && fail.isVisible()) {
            failStateTime += delta;
            TextureRegion currentFrame = failAnimation.getKeyFrame(failStateTime, false);
            ((TextureRegionDrawable) fail.getDrawable()).setRegion(currentFrame);
        }

        if (successArray != null && success.isVisible()) {
            successStateTime += delta;
            TextureRegion currentFrame = successAnimation.getKeyFrame(successStateTime, false);
            ((TextureRegionDrawable) success.getDrawable()).setRegion(currentFrame);
        }

        if (monsterAAnimation != null) {
            TextureRegion currentFrame = monsterAAnimation.getKeyFrame(stateTime, true);
            ((TextureRegionDrawable) currentMonster.getDrawable()).setRegion(currentFrame);
        }
    }

    private void handleGameLogic(float delta) {
        if (isNewRound) {
            initializeNewRound();
            return;
        }

        updateMonsterPosition(delta);
        handlePlayerInput();
        updateMonsterBall(delta);
        checkCollisions();
    }

    private void initializeNewRound() {
        isNewRound = false;
        float initialX = MathUtils.random(0, this.getWidth() - currentMonster.getWidth());
        currentMonster.setPosition(initialX, 260);
    }

    private void updateMonsterPosition(float delta) {
        float x = currentMonster.getX();
        float y = 230;

        if (currentMonsterMoving) {
            x += currentMonsterSpeedX * delta;
            if (x > (this.getWidth() - 70) - currentMonster.getWidth() || Math.random() < 0.006) {
                currentMonsterMoving = false;
            }
        } else {
            x -= currentMonsterSpeedX * delta;
            if (x < 60 || Math.random() < 0.006) {
                currentMonsterMoving = true;
            }
        }
        currentMonster.setPosition(x, y);
        aimImage.setRotation(aimingAngle - 90);
    }

    private void handlePlayerInput() {
        // 조준 방향 조절
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            aimingAngle = Math.min(140, aimingAngle + aimingAngleSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            aimingAngle = Math.max(40, aimingAngle - aimingAngleSpeed);
        }

        // 발사
        if (Gdx.input.isKeyPressed(Input.Keys.A) && !isMonsterBallMoving && remainingBalls > 0) {
            shootMonsterBall();
        }
    }

    private void shootMonsterBall() {
        shooterImage.setDrawable(new TextureRegionDrawable(new TextureRegion(shooter2)));
        isMonsterBallMoving = true;
        remainingBalls--;

        float radians = MathUtils.degreesToRadians * aimingAngle;
        monsterBallSpeedX = monsterBallSpeed * MathUtils.cos(radians);
        monsterBallSpeedY = monsterBallSpeed * MathUtils.sin(radians);
    }

    private void updateMonsterBall(float delta) {
        if (isMonsterBallMoving) {
            float x = monsterBallImage.getX() + monsterBallSpeedX * delta;
            float y = monsterBallImage.getY() + monsterBallSpeedY * delta;
            monsterBallImage.setPosition(x, y);

            if (y > this.getHeight() || x < 0 || x > this.getWidth()) {
                resetMonsterBall();
            }
        }
    }

    private void resetMonsterBall() {
        shooterImage.setDrawable(new TextureRegionDrawable(new TextureRegion(shooter1)));
        isMonsterBallMoving = false;
        monsterBallImage.setPosition(monsterBallInitialX, monsterBallInitialY);
        aimingAngle = 90;
    }

    private void checkCollisions() {
        if (!roundClear && isCollision(currentMonster, monsterBallImage)) {
            handleMonsterHit();
        }

        if (remainingBalls == 0 && !roundClear && !isMonsterBallMoving) {
            handleRoundFail();
        }
    }

    private void handleMonsterHit() {
        roundClear = true;
        currentRound++;
        monsterBallImage.setPosition(monsterBallInitialX, monsterBallInitialY);
        nextRound();
    }

    private void handleRoundFail() {
        fail.setVisible(true);
        roundFail = true;
    }

    private boolean isCollision(Image img1, Image img2) {
        Rectangle rect1 = new Rectangle(img1.getX(), img1.getY(), img1.getWidth(), img1.getHeight());
        Rectangle rect2 = new Rectangle(img2.getX(), img2.getY(), img2.getWidth(), img2.getHeight());
        return rect1.overlaps(rect2);
    }

    private void notifyMissionComplete() {
        if (completeCallback != null && missionComplete) {
            completeCallback.onMissionComplete();
        }
    }

    private void GameInitialization() {
        shooterImage.setDrawable(new TextureRegionDrawable(new TextureRegion(shooter1)));
        currentRound = 1;
        roundClear = false;
        roundFail = false;
        fail.setVisible(false);
        failStateTime=0f;
        remainingBalls = 2;
        currentMonsterMoving = false;
        missionComplete = false;

        monsterBallImage.setPosition(monsterBallInitialX, monsterBallInitialY);
        initializePositions();

        if (currentMonster != null) {
            currentMonster.remove();
        }

        nextRound();
    }

    private void changeStageCard() {
        TextureRegionDrawable drawable = stage1Drawable;
        switch(currentRound) {
            case 1: drawable = stage1Drawable; break;
            case 2: drawable = stage2Drawable; break;
            case 3: drawable = stage3Drawable; break;
            case 4: drawable = stage4Drawable; break;
        }
        card.setDrawable(drawable);
    }

    @Override
    public void dispose() {
        aim.dispose();
        closeButtonTexture.dispose();
        closeButtonTextureHover.dispose();
        field.dispose();
        border.dispose();
        shooter1.dispose();
        shooter2.dispose();
        monsters.dispose();
        successAtals.dispose();
        failAtals.dispose();
        roundsAtlas.dispose();
    }
}
