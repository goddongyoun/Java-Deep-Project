package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.util.FontManager;

import java.util.Random;

import static java.lang.Thread.sleep;

public class MissionDialog3 extends Dialog implements Disposable {
    Stage stage;
    Table contentTable = getContentTable();

    // 미션 완료 콜백 인터페이스
    public interface MissionCompleteCallback {
        void onMissionComplete();
    }

    private MissionCompleteCallback completeCallback;
    private boolean isShowingMission = false;
    private boolean missionComplete = false;  // 미션 전체 완료 상태

    private Texture borderTexture = new Texture(Gdx.files.internal("publicImages/mission_box.png"));
    private Texture backgroundTexture = new Texture(Gdx.files.internal("amongus/mission-3-background-pivot.png"));
    private TextureAtlas roundsAtlas = new  TextureAtlas("publicImages/rounds.atlas");
    private TextureAtlas amongusColors = new TextureAtlas("amongus/amongusColors.atlas");
    private TextureAtlas backgroundAtlas = new TextureAtlas("amongus/m3background.atlas");
    private Array<TextureRegion> amongusArray1 = new Array<TextureRegion>();
    private Array<TextureRegion> amongusArray2 = new Array<TextureRegion>();
    private Array<TextureRegion> amongusArray3 = new Array<TextureRegion>();
    private Array<TextureRegion> amongusArray4 = new Array<TextureRegion>();
    private Array<TextureRegion> m3backgroundArray = new Array<>();
    private TextureRegionDrawable amongusDrawable1;
    private TextureRegionDrawable amongusDrawable2;
    private TextureRegionDrawable amongusDrawable3;
    private TextureRegionDrawable amongusDrawable4;
    private TextureRegionDrawable borderDrawable;
    private TextureRegionDrawable backgroundDrawable;
    private Animation<TextureRegion> m3Animation;
    private float m3bStateTime = 0f;
    private Image amongus1;
    private Image amongus2;
    private Image amongus3;
    private Image amongus4;
    private Image border;
    private Image background;

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

    private TextureRegionDrawable stage1Drawable = new TextureRegionDrawable(roundsAtlas.findRegion("mission_round1"));
    private TextureRegionDrawable stage2Drawable = new TextureRegionDrawable(roundsAtlas.findRegion("mission_round2"));
    private TextureRegionDrawable stage3Drawable = new TextureRegionDrawable(roundsAtlas.findRegion("mission_round3"));
    private TextureRegionDrawable stage4Drawable = new TextureRegionDrawable(roundsAtlas.findRegion("mission_round4"));
    private Image card;
    private Image startCard = new Image(new TextureRegionDrawable(new Texture(Gdx.files.internal("publicImages/start.png"))));

    private TextButton closeButton;
    private Image closeButtonImage;
    Texture closeButtonTexture = new Texture(Gdx.files.internal("images/mission_button1.png"));
    Texture closeButtonTextureHover = new Texture(Gdx.files.internal("images/mission_button2.png"));

    private int amongusWidth = 84;
    private int amongusHeight = 84;
    private int currentRound = 1;
    private int totalAmongus = 4;
    private int totalRound = 3;
    private int totalSequence = 6;
    private int thisRoundSequenceCount = 0; // 해당 라운드 순서 횟수
    private int correctCount = 0;
    private int amongusXGap = 144;
    private int amongusYGap = 82;
    private int setX = 82;
    private int setY = 18;
    private IntArray randomSequence = new IntArray();
    private IntArray intputSequence = new IntArray();

    private float initialX = 178;
    private float initialY = 90;

    private boolean gameClear = false;
    private boolean roundClear = false;
    private boolean isShowSequence = false;
    private boolean isUserTurn = false;
    private boolean isShowCard = false;
    private boolean isShowGameStatus = true;

    private Random random = new Random();

    public void stopShowMission() {
    	isShowingMission = false;
        MissionDialog3.this.hide();
    }

    //GameScreen에서 미니게임이 열려있는지 확인 용도
    public boolean isShowingMission(){
        return isShowingMission;
    }

