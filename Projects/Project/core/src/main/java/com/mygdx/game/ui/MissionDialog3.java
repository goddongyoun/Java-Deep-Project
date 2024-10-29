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
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Timer;

import java.util.Random;

import static java.lang.Thread.sleep;

public class MissionDialog3 extends Dialog {
    Stage stage;
    Table contentTable = getContentTable();

    private Texture borderTexture = new Texture(Gdx.files.internal("publicImages/mission_box.png"));
    private TextureAtlas roundsAtlas = new  TextureAtlas("publicImages/rounds.atlas");
    private TextureAtlas amongusColors = new TextureAtlas("amongus/amongusColors.atlas");
    private Array<TextureRegion> amongusArray1 = new Array<TextureRegion>();
    private Array<TextureRegion> amongusArray2 = new Array<TextureRegion>();
    private Array<TextureRegion> amongusArray3 = new Array<TextureRegion>();
    private Array<TextureRegion> amongusArray4 = new Array<TextureRegion>();
    private TextureRegionDrawable amongusDrawable1;
    private TextureRegionDrawable amongusDrawable2;
    private TextureRegionDrawable amongusDrawable3;
    private TextureRegionDrawable amongusDrawable4;
    private TextureRegionDrawable borderDrawable;
    private Image amongus1;
    private Image amongus2;
    private Image amongus3;
    private Image amongus4;
    private Image border;

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
    private float failStateTime = 0f; //애니메이션이 0f->처음부터 시작, 0.3f->0.3초 이후부터 시작

    private TextureAtlas cardsAtlas = new TextureAtlas("amongus/cards.atlas");
    private TextureRegionDrawable stage1Drawable = new TextureRegionDrawable(roundsAtlas.findRegion("mission_round1"));
    private TextureRegionDrawable stage2Drawable = new TextureRegionDrawable(roundsAtlas.findRegion("mission_round2"));
    private TextureRegionDrawable stage3Drawable = new TextureRegionDrawable(roundsAtlas.findRegion("mission_round3"));
    private TextureRegionDrawable demonstrationDrawable = new TextureRegionDrawable(cardsAtlas.findRegion("demonstration"));
    private TextureRegionDrawable nowTryItDrawable = new TextureRegionDrawable(cardsAtlas.findRegion("nowTryIt"));
    private Image card;

    private int amongusWidth = 100;
    private int amongusHeight = 100;
    private int currentRound = 1;
    private int totalAmongus = 4;
    private int totalRound = 3;
    private int totalSequence = 9;
    private int thisRoundSequenceCount = 0; // 해당 라운드 순서 횟수
    private int correctCount = 0;
    private int amongusXGap = 100;
    private int amongusYGap = 100;
    private IntArray randomSequence = new IntArray();
    private IntArray intputSequence = new IntArray();

    private float initialX = 200;
    private float initialY = 70;

    private boolean gameClear = false;
    private boolean roundClear = false;
    private boolean isShowSequence = false;
    private boolean isUserTurn = false;
    private boolean isShowCard = false;
    private boolean isShowGameStatus = true;

    private Random random = new Random();

    public MissionDialog3(String title, Skin skin, Stage stage){
        super(title, skin);
        this.stage = stage;

        this.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    MissionDialog3.this.hide(); // 팝업 창 닫기
                    return true; // 키 입력 처리됨을 알림
                }
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

        amongusArray1.add(amongusColors.findRegion("mission3_Blue1"));
        amongusArray1.add(amongusColors.findRegion("mission3_Blue2"));
        amongusArray2.add(amongusColors.findRegion("mission3_Green1"));
        amongusArray2.add(amongusColors.findRegion("mission3_Green2"));
        amongusArray3.add(amongusColors.findRegion("mission3_Red1"));
        amongusArray3.add(amongusColors.findRegion("mission3_Red2"));
        amongusArray4.add(amongusColors.findRegion("mission3_Yellow1"));
        amongusArray4.add(amongusColors.findRegion("mission3_Yellow2"));

        amongusDrawable1 = new TextureRegionDrawable(amongusArray1.get(0));
        amongus1 = new Image(amongusDrawable1);

        amongusDrawable2 = new TextureRegionDrawable(amongusArray2.get(0));
        amongus2 = new Image(amongusDrawable2);

