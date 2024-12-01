package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.util.FontManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MissionDialog2 extends Dialog implements Disposable {
    // 미션 완료 콜백 인터페이스
    public interface MissionCompleteCallback {
        void onMissionComplete();
    }

    // 블럭 클래스
    public class Block {
        private int stage; // 1 ~ 5 단계
        public String mineral; // 다이아몬드, 에메랄드, 금, 철, 청금석, 레드스톤, 심층암 중 하나
        private TextureAtlas blocks = new TextureAtlas(Gdx.files.internal("minecraft/blocks.atlas")); // 블록 이미지가 저장된 TextureAtlas
        private Texture block0 = new Texture(Gdx.files.internal("minecraft/block0.png"));
        private Array<TextureRegion> blocksArray = new Array<TextureRegion>(); // 블록 이미지를 저장할 배열
        private TextureRegion blockEmpty = new TextureRegion(block0);

        // 생성자
        public Block(int stage, String mineral) {
            this.stage = stage;
            this.mineral = mineral;

            // 블록 이미지를 로드
            loadBlockImages();
        }

        // 블록 이미지 로드 메서드
        private void loadBlockImages() {
            // 단계에 따라 블록을 가져옴
            if (stage == 1) {
                blocksArray.add(blocks.findRegion("Block1"));
            } else if (stage == 2) {
                blocksArray.add(blocks.findRegion("Block2"));
            } else if (stage == 3) {
                blocksArray.add(blocks.findRegion("Block3"));
            } else if (stage == 4) {
                // 4단계일 경우 광물 변수에 따라 다른 블록을 로드
                switch (mineral.toLowerCase()) {
                    case "diamond":
                        blocksArray.add(blocks.findRegion("Block4_Dia"));
                        break;
                    case "emerald":
                        blocksArray.add(blocks.findRegion("Block4_Eme"));
                        break;
                    case "gold":
                        blocksArray.add(blocks.findRegion("Block4_Gold"));
                        break;
                    case "iron":
                        blocksArray.add(blocks.findRegion("Block4_Iron"));
                        break;
                    case "bluegold":
                        blocksArray.add(blocks.findRegion("Block4_Lapis"));
                        break;
                    case "redstone":
                        blocksArray.add(blocks.findRegion("Block4_Red"));
                        break;
                    case "ggwang":
                        blocksArray.add(blocks.findRegion("Block4"));
                        break;
                    default:
                        Gdx.app.log("Block", "Unknown mineral type: " + mineral);
                }
            } else if (stage == 5) {
                // 4단계일 경우 광물 변수에 따라 다른 블록을 로드
                switch (mineral.toLowerCase()) {
                    case "diamond":
                        blocksArray.add(blocks.findRegion("Item_Dia"));
                        break;
                    case "emerald":
                        blocksArray.add(blocks.findRegion("Item_Eme"));
                        break;
                    case "gold":
                        blocksArray.add(blocks.findRegion("Item_Gold"));
                        break;
                    case "iron":
                        blocksArray.add(blocks.findRegion("Item_Iron"));
                        break;
                    case "bluegold":
                        blocksArray.add(blocks.findRegion("Item_Lapis"));
                        break;
                    case "redstone":
                        blocksArray.add(blocks.findRegion("Item_Red"));
                        break;
                    case "ggwang":
                        blocksArray.add(blocks.findRegion("Block5"));
                        break;
                    default:
                        Gdx.app.log("Block", "Unknown mineral type: " + mineral);
                }
            } else if (stage == 6) {
                blocksArray.add(blocks.findRegion("Block5"));
            }
        }

        // 단계 변경 메서드
        public void setStage(int newStage) {
            if (stage <= 6) {
                this.stage = newStage;
                blocksArray.clear(); // 기존 블록 이미지를 지우고
                loadBlockImages();    // 새로운 블록 이미지를 로드
            }
        }

        // 현재 단계 출력
        public int getStage() {
            return this.stage;
        }

        // 광물 변경 메서드 (4단계에서만 유효)
//        public void setMineral(String newMineral) {
//            if (stage == 5) {
//                this.mineral = newMineral;
//                blocksArray.clear(); // 기존 블록 이미지를 지우고
//                loadBlockImages();    // 새로운 블록 이미지를 로드
//            }
//        }

        // 현재 블록 이미지를 반환하는 메서드
        public TextureRegion getCurrentBlockImage() {
            return blocksArray.first();
        }

        public boolean gotMineral() {
            if (this.mineral != "ggwang" && this.stage == 6) {
                return true;
            }
            return false;
        }

        public String getMineral() {
            return mineral;
        }
    }

    // 블럭 깨기 클래스
    public class BlockBreaker {
        private int breakStage = 0; // 블럭 깨기 단계 (1 ~ 10)
        private Block block; // 연결된 Block 객체
        private TextureAtlas breaks = new TextureAtlas(Gdx.files.internal("minecraft/breaks.atlas"));
        private Array<TextureRegion> breaksArray = new Array<>();

        // 생성자: 블럭 객체를 받아서 블럭 깨기 이미지를 설정
        public BlockBreaker(Block block) {
            this.block = block;

            // 블록 깨기 이미지 초기화
            loadBreakImages();
        }

        // 블럭 깨기 이미지 로드
        private void loadBreakImages() {
            for (int i = breakStage; i <= 10; i++) {
                breaksArray.add(breaks.findRegion("Break" + i));
            }
        }

        // 마우스 클릭 시 호출되는 메서드
        public void onClick() {
            if (block.getStage() == 5) {
                block.setStage(block.getStage() + 1);
            } else {
                if (breakStage < 10) {
                    breakStage++;
                } else {
                    // 10단계에 도달하면 초기화 및 블럭의 단계 증가
                    breakStage = 0;
                    block.setStage(block.getStage() + 1);
                }
            }
//            Gdx.app.log("onclick","breakStage"+breakStage);
        }

        // 현재 블럭 깨기 이미지를 반환
        public TextureRegion getCurrentBreakImage() {
            return breaksArray.get(breakStage); // 배열 인덱스는 0부터 시작
        }
    }

    private MissionCompleteCallback completeCallback;
    private Stage stage;
    private Table contentTable;
    // Block과 BlockBreaker 배열
    Array<Block> blockObjects = new Array<>();
    Array<BlockBreaker> breakerObjects = new Array<>();
    // 이미지 저장용 리스트 선언
    // 블럭 + 블럭깨기 합치기 위한 스택 배열 선언
    Array<Stack> blockStacks = new Array<>();
    // Block과 BlockBreaker 객체의 수
    int numBlocks = 4; // 예를 들어 5개의 블럭
    private Block block;
    private BlockBreaker breaker;
    private TextureRegion currentBlockImage;
    private TextureRegion currentBreakImage;
    private Texture border = new Texture(Gdx.files.internal("minecraft/border.png"));
    private Texture missionBorderTexture = new Texture(Gdx.files.internal("mission/mission_box-90x90.png"));
    private Texture block0 = new Texture(Gdx.files.internal("minecraft/block0.png"));
    private Texture itemListTexture = new Texture(Gdx.files.internal("minecraft/checkItemBox.png"));
    private TextureRegionDrawable itemListDrawable = new TextureRegionDrawable(itemListTexture);
    private TextureRegionDrawable checkDrawable = new TextureRegionDrawable(new Texture(Gdx.files.internal("minecraft/Item_check.png")));
    private TextureRegionDrawable missionBorderDrawable = new TextureRegionDrawable(missionBorderTexture);
    private TextureRegion missionBorderRegion = new TextureRegion(missionBorderTexture);
    private Image block0Image;
    private Image borderImage;
    private Image blockImage;
    private Image breakImage;
    private Image missionBorder = new Image(missionBorderDrawable);
    private Image itemList = new Image(itemListDrawable);
    private Image check;
    private Array<Image> checkArray = new Array<>();
    private Stack blockStack;
    private float initialX = 0;
    private float initialY = 0;
    private float x = initialX;
    private float y = initialY;
    private int checkSize = 28;
    private int index = 0;
    private int mineralCount = 0;
    private boolean gameClear = false;
    private boolean missionComplete = false;  // 미션 전체 완료 상태
    private int blockLength = 75;
    private int boxLength = blockLength * numBlocks;
    private int gap = 20;
    private float checkInitialX;
    private float checkInitialY;
    private float checkX;
    private float checkY;
    private boolean borderOn = false;
    private boolean isShowingMission = false;

    private TextButton closeButton;
    private Image closeButtonImage;
    Texture closeButtonTexture = new Texture(Gdx.files.internal("images/mission_button1.png"));
    Texture closeButtonTextureHover = new Texture(Gdx.files.internal("images/mission_button2.png"));

    private TextureAtlas successAtals = new TextureAtlas("publicImages/successes.atlas");
    private TextureRegionDrawable successDrawable;
    private Array<TextureRegion> successArray = new Array<>();
    private Animation<TextureRegion> successAnimation;
    private Image success = new Image();
    private float successStateTime = 0f; //애니메이션이 0f->처음부터 시작, 0.3f->0.3초 이후부터 시작

    Random random = new Random();

    public void stopShowMission() {
    	isShowingMission = false;
        MissionDialog2.this.hide();
    }
    
    //GameScreen에서 미니게임이 열려있는지 확인 용도
    public boolean isShowingMission(){
        return isShowingMission;
    }
    
    public MissionDialog2(String title, Skin skin, Stage stage) {
        super(title, skin);
        this.stage = stage;
        contentTable = getContentTable();

//        Drawable borderDrawable = borderImage.getDrawable();

        // ESC 키를 눌렀을 때 닫기 버튼 동작을 실행
        this.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    isShowingMission = false;
                    MissionDialog2.this.hide(); // 팝업 창 닫기
                    return true; // 키 입력 처리됨을 알림
                }
                return super.keyDown(event, keycode);
            }
        });

        for (int i = 1; i <= 6; i++) {
            successArray.add(successAtals.findRegion("mission_success" + i));
        }
        successAnimation = new Animation<TextureRegion>(0.15f, successArray, Animation.PlayMode.NORMAL);
        successDrawable = new TextureRegionDrawable(successAnimation.getKeyFrame(0));
        success = new Image(successDrawable);

        // 반복문 밖에서 리스트를 생성하고 섞음
        ArrayList<String> blockTypes = new ArrayList<>();
        blockTypes.add("diamond");
        blockTypes.add("emerald");
        blockTypes.add("gold");
        blockTypes.add("iron");
        blockTypes.add("bluegold");
        blockTypes.add("redstone");

        //닫기 버튼
        closeButtonImage = new Image(closeButtonTexture);
        closeButtonImage.setSize(50, 50);
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
                MissionDialog2.this.hide(); // 팝업 창 닫기
            }
        });

        // 리스트를 섞어서 랜덤 순서로 배치
        Collections.shuffle(blockTypes);
        int blockTypeCount = 0;

        //boolean 배열을 생성함
        boolean[] randomTrueFlags = new boolean[numBlocks * numBlocks];
        //true로 설정된 요소의 개수를 추적하는 변수임
        int trueCount = 0;

        // 반복문 실행 전에 6번 true로 설정
        while (trueCount < 6) {
            //블럭 인덱스 크기 내에서 랜덤한 변수를 생성해서 저장함
            int randomIndex = random.nextInt(numBlocks * numBlocks);
            //뽑은 랜덤 인덱스값이 기존에 없는 인덱스값인것을 확인, 기존에 없는 인덱스 값이면 해당 인덱스를 true로 지정해줌
            if (!randomTrueFlags[randomIndex]) {
                randomTrueFlags[randomIndex] = true;
                trueCount++;
            }
        }

        for (int i = 0; i < numBlocks * numBlocks; i++) {
            if (randomTrueFlags[i]) {
                // 섞인 blockTypes 리스트에서 하나씩 선택
                block = new Block(1, blockTypes.get(blockTypeCount));
                blockTypeCount++;
            } else {
                // 나머지 블럭은 모두 "ggwang"으로 생성
                block = new Block(1, "ggwang");
            }

            blockObjects.add(block);

            breaker = new BlockBreaker(block);
            breakerObjects.add(breaker);

            currentBlockImage = block.getCurrentBlockImage();
            blockImage = new Image(currentBlockImage);
            blockImage.setSize(blockLength, blockLength);

            currentBreakImage = breaker.getCurrentBreakImage();
            breakImage = new Image(currentBreakImage);
            breakImage.setSize(blockLength, blockLength);

            borderImage = new Image(border);
            borderImage.setSize(blockLength, blockLength);
            borderImage.setVisible(false);


            blockStack = new Stack();
            blockStack.add(blockImage);
            blockStack.add(breakImage);
            blockStack.add(borderImage);

            blockStacks.add(blockStack);
        }

        for (int i = 0; i < 6; i++) {
            check = new Image(checkDrawable);
            checkArray.add(check);
        }

        //생성된 블럭 스택들을 table에 차례로 추가하기
        for (int i = 0; i < numBlocks * numBlocks; i++) {
            contentTable.add(blockStacks.get(i)).width(blockLength).height(blockLength).expand().fill().pad(10);
        }

        contentTable.add(itemList).width(80).height(180);

        for (int i = 0; i < 6; i++) {
            contentTable.add(checkArray.get(i)).width(checkSize).height(checkSize).expand().fill().pad(10);
            checkArray.get(i).setVisible(false);
        }

        contentTable.add(success).width(250).height(100).expand().fill();
        success.setVisible(false);


        initialX = 100;
        initialY = 20;

        checkInitialX = initialX;
        checkInitialY = initialY;

        checkX = (checkInitialX - 20) + (boxLength + 40) + 45;
        checkY = (checkInitialY - 20) + 164;

        //테두리 추가
        contentTable.add(missionBorder).width(boxLength + 40).height(boxLength + 40).expand().fill();
        contentTable.add(closeButton).width(32).height(32).expand().fill().pad(10);
        missionBorderClickEvent();
        blockClick();
        this.getCell(contentTable).width(550).height(340);
        this.setBackground((Drawable) null);

    }

    public void showMission(Stage stage) {
        this.setSize(640, 360);  // 팝업창 크기를 640x360으로 설정
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

        this.show(stage);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        itemList.setPosition(
            boxLength + blockLength + 44,
            (this.getHeight() - itemList.getHeight()) - 8
        );

        for (int i = 0; i < 6; i++) {
            checkArray.get(i).setPosition(checkX, checkY + (checkSize - 1) * (i));
        }

        success.setPosition(
            (this.getWidth() - success.getWidth()) / 2 - 22,
            (this.getHeight() - success.getHeight()) / 2
        );

        missionBorder.setPosition(initialX - 20, initialY - 20);

        closeButton.setPosition(
            missionBorder.getX() + missionBorder.getWidth() - closeButton.getWidth(),
            missionBorder.getY() + missionBorder.getHeight() - closeButton.getHeight()
        );

        if (successArray != null && success.isVisible()) {
            //애니메이션 시간 업데이트
            successStateTime += delta;
            //현재 애니메이션 프레임 가져오기
            TextureRegion currentFrame = successAnimation.getKeyFrame(successStateTime, false);
            ((TextureRegionDrawable) success.getDrawable()).setRegion(currentFrame);
        }

        x = initialX;
        y = initialY;
        for (int i = 0; i < numBlocks; i++) {
            for (int j = 0; j < numBlocks; j++) {
                index = i * numBlocks + j; // 1차원 배열을 2차원 배열처럼 사용할 수 있도록 함
                blockStacks.get(index).setPosition(x, y);

                x += blockStacks.get(index).getWidth();
            }
            x = initialX;
            y += blockStacks.get(i).getHeight();
        }

        //광물 다 모으면 게임 클리어
        if (mineralCount == 6 && !gameClear) {
            Gdx.app.log("", "game clear!");
            gameClear = true;
            missionComplete = true;
            notifyMissionComplete();
            success.setVisible(true);
        }
    }

    public void blockClick() {
        // 각 블럭 객체당 별도의 클릭 시 블럭 깨기 이벤트
        for (int i = 0; i < numBlocks * numBlocks; i++) {
            final int finalI = i;

            // 클릭 이벤트 처리
            blockStacks.get(i).addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // 클릭 로직을 두 번 실행
                    for (int j = 0; j < 2; j++) {
                        // 블럭 6단계를 넘어가면 클릭 이벤트 비활성화
                        if (blockObjects.get(finalI).getStage() >= 6) {
                            return;
                        }

                        // 블럭 클릭 시 처리
                        breakerObjects.get(finalI).onClick();

                        currentBlockImage = blockObjects.get(finalI).getCurrentBlockImage();
                        blockImage = new Image(currentBlockImage);
                        blockImage.setSize(blockLength, blockLength);

                        currentBreakImage = breakerObjects.get(finalI).getCurrentBreakImage();
                        breakImage = new Image(currentBreakImage);
                        breakImage.setSize(blockLength, blockLength);

                        blockStacks.get(finalI).add(blockImage);
                        blockStacks.get(finalI).add(breakImage);

                        if (blockObjects.get(finalI).gotMineral()) {
                            mineralCount++;
                            switch (blockObjects.get(finalI).getMineral()) {
                                case "diamond":
                                    checkArray.get(5).setVisible(true);
                                    break;
                                case "emerald":
                                    checkArray.get(4).setVisible(true);
                                    break;
                                case "gold":
                                    checkArray.get(3).setVisible(true);
                                    break;
                                case "iron":
                                    checkArray.get(2).setVisible(true);
                                    break;
                                case "bluegold":
                                    checkArray.get(1).setVisible(true);
                                    break;
                                case "redstone":
                                    checkArray.get(0).setVisible(true);
                                    break;
                                default:
                                    Gdx.app.log("Block", "Unknown mineral type in clicked method");
                            }
                            Gdx.app.log("", "mineralCount : " + mineralCount);
                        }
                    }
                }
            });
        }
    }

    public void missionBorderClickEvent() {
        missionBorder.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (checkTransparent(missionBorderRegion, missionBorder, x, y)) {
                    //미션 보더 초깃값 이동한 만큼 x,y 좌표에 설정해줘야함
                    x += missionBorder.getX();
                    y += missionBorder.getY();

//                    System.out.println("false");
                    // 투명한 부분 클릭 시 이벤트 전파를 중지하여 다른 대상이 처리하게 함
                    // 투명한 부분 클릭 시 blockStacks의 클릭 이벤트 호출
                    for (Stack blockStack : blockStacks) {
                        // Stack의 경계를 Rectangle로 생성
                        Rectangle bounds = new Rectangle(blockStack.getX(), blockStack.getY(), blockStack.getWidth(), blockStack.getHeight());

                        // 클릭한 위치가 블록 스택의 범위에 포함되는지 확인
                        if (bounds.contains(x, y)) {
                            // 블록 스택 내의 상대 좌표 계산
                            float localX = x - blockStack.getX();
                            float localY = y - blockStack.getY();

                            // blockStack의 ClickListener 호출
                            for (EventListener listener : blockStack.getListeners()) {
                                if (listener instanceof ClickListener) {
                                    ((ClickListener) listener).clicked(event, localX, localY);
                                }
                            }
                            break; // 클릭된 블록만 처리하고 루프를 종료
                        }
                    }

                    return false;
                }
                // 투명하지 않은 부분 클릭 시 정상 처리
//                System.out.println("true");
                return true;
            }
        });
    }

    //이 게임에 사용되는 캐릭터의 크기와 모양은 전부 동일하니 임의로 하나만 넣어서 모든 캐릭터에 사용 가능
    private boolean checkTransparent(TextureRegion textureRegion, Image image, float x, float y) {
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

//        Gdx.app.log("", "texturexy " + textureX + ", " + textureY + " regionxy " + region.getRegionX() + ", " + region.getRegionY() + " " + texture);

        // 해당 픽셀의 알파값 확인
        int pixel = pixmap.getPixel(textureX, textureY);
        int alpha = (pixel >>> 24) & 0xff;
        int red = (pixel >>> 16) & 0xff;
        int green = (pixel >>> 8) & 0xff;
        int blue = pixel & 0xff;

        // 알파값과 RGB 값 로그로 출력
//        Gdx.app.log("Alpha Value", "Clicked Alpha: " + alpha + " pixel = " + pixel);
//        Gdx.app.log("Pixel Color", "Red: " + red + ", Green: " + green + ", Blue: " + blue);

        // Pixmap 메모리 해제
        pixmap.dispose();

        return alpha == 0;
    }

    //GameScreen에서 미니게임이 열려있는지 확인 용도
    public boolean isShowingMission2() {
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
        border.dispose();
        successAtals.dispose();
    }
}
