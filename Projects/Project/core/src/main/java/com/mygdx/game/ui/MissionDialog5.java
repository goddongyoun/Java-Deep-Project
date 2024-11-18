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
    private int pointSize = 40;
    private float pointInitialX;
    private float pointInitialY;
    private float pointX;
    private float pointY;

    //점수 및 유렁 배치를 위한 배열
    private ArrayList<Image> objs = new ArrayList<>();
    private float objsSpeed = 4f;

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
            objs.add(point);
            contentTable.add(objs.get(i)).width(pointSize).height(pointSize).expand().fill();
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

        pointInitialX = (this.getWidth() - point.getWidth())*2/5;
        pointInitialY = (this.getHeight() - point.getHeight())/2;
        pointX = pointInitialX + 34;
        pointY = pointInitialY;

        this.addListener(eatListener);
        this.addListener(avoidlistener);
//        this.addListener(gobacklistener);
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

        for (int i=0;i<4;i++){
            objs.get(i).setPosition(pointX+(i*120), pointY);
        }

        //먹는 애니메이션
        if(spaceClicked){
            this.removeListener(avoidlistener);
            pacmanEatStatetime += delta;
            //objs 이동
            objsMoving(delta);

            TextureRegion currentFrame = pacmanEatAnimation.getKeyFrame(pacmanEatStatetime, false);
            ((TextureRegionDrawable) pacmanEat.getDrawable()).setRegion(currentFrame);

            if(pacmanEatAnimation.isAnimationFinished(pacmanEatStatetime)){
                this.addListener(avoidlistener);
                pacmanEatStatetime = 0f;
                //첫 프레임으로 다시 돌아옴
                currentFrame = pacmanEatAnimation.getKeyFrame(pacmanEatStatetime, false);
                ((TextureRegionDrawable) pacmanEat.getDrawable()).setRegion(currentFrame);
                spaceClicked = false;
            }
        }

        // 피하는 로직
        if (isAvoid) {
            //피하는 즉시 먹는 로직 비활성
            this.removeListener(eatListener);

            // pacmanY가 초기 위치에서 100만큼 아래로 이동했는지 확인
            if (pacmanY > (pacmanInitialY - maxDistance)) {
                // pacmanY를 이동 속도만큼 감소시킴 (아래로 이동)
                pacmanY -= pacmanSpeed;

                // pacmanEat의 위치 업데이트
                pacmanEat.setPosition(pacmanEat.getX(), pacmanY);
            }
        }else{ //원위치 로직
            if (pacmanY < pacmanInitialY) {
                // pacmanY를 이동 속도만큼 감소시킴 (아래로 이동)
                pacmanY += pacmanSpeed;

                // pacmanEat의 위치 업데이트
                pacmanEat.setPosition(pacmanEat.getX(), pacmanY);
            }else{
                //원위치로 돌아가면 먹는 로직 재활성
                this.addListener(eatListener);
            }
        }

        if(gameOver) {
            pacmanDeadAnimation(delta);
        }
    }

    public void objsMoving(float delta){
        for (int i=1;i<4;i++){
            System.out.println(1);
            objs.get(i).setPosition(pointX+(i*120)-(objsSpeed*delta), pointY);
        }
    }

    public void pacmanDeadAnimation(float delta){
        pacmanDeadStatetime+= (deadSpeed*delta);

        TextureRegion currentFrame = pacmanDeadAnimation.getKeyFrame(pacmanDeadStatetime, false);
        ((TextureRegionDrawable) pacmanDead.getDrawable()).setRegion(currentFrame);

        if(pacmanDeadAnimation.isAnimationFinished(pacmanDeadStatetime)){
            pacmanDead.setVisible(false);
        }
    }

    //먹는 이벤트 리스너
    InputListener eatListener = new InputListener(){
        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            if (keycode == Input.Keys.SPACE) {
                spaceClicked = true;
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
                isAvoid = true;
                Timer.schedule(new Timer.Task(){
                    @Override
                    public void run() {
                        isAvoid = false;
                    }
                },1f);
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
