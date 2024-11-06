package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

public class MissionDialog4 extends Dialog {
    Stage stage;
    Table contentTable = getContentTable();

    private int dialogSize = 620;

    //땅
    private Texture landTexture = new Texture(Gdx.files.internal("mission4/mission4Land.png"));
    private Image land = new Image(landTexture);

    //언덕
    private Texture hillTexture = new Texture(Gdx.files.internal("mission4/mission4Hill.png"));
    private Image hill = new Image(hillTexture);

    //하늘
    private Texture skyTexture = new Texture(Gdx.files.internal("mission4/mission4Sky.png"));
    private Image sky = new Image(skyTexture);

    //건슈터
    private TextureAtlas gunShooterAtlas = new TextureAtlas(Gdx.files.internal("mission4/gunShooter.atlas"));
    private Array<TextureRegion> gunShooterRegion = new Array<>();
    private Animation<TextureRegion> gunShooterAnime;
    private TextureRegionDrawable gunShooterDrawable;
    private Image gunShooter;
    private float gunShooterStateTime = 0f;
    private int gunShooterSize = 200;

    //피격 효과
    private TextureAtlas hitAtlas = new TextureAtlas(Gdx.files.internal("mission4/fire.atlas"));
    private Array<TextureRegion> hitRegion = new Array<>();
    private Animation<TextureRegion> hitAnime;
    private TextureRegionDrawable hitDrawable;
    private Image hit;
    private float hitStateTime = 0f;
    private int hitSize = 50;

    //롤링 마린
    private Texture rollingMarineTexture = new Texture(Gdx.files.internal("mission4/RollingMarine.png"));
    private Image rollingMarine = new Image(rollingMarineTexture);
    private int rollingMarineSize = 80;
    private float rollingMarineSpeed = 80.0f;  // 이동 속도
    private float rollingMarineDirectionX = 1.0f;  // x 방향 (1: 오른쪽, -1: 왼쪽)
    private float rollingMarineDirectionY = 1.0f;  // y 방향 (1: 위쪽, -1: 아래쪽)

    private float clickedX;
    private float clickedY;
    private boolean isShooting = false;
    private Random random = new Random();

