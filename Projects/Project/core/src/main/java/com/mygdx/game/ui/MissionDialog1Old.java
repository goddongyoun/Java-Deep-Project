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
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.util.FontManager;

public class MissionDialog1Old extends Dialog {
    private Image currentMonster;  // 현재 표시된 몬스터 이미지
    private float currentMonsterSpeedX = 0f;
    private boolean currentMonsterMoving = false;
    private int currentRound = 1;  // 현재 라운드 (1부터 시작)

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
    private float aimingAngleSpeed = 1.0f; // 각도 변경 속도 (조정 가능)
    private float monsterBallSpeed = 500; // 몬스터볼 속도
    private boolean isNewRound = true;
    private int remainingBalls = 2;  // 총 5개의 몬스터볼 및 남은 몬스터볼 갯수
    private float stateTime = 0f; //애니메이션이 0f->처음부터 시작, 0.3f->0.3초 이후부터 시작

    private TextureAtlas successAtals = new TextureAtlas("publicImages/successes.atlas");
    private TextureRegionDrawable successDrawable;
    private Array<TextureRegion> successArray = new Array<>();
    private Animation<TextureRegion> successAnimation;
    private Image success = new Image();
    private float successStateTime = 0f; //애니메이션이 0f->처음부터 시작, 0.3f->0.3초 이후부터 시작

    private TextureAtlas failAtals = new TextureAtlas("publicImages/failes.atlas");
    private TextureRegionDrawable failDrawable;
    private Array<TextureRegion> failArray = new Array<>();
    private Animation<TextureRegion> failAnimation;
    private Image fail;
    private float failStateTime = 0f;

    private boolean roundClear = false;
    private boolean roundFail = false;  // 라운드 실패 여부

    Texture aim = new Texture(Gdx.files.internal("images/aim.png"));
    Texture closeButtonTexture = new Texture(Gdx.files.internal("images/mission_button1.png"));
    Texture closeButtonTextureHover = new Texture(Gdx.files.internal("images/mission_button2.png"));
    Texture field = new Texture(Gdx.files.internal("images/mission-field.png"));
    Texture border = new Texture(Gdx.files.internal("images/mission_box.png"));
    Texture shooter1 = new Texture(Gdx.files.internal("images/mssn1Shooter1.png"));
    Texture shooter2 = new Texture(Gdx.files.internal("images/mssn1Shooter2.png"));
    TextureAtlas monsters = new TextureAtlas(Gdx.files.internal("images/m1monsters.atlas")); //아틀라스 이미지 불러오기
    Array<TextureRegion> monsterAArray;
    private Animation<TextureRegion> monsterAAnimation;
    TextureRegionDrawable monsterADrawable;

    private TextureAtlas roundsAtlas = new  TextureAtlas("publicImages/rounds.atlas");
    private TextureRegionDrawable stage1Drawable = new TextureRegionDrawable(roundsAtlas.findRegion("mission_round1"));
    private TextureRegionDrawable stage2Drawable = new TextureRegionDrawable(roundsAtlas.findRegion("mission_round2"));
    private TextureRegionDrawable stage3Drawable = new TextureRegionDrawable(roundsAtlas.findRegion("mission_round3"));
    private TextureRegionDrawable stage4Drawable = new TextureRegionDrawable(roundsAtlas.findRegion("mission_round4"));
    private Image card;

    private Stage stage; // Stage 참조를 멤버 변수로 추가

    private MissionDialog.MissionCompleteCallback completeCallback;

