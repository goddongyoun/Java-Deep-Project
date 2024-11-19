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
import java.util.Formattable;
import java.util.Objects;
import java.util.TimerTask;

public class MissionDialog5 extends Dialog {
    Stage stage;
    Table contentTable = getContentTable();

    //게임상태
    private boolean gameOver = false;

    // 실행 제어
    private boolean continueExecution = true; // 초기값

    //act 메소드에서 딱 1번만 실행되어야 하는 것들을 위한 변수
    private boolean isActivated = false;

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

    //점수
    private Image point;
    private int objSize = 60;

    //점수 및 유렁 배치를 위한 배열
    private ArrayList<Image> objs = new ArrayList<>();
    private float objsSpeed = 800f;
    private float objsInitialX;
    private float objsInitialY;
    private float objsX;
    private float objsY;
    private float objs0X;
    private float objs0Y;
    private boolean isDeleted=false;

    //팩맨
    private TextureAtlas pacmanEatAtlas = new TextureAtlas("mission5/pacmanEat.atlas");
    private TextureRegionDrawable pacmanEatDrawable;
    private Array<TextureRegion> pacmanEatArray = new Array<>();
    private Animation<TextureRegion> pacmanEatAnimation;
    private Image pacmanEat;
    private float pacmanEatStatetime = 0f;
    private final float pacmanSpeed = 4f;  // 이동 속도 설정 (예: 1씩 이동)
    private final float maxDistance = 130f;  // 최대 이동 거리 설정
    private int pacmanSize = 60;
    private boolean startAnimation = false;
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

        //옵젝들 위치배열에 넣어줌
        for (int i=0;i<4;i++){
            point = new Image(new Texture(Gdx.files.internal("mission5/Point.png")));
            point.setName("point");
            objs.add(point);
            contentTable.add(objs.get(i)).width(objSize).height(objSize).expand().fill();
        }

        //팩맨 추가
        contentTable.add(pacmanEat).width(pacmanSize).height(pacmanSize).expandX().fillX();
        //팩맨 죽는 이미지 추가
        contentTable.add(pacmanDead).width(pacmanSize).height(pacmanSize).expandX().fillX();
        pacmanDead.setVisible(false);

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
        objsX = objs0X = objsInitialX;
        objsY = objs0Y = objsInitialY;

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

        pacmanEat.setPosition(pacmanX,pacmanY);
        pacmanDead.setPosition(pacmanX,pacmanY);

        //1번째 objs 인덱스 위치 설정
        objs.get(0).setPosition(objs0X,objs0Y);
        //2번째 objs 인덱스 부터 위치 설정
        for (int i=1;i<objs.size();i++){
            objs.get(i).setPosition(objsX+(i*120), objsY);
        }

        //먹는 애니메이션
        if(startAnimation){
            pacmanEatStatetime += delta;

            TextureRegion currentFrame = pacmanEatAnimation.getKeyFrame(pacmanEatStatetime, false);
            ((TextureRegionDrawable) pacmanEat.getDrawable()).setRegion(currentFrame);


            if(pacmanEatAnimation.isAnimationFinished(pacmanEatStatetime)){
                pacmanEatStatetime = 0f;
                //첫 프레임으로 다시 돌아옴
                currentFrame = pacmanEatAnimation.getKeyFrame(pacmanEatStatetime, false);
                ((TextureRegionDrawable) pacmanEat.getDrawable()).setRegion(currentFrame);
                startAnimation = false;
            }
        }

        // 피하는 로직
        if (isAvoid) {
            // pacmanY가 초기 위치에서 100만큼 아래로 이동했는지 확인
            if (pacmanY > (pacmanInitialY - maxDistance)) {
                // pacmanY를 이동 속도만큼 감소시킴 (아래로 이동)
                pacmanY -= pacmanSpeed;

                // pacmanEat의 위치 업데이트
                pacmanEat.setPosition(pacmanEat.getX(), pacmanY);
            }
        }else{ //원위치 로직
            if (pacmanY < pacmanInitialY) {
                // pacmanY를 이동 속도만큼 증가시킴 (위로이동)
                pacmanY += pacmanSpeed;

                // pacmanEat의 위치 업데이트
                pacmanEat.setPosition(pacmanEat.getX(), pacmanY);
            }else{
                //원위치로 돌아가면 ....
                isOnOrigin=true;
            }
        }

