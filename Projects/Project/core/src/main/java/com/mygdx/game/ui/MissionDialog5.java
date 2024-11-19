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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.util.FontManager;

import java.util.ArrayList;
import java.util.TimerTask;

public class MissionDialog5 extends Dialog {
    Stage stage;
    Table contentTable = getContentTable();

    //게임상태
    private boolean gameOver = false;
    private boolean gameClear = false;
    private int maxPointCount = 20;
    private int pointCount = 0;

    // 실행 제어
    private boolean continueExecution = true; // 초기값

    //다이어로그, contentTable 크기를 이 변수로 설정
    private int dialogSizeWidth = 600;
    private int dialogSizeHeight = 450;

    //배경
    private Image background = new Image(new Texture(Gdx.files.internal("mission5/mission5Background.png")));
    private int backgroundWidth = dialogSizeWidth - 50;
    private int backgroundHeight = dialogSizeHeight - 50;

    //테두리
    private Image border = new Image(new Texture(Gdx.files.internal("publicImages/missionBox120x90.png")));

    //닫기버튼
    private TextButton closeButton;
    private Image closeButtonImage;
    Texture closeButtonTexture = new Texture(Gdx.files.internal("images/mission_button1.png"));
    Texture closeButtonTextureHover = new Texture(Gdx.files.internal("images/mission_button2.png"));

    //objs #점수, 유렁 이미지 등을 objs라고 하겠음
    private ArrayList<Image> objs = new ArrayList<>();
    private Image image;
    private int objArrSize = 5;
    private int firstIndex;
    private int lastIndex;
    private int objSize = 60;
    private float objsSpeed = 800f;
    private float objsInitialX;
    private float objsInitialY;
    private float[] objsX = new float[objArrSize]; // 각 인덱스의 x좌표
    private float objsY;
    private boolean isActivated =false;

    //팩맨
    private TextureAtlas pacmanEatAtlas = new TextureAtlas("mission5/pacmanEat.atlas");
    private TextureRegionDrawable pacmanEatDrawable;
    private Array<TextureRegion> pacmanEatArray = new Array<>();
    private Animation<TextureRegion> pacmanEatAnimation;
    private Image pacmanEat;
    private float pacmanEatStatetime = 0f;
    private final float pacmanSpeed = 6f;  // 이동 속도 설정 (예: 1씩 이동)
    private final float maxDistance = 130f;  // 최대 이동 거리 설정
    private int pacmanSize = 60;
    private boolean spaceClicked = false;
    private boolean isAvoid = false;
    private boolean isOnOrigin = true;
    private float pacmanInitialX;
    private float pacmanInitialY;
    private float pacmanX;
    private float pacmanY;

    //팩맨 죽음
    private TextureAtlas pacmanDeadAtlas = new TextureAtlas("mission5/pacmanDead.atlas");
    private TextureRegionDrawable pacmanDeadDrawable;
    private Array<TextureRegion> pacmanDeadArray = new Array<>();
    private Animation<TextureRegion> pacmanDeadAnimation;
    private Image pacmanDead;
    private float pacmanDeadStatetime = 0f;
    private final float deadSpeed = 2f;

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