    public MissionDialog1Old(String title, Skin skin, Stage stage) {
        super(title, skin);
        this.stage = stage;

        // 팝업 창의 배경과 크기 설정
        this.setModal(true);
        this.setMovable(true);
        this.setResizable(false);

        // ESC 키를 눌렀을 때 닫기 버튼 동작을 실행
        this.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                System.out.println(1);
                if (keycode == Input.Keys.ESCAPE) {
                    System.out.println(2);
                    MissionDialog1Old.this.hide(); // 팝업 창 닫기
                    System.out.println(3);
                    return true; // 키 입력 처리됨을 알림
                }
                System.out.println(4);
                return super.keyDown(event, keycode);
            }
        });

        for(int i=1; i <= 6 ; i++){
            successArray.add(successAtals.findRegion("mission_success" + i));
        }
        successAnimation = new Animation<TextureRegion>(0.15f, successArray,Animation.PlayMode.NORMAL);
        successDrawable = new TextureRegionDrawable(successAnimation.getKeyFrame(0));
        success = new Image(successDrawable);

        for(int i=1; i <= 6 ; i++){
            failArray.add(failAtals.findRegion("mission_false" + i));
        }
        failAnimation = new Animation<TextureRegion>(0.15f,failArray,Animation.PlayMode.NORMAL);
        failDrawable = new TextureRegionDrawable(failAnimation.getKeyFrame(0));
        fail = new Image(failDrawable);

        // 레이아웃 설정
        Table contentTable = getContentTable();

        // 몬스터볼 이미지 로드
        Texture monsterBall = new Texture(Gdx.files.internal("images/ball.png"));
        monsterBallImage = new Image(monsterBall);
        monsterBallImage.setSize(50, 50);

        // 플레이어 이미지
        shooterImage = new Image(shooter1);
        shooterImage.setSize(50, 50);

        //테두리 창 이미지
        borderImage = new Image(border);
        borderImage.setSize(640,360);

        //배경 이미지
        fieldImage = new Image(field);
        //NinePatch란? 이미지를 9개의 부분으로 나누어 테두리와 안쪽 영역을 확장하는 방식. 테두리의 크기가 변경에 대응하기 위함
        NinePatch fieldPatch = new NinePatch(field, 10, 10, 10, 10);
        NinePatchDrawable fieldDrawable = new NinePatchDrawable(fieldPatch); //Drawable은 LibGdx에서 이미지를 화면에 그릴 수 있는 객체