    public MissionDialog3(String title, Skin skin, Stage stage){
        super(title, skin);
        this.stage = stage;

        this.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    isShowingMission = false;
                    MissionDialog3.this.hide(); // 팝업 창 닫기
                    return true; // 키 입력 처리됨을 알림
                }
                return super.keyDown(event, keycode);
            }
        });

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
                isShowingMission = false;
                MissionDialog3.this.hide(); // 팝업 창 닫기
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

        for(int i=1;i<=19;i++){
            m3backgroundArray.add(backgroundAtlas.findRegion("m3background" + i));
        }
        m3Animation = new Animation<TextureRegion>(0.1f,m3backgroundArray,Animation.PlayMode.LOOP);
        backgroundDrawable = new TextureRegionDrawable(m3Animation.getKeyFrame(0));
        background = new Image(backgroundDrawable);

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

        contentTable.add(background).width(620).height(340).expand().fill();
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
        contentTable.add(startCard).width(250).height(80).expand().fill();
        startCard.setVisible(false);

        contentTable.add(closeButton).width(32).height(32).expand().fill().pad(10);

        this.getCell(contentTable).width(650).height(370).expand().fill();
//        // 미션 클래스 자체의 배경을 제거
        this.setBackground((Drawable) null);
    }

    public void showMission(Stage stage){
        this.setSize(650, 370);
        stage.addActor(this);

        isShowingMission = true;

        // 레이아웃 업데이트
        this.invalidate();
        this.layout();

        for(int i = 0; i< totalSequence; i++){
            int nextInt = random.nextInt(totalAmongus);

            randomSequence.add(nextInt);
            System.out.println(randomSequence);
        }

        // 팝업 창을 중앙에 배치
        this.setPosition(
            (stage.getWidth() - this.getWidth()) / 2,
            (stage.getHeight() - this.getHeight()) / 2
        );

        nextRound();
        this.show(stage);
    }

    public void act(float delta){
        super.act(delta);

        //닫기 버튼 위치 설정
        closeButton.setPosition(this.getWidth()-(closeButton.getWidth()+6),this.getHeight()-(closeButton.getHeight()+8));

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
        background.setPosition(
            (this.getWidth() - background.getWidth()) / 2,
            (this.getHeight() - background.getHeight()) / 2
        );
        startCard.setPosition(
            (this.getWidth() - startCard.getWidth()) / 2,
            (this.getHeight() - startCard.getHeight()) / 2
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
        //애니메이션 시간 업데이트
        m3bStateTime += delta;
        //현재 애니메이션 프레임 가져오기
        TextureRegion currentFrame = m3Animation.getKeyFrame(m3bStateTime, true);
        ((TextureRegionDrawable) background.getDrawable()).setRegion(currentFrame);

        amongus1.setPosition(initialX, initialY);
        amongus2.setPosition(initialX+amongusXGap, initialY-setY);
        amongus3.setPosition(initialX+setX, initialY+amongusYGap);
        amongus4.setPosition(initialX+amongusXGap+setX, initialY+amongusYGap-setY);

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
                missionComplete = true;
                notifyMissionComplete();
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

        thisRoundSequenceCount += 2;

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
                                        Timer.schedule(new Timer.Task(){
                                            @Override
                                            public void run() {
                                                startCard.setVisible(true);
                                                Timer.schedule(new Timer.Task(){
                                                    @Override
                                                    public void run() {
                                                        startCard.setVisible(false);
                                                        UserTurn();
                                                    }
                                                }, 1f);
                                            }
                                        }, 0.5f);
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
            case 4:
                card.setDrawable(stage4Drawable);
                break;
            default:
                break;
        }
    }

    public void UserTurn(){
        amongus1.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(checkTransparent(amongusArray1.get(0),amongus1,x,y)){
                    return false;
                }

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
                if(checkTransparent(amongusArray1.get(0),amongus2,x,y)){
                    return false;
                }

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
                if(checkTransparent(amongusArray1.get(0),amongus3,x,y)){
                    return false;
                }

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
                if(checkTransparent(amongusArray1.get(0),amongus4,x,y)){
                    return false;
                }

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

    //GameScreen에서 미니게임이 열려있는지 확인 용도
    public boolean isShowingMission3() {
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