    public MissionDialog5(String title, Skin skin, Stage stage) {
        super(title, skin);
        this.stage = stage;

        // ESC 키를 눌렀을 때 닫기 버튼 동작을 실행
        this.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    MissionDialog5.this.hide(); // 팝업 창 닫기
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
                MissionDialog5.this.hide(); // 팝업 창 닫기
            }
        });

        //팩맨 애니메이션, 이미지 생성
        for (int i=0;i<4;i++){
            pacmanEatArray.add(pacmanEatAtlas.findRegion("PacmanEat"+(i+1)));
        }

        pacmanEatAnimation = new Animation<TextureRegion>(0.15f, pacmanEatArray,Animation.PlayMode.NORMAL);
        pacmanEatDrawable = new TextureRegionDrawable(new TextureRegion(pacmanEatAnimation.getKeyFrame(0)));
        pacmanEat = new Image(pacmanEatDrawable);

        //팩맨 죽는 애니메이션, 이미지 생성
        for (int i=0;i<11;i++){
            pacmanDeadArray.add(pacmanDeadAtlas.findRegion("PacmanDead"+(i+1)));
        }

        pacmanDeadAnimation = new Animation<TextureRegion>(0.15f, pacmanDeadArray,Animation.PlayMode.NORMAL);
        pacmanDeadDrawable = new TextureRegionDrawable(new TextureRegion(pacmanDeadAnimation.getKeyFrame(0)));
        pacmanDead = new Image(pacmanDeadDrawable);

        //배경 추가
        contentTable.add(background).width(backgroundWidth).height(backgroundHeight).expandX().fillX();

        // 옵젝 초기화 및 테이블에 추가
        initialObjs();

        //팩맨 추가
        contentTable.add(pacmanEat).width(pacmanSize).height(pacmanSize).expandX().fillX();
        //팩맨 죽는 이미지 추가
        contentTable.add(pacmanDead).width(pacmanSize).height(pacmanSize).expandX().fillX();
        pacmanDead.setVisible(false);

        //실패 추가
        contentTable.add(fail).width(250).height(100).expand().fill();
        fail.setName("fail");
        fail.setVisible(false);

        //성공 추가
        contentTable.add(success).width(250).height(100).expand().fill();
        success.setName("success");
        success.setVisible(false);

        //테두리 추가
        contentTable.add(border).width(dialogSizeWidth).height(dialogSizeHeight).expandX().fillX();

        //닫기버튼 추가
        contentTable.add(closeButton).width(32).height(32).expand().fill().pad(10);

        this.getCell(contentTable).width(dialogSizeWidth).height(dialogSizeHeight).expand().fill();
        // 미션 클래스 자체의 배경을 제거
        this.setBackground((Drawable) null);
        stage.addActor(this);
    }

    public void showMission(Stage stage){
        this.setSize(dialogSizeWidth, dialogSizeHeight);

        // 레이아웃 업데이트
        this.invalidate();
        this.layout();

        // 팝업 창을 중앙에 배치
        this.setPosition(
            (stage.getWidth() - this.getWidth()) / 2,
            (stage.getHeight() - this.getHeight()) / 2
        );

        //초기값 위치 설정
        pacmanInitialX = (this.getWidth() - pacmanEat.getWidth())*1/5 - 10;
        pacmanInitialY = (this.getHeight() - pacmanEat.getHeight())/2;
        pacmanX = pacmanInitialX;
        pacmanY = pacmanInitialY;

        objsInitialX = (this.getWidth() - objSize)*2/5 + 34;
        objsInitialY = (this.getHeight() - objSize)/2;
        for (int i = 0; i < objArrSize; i++) {
            objsX[i] = objsInitialX + (i * 120); // 각 인덱스가 120만큼 오른쪽에 위치
        }
        objsY = objsInitialY;

        this.addListener(eatListener);
        this.addListener(avoidlistener);
    }

    public void act(float delta) {
        super.act(delta);

        closeButton.setPosition(
            this.getWidth()-closeButton.getWidth(),
            this.getHeight()-(closeButton.getHeight()+3)
        );

        background.setPosition(
            (this.getWidth() - background.getWidth())/2,
            (this.getHeight() - background.getHeight())/2
        );

        border.setPosition(
            (this.getWidth() - border.getWidth())/2,
            (this.getHeight() - border.getHeight())/2
        );

        fail.setPosition(
            (this.getWidth() - fail.getWidth()) / 2,
            (this.getHeight() - fail.getHeight()) / 2
        );

        success.setPosition(
            (this.getWidth() - success.getWidth()) / 2,
            (this.getHeight() - success.getHeight()) / 2
        );

        pacmanEat.setPosition(pacmanX,pacmanY);
        pacmanDead.setPosition(pacmanX,pacmanY);

        //objs 위치 설정
        for (int i=0;i<objArrSize;i++){
            objs.get(i).setPosition(objsX[i], objsY);
        }

        //먹는 애니메이션
        if(spaceClicked){
            // 행동 중에는 이벤트리스너 제거
            this.removeListener(eatListener);
            this.removeListener(avoidlistener);
            pacmanEatStatetime += delta*2f;

            TextureRegion currentFrame = pacmanEatAnimation.getKeyFrame(pacmanEatStatetime, false);
            ((TextureRegionDrawable) pacmanEat.getDrawable()).setRegion(currentFrame);

            if(pacmanEatAnimation.isAnimationFinished(pacmanEatStatetime)){
                pacmanEatStatetime = 0f;
                //첫 프레임으로 다시 돌아옴
                currentFrame = pacmanEatAnimation.getKeyFrame(pacmanEatStatetime, false);
                ((TextureRegionDrawable) pacmanEat.getDrawable()).setRegion(currentFrame);
                this.addListener(eatListener);
                this.addListener(avoidlistener);
                spaceClicked = false;
            }
        }

        // 피하는 로직
        if (isAvoid) {
            // 행동 중에는 이벤트리스너 제거
            this.removeListener(eatListener);
            this.removeListener(avoidlistener);

            // pacmanY가 초기 위치에서 maxDistance만큼 아래로 이동했는지 확인
            if (pacmanY > (pacmanInitialY - maxDistance)) {
                // pacmanY를 이동 속도만큼 감소시킴 (아래로 이동)
                pacmanY -= pacmanSpeed;

                // pacmanEat의 위치 업데이트
                pacmanEat.setPosition(pacmanEat.getX(), pacmanY);
            } else {
                // 원하는 아래 위치에 도달하면 다시 올라가도록 설정
                isAvoid = false;
            }
        } else { // 원위치 로직
            if (pacmanY <= pacmanInitialY) {
                // pacmanY를 이동 속도만큼 증가시킴 (위로 이동)
                pacmanY += pacmanSpeed;

                // pacmanEat의 위치 업데이트
                pacmanEat.setPosition(pacmanEat.getX(), pacmanY);
            } else {
                // 원위치로 돌아가면 .....
                // 만약 먹는 중이 아니라면 이벤트 활성화
                if (!spaceClicked) {
                    this.addListener(eatListener);
                    this.addListener(avoidlistener);
                }
                isOnOrigin = true;
            }
        }


        // 먹기 or 피하기 발동시 실행
        if ((spaceClicked || !isOnOrigin) && continueExecution) {
            //피하기
            if (!isOnOrigin){
                continueExecution = moveObjs(false, delta);
            //먹기
            }else if (spaceClicked){
                continueExecution = moveObjs(true, delta);
            }
        } else {
            continueExecution = false;
        }

        //게임 오버시 팩맨 죽음 애니메이션 실행
        if(gameOver) {
            this.removeListener(eatListener);
            this.removeListener(avoidlistener);
            pacmanDeadAnimation(delta);
        }

        //게임 클리어시
        if (gameClear){
            this.removeListener(eatListener);
            this.removeListener(avoidlistener);

            //성공 애니메이션
            success.setVisible(true);
            successStateTime += delta;
            TextureRegion currentFrame = successAnimation.getKeyFrame(successStateTime, false);
            ((TextureRegionDrawable) success.getDrawable()).setRegion(currentFrame);
        }
    }

    //팩맨 죽는 애니메이션
    public void pacmanDeadAnimation(float delta){
        pacmanEat.setVisible(false);
        pacmanDead.setVisible(true);
        pacmanDeadStatetime+= (deadSpeed*delta);

        TextureRegion currentFrame = pacmanDeadAnimation.getKeyFrame(pacmanDeadStatetime, false);
        ((TextureRegionDrawable) pacmanDead.getDrawable()).setRegion(currentFrame);

        if(pacmanDeadAnimation.isAnimationFinished(pacmanDeadStatetime)){
            pacmanDead.setVisible(false);

            //실패 애니메이션
            fail.setVisible(true);
            failStateTime += delta;
            TextureRegion currentFailedFrame = failAnimation.getKeyFrame(failStateTime, false);
            ((TextureRegionDrawable) fail.getDrawable()).setRegion(currentFailedFrame);
        }
    }

    // objs 이동 로직, true 리턴 -> 아직 움직임이 끝나지 않음, false 리턴 -> 움직임 끝
    public boolean moveObjs(boolean isEating, float delta){
            //두번째 인덱스부터 마지막 인덱스까지 움직임
            if (firstIndex>=4){
                if(objsInitialX <= objs.get(0).getX()){
                    for (int i=0;i<objArrSize;i++){
                        if (i!=firstIndex)
                            objsX[i] -= objsSpeed*delta;
                    }
                }
            }else{
                if(objsInitialX <= objs.get(firstIndex+1).getX()){
                    for (int i=0;i<objArrSize;i++){
                        if (i!=firstIndex)
                            objsX[i] -= objsSpeed*delta;
                    }
                }
            }

        //맨 앞 인덱스 움직임
            //먹는 행동 실행, objs가 pacman까지 도달할 때까지 움직이고 true 리턴
            if (isEating && objsX[firstIndex] > pacmanEat.getX()){
                if (objs.get(firstIndex).getName() == ("ghost") && objsX[firstIndex] < pacmanEat.getX()+pacmanEat.getWidth()-10){
                    gameOver = true;
                    return false;
                }
                objsX[firstIndex] -= objsSpeed*delta;
                return true;
            //피하는 행동 실행, objs가 맵밖으로 나갈 때까지 움직이고 true 리턴
            }else if (!isEating && objsX[firstIndex] > -20){
                objsX[firstIndex] -= objsSpeed*delta;
                return true;
            //행동이 끝났으면 위치 초기화, 이미지 한 칸씩 땡기기, false 리턴
            }else{
                if (objs.get(firstIndex).getName() == ("point")){
                    pointCount++;
                    if (pointCount == maxPointCount){
                        gameClear = true;
                        System.out.println(gameClear);
                    }
                    System.out.println(pointCount);
                }
                putAndChangeImages();
                return false;
            }
    }

    // 이미지 한 칸씩 앞으로 끌어 당기기
    public void putAndChangeImages() {
        if (!isActivated) {
            isActivated = true;

            // 두 개의 TextureRegionDrawable 생성
            TextureRegionDrawable pointImage = new TextureRegionDrawable(new Texture(Gdx.files.internal("mission5/point.png")));
            pointImage.setName("point");

            TextureRegionDrawable ghostImage = new TextureRegionDrawable(new Texture(Gdx.files.internal("mission5/GhostMint1.png")));
            ghostImage.setName("ghost");

            // 랜덤으로 이미지 선택
            TextureRegionDrawable image;
            String name;
            if (Math.random() < 0.5) {
                image = pointImage;
                name = "point";
            } else {
                image = ghostImage;
                name = "ghost";
            }
            objs.get(firstIndex).setDrawable(image);
            objs.get(firstIndex).setName(name); // name 속성 업데이트

            //맨 앞 요소 위치 맨 뒤로 설정
            objsX[firstIndex] = objsX[lastIndex] + 120;

            //다음 인덱스를 맨 앞 요소로 지정
            if (firstIndex>=4){
                firstIndex = 0;
            }else{
                firstIndex++;
            }

            //맨 마지막 인덱스 지정
            if((firstIndex+3)/4==0){
                lastIndex = 4;
            }else{
                lastIndex = ((firstIndex+3)%4);
            }
        }
    }

    // objs 초기화
    public void initialObjs(){
        for (int i=0;i<objArrSize;i++){
            // 두 개의 TextureRegionDrawable 생성
            Image pointImage = new Image(new Texture(Gdx.files.internal("mission5/point.png")));
            pointImage.setName("point");

            Image ghostImage = new Image(new Texture(Gdx.files.internal("mission5/GhostMint1.png")));
            ghostImage.setName("ghost");

            // 랜덤으로 이미지 선택
            Image image;
            if (Math.random() < 0.5) {
                image = pointImage;
            } else {
                image = ghostImage;
            }

            objs.add(image);
            contentTable.add(image).width(objSize).height(objSize).expand().fill();
        }
        //0 인덱스를 맨 앞 인덱스로 지정
        firstIndex = 0;
        lastIndex = objArrSize-1;
    }

    //먹는 이벤트 리스너
    InputListener eatListener = new InputListener(){
        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            if (keycode == Input.Keys.SPACE) {
                continueExecution = true;
                spaceClicked = true;
                isActivated = false;
                return true;
            }
            return super.keyDown(event, keycode);
        }
    };

    //팩맨 - 피하는 이벤트 리스너
    InputListener avoidlistener = new InputListener(){
        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            if (keycode == Input.Keys.DOWN) {
                continueExecution = true;
                isAvoid = true;
                isActivated = false;
                isOnOrigin = false;
                return true;
            }
            return super.keyDown(event, keycode);
        }
    };
}