//        fieldImage.setSize(640,360);

        //닫기 버튼
        closeButtonImage = new Image(closeButtonTexture);
        closeButtonImage.setSize(50,50);
        // 닫기 버튼
        closeButton = new TextButton("", skin);
        Drawable closeButtonDrawable = new TextureRegionDrawable(new TextureRegion(closeButtonTexture));
        Drawable closeButtonHoverDrawable = new TextureRegionDrawable(new TextureRegion(closeButtonTextureHover));
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = closeButtonDrawable;
        buttonStyle.down = closeButtonHoverDrawable;
        buttonStyle.over = closeButtonHoverDrawable;
        buttonStyle.font = FontManager.getInstance().getFont(16);
        //스타일은 up,down,font 이 3개는 필수로 초기화되어있어야 함
        // 설정한 스타일들을 적용
        closeButton.setStyle(buttonStyle);

        // 버튼 클릭 이벤트 처리
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MissionDialog1Old.this.hide(); // 팝업 창 닫기
            }
        });

        //조준선
        aimImage = new Image(aim);
        aimImage.setSize(150,150);

        card = new Image(stage1Drawable);

        contentTable.add(fieldImage).width(640).height(365).expand().fill().pad(30); //필드 이미지를 배경으로 설정
        contentTable.add(monsterBallImage).width(30).height(30).expand().fill().pad(10);
        contentTable.add(shooterImage).width(80).height(80).expand().fill().pad(10);
        contentTable.add(borderImage).width(640).height(365).expand().fill().pad(10); //테두리 적용
        contentTable.add(closeButton).width(32).height(32).expand().fill().pad(10);
        contentTable.add(aimImage).width(150).height(150).expand().fill().pad(10);
        //expand(): 셀이 가능한 공간을 확장해서 차지하도록 설정합니다. 셀 자체는 크기가 늘어나지만, 그 안의 위젯은 원래 크기를 유지합니다.
        //fill(): 셀의 크기가 확장된 후, 위젯이 그 확장된 셀을 채우도록 설정합니다. 즉, 셀의 크기에 맞춰 위젯의 크기도 늘어납니다.
        contentTable.add(card).width(250).height(80).expand().fill();
        card.setVisible(false);
        contentTable.add(fail).width(250).height(100).expand().fill();
        fail.setVisible(false);
        contentTable.add(success).width(250).height(100).expand().fill();
        success.setVisible(false);

        // 크기를 명시적으로 설정하여 다이얼로그가 팝업 크기를 따르도록 함
        this.getCell(contentTable).width(640).height(360);
        // 미션 클래스 자체의 배경을 제거
        this.setBackground((Drawable) null);
        // drawable : 이미지나 그래픽 개체를 그릴 수 있는 인터페이스 즉 화면에 무언가를 그릴 수 있는 객체

        // 스테이지에 다이얼로그 추가
        stage.addActor(this);

        // 초기 몬스터 설정 (파이리)
        nextRound();
    }

    public void setMissionCompleteCallback(MissionDialog.MissionCompleteCallback callback) {
        this.completeCallback = callback;
    }

    // 팝업창을 띄우는 메서드
    public void showMission(Stage stage) {
        this.setSize(640, 360);  // 팝업창 크기를 640x360으로 설정

        GameInitialization(); // 게임 초기화

        // 레이아웃 업데이트
        this.invalidate();
        this.layout();

        // 몬스터볼 초기 위치 설정 (화면 아래 정중앙)
        monsterBallInitialX = (this.getWidth() - monsterBallImage.getWidth()) / 2;
        monsterBallInitialY = 25;
        monsterBallImage.setPosition(monsterBallInitialX, monsterBallInitialY);

        // 조준선 초기 위치 설정
        aimInitialX = (this.getWidth() - aimImage.getWidth())/2;
        aimInitialY = 15;
        aimImage.setPosition(aimInitialX,aimInitialY);

        // 플레이어 초기 위치 설정 (화면 아래 정중앙)
        shooterInitialX = ((this.getWidth() - shooterImage.getWidth()) / 2)-(shooterImage.getWidth() / 2.5f);
        shooterInitialY = 15;
        shooterImage.setPosition(shooterInitialX, shooterInitialY);

        //닫기 버튼 위치 설정
        closeButton.setPosition(this.getWidth()-closeButton.getWidth(),this.getHeight()-(closeButton.getHeight()+5));

        //테두리 위치 설정
        borderImage.setPosition(0,-5);
        fieldImage.setPosition(0,-5);

        // 팝업 창을 중앙에 배치
        this.setPosition(
            (stage.getWidth() - this.getWidth()) / 2,
            (stage.getHeight() - this.getHeight()) / 2
        );

        this.show(stage);
    }

    private void nextRound() {
        // 테이블에서 이전 몬스터만 삭제하고 새로운 몬스터 추가
        Table contentTable = getContentTable();

        changeStageCard();

        if(currentRound!=5) {
            card.setVisible(true);
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    card.setVisible(false);
                }
            }, 1f);
        }

