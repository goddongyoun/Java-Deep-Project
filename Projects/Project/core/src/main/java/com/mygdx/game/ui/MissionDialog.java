package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;

public class MissionDialog extends Dialog {
    private Image currentMonster;  // 현재 표시된 몬스터 이미지
    private float currentMonsterSpeedX = 0f;
    private boolean currentMonsterMoving = false;
    private int currentRound = 1;  // 현재 라운드 (1부터 시작)

    private Image monsterBallImage;
    private float monsterBallSpeedX;
    private float monsterBallSpeedY;
    private boolean isMonsterBallMoving = false;
    private float monsterBallInitialX;
    private float monsterBallInitialY;
    private float aimingAngle = 90;
    private float aimingAngleSpeed = 1.0f; // 각도 변경 속도 (조정 가능)
    private float monsterBallSpeed = 500; // 몬스터볼 속도
    private boolean isNewRound = true;
    private int remainingBalls = 2;  // 총 5개의 몬스터볼 및 남은 몬스터볼 갯수
    private boolean isHitMonster = false;

    private boolean roundClear = false;
    private boolean roundFail = false;  // 라운드 실패 여부

    private Stage stage; // Stage 참조를 멤버 변수로 추가

    public MissionDialog(String title, Skin skin, Stage stage) {
        super(title, skin);
        this.stage = stage;

        // 팝업 창의 배경과 크기 설정
        this.setModal(true);
        this.setMovable(true);
        this.setResizable(false);

        // 미션 설명 텍스트 추가
        Label missionLabel = new Label("Complete the following mission:", skin);

        // 미션 내용 추가
        Label missionDetails = new Label("Throw the monster ball to defeat the monster", skin);

        // 확인 버튼 추가
        TextButton closeButton = new TextButton("Close", skin);

        // 버튼 클릭 이벤트 처리
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MissionDialog.this.hide(); // 팝업 창 닫기
            }
        });

        // ESC 키를 눌렀을 때 닫기 버튼 동작을 실행
        this.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    MissionDialog.this.hide(); // 팝업 창 닫기
                    return true; // 키 입력 처리됨을 알림
                }
                return super.keyDown(event, keycode);
            }
        });

        // 레이아웃 설정
        Table contentTable = getContentTable();

        // 몬스터볼 이미지 로드
        Texture monsterBall = new Texture(Gdx.files.internal("images/ball.jpg"));
        monsterBallImage = new Image(monsterBall);
        monsterBallImage.setSize(50, 50);


        // 팝업 내부 UI 요소들이 팝업 크기에 맞게 확장되도록 설정
        contentTable.add(monsterBallImage).width(50).height(50).expand().fill().pad(10);
        contentTable.add(closeButton).expandX().pad(10);  // X축으로 확장되도록 설정

        button(closeButton); // 버튼 추가

        // 크기를 명시적으로 설정하여 다이얼로그가 팝업 크기를 따르도록 함
        this.getCell(contentTable).width(640).height(360);

        // 스테이지에 다이얼로그 추가
        stage.addActor(this);

        // 초기 몬스터 설정 (파이리)
        nextRound();
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
        monsterBallInitialY = 0;
        setBallPosition(monsterBallImage,monsterBallInitialX,monsterBallInitialY);

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
        // 현재 몬스터 제거
        if (currentMonster != null) {
            contentTable.removeActor(currentMonster);
            currentMonster.remove();
        }

        // 라운드에 따른 몬스터 이미지 설정
        Texture monsterTexture;
        switch (currentRound) {
            case 1:
                monsterTexture = new Texture(Gdx.files.internal("images/Pa2ri.jpg"));  // 파이리
                currentMonsterSpeedX = 200f;
                break;
            case 2:
                monsterTexture = new Texture(Gdx.files.internal("images/ggobugi.jpg"));  // 꼬부기
                currentMonsterSpeedX = 300f;
                break;
            case 3:
                monsterTexture = new Texture(Gdx.files.internal("images/StrangeSeed.jpg"));  // 이상해씨
                currentMonsterSpeedX = 400f;
                break;
            case 4:
                monsterTexture = new Texture(Gdx.files.internal("images/pikachu.png"));  // 피카츄
                currentMonsterSpeedX = 500f;
                break;
            default:
                Gdx.app.log("MissionDialog", "Game Clear! All rounds finished.");
                return;
        }

        // 새로운 몬스터 이미지 생성 및 추가
        currentMonster = new Image(monsterTexture);
        currentMonster.setSize(70, 70);
        // 몬스터볼 이미지의 회전 중심을 이미지의 중앙으로 설정
        monsterBallImage.setOrigin(monsterBallImage.getWidth() / 2, monsterBallImage.getHeight() / 2);
        monsterBallImage.setRotation(aimingAngle);
        contentTable.add(currentMonster).width(70).height(70).expand().fill().pad(10).row();  // 새로운 몬스터 추가

        // roundClear 초기화
        roundClear = false;
        remainingBalls = 2;
        // 새로운 라운드가 시작되었음을 표시
        isNewRound = true;
    }

    @Override
    public void act(float delta) {
        //이 if문은 몬스터를 맞췄을 시 모든 동작을 1초 딜레이 주고 넘어가기 위함임
        if(!isHitMonster) {
            super.act(delta);

            // 이미 실패한 상태라면 추가적인 처리를 중지
            if (roundFail) {
                return;  // roundFail이 true일 경우 더 이상의 처리 중지
            }

            // 첫 번째 act 호출에서만 초기 위치 설정을 사용하고 그 이후는 움직임 적용
            if (isNewRound) {
                isNewRound = false;  // 새로운 라운드가 아니라는 표시
                float initialX = MathUtils.random(0, this.getWidth() - currentMonster.getWidth());
                currentMonster.setPosition(initialX, 260);  // Y 좌표는 고정
                return;  // 첫 번째 프레임에서 초기 위치만 적용하고 종료
            }

            // 몬스터 이미지 움직임
            float x = currentMonster.getX();
            float y = 260;

            // 몬스터 좌우로 움직임 설정
            if (currentMonsterMoving) {
                x += currentMonsterSpeedX * delta;
                // 오른쪽으로 이동 중일 때, 화면 끝 또는 랜덤 확률로 방향 전환
                if (x > this.getWidth() - currentMonster.getWidth() || Math.random() < 0.006) {
                    currentMonsterMoving = false; // 왼쪽 방향으로 전환
                }
            } else {
                x -= currentMonsterSpeedX * delta;
                // 왼쪽으로 이동 중일 때, 화면 끝 또는 랜덤 확률로 방향 전환
                if (x < 0 || Math.random() < 0.006) {
                    currentMonsterMoving = true; // 오른쪽 방향으로 전환
                }
            }
            currentMonster.setPosition(x, y);

            // 몬스터볼 좌 우 조준
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && !isMonsterBallMoving) {
                aimingAngle += aimingAngleSpeed;  // 왼쪽으로 조준 각도 증가
                if (aimingAngle > 140) aimingAngle = 140;  // 최대 각도 제한
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !isMonsterBallMoving) {
                aimingAngle -= aimingAngleSpeed;  // 오른쪽으로 조준 각도 감소
                if (aimingAngle < 40) aimingAngle = 40;  // 최소 각도 제한
            }
            monsterBallImage.setRotation(aimingAngle - 90);

            // A 키가 눌리면 몬스터볼 발사
            if (Gdx.input.isKeyPressed(Input.Keys.A) && !isMonsterBallMoving && remainingBalls > 0) {
                isMonsterBallMoving = true;
                remainingBalls--;  // 몬스터볼 갯수 감소
                Gdx.app.log("MissionDialog", "Monster balls left: " + remainingBalls);

                // 각도에 따라 몬스터볼의 x, y 속도를 설정 (90도 보정)
                float radians = MathUtils.degreesToRadians * aimingAngle;
                monsterBallSpeedX = monsterBallSpeed * MathUtils.cos(radians);
                monsterBallSpeedY = monsterBallSpeed * MathUtils.sin(radians);
            }

            // 몬스터볼이 움직일 때 x, y 좌표 업데이트
            if (isMonsterBallMoving) {
                float monsterBallX = monsterBallImage.getX() + monsterBallSpeedX * delta;
                float monsterBallY = monsterBallImage.getY() + monsterBallSpeedY * delta;
                setBallPosition(monsterBallImage,monsterBallX,monsterBallY);

                // 화면 밖으로 나가면 위치 초기화
                if (monsterBallY > this.getHeight() || monsterBallX < 0 || monsterBallX > this.getWidth()) {
                    isMonsterBallMoving = false;
                    setBallPosition(monsterBallImage, monsterBallInitialX, monsterBallInitialY); // 초기 위치로 되돌림
                    aimingAngle = 90; // 각도를 초기화 (필요시)
                }
            }

            // 현재 라운드의 몬스터와 몬스터볼이 닿았는지 확인 (충돌 감지)
            if (!roundClear && isCollision(currentMonster, monsterBallImage)) {
                isMonsterBallMoving = false;
                isHitMonster = true;
                setBallPosition(monsterBallImage, monsterBallInitialX, monsterBallInitialY);
                roundClear = true;
                Gdx.app.log("MissionDialog", "Round " + currentRound + " Clear!");
                currentRound++;
                nextRound();

//                // 일정 시간 딜레이 후 다음 라운드로 이동
//                Timer.schedule(new Timer.Task() {
//                    @Override
//                    public void run() {
//                        isHitMonster = false;
//                        currentRound++;  // 다음 라운드로 설정
//                        nextRound();
//                    }
//                }, 1f);  // 1초 딜레이를 준 후 다음 라운드 시작
            }

            // 남은 몬스터볼이 없고, 라운드 클리어가 안되어 있으며, 몬스터볼이 더이상 움직이지 않는 경우(마지막으로 던진 몬스터볼이 끝까지 날라갔는지 체크) 라운드 실패로 처리
            if (remainingBalls == 0 && !roundClear && !isMonsterBallMoving) {
                roundFail = true;  // 몬스터볼이 모두 소진된 상태에서 충돌이 없으면 실패로 처리
                Gdx.app.log("MissionDialog", "Game Over!");  // 게임 실패 메시지 출력
            }
        }
    }

    private void setBallPosition(Image monsterBall, float locationX, float locationY) {
        monsterBall.setPosition(locationX, locationY);
    }

    // 몬스터와 몬스터볼의 충돌을 감지하는 메서드
    private boolean isCollision(Image img1, Image img2) {
        Rectangle rect1 = new Rectangle(img1.getX(), img1.getY(), img1.getWidth(), img1.getHeight());
        Rectangle rect2 = new Rectangle(img2.getX(), img2.getY(), img2.getWidth(), img2.getHeight());
        return rect1.overlaps(rect2);
    }

    // 게임을 다시 시작하는 메서드 (초기화)
    public void GameInitialization() {
        currentRound = 1;  // 라운드를 1로 초기화
        roundClear = false;  // 라운드 클리어 상태 초기화
        roundFail = false;  // 라운드 실패 상태 초기화
        remainingBalls = 2;  // 남은 몬스터볼 갯수 초기화
        aimingAngle = 90;
        currentMonsterMoving = false;
        isMonsterBallMoving = false;
        setBallPosition(monsterBallImage,monsterBallInitialX,monsterBallInitialY);
        monsterBallImage.setRotation(aimingAngle);
        if (currentMonster != null) {
            currentMonster.remove();  // 현재 몬스터 제거
        }

        nextRound();  // 첫 번째 라운드로 다시 시작
    }
}
