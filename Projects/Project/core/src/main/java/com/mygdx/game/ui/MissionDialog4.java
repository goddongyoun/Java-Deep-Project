package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.util.FontManager;

import java.util.Random;
import java.util.TimerTask;

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

    //오버로드
    private Array<Overlord> overlord = new Array<>();
    private int overlordCount = 3;
    private int hitOverlordCount = 0;

    //뮤탈리스크
    private Array<Mutalisk> mutalisk = new Array<>();
    private int mutaliskCount = 5;
    private int hitMutaliskCount = 0;

    //피격 효과
    private TextureAtlas hitAtlas = new TextureAtlas(Gdx.files.internal("mission4/fire.atlas"));
    private Array<TextureRegion> hitRegion = new Array<>();
    private Animation<TextureRegion> hitAnime;
    private TextureRegionDrawable hitDrawable;
    private Image hit;
    private float hitStateTime = 0f;
    private int hitSize = 50;

    //롤링 마린
    private Array<RollingMarine> rollingMarine = new Array<>();
    private int rollingMarineCount = 3;
    private int direction = 1;

    //성공
    private TextureAtlas successAtals = new TextureAtlas("publicImages/successes.atlas");
    private TextureRegionDrawable successDrawable;
    private Array<TextureRegion> successArray = new Array<>();
    private Animation<TextureRegion> successAnimation;
    private Image success = new Image();
    private float successStateTime = 0f; //애니메이션이 0f->처음부터 시작, 0.3f->0.3초 이후부터 시작

    //실패
    private TextureAtlas failAtals = new TextureAtlas("publicImages/failes.atlas");
    private TextureRegionDrawable failDrawable;
    private Array<TextureRegion> failArray = new Array<>();
    private Animation<TextureRegion> failAnimation;
    private Image fail;
    private float failStateTime = 0f; //애니메이션이 0f->처음부터 시작, 0.3f->0.3초 이후부터 시작

    //테두리
    private Image border = new Image(new Texture(Gdx.files.internal("mission/missionBox240240.png")));

    //시작카드
    private Image startCard = new Image(new TextureRegionDrawable(new Texture(Gdx.files.internal("publicImages/start.png"))));

    //닫기버튼
    private TextButton closeButton;
    private Image closeButtonImage;
    Texture closeButtonTexture = new Texture(Gdx.files.internal("images/mission_button1.png"));
    Texture closeButtonTextureHover = new Texture(Gdx.files.internal("images/mission_button2.png"));

    private float clickedX;
    private float clickedY;
    private boolean isShooting = false;
    private boolean gameClear = false;
    private boolean isSuccessed = false;
    private boolean isFailed = false;
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

        //성공카드
        for(int i=1; i <= 6 ; i++){
            successArray.add(successAtals.findRegion("mission_success" + i));
        }
        successAnimation = new Animation<TextureRegion>(0.15f, successArray,Animation.PlayMode.NORMAL);
        successDrawable = new TextureRegionDrawable(successAnimation.getKeyFrame(0));
        success = new Image(successDrawable);

        //실패카드
        for(int i=1; i <= 6 ; i++){
            failArray.add(failAtals.findRegion("mission_false" + i));
        }
        failAnimation = new Animation<TextureRegion>(0.15f,failArray,Animation.PlayMode.NORMAL);
        failDrawable = new TextureRegionDrawable(failAnimation.getKeyFrame(0));
        fail = new Image(failDrawable);

        //닫기 버튼
        closeButtonImage = new Image(closeButtonTexture);
        closeButtonImage.setSize(50,50);
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
                MissionDialog4.this.hide(); // 팝업 창 닫기
            }
        });

        //건슈터
        for (int i = 0; i<6;i++){
            gunShooterRegion.add(gunShooterAtlas.findRegion("ShooterMarine"+(i+1)));
        }
        gunShooterAnime = new Animation<TextureRegion>(0.08f,gunShooterRegion,Animation.PlayMode.NORMAL);
        gunShooterDrawable = new TextureRegionDrawable(gunShooterAnime.getKeyFrame(0));
        gunShooter = new Image(gunShooterDrawable);

        //오버로드
        for (int i=0;i<overlordCount;i++) {
            overlord.add(new Overlord(MathUtils.random(0, dialogSize),MathUtils.random(40, dialogSize)));
        }

        //뮤탈리스크
        for (int i=0;i<mutaliskCount;i++){
            mutalisk.add(new Mutalisk(MathUtils.random(0, dialogSize),MathUtils.random(40, dialogSize)));
        }

        //롤링마린
        for(int i=0;i<rollingMarineCount;i++){
            if(i/2 == 0) direction *= -1;
            rollingMarine.add(new RollingMarine(MathUtils.random(0, dialogSize),MathUtils.random(40, dialogSize),direction));
        }
        System.out.println(rollingMarine.size);

        //피격 효과
        for (int i = 0; i<6;i++){
            hitRegion.add(hitAtlas.findRegion("fire"+(i+1)));
        }
        hitAnime = new Animation<TextureRegion>(0.08f,hitRegion,Animation.PlayMode.NORMAL);
        hitDrawable = new TextureRegionDrawable(hitAnime.getKeyFrame(0));
        hit = new Image(hitDrawable);

        //요소 테이블에 추가
        contentTable.add(sky).width(dialogSize-20).height(dialogSize-20).expand().fill();
        sky.setName("sky");

        //하늘에 떠다니는 요소는 이 사이로 추가 ~~
        for (Overlord overlord : overlord) {
            contentTable.add(overlord.getImage()).width(overlord.getOverlordSize()).height(overlord.getOverlordSize()).expand().fill();
        }
        for (Mutalisk mutalisk : mutalisk) {
            contentTable.add(mutalisk.getImage()).width(mutalisk.getMutaliskSize()).height(mutalisk.getMutaliskSize()).expand().fill();
        }
        for(RollingMarine rollingMarine : rollingMarine){
            contentTable.add(rollingMarine.getImage()).width(rollingMarine.getRollingMarineSize()).height(rollingMarine.getRollingMarineSize()).expand().fill();
        }
        // ~~ 여기 까지

        contentTable.add(land).width(dialogSize).height(dialogSize).expand().fill();
        land.setName("land");

        contentTable.add(hit).width(hitSize).height(hitSize).expand().fill();
        hit.setName("hit");
        hit.setVisible(false);

        contentTable.add(hill).width(dialogSize).height(dialogSize).expand().fill();
        hill.setName("hill");

        contentTable.add(gunShooter).width(gunShooterSize).height(gunShooterSize).expand().fill();
        gunShooter.setName("gunShooter");

        contentTable.add(fail).width(250).height(100).expand().fill();
        fail.setName("fail");
        fail.setVisible(false);

        contentTable.add(success).width(250).height(100).expand().fill();
        success.setName("success");
        success.setVisible(false);

        contentTable.add(startCard).width(250).height(80).expand().fill();
        startCard.setName("startCard");

        contentTable.add(border).width(dialogSize).height(dialogSize).expand().fill();
        border.setName("border");

        contentTable.add(closeButton).width(32).height(32).expand().fill().pad(10);

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

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                startCard.setVisible(false);
                //발사 이벤트 추가
                contentTable.addListener(clickListener);
            }
        },1f);
    }

    public void act(float delta) {
        super.act(delta);

        if(gameClear || isFailed){
            contentTable.removeListener(clickListener);
        }

        //닫기 버튼 위치 설정
        closeButton.setPosition(this.getWidth()-closeButton.getWidth(),this.getHeight()-(closeButton.getHeight()+3));

        border.setPosition(
            (this.getWidth() - border.getWidth()) / 2,
            (this.getHeight() - border.getHeight()) / 2
        );
        startCard.setPosition(
            (this.getWidth() - startCard.getWidth()) / 2,
            (this.getHeight() - startCard.getHeight()) / 2
        );
        fail.setPosition(
            (this.getWidth() - fail.getWidth()) / 2,
            (this.getHeight() - fail.getHeight()) / 2
        );
        success.setPosition(
            (this.getWidth() - success.getWidth()) / 2,
            (this.getHeight() - success.getHeight()) / 2
        );
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
            if (gunShooterAnime.isAnimationFinished(gunShooterStateTime) && hitAnime.isAnimationFinished(hitStateTime)) {
                hit.setVisible(false);
                isShooting = false; // 애니메이션이 끝나면 isShooting을 false로 설정
            }
        }

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

        //몬스터 둘 다 잡으면 게임 클리어
        if(hitMutaliskCount == mutaliskCount && hitOverlordCount == overlordCount){
            gameClear = true;
            success.setVisible(true);
        }

        for(RollingMarine rollingMarine : rollingMarine){
            rollingMarine.moving(delta);
        }

        for (Overlord overlord : overlord) {
            overlord.moving(delta);
        }

        for (Mutalisk mutalisk : mutalisk) {
            mutalisk.moving(delta);
        }
    }

    private ClickListener clickListener = new ClickListener(Input.Buttons.LEFT) {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            // 기존 리스너 코드
            clickedX = x;
            clickedY = y;
            gunShooterStateTime = 0f;  // 애니메이션 시작을 위해 상태 시간 초기화
            hitStateTime = 0f;
            isShooting = true;        // 애니메이션이 시작되었음을 표시
            hit.setVisible(true);
            Array<Actor> actors = contentTable.getChildren();
            for (int i = actors.size - 1; i >= 0; i--) { // 뒤에서부터 탐색하여 투명한 이미지를 무시하고 클릭된 이미지를 찾음
                Actor actor = actors.get(i);
                if (actor instanceof Image) {
                    Image image = (Image) actor;
                    float localX = x - image.getX();
                    float localY = y - image.getY();

                    if (localX >= 0 && localX <= image.getWidth() && localY >= 0 && localY <= image.getHeight()) {
                        TextureRegionDrawable drawable = (TextureRegionDrawable) image.getDrawable();
                        TextureRegion textureRegion = drawable.getRegion();

//                        // 투명도 확인 - 미리 계산된 데이터 사용
//                        if (image.getName().equals("mutalisk") || image.getName().equals("overlord") || image.getName().equals("rollingMarine")) {
//                            TransparentData transparentData = (TransparentData) image.getUserObject();  // 이미지 객체에 미리 계산된 데이터를 저장해 두었음
//                            int textureX = (int) (localX / image.getWidth() * textureRegion.getRegionWidth());
//                            int textureY = (int) (localY / image.getHeight() * textureRegion.getRegionHeight());
//
//                            if (!transparentData.isTransparent(textureX, textureY)) {
//                                // 클릭된 위치가 투명하지 않음
//                                actors.removeIndex(i);
//                                break;
//                            }
//                        }



                        // 투명 여부 확인
                        if (!checkTransparent(textureRegion, image, localX, localY)) {
                            if (image.getName().equals("mutalisk") || image.getName().equals("overlord") || image.getName().equals("rollingMarine")) {
                                if (image.getName().equals("rollingMarine")) {
                                    isFailed = true;
                                    fail.setVisible(true);
                                }else if (image.getName().equals("mutalisk")) {
                                    hitMutaliskCount++;
                                } else if (image.getName().equals("overlord")) {
                                    hitOverlordCount++;
                                }
                                // 투명하지 않다면 이미지 제거
                                actors.removeIndex(i);
                                break;
                            }
                        }
                    }
                }
            }
        }
    };

    private class RollingMarine{
        private Texture rollingMarineTexture = new Texture(Gdx.files.internal("mission4/RollingMarine.png"));
        private Image rollingMarine = new Image(rollingMarineTexture);
        private int rollingMarineSize = 80;
        private int initialAngle = MathUtils.random(0,360);
        private int rotateSpeed = MathUtils.random(40,55);
        private int direction;
        private float rollingMarineSpeed = MathUtils.random(75.0f,80.0f);  // 이동 속도
        private float rollingMarineDirectionX = MathUtils.randomBoolean()?-1.0f:1.0f;  // x 방향 (1: 오른쪽, -1: 왼쪽)
        private float rollingMarineDirectionY = MathUtils.randomBoolean()?-1.0f:1.0f;  // y 방향 (1: 위쪽, -1: 아래쪽)
        float rollingMarineX;
        float rollingMarineY;
        private float impactAngle = 30.0f; // 벽에 부딪힌 각도

        private RollingMarine(float x, float y, int direction) {
            this.direction = direction;
            rollingMarine.setPosition(x, y);
            rollingMarineX = rollingMarine.getX();
            rollingMarineY = rollingMarine.getY();
            System.out.println(x+". "+y+" real position = "+rollingMarine.getX()+","+rollingMarine.getY());
            rollingMarine.setName("rollingMarine");
            rollingMarine.setRotation(initialAngle);
            rollingMarine.setUserObject(this);
        }

        private void moving(float delta) {
            // 새로운 위치 계산
            rollingMarineX += rollingMarineSpeed * rollingMarineDirectionX * delta;
            rollingMarineY += rollingMarineSpeed * rollingMarineDirectionY * delta;

            // 오른쪽 벽에 부딪히는 경우
            if (rollingMarineX + rollingMarineSize / 2 > contentTable.getWidth() - 50) {
                rollingMarineDirectionX = -1.0f;  // X 방향 반전
                rollingMarineX = contentTable.getWidth() - 50 - rollingMarineSize / 2;  // 경계값으로 위치 조정
            }
            // 왼쪽 벽에 부딪히는 경우
            else if (rollingMarineX - rollingMarineSize / 2 < 0) {
                rollingMarineDirectionX = 1.0f;  // X 방향 반전
                rollingMarineX = rollingMarineSize / 2;  // 경계값으로 위치 조정
            }

            // 위쪽 벽에 부딪히는 경우
            if (rollingMarineY + rollingMarineSize / 2 > contentTable.getHeight() - 50) {
                rollingMarineDirectionY = -1.0f;  // Y 방향 반전
                rollingMarineY = contentTable.getHeight() - 50 - rollingMarineSize / 2;  // 경계값으로 위치 조정
            }
            // 아래쪽 벽에 부딪히는 경우
            else if (rollingMarineY - rollingMarineSize / 2 < 0) {
                rollingMarineDirectionY = 1.0f;  // Y 방향 반전
                rollingMarineY = rollingMarineSize / 2;  // 경계값으로 위치 조정
            }

            // 새로운 위치 설정
            rollingMarine.setPosition(rollingMarineX, rollingMarineY);

            // 기존의 회전 각도에 delta에 따른 각도 추가
            float currentRotation = rollingMarine.getRotation(); // 현재 각도 가져오기
            rollingMarine.setOrigin(rollingMarine.getWidth() / 2, rollingMarine.getHeight() / 2);
            rollingMarine.setRotation(currentRotation - rotateSpeed * direction * delta);
        }


        private Image getImage(){
            return rollingMarine;
        }

        private int getRollingMarineSize(){
            return rollingMarineSize;
        }
    }


    private class Overlord {
        private TextureAtlas overlordAtlas = new TextureAtlas(Gdx.files.internal("mission4/overlord.atlas"));
        private Array<TextureRegion> overlordRegionL = new Array<>();
        private Array<TextureRegion> overlordRegionR = new Array<>();
        private Animation<TextureRegion> overlordAnimeL;
        private TextureRegionDrawable overlordDrawableL;
        private Animation<TextureRegion> overlordAnimeR;
        private TextureRegionDrawable overlordDrawableR;
        private Image overlord;
        private float overlordStateTime = 0f;
        private int overlordSize = 160;
        private boolean movingLeft = true;
        private float x, y;
        private float speed = 100f; // 이동 속도 (픽셀/초)
        private float directionChangeCooldown; // 1~5초 사이 랜덤 쿨다운 시간
        private float timeSinceLastDirectionChange = 0f;
        private float angle;

        private Overlord(float x, float y) {
            for (int i = 0; i < 4; i++) {
                overlordRegionL.add(overlordAtlas.findRegion("OverlordL" + (i + 1)));
                overlordRegionR.add(overlordAtlas.findRegion("OverlordR" + (i + 1)));
            }
            overlordAnimeL = new Animation<TextureRegion>(0.18f, overlordRegionL, Animation.PlayMode.LOOP);
            overlordDrawableL = new TextureRegionDrawable(overlordAnimeL.getKeyFrame(0));
            overlordAnimeR = new Animation<TextureRegion>(0.18f, overlordRegionR, Animation.PlayMode.LOOP);
            overlordDrawableR = new TextureRegionDrawable(overlordAnimeR.getKeyFrame(0));
            overlord = new Image(overlordDrawableL);
            overlord.setName("overlord");
            overlord.setUserObject(this);

            // 초기 위치 및 각도 설정
            this.x = x;
            this.y = y;
            angle = (movingLeft ? 180 : 0) + MathUtils.random(-40, 40);
        }

        //위치 이동
        private void moving(float delta){
            // 상태 시간 업데이트
            setOverlordStateTime(getOverlordStateTime() + delta);
            TextureRegion currentFrameOverlord = getAnimation().getKeyFrame(getOverlordStateTime(), false);
            // Image 객체의 Drawable을 현재 프레임으로 업데이트
            ((TextureRegionDrawable) getImage().getDrawable()).setRegion(currentFrameOverlord);

            // 이동 거리 계산
            float distance = speed * delta;
            timeSinceLastDirectionChange += delta;
            x += MathUtils.cosDeg(angle) * distance;
            y += MathUtils.sinDeg(angle) * distance;

            // 벽에 닿았을 때 반대 방향으로 전환
            if (x <= 0) {
                setMovingLeft(false);
                angle = MathUtils.random(-40, 40);
                x = 0;
                timeSinceLastDirectionChange = 0;
                directionChangeCooldown = MathUtils.random(1f, 5f); // 새로운 쿨다운 설정
            } else if (x + getOverlordSize() >= contentTable.getWidth()) {
                setMovingLeft(true);
                angle = 180 + MathUtils.random(-40, 40);
                x = contentTable.getWidth() - getOverlordSize();
                timeSinceLastDirectionChange = 0;
                directionChangeCooldown = MathUtils.random(1f, 5f); // 새로운 쿨다운 설정
            }

            if (y <= 0) {
                y = 0;
                angle = 360 - angle;
                timeSinceLastDirectionChange = 0;
                directionChangeCooldown = MathUtils.random(1f, 5f); // 새로운 쿨다운 설정
            } else if (y + getOverlordSize() >= contentTable.getHeight() - 20) {
                y = contentTable.getHeight() - 20 - getOverlordSize();
                angle = 360 - angle;
                timeSinceLastDirectionChange = 0;
                directionChangeCooldown = MathUtils.random(1f, 5f); // 새로운 쿨다운 설정
            }

            // 쿨다운 시간이 지난 후 방향 전환 시도
            if (timeSinceLastDirectionChange >= directionChangeCooldown) {
                setMovingLeft(!isMovingLeft());
                angle = (isMovingLeft() ? 180 : 0) + MathUtils.random(-40, 40);
                timeSinceLastDirectionChange = 0;
                directionChangeCooldown = MathUtils.random(1f, 5f); // 새로운 쿨다운 설정
            }
            // 위치 설정
            getImage().setPosition(x, y);
        }

        //값 변경 메소드
        private void setMovingLeft(boolean value){
            movingLeft = value;
        }

        private void setOverlordStateTime(float value){
            overlordStateTime = value;
        }
        //~~~변경

        //값 반환 메소드
        private Image getImage(){
            return overlord;
        }

        private Animation<TextureRegion> getAnimation(){
            if(this.movingLeft){
                return overlordAnimeL;
            }else{
                return overlordAnimeR;
            }
        }

        private int getOverlordSize(){
            return overlordSize;
        }

        private float getOverlordStateTime(){
            return overlordStateTime;
        }

        private boolean isMovingLeft(){
            return movingLeft;
        }
        //~~~반환
    }

    private class Mutalisk {
        private TextureAtlas mutaliskAtlas = new TextureAtlas(Gdx.files.internal("mission4/mutalisk.atlas"));
        private Array<TextureRegion> mutaliskRegionL = new Array<>();
        private Animation<TextureRegion> mutaliskAnimeL;
        private TextureRegionDrawable mutaliskDrawableL;
        private Array<TextureRegion> mutaliskRegionR = new Array<>();
        private Animation<TextureRegion> mutaliskAnimeR;
        private TextureRegionDrawable mutaliskDrawableR;
        private TransparentData mutaliskTransparent;
        private Image mutalisk;
        private float mutaliskStateTime = 0f;
        private int mutaliskSize = 80;
        private boolean movingLeft = true;
        private float x, y;
        private float localX, localY;
        private float speed = 200f; // 이동 속도 (픽셀/초)
        private float directionChangeCooldown; // 1~5초 사이 랜덤 쿨다운 시간
        private float timeSinceLastDirectionChange = 0f;
        private float angle;

        private Mutalisk(float x, float y) {
            for (int i = 0; i < 6; i++) {
                mutaliskRegionL.add(mutaliskAtlas.findRegion("MutaliskL" + (i + 1)));
                mutaliskRegionR.add(mutaliskAtlas.findRegion("MutaliskR" + (i + 1)));
            }
            mutaliskAnimeL = new Animation<TextureRegion>(0.13f, mutaliskRegionL, Animation.PlayMode.LOOP);
            mutaliskDrawableL = new TextureRegionDrawable(mutaliskAnimeL.getKeyFrame(0));
            mutaliskAnimeR = new Animation<TextureRegion>(0.13f, mutaliskRegionR, Animation.PlayMode.LOOP);
            mutaliskDrawableR = new TextureRegionDrawable(mutaliskAnimeR.getKeyFrame(0));
            mutalisk = new Image(mutaliskDrawableR);
            mutalisk.setName("mutalisk");
            mutalisk.setUserObject(this);

            // 초기 위치 및 각도 설정
            this.x = x;
            this.y = y;
            angle = (movingLeft ? 180 : 0) + MathUtils.random(-40, 40);

            //투명 체크 클래스 호출
            mutaliskTransparent = new TransparentData (mutalisk);
        }

        //투명 체크
        private boolean isMutaliskTrasnparent(float x, float y){
            return mutaliskTransparent.isTransparent(x,y);
        }

        //위치이동
        private void moving(float delta){
            // 상태 시간 업데이트
            mutaliskStateTime += delta;
            TextureRegion currentFrameOverlord = getAnimation().getKeyFrame(getMutaliskStateTime(), false);
            // Image 객체의 Drawable을 현재 프레임으로 업데이트
            ((TextureRegionDrawable) getImage().getDrawable()).setRegion(currentFrameOverlord);

            // 이동 거리 계산
            float distance = speed * delta;
            timeSinceLastDirectionChange += delta;
            x += MathUtils.cosDeg(angle) * distance;
            y += MathUtils.sinDeg(angle) * distance;

            // 벽에 닿았을 때 반대 방향으로 전환
            if (x <= 0) {
                setMovingLeft(false);
                angle = MathUtils.random(-40, 40);
                x = 0;
                timeSinceLastDirectionChange = 0;
                directionChangeCooldown = MathUtils.random(1f, 3f); // 새로운 쿨다운 설정
            } else if (x + getMutaliskSize() >= contentTable.getWidth()) {
                setMovingLeft(true);
                angle = 180 + MathUtils.random(-40, 40);
                x = contentTable.getWidth() - getMutaliskSize();
                timeSinceLastDirectionChange = 0;
                directionChangeCooldown = MathUtils.random(1f, 3f); // 새로운 쿨다운 설정
            }

            if (y <= 40) {
                y = 40;
                angle = 360 - angle;
                timeSinceLastDirectionChange = 0;
                directionChangeCooldown = MathUtils.random(1f, 5f); // 새로운 쿨다운 설정
            } else if (y + getMutaliskSize() >= contentTable.getHeight() - 20) {
                y = contentTable.getHeight() - 20 - getMutaliskSize();
                angle = 360 - angle;
                timeSinceLastDirectionChange = 0;
                directionChangeCooldown = MathUtils.random(1f, 3f); // 새로운 쿨다운 설정
            }

            // 쿨다운 시간이 지난 후 방향 전환 시도
            if (timeSinceLastDirectionChange >= directionChangeCooldown) {
                setMovingLeft(!isMovingLeft());
                angle = (isMovingLeft() ? 180 : 0) + MathUtils.random(-40, 40);
                timeSinceLastDirectionChange = 0;
                directionChangeCooldown = MathUtils.random(1f, 3f); // 새로운 쿨다운 설정
            }
            // 위치 설정
            getImage().setPosition(x, y);
        }

        //값 변경 메소드
        private void setMovingLeft(boolean value){
            movingLeft = value;
        }

        private void setMutaliskStateTime(float value){
            mutaliskStateTime = value;
        }
        //~~~변경

        //값 반환 메소드
        private Image getImage(){
            return mutalisk;
        }

        private Animation<TextureRegion> getAnimation(){
            if(this.movingLeft){
                return mutaliskAnimeL;
            }else{
                return mutaliskAnimeR;
            }
        }

        private int getMutaliskSize(){
            return mutaliskSize;
        }

        private float getMutaliskStateTime(){
            return mutaliskStateTime;
        }

        private boolean isMovingLeft(){
            return movingLeft;
        }
        //~~~반환
    }

    public class TransparentData {
        private boolean[][] transparencyMap;

        // 이미지의 투명도 정보를 계산하여 저장하는 생성자
        public TransparentData(Image image) {
            TextureRegionDrawable drawable = (TextureRegionDrawable) image.getDrawable();
            TextureRegion textureRegion = drawable.getRegion();
            Texture texture = textureRegion.getTexture();

            // Pixmap 준비
            if (!texture.getTextureData().isPrepared()) {
                texture.getTextureData().prepare();
            }
            Pixmap pixmap = texture.getTextureData().consumePixmap();

            // 투명도 배열 초기화
            transparencyMap = new boolean[textureRegion.getRegionWidth()][textureRegion.getRegionHeight()];

            // 각 픽셀의 투명도를 미리 계산하여 배열에 저장
            for (int x = 0; x < textureRegion.getRegionWidth(); x++) {
                for (int y = 0; y < textureRegion.getRegionHeight(); y++) {
                    int pixel = pixmap.getPixel(x + textureRegion.getRegionX(), y + textureRegion.getRegionY());
                    int alpha = (pixel >>> 24) & 0xff;
                    transparencyMap[x][y] = (alpha == 0);  // 투명하면 true
                }
            }

            // Pixmap 메모리 해제
            pixmap.dispose();
        }

        // 특정 좌표가 투명한지 여부 확인
        public boolean isTransparent(float x, float y) {
            if (x < 0 || x >= transparencyMap.length || y < 0 || y >= transparencyMap[0].length) {
                return true; // 경계 밖은 투명한 것으로 간주
            }
            return transparencyMap[(int)x][(int)y];
        }
    }



    //이 게임에 사용되는 캐릭터의 크기와 모양은 전부 동일하니 임의로 하나만 넣어서 모든 캐릭터에 사용 가능
    private boolean checkTransparent(TextureRegion textureRegion,Image image , float x, float y) {
        // amongus1의 TextureRegion을 Pixmap으로 변환하여 픽셀 알파값 확인
        TextureRegion region = textureRegion;
        Texture texture = region.getTexture();

        // 텍스처 데이터를 Pixmap으로 추출
        texture.getTextureData().prepare();
        Pixmap pixmap = texture.getTextureData().consumePixmap();

        // 클릭한 위치의 실제 텍스처 좌표 계산 (84x84 크기를 320x320 크기에 맞게 변환)
        float scaleX = (float) region.getRegionWidth() / image.getWidth();
        float scaleY = (float) region.getRegionHeight() / image.getHeight();
        int textureX = (int) (x * scaleX) + region.getRegionX();
        int textureY = (int) (y * scaleY) + region.getRegionY();

        // Pixmap의 좌표계는 위쪽이 0이므로 Y축을 변환
        textureY = pixmap.getHeight() - textureY - 1;

        Gdx.app.log("", "texturexy " + textureX + ", " + textureY + " regionxy " + region.getRegionX() + ", " + region.getRegionY() + " " + texture);

        // 해당 픽셀의 알파값 확인
        int pixel = pixmap.getPixel(textureX, textureY);
        int alpha = (pixel >>> 24) & 0xff;
        int red = (pixel >>> 16) & 0xff;
        int green = (pixel >>> 8) & 0xff;
        int blue = pixel & 0xff;

        // 알파값과 RGB 값 로그로 출력
        Gdx.app.log("Alpha Value", "Clicked Alpha: " + alpha + " pixel = " + pixel);
        Gdx.app.log("Pixel Color", "Red: " + red + ", Green: " + green + ", Blue: " + blue);

        // Pixmap 메모리 해제
        pixmap.dispose();

        return alpha == 0;
    }
}