//        this.invalidate();
//        this.layout();

        shooterImage.setDrawable(new TextureRegionDrawable(new TextureRegion(shooter1))); //슈터 이미지 초기화

        // 현재 몬스터 제거
        if (currentMonster != null) {
            contentTable.removeActor(currentMonster);
            currentMonster.remove();
        }

        // 라운드에 따른 몬스터 이미지 설정
        Texture monsterTexture;
        monsterAArray = new Array<TextureRegion>();

        switch (currentRound) {
            case 1:
                //두 이미지를 찾고 배열에 담기
                monsterAArray.add(monsters.findRegion("mssn1MstrA1"));
                monsterAArray.add(monsters.findRegion("mssn1MstrA2"));

                // 애니메이션 객체 생성
                monsterAAnimation = new Animation<TextureRegion>(0.1f,monsterAArray,Animation.PlayMode.LOOP);
                monsterADrawable = new TextureRegionDrawable(monsterAAnimation.getKeyFrame(0));
                currentMonsterSpeedX = 200f;
                break;
            case 2:
                //두 이미지를 찾고 배열에 담기
                monsterAArray.add(monsters.findRegion("mssn1MstrB1"));
                monsterAArray.add(monsters.findRegion("mssn1MstrB2"));

                // 애니메이션 객체 생성
                monsterAAnimation = new Animation<TextureRegion>(0.1f,monsterAArray,Animation.PlayMode.LOOP);
                monsterADrawable = new TextureRegionDrawable(monsterAAnimation.getKeyFrame(0));
                currentMonsterSpeedX = 300f;
                break;
            case 3:
                //두 이미지를 찾고 배열에 담기
                monsterAArray.add(monsters.findRegion("mssn1MstrC1"));
                monsterAArray.add(monsters.findRegion("mssn1MstrC2"));

                // 애니메이션 객체 생성
                monsterAAnimation = new Animation<TextureRegion>(0.1f,monsterAArray,Animation.PlayMode.LOOP);
                monsterADrawable = new TextureRegionDrawable(monsterAAnimation.getKeyFrame(0));
                currentMonsterSpeedX = 400f;
                break;
            case 4:
                //두 이미지를 찾고 배열에 담기
                monsterAArray.add(monsters.findRegion("mssn1MstrD1"));
                monsterAArray.add(monsters.findRegion("mssn1MstrD2"));

                // 애니메이션 객체 생성
                monsterAAnimation = new Animation<TextureRegion>(0.1f,monsterAArray,Animation.PlayMode.LOOP);
                monsterADrawable = new TextureRegionDrawable(monsterAAnimation.getKeyFrame(0));
                currentMonsterSpeedX = 500f;
                break;
            default:
                success.setVisible(true);
                Gdx.app.log("MissionDialog", "Game Clear! All rounds finished.");
                return;
        }

        // 새로운 몬스터 이미지 생성 및 추가
        currentMonster = new Image(monsterADrawable);
        currentMonster.setSize(91, 91);

        // 몬스터볼 이미지의 회전 중심을 이미지의 중앙으로 설정
        monsterBallImage.setOrigin(monsterBallImage.getWidth() / 2, monsterBallImage.getHeight() / 2);

        // 조준선 회전 중심 설정
        aimImage.setOrigin(aimImage.getWidth() / 2, monsterBallImage.getHeight() / 2);
        aimImage.setRotation(aimingAngle-90);
        contentTable.add(currentMonster).width(91).height(91).expand().fill().pad(10).row();  // 새로운 몬스터 추가

        // roundClear 초기화
        roundClear = false;
        remainingBalls = 2;
        // 새로운 라운드가 시작되었음을 표시
        isNewRound = true;
    }

    public void changeStageCard(){
        switch(currentRound){
            case 1:
                card.setDrawable(stage1Drawable);
                break;
            case 2:
                card.setDrawable(stage2Drawable);
                break;
            case 3:
                card.setDrawable(stage3Drawable);
                break;
            case 4:
                card.setDrawable(stage4Drawable);
                break;
            default:
                break;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        card.setPosition(
            (this.getWidth() - card.getWidth()) / 2,
            (this.getHeight() - card.getHeight()) / 2
        );
        fail.setPosition(
            (this.getWidth() - fail.getWidth()) / 2,
            (this.getHeight() - fail.getHeight()) / 2
        );
        success.setPosition(
            (this.getWidth() - success.getWidth()) / 2,
            (this.getHeight() - success.getHeight()) / 2
        );

        if(failArray!=null && fail.isVisible()) {
            //애니메이션 시간 업데이트
            failStateTime += delta;
            //현재 애니메이션 프레임 가져오기
            TextureRegion currentFrame = failAnimation.getKeyFrame(failStateTime, false);
            ((TextureRegionDrawable) fail.getDrawable()).setRegion(currentFrame);
        }
        if(successArray !=null && success.isVisible()) {
            //애니메이션 시간 업데이트
            successStateTime += delta;
            //현재 애니메이션 프레임 가져오기
            TextureRegion currentFrame = successAnimation.getKeyFrame(successStateTime, false);
            ((TextureRegionDrawable) success.getDrawable()).setRegion(currentFrame);
        }

        // 플레이어 초기 위치 설정 (화면 아래 정중앙)
        shooterInitialX = ((this.getWidth() - shooterImage.getWidth()) / 2)-(shooterImage.getWidth() / 2.5f);
        shooterInitialY = 15;
        shooterImage.setPosition(shooterInitialX, shooterInitialY);

        //닫기 버튼 위치 설정
        closeButton.setPosition(this.getWidth()-closeButton.getWidth(),this.getHeight()-(closeButton.getHeight()+5));

        //테두리 위치 설정
        borderImage.setPosition(0,-5);
        fieldImage.setPosition(0,-5);

        // 조준선 초기 위치 설정
        aimInitialX = (this.getWidth() - aimImage.getWidth())/2;
        aimInitialY = 15;
        aimImage.setPosition(aimInitialX,aimInitialY);

        //애니메이션 시간 업데이트
        stateTime += delta;
        if(monsterAAnimation!=null) {
            //현재 애니메이션 프레임 가져오기
            TextureRegion currentFrame = monsterAAnimation.getKeyFrame(stateTime, true);
            //nextRound메소드에서 초기화한 Image내의 monsterDrawable을 현재 프레임으로 설정
            ((TextureRegionDrawable) currentMonster.getDrawable()).setRegion(currentFrame);
        }

        // 이미 실패한 상태라면 추가적인 처리를 중지
        if (roundFail) {
            return;  // roundFail이 true일 경우 더 이상의 처리 중지
        }

        // 첫 번째 act 호출에서만 초기 위치 설정을 사용하고 그 이후는 움직임 적용
        if (isNewRound) {
            isNewRound = false;  // 새로운 라운드가 아니라는 표시
            float initialX = MathUtils.random(0, this.getWidth() - currentMonster.getWidth());
            currentMonster.setPosition(initialX, 260);  // Y 좌표는 고정
            Gdx.app.log("MissionDialog", "x좌표 : " + initialX);
            return;  // 첫 번째 프레임에서 초기 위치만 적용하고 종료
        }

        // 몬스터 이미지 움직임
        float x = currentMonster.getX();
        float y = 230;

        // 몬스터 좌우로 움직임 설정
        if (currentMonsterMoving) {
            x += currentMonsterSpeedX * delta;
            // 오른쪽으로 이동 중일 때, 화면 끝 또는 랜덤 확률로 방향 전환
            if (x > (this.getWidth()-70) - currentMonster.getWidth() || Math.random() < 0.006) {
                currentMonsterMoving = false; // 왼쪽 방향으로 전환
            }
        } else {
            x -= currentMonsterSpeedX * delta;
            // 왼쪽으로 이동 중일 때, 화면 끝 또는 랜덤 확률로 방향 전환
            if (x < 60 || Math.random() < 0.006) {
                currentMonsterMoving = true; // 오른쪽 방향으로 전환
            }
        }
        currentMonster.setPosition(x, y);

        aimImage.setRotation(aimingAngle - 90);

        if(!card.isVisible() && currentRound != 5) {
            // 키 입력 및 조준 방향 설정
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                aimingAngle += aimingAngleSpeed;  // 왼쪽으로 조준 각도 증가
                if (aimingAngle > 140) aimingAngle = 140;  // 최대 각도 제한
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                aimingAngle -= aimingAngleSpeed;  // 오른쪽으로 조준 각도 감소
                if (aimingAngle < 40) aimingAngle = 40;  // 최소 각도 제한
            }

            // A 키가 눌리면 몬스터볼 발사
            if (Gdx.input.isKeyPressed(Input.Keys.A) && !isMonsterBallMoving && remainingBalls > 0) {
                shooterImage.setDrawable(new TextureRegionDrawable(new TextureRegion(shooter2)));
                isMonsterBallMoving = true;
                remainingBalls--;  // 몬스터볼 갯수 감소
                Gdx.app.log("MissionDialog", "Monster balls left: " + remainingBalls);

                // 각도에 따라 몬스터볼의 x, y 속도를 설정 (90도 보정)
                float radians = MathUtils.degreesToRadians * aimingAngle;
                monsterBallSpeedX = monsterBallSpeed * MathUtils.cos(radians);
                monsterBallSpeedY = monsterBallSpeed * MathUtils.sin(radians);
            }
        }

        // 몬스터볼이 움직일 때 x, y 좌표 업데이트
        if (isMonsterBallMoving) {
            float monsterBallX = monsterBallImage.getX() + monsterBallSpeedX * delta;
            float monsterBallY = monsterBallImage.getY() + monsterBallSpeedY * delta;
            monsterBallImage.setPosition(monsterBallX, monsterBallY);

            // 화면 밖으로 나가면 위치 초기화
            if (monsterBallY > this.getHeight() || monsterBallX < 0 || monsterBallX > this.getWidth()) {
                shooterImage.setDrawable(new TextureRegionDrawable(new TextureRegion(shooter1)));
                isMonsterBallMoving = false;
                monsterBallImage.setPosition(monsterBallInitialX, monsterBallInitialY);  // 초기 위치로 되돌림
                aimingAngle = 90; // 각도를 초기화 (필요시)
            }
        }

        // 현재 라운드의 몬스터와 몬스터볼이 닿았는지 확인 (충돌 감지)
        if (!roundClear && isCollision(currentMonster, monsterBallImage)) {
            roundClear = true;
            Gdx.app.log("MissionDialog", "Round " + currentRound + " Clear!");
            currentRound++;  // 다음 라운드로 이동
            monsterBallImage.setPosition(monsterBallInitialX, monsterBallInitialY); // 초기 위치로
            nextRound();  // 다음 라운드 호출
        }

        // 남은 몬스터볼이 없고, 라운드 클리어가 안되어 있으며, 몬스터볼이 더이상 움직이지 않는 경우(마지막으로 던진 몬스터볼이 끝까지 날라갔는지 체크) 라운드 실패로 처리
        if (remainingBalls == 0 && !roundClear && !isMonsterBallMoving) {
            fail.setVisible(true);
            roundFail = true;  // 몬스터볼이 모두 소진된 상태에서 충돌이 없으면 실패로 처리
            Gdx.app.log("MissionDialog", "Game Over!");  // 게임 실패 메시지 출력
        }
    }

    // 몬스터와 몬스터볼의 충돌을 감지하는 메서드
    private boolean isCollision(Image img1, Image img2) {
        Rectangle rect1 = new Rectangle(img1.getX(), img1.getY(), img1.getWidth(), img1.getHeight());
        Rectangle rect2 = new Rectangle(img2.getX(), img2.getY(), img2.getWidth(), img2.getHeight());
        return rect1.overlaps(rect2);
    }

    // 게임을 다시 시작하는 메서드 (초기화)
    private void GameInitialization() {
        shooterImage.setDrawable(new TextureRegionDrawable(new TextureRegion(shooter1))); //슈터 이미지 초기화
        currentRound = 1;  // 라운드를 1로 초기화
        roundClear = false;  // 라운드 클리어 상태 초기화
        roundFail = false;  // 라운드 실패 상태 초기화
        remainingBalls = 2;  // 남은 몬스터볼 갯수 초기화
        currentMonsterMoving = false;
        monsterBallImage.setPosition(monsterBallInitialX, monsterBallInitialY);
        // 플레이어 초기 위치 설정 (화면 아래 정중앙)
        shooterInitialX = ((this.getWidth() - shooterImage.getWidth()) / 2)-(shooterImage.getWidth() / 2.5f);
        shooterInitialY = 15;
        shooterImage.setPosition(shooterInitialX, shooterInitialY);

        //닫기 버튼 위치 설정
        closeButton.setPosition(this.getWidth()-closeButton.getWidth(),this.getHeight()-(closeButton.getHeight()+5));

        //테두리 위치 설정
        borderImage.setPosition(0,-5);
        fieldImage.setPosition(0,-5);

        // 조준선 초기 위치 설정
        aimInitialX = (this.getWidth() - aimImage.getWidth())/2;
        aimInitialY = 15;
        aimImage.setPosition(aimInitialX,aimInitialY);
        if (currentMonster != null) {
            currentMonster.remove();  // 현재 몬스터 제거
        }

        nextRound();  // 첫 번째 라운드로 다시 시작
    }
}