        // 업데이트 메서드 등에서 실행
        if ((spaceClicked || !isOnOrigin) && continueExecution) {
            this.removeListener(eatListener);
            this.removeListener(avoidlistener);
            //피하기
            if (!isOnOrigin){
                continueExecution = handleFirstObj(false, delta); // 메서드 값에 따라 continueExecution 변경
            //먹기
            }else if (spaceClicked){
                continueExecution = handleFirstObj(true, delta); // 메서드 값에 따라 continueExecution 변경
            }
        } else {
            this.addListener(eatListener);
            this.addListener(avoidlistener);
            continueExecution = false; // 조건이 충족되지 않으면 실행 멈춤
        }

        //게임 오버시 팩맨 죽음 애니메이션 실행
        if(gameOver) {
            pacmanDeadAnimation(delta);
        }
    }

//    //옵젝 움직임 함수
//    public boolean objsMoving(float delta){
//        if(objsInitialX <= objs.get(1).getX())
//            objsX -= objsSpeed*delta;
//    }

    //팩맨 죽는 애니메이션
    public void pacmanDeadAnimation(float delta){
        pacmanDeadStatetime+= (deadSpeed*delta);

        TextureRegion currentFrame = pacmanDeadAnimation.getKeyFrame(pacmanDeadStatetime, false);
        ((TextureRegionDrawable) pacmanDead.getDrawable()).setRegion(currentFrame);

        if(pacmanDeadAnimation.isAnimationFinished(pacmanDeadStatetime)){
            pacmanDead.setVisible(false);
        }
    }

    //0번째 인덱스 처리 로직
    public boolean handleFirstObj(boolean isSucceeded, float delta){
        if (Objects.equals(objs.get(0).getName(), "point")){
            if(objsInitialX <= objs.get(1).getX())
                objsX -= objsSpeed*delta;

            //점수 먹을시
            if (isSucceeded && objs0X > pacmanEat.getX()){
                objs0X -= objsSpeed*delta;
                return true;
            //점수 피할시
            }else if (!isSucceeded && objs0X > -20){
                objs0X -= objsSpeed*delta;
                return true;
            }else{
                //0번째 인덱스 제거
                del0Index();
                objsX = objs0X = objsInitialX;
                objsY = objs0Y = objsInitialY;
                return false;
            }
        }else{
            System.out.println("handleFirstObj에서 첫 if문 다 만족하지 않음");
            return false;
        }
    }

    //0번째 옵젝 인덱스 제거
    public void del0Index(){
        if (!isDeleted){
            isDeleted = true;

            TextureRegionDrawable point = new TextureRegionDrawable(new Texture(Gdx.files.internal("mission5/GhostMint1.png")));
            point.setName("point");

            for (int i=0;i<objs.size();i++){
                if (i<objs.size()-1){
                    objs.get(i).setDrawable(objs.get(i+1).getDrawable());
                }else{
                    objs.get(i).setDrawable(point);
                }
            }

//            // Table 내에서 0번째 요소 삭제
//            contentTable.removeActor(objs.get(0));
//            // 배열리스트 내에서 0번째 요소 삭제
//            objs.remove(0);
            //새로운 요소 추가
//            addObjIntoArrlist();
        }
    }

    //새로운 옵젝을 배열리스트에 추가
    public void addObjIntoArrlist(){
//        point = new Image(new Texture(Gdx.files.internal("mission5/Point.png")));
//        point.setName("point");
//        objs.add(point);
//
//        System.out.println(objs);
//        System.out.println(objs.size());

//        contentTable.add(objs.get(3)).width(objSize).height(objSize).expand().fill();
    }

    //먹는 이벤트 리스너
    InputListener eatListener = new InputListener(){
        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            if (keycode == Input.Keys.SPACE) {
                continueExecution = true;
                isActivated = false;
                startAnimation = true;
                spaceClicked = true;
                isDeleted = false;
                Timer.schedule(new Timer.Task(){
                    @Override
                    public void run() {
                        spaceClicked = false;
                    }
                },0.2f);
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
                isDeleted = false;
                isOnOrigin = false;
                Timer.schedule(new Timer.Task(){
                    @Override
                    public void run() {
                        isAvoid = false;
                    }
                },0.2f);
                return true;
            }
            return super.keyDown(event, keycode);
        }
    };

//    //팩맨 - 원위치 이벤트 리스너
//    InputListener gobacklistener = new InputListener(){
//        @Override
//        public boolean keyDown(InputEvent event, int keycode) {
//            if (keycode == Input.Keys.UP) {
//                isAvoid = false;
//                return true;
//            }
//            return super.keyDown(event, keycode);
//        }
//    };
}