    public MissionDialog4(String title, Skin skin, Stage stage) {
        super(title, skin);
        this.stage = stage;

        // ESC 키를 눌렀을 때 닫기 버튼 동작을 실행
        this.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    MissionDialog4.this.hide(); // 팝업 창 닫기
                    return true; // 키 입력 처리됨을 알림
                }
                return super.keyDown(event, keycode);
            }
        });

        //건슈터
        for (int i = 0; i<6;i++){
            gunShooterRegion.add(gunShooterAtlas.findRegion("ShooterMarine"+(i+1)));
        }
        gunShooterAnime = new Animation<TextureRegion>(0.08f,gunShooterRegion,Animation.PlayMode.NORMAL);
        gunShooterDrawable = new TextureRegionDrawable(gunShooterAnime.getKeyFrame(0));
        gunShooter = new Image(gunShooterDrawable);

        //피격 효과
        for (int i = 0; i<6;i++){
            hitRegion.add(hitAtlas.findRegion("fire"+(i+1)));
        }
        hitAnime = new Animation<TextureRegion>(0.08f,hitRegion,Animation.PlayMode.NORMAL);
        hitDrawable = new TextureRegionDrawable(hitAnime.getKeyFrame(0));
        hit = new Image(hitDrawable);

        //요소 테이블에 추가
        contentTable.add(sky).width(dialogSize).height(dialogSize).expand().fill();

        //하늘에 떠다니는 요소는 이 사이로 추가 ~~
        contentTable.add(rollingMarine).width(rollingMarineSize).height(rollingMarineSize).expand().fill();
        // ~~ 여기 까지

        contentTable.add(land).width(dialogSize).height(dialogSize).expand().fill();

        contentTable.add(hit).width(hitSize).height(hitSize).expand().fill();
        hit.setVisible(false);

        contentTable.add(hill).width(dialogSize).height(dialogSize).expand().fill();

        contentTable.add(gunShooter).width(gunShooterSize).height(gunShooterSize).expand().fill();

        this.getCell(contentTable).width(dialogSize).height(dialogSize).expand().fill();
        // 미션 클래스 자체의 배경을 제거
        this.setBackground((Drawable) null);
        stage.addActor(this);
    }

    public void showMission(Stage stage){
        this.setSize(dialogSize, dialogSize);

        // 레이아웃 업데이트
        this.invalidate();
        this.layout();

        // 팝업 창을 중앙에 배치
        this.setPosition(
            (stage.getWidth() - this.getWidth()) / 2,
            (stage.getHeight() - this.getHeight()) / 2
        );

        contentTable.addListener(new ClickListener(Input.Buttons.LEFT) { // 오른쪽 버튼 클릭 리스너 추가
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("오른쪽 클릭 됨");
                clickedX = x;
                clickedY = y;
                gunShooterStateTime = 0f;  // 애니메이션 시작을 위해 상태 시간 초기화
                hitStateTime = 0f;
                isShooting = true;        // 애니메이션이 시작되었음을 표시
                hit.setVisible(true);
            }
        });
    }

    public void act(float delta) {
        super.act(delta);

        sky.setPosition(
            (this.getWidth() - sky.getWidth()) / 2,
            (this.getHeight() - sky.getHeight()) / 2
        );
        land.setPosition(
            (this.getWidth() - land.getWidth()) / 2,
            (this.getHeight() - land.getHeight()) / 2
        );
        hill.setPosition(
            (this.getWidth() - hill.getWidth()) / 2,
            (this.getHeight() - hill.getHeight()) / 2
        );
        gunShooter.setPosition(440,60);

        //하늘 떠다니는 요소들 포지션 + y축 최저 값은 50으로
        // 현재 위치 가져오기
        float rollingMarineX = rollingMarine.getX();
        float rollingMarineY = rollingMarine.getY();

        // 새로운 위치 계산 (30도 대각선 방향 이동)
        rollingMarineX += rollingMarineSpeed * rollingMarineDirectionX * delta;
        rollingMarineY += rollingMarineSpeed * rollingMarineDirectionY * delta;

        // contentTable의 크기 가져오기
        float rollingMarineContentTableWidth = contentTable.getWidth();
        float rollingMarineContentTableHeight = contentTable.getHeight();

        // rollingMarine의 크기 고려한 충돌 감지
        float rollingMarineWidth = rollingMarine.getWidth();
        float rollingMarineHeight = rollingMarine.getHeight();

        // 오른쪽 벽에 부딪히는 경우
        if (rollingMarineX + rollingMarineWidth / 2 > rollingMarineContentTableWidth - 50) {
            rollingMarineDirectionX = -1.0f;  // 왼쪽으로 방향 전환
            rollingMarineDirectionY = (float) Math.tan(Math.toRadians(random.nextInt(21) + 20));   // 위쪽으로 무작위 각도 변경
            rollingMarineX = rollingMarineContentTableWidth - 50 - rollingMarineWidth / 2;  // 경계값으로 위치 조정
        }
        // 왼쪽 벽에 부딪히는 경우
        else if (rollingMarineX - rollingMarineWidth / 2 < 0) {
            rollingMarineDirectionX = 1.0f;  // 오른쪽으로 방향 전환
            rollingMarineDirectionY = -(float) Math.tan(Math.toRadians(random.nextInt(21) + 20)); // 아래쪽으로 무작위 각도 변경
            rollingMarineX = rollingMarineWidth / 2;  // 경계값으로 위치 조정
        }

        // 위쪽 벽에 부딪히는 경우
        if (rollingMarineY + rollingMarineHeight / 2 > rollingMarineContentTableHeight - 50) {
            rollingMarineDirectionX = -(float) Math.tan(Math.toRadians(random.nextInt(21) + 20)); // 왼쪽으로 무작위 각도 변경
            rollingMarineDirectionY = -1.0f; // 아래쪽으로 방향 전환
            rollingMarineY = rollingMarineContentTableHeight - 50 - rollingMarineHeight / 2;  // 경계값으로 위치 조정
        }
        // 아래쪽 벽에 부딪히는 경우
        else if (rollingMarineY - rollingMarineHeight / 2 < 0) {
            rollingMarineDirectionX = (float) Math.tan(Math.toRadians(random.nextInt(21) + 20));  // 오른쪽으로 무작위 각도 변경
            rollingMarineDirectionY = 1.0f;  // 위쪽으로 방향 전환
            rollingMarineY = rollingMarineHeight / 2;  // 경계값으로 위치 조정
        }

        // 새로운 위치 설정
        rollingMarine.setPosition(rollingMarineX, rollingMarineY);

        // 기존의 회전 각도에 delta에 따른 각도 추가
        float currentRotation = rollingMarine.getRotation(); // 현재 각도 가져오기
        rollingMarine.setOrigin(rollingMarine.getWidth()/2,rollingMarine.getHeight()/2);
        rollingMarine.setRotation(currentRotation - 45 * delta);

        if (isShooting) {
            //애니메이션 시간 업데이트
            gunShooterStateTime += delta;
            hitStateTime += delta;

            //클릭한 위치로 피격효과 설정
            hit.setPosition(clickedX - hit.getWidth() / 2, clickedY - hit.getHeight() / 2); //마우스 클릭한 위치에 피격효과 생성

            //현재 애니메이션 프레임 가져오기
            TextureRegion currentFrame = gunShooterAnime.getKeyFrame(gunShooterStateTime, false);
            ((TextureRegionDrawable) gunShooter.getDrawable()).setRegion(currentFrame);

            TextureRegion currentFrame2 = hitAnime.getKeyFrame(hitStateTime, false);
            ((TextureRegionDrawable) hit.getDrawable()).setRegion(currentFrame2);

            // 애니메이션이 끝났는지 확인
            if (gunShooterAnime.isAnimationFinished(gunShooterStateTime) &&
                hitAnime.isAnimationFinished(hitStateTime)) {
                hit.setVisible(false);
                isShooting = false; // 애니메이션이 끝나면 isShooting을 false로 설정
            }
        }
    }

    public void fire(float delta){

    }
}
