package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.util.FontManager;

import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;

public class MissionDialog5 extends Dialog implements Disposable {
    Stage stage;
    Table contentTable = getContentTable();

    // 미션 완료 콜백 인터페이스
    public interface MissionCompleteCallback {
        void onMissionComplete();
    }

    private MissionCompleteCallback completeCallback;
    private boolean isShowingMission = false;
    private boolean missionComplete = false;  // 미션 전체 완료 상태

    //게임상태
    private boolean gameOver = false;
    private boolean gameClear = false;
    private boolean isStart = false;
    private int maxPointCount = 20;
    private int pointCount = 0;

    //스코어 이미지
    private TextureAtlas numberAtlas = new TextureAtlas(Gdx.files.internal("mission5/numbers.atlas"));
    private Array<TextureRegion> numberArray = new Array<>();
    private Image numberImage;
    private Image score = new Image(new Texture("mission5/score.png"));
    private Image slash = new Image(new Texture("mission5/slash.png"));
    private Array<Image> number = new Array<>();
    private int numWidth = 14;
    private int numHeight = 24;
    private int slashWidth = 20;
    private int slashHeight = 36;
    private int scoreWidth = 100;
    private int scoreHeight = 20;
    private int maxScore = 20;
    private float scoreX;
    private float scoreY;

    //시작카드
    private Image startCard = new Image(new TextureRegionDrawable(new Texture(Gdx.files.internal("publicImages/start.png"))));

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
    private float objsSpeed = 900f;
    private float objsInitialX;
    private float objsInitialY;
    private float[] objsX = new float[objArrSize]; // 각 인덱스의 x좌표
    private float objsY;
    private boolean isActivated =false;
    private boolean isGetPoint = false;

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

    public void stopShowMission() {
    	isShowingMission = false;
        MissionDialog5.this.hide();
    }
    
    //GameScreen에서 미니게임이 열려있는지 확인 용도
    public boolean isShowingMission(){
        return isShowingMission;
    }
    
    public MissionDialog5(String title, Skin skin, Stage stage) {
        super(title, skin);
        this.stage = stage;

        // ESC 키를 눌렀을 때 닫기 버튼 동작을 실행
        this.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    isShowingMission = false;
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
                isShowingMission = false;
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

        contentTable.add(startCard).width(250).height(80).expand().fill();
        startCard.setName("startCard");

        //스코어, 슬래쉬 이미지 추가
        contentTable.add(score).width(scoreWidth).height(scoreHeight).expand().fill();
        contentTable.add(slash).width(slashWidth).height(slashHeight).expand().fill();

        //넘버 추가
        for (int i=0;i<10;i++){
            numberArray.add(numberAtlas.findRegion("num"+i));
        }
        for (int i=0;i<4;i++){
            numberImage = new Image(numberArray.get(0));
            number.add(numberImage);
            contentTable.add(number.get(i)).width(numWidth).height(numHeight).expand().fill();
        }

        //테두리 추가
        contentTable.add(border).width(dialogSizeWidth).height(dialogSizeHeight).expandX().fillX();

        //닫기버튼 추가
        contentTable.add(closeButton).width(32).height(32).expand().fill().pad(10);

        this.getCell(contentTable).width(dialogSizeWidth+10).height(dialogSizeHeight+10).expand().fill();
        // 미션 클래스 자체의 배경을 제거
        this.setBackground((Drawable) null);
    }