        amongusDrawable3 = new TextureRegionDrawable(amongusArray3.get(0));
        amongus3 = new Image(amongusDrawable3);

        amongusDrawable4 = new TextureRegionDrawable(amongusArray4.get(0));
        amongus4 = new Image(amongusDrawable4);

        borderDrawable = new TextureRegionDrawable(new TextureRegion(borderTexture));
        border = new Image(borderDrawable);

        card = new Image(stage1Drawable);

        contentTable.add(border).width(640).height(360).expand().fill();
        contentTable.add(amongus1).width(amongusWidth).height(amongusHeight).expand().fill();
        contentTable.add(amongus2).width(amongusWidth).height(amongusHeight).expand().fill();
        contentTable.add(amongus3).width(amongusWidth).height(amongusHeight).expand().fill();
        contentTable.add(amongus4).width(amongusWidth).height(amongusHeight).expand().fill();
        contentTable.add(card).width(250).height(80).expand().fill();
        card.setVisible(false);
        contentTable.add(fail).width(250).height(100).expand().fill();
        fail.setVisible(false);
        contentTable.add(success).width(250).height(100).expand().fill();
        success.setVisible(false);

        this.getCell(contentTable).width(640).height(360).expand().fill();
        stage.addActor(this);

        nextRound();
    }

    public void showMission(Stage stage){
        this.setSize(640, 360);  // 팝업창 크기를 640x360으로 설정

        // 레이아웃 업데이트
        this.invalidate();
        this.layout();

        for(int i = 0; i< totalSequence; i++){
            int nextInt = random.nextInt(totalAmongus);

            randomSequence.add(nextInt);
        }

        // 팝업 창을 중앙에 배치
        this.setPosition(
            (stage.getWidth() - this.getWidth()) / 2,
            (stage.getHeight() - this.getHeight()) / 2
        );
    }

    public void act(float delta){
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
        border.setPosition(
            (this.getWidth() - border.getWidth()) / 2,
            (this.getHeight() - border.getHeight()) / 2
        );

        if(failArray!=null && fail.isVisible() && isShowGameStatus) {
            //애니메이션 시간 업데이트
            failStateTime += delta;
            //현재 애니메이션 프레임 가져오기
            TextureRegion currentFrame = failAnimation.getKeyFrame(failStateTime, false);
            ((TextureRegionDrawable) fail.getDrawable()).setRegion(currentFrame);
        }
        if(successArray !=null && success.isVisible() && isShowGameStatus) {
            //애니메이션 시간 업데이트
            successStateTime += delta;
            //현재 애니메이션 프레임 가져오기
            TextureRegion currentFrame = successAnimation.getKeyFrame(successStateTime, false);
            ((TextureRegionDrawable) success.getDrawable()).setRegion(currentFrame);
        }

        amongus1.setPosition(initialX, initialY);
        amongus2.setPosition(initialX+amongusXGap, initialY);
        amongus3.setPosition(initialX+50, initialY+amongusYGap);
        amongus4.setPosition(initialX+amongusXGap+50, initialY+amongusYGap);

        if(intputSequence.size==thisRoundSequenceCount) {
            Gdx.app.log("","round : "+currentRound);
            for (int i = 0; i < thisRoundSequenceCount; i++) {
                if (randomSequence.get(i) == intputSequence.get(i)) {
                    correctCount++;
                }
                Gdx.app.log("",randomSequence.get(i)+", "+intputSequence.get(i));
            }

            if(correctCount == thisRoundSequenceCount){
                roundClear = true;
            }

            if (roundClear && currentRound != totalRound) {
                currentRound++;
                Gdx.app.log("","round claer");
                nextRound();
            }else if(roundClear && currentRound == totalRound){
                Gdx.app.log("","game clear");
                success.setVisible(true);
                intputSequence.clear(); // 이전 입력값 없앰
                disableListeners();
            }else{
                Gdx.app.log("","game over");
                fail.setVisible(true);
                intputSequence.clear(); // 이전 입력값 없앰
                disableListeners();
            }
        }
    }

    public void nextRound(){
        correctCount = 0;
        roundClear = false;
        isShowSequence = true;
        isUserTurn = false;
        intputSequence.clear(); // 이전 입력값 없앰
        disableListeners(); // 키 리스너를 없앰

        thisRoundSequenceCount += 3;

        changeStageCard();
        card.setVisible(true);

        if(isShowSequence) {
            isShowSequence = false;

            // 완료된 작업 횟수를 추적할 변수
            final int[] completedTasks = {0};

                Timer.schedule(new Timer.Task(){
                    @Override
                    public void run() {
                        card.setVisible(false);
                        for (int i = 0; i < thisRoundSequenceCount; i++) {
                            final int index = i;
                            Timer.schedule(new Timer.Task() {
                                @Override
                                public void run() {
                                    switch (randomSequence.get(index)) {
                                        case 0:
                                            amongus1.setDrawable(new TextureRegionDrawable(new TextureRegion(amongusArray1.get(1))));
                                            resetImageAfterDelay(amongus1, amongusArray1);
                                            break;
                                        case 1:
                                            amongus2.setDrawable(new TextureRegionDrawable(new TextureRegion(amongusArray2.get(1))));
                                            resetImageAfterDelay(amongus2, amongusArray2);
                                            break;
                                        case 2:
                                            amongus3.setDrawable(new TextureRegionDrawable(new TextureRegion(amongusArray3.get(1))));
                                            resetImageAfterDelay(amongus3, amongusArray3);
                                            break;
                                        case 3:
                                            amongus4.setDrawable(new TextureRegionDrawable(new TextureRegion(amongusArray4.get(1))));
                                            resetImageAfterDelay(amongus4, amongusArray4);
                                            break;
                                        default:
                                            Gdx.app.log("showMission", "error occurred.");
                                            break;
                                    }

                                    // 작업 완료 시마다 completedTasks 증가
                                    completedTasks[0]++;
                                    if (completedTasks[0] == thisRoundSequenceCount) {
                                        // 모든 작업이 완료되었을 때 실행
                                        UserTurn();
                                    }
                                }
                            }, i * 0.5f); // 각 반복마다 0.5초 간격으로 실행
                        }
                    }
                },1f);
        }
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
            default:
                break;
        }
    }

    public void UserTurn(){
        amongus1.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                amongus1.setDrawable(new TextureRegionDrawable(new TextureRegion(amongusArray1.get(1))));

                return true; // true를 반환하면 touchUp 이벤트도 받습니다.
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                amongus1.setDrawable(new TextureRegionDrawable(new TextureRegion(amongusArray1.get(0))));
                intputSequence.add(0);
            }
        });
        amongus2.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                amongus2.setDrawable(new TextureRegionDrawable(new TextureRegion(amongusArray2.get(1))));

                return true; // true를 반환하면 touchUp 이벤트도 받습니다.
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                amongus2.setDrawable(new TextureRegionDrawable(new TextureRegion(amongusArray2.get(0))));
                intputSequence.add(1);
            }
        });
        amongus3.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                amongus3.setDrawable(new TextureRegionDrawable(new TextureRegion(amongusArray3.get(1))));

                return true; // true를 반환하면 touchUp 이벤트도 받습니다.
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                amongus3.setDrawable(new TextureRegionDrawable(new TextureRegion(amongusArray3.get(0))));
                intputSequence.add(2);
            }
        });
        amongus4.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                amongus4.setDrawable(new TextureRegionDrawable(new TextureRegion(amongusArray4.get(1))));

                return true; // true를 반환하면 touchUp 이벤트도 받습니다.
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                amongus4.setDrawable(new TextureRegionDrawable(new TextureRegion(amongusArray4.get(0))));
                intputSequence.add(3);
            }
        });
    }

    public void disableListeners() {
        amongus1.clearListeners();
        amongus2.clearListeners();
        amongus3.clearListeners();
        amongus4.clearListeners();
    }

    // 이미지를 기본값으로 되돌리는 함수
    private void resetImageAfterDelay(final Image amongus, final Array<TextureRegion> amongusArray) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                amongus.setDrawable(new TextureRegionDrawable(new TextureRegion(amongusArray.get(0))));
            }
        }, 0.2f); // 0.5초 후에 원래 이미지로 되돌림
    }
}