    public void showMission(Stage stage){
        this.setSize(dialogSizeWidth+10, dialogSizeHeight+10);
        stage.addActor(this);

        isShowingMission = true;

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

        scoreX = this.getWidth() - score.getWidth()*3;
        scoreY = this.getHeight() - score.getHeight()*5;

        number.get(2).setDrawable(new TextureRegionDrawable(new TextureRegion(numberArray.get(maxScore/10))));
        number.get(3).setDrawable(new TextureRegionDrawable(new TextureRegion(numberArray.get(maxScore%10))));

        // 시작카드 1초 보여주고 시작
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                startCard.setVisible(false);
                isStart = true;
            }
        },1f);
        if (isStart){
            this.addListener(eatListener);
            this.addListener(avoidlistener);
        }

        this.show(stage);
    }

    public void act(float delta) {
        super.act(delta);

        //닫기 버튼 위치 설정
        closeButton.setPosition(this.getWidth()-(closeButton.getWidth()+8),this.getHeight()-(closeButton.getHeight()+10));

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

        startCard.setPosition(
            (this.getWidth() - startCard.getWidth()) / 2,
            (this.getHeight() - startCard.getHeight()) / 2
        );

        //ㅅㅂ 기준점을 이미지 한 가운데로 잡을걸
        score.setPosition(scoreX,scoreY);
        for (int i=0;i<2;i++){
            if (i==0){
                number.get(i).setPosition(scoreX+score.getWidth() + 14,scoreY-2);
            }else{
                number.get(i).setPosition(number.get(i-1).getX()+number.get(i-1).getWidth()+10,scoreY-2);
            }
        }
        slash.setPosition(number.get(1).getX()+number.get(1).getWidth()+6,scoreY-score.getHeight()/2);
        for (int i=2;i<4;i++){
            if (i==2){
                number.get(i).setPosition(slash.getX()+slash.getWidth()+10,scoreY-2);
            }else{
                number.get(i).setPosition(number.get(i-1).getX()+number.get(i-1).getWidth()+10,scoreY-2);
            }
        }

        pacmanEat.setPosition(pacmanX,pacmanY);
        pacmanDead.setPosition(pacmanX,pacmanY);

        //score 카운트
        number.get(0).setDrawable(new TextureRegionDrawable(new TextureRegion(numberArray.get(pointCount/10))));
        number.get(1).setDrawable(new TextureRegionDrawable(new TextureRegion(numberArray.get(pointCount%10))));

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

            missionComplete = true;
            notifyMissionComplete();
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
                if (objs.get(firstIndex).getName() == ("point")){
                    isGetPoint = true;
                }
                objsX[firstIndex] -= objsSpeed*delta;
                return true;
            //피하는 행동 실행, objs가 맵밖으로 나갈 때까지 움직이고 true 리턴
            }else if (!isEating && objsX[firstIndex] > -20){
                objsX[firstIndex] -= objsSpeed*delta;
                return true;
            //행동이 끝났으면 위치 초기화, 이미지 한 칸씩 땡기기, false 리턴
            }else{
                if (objs.get(firstIndex).getName() == ("point") && isGetPoint){
                    pointCount++;
                    if (pointCount == maxPointCount){
                        gameClear = true;
                        System.out.println(gameClear);
                    }
                    System.out.println(pointCount);
                    isGetPoint = false;
                }
                putAndChangeImages();
                return false;
            }
    }

    // 이미지 한 칸씩 앞으로 끌어 당기기
    public void putAndChangeImages() {
        if (!isActivated) {
            isActivated = true;

            String selectColor = "";
            int RandomNumForColor = MathUtils.random(1,4);
            if (RandomNumForColor == 1){
                selectColor = "Mint";
            }else if (RandomNumForColor == 2){
                selectColor = "Orange";
            }else if (RandomNumForColor == 3){
                selectColor = "Pink";
            }else{
                selectColor = "Red";
            }

            TextureRegionDrawable ghostImage = new TextureRegionDrawable(new Texture(Gdx.files.internal("mission5/Ghost"+selectColor+"1.png")));
            ghostImage.setName("ghost");

            TextureRegionDrawable pointImage = new TextureRegionDrawable(new Texture(Gdx.files.internal("mission5/point.png")));
            pointImage.setName("point");

            // 랜덤으로 이미지 선택
            TextureRegionDrawable image;
            String name;
            if (Math.random() < 0.65) {
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

            String selectColor = "";
            int RandomNumForColor = MathUtils.random(1,4);
            if (RandomNumForColor == 1){
                selectColor = "Mint";
            }else if (RandomNumForColor == 2){
                selectColor = "Orange";
            }else if (RandomNumForColor == 3){
                selectColor = "Pink";
            }else{
                selectColor = "Red";
            }

            Image ghostImage = new Image(new Texture(Gdx.files.internal("mission5/Ghost"+selectColor+"1.png")));
            ghostImage.setName("ghost");

            Image pointImage = new Image(new Texture(Gdx.files.internal("mission5/point.png")));
            pointImage.setName("point");

            // 랜덤으로 이미지 선택
            Image image;
            if (Math.random() < 0.65) {
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

    //GameScreen에서 미니게임이 열려있는지 확인 용도
    public boolean isShowingMission5() {
        return isShowingMission;
    }

    private void notifyMissionComplete() {
        if (completeCallback != null && missionComplete) {
            completeCallback.onMissionComplete();
        }
    }

    public void setMissionCompleteCallback(MissionCompleteCallback callback) {
        this.completeCallback = callback;
    }

    @Override
    public void dispose() {
        closeButtonTexture.dispose();
        closeButtonTextureHover.dispose();
        successAtals.dispose();
    }
}
