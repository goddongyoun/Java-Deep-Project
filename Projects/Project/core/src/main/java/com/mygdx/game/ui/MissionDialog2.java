package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MissionDialog2 extends Dialog {
    // 블럭 클래스
    public class Block {
        private int stage; // 1 ~ 5 단계
        public String mineral; // 다이아몬드, 에메랄드, 금, 철, 청금석, 레드스톤, 심층암 중 하나
        private TextureAtlas blocks = new TextureAtlas(Gdx.files.internal("minecraft/blocks.atlas"));; // 블록 이미지가 저장된 TextureAtlas
        private Array<TextureRegion> blocksArray = new Array<TextureRegion>();; // 블록 이미지를 저장할 배열

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
                blocksArray.add(blocks.findRegion("Block5"));
            }
        }

        // 단계 변경 메서드
        public void setStage(int newStage) {
            if(stage <= 5) {
                this.stage = newStage;
                blocksArray.clear(); // 기존 블록 이미지를 지우고
                loadBlockImages();    // 새로운 블록 이미지를 로드
            }
        }

        // 현재 단계 출력
        public int getStage(){
            return this.stage;
        }

        // 광물 변경 메서드 (4단계에서만 유효)
        public void setMineral(String newMineral) {
            if (stage == 4) {
                this.mineral = newMineral;
                blocksArray.clear(); // 기존 블록 이미지를 지우고
                loadBlockImages();    // 새로운 블록 이미지를 로드
            }
        }

        // 현재 블록 이미지를 반환하는 메서드
        public TextureRegion getCurrentBlockImage() {
            return blocksArray.first();
        }

        public boolean gotMineral(){
            if(this.mineral != "ggwang" && this.stage == 5) {
                return true;
            }
            return false;
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
            if (breakStage < 10) {
                breakStage++;
            } else {
                // 10단계에 도달하면 초기화 및 블럭의 단계 증가
                breakStage = 0;
                block.setStage(block.getStage() + 1);
            }
//            Gdx.app.log("onclick","breakStage"+breakStage);
        }

        // 현재 블럭 깨기 이미지를 반환
        public TextureRegion getCurrentBreakImage() {
            return breaksArray.get(breakStage); // 배열 인덱스는 0부터 시작
        }
    }

    private Stage stage;
    private Table contentTable;
    // Block과 BlockBreaker 배열
    Array<Block> blockObjects = new Array<>();
    Array<BlockBreaker> breakerObjects = new Array<>();
    // 이미지 저장용 리스트 선언
    Array<Image> blockImages = new Array<>();
    Array<Image> breakImages = new Array<>();
    // 블럭 + 블럭깨기 합치기 위한 스택 배열 선언
    Array<Stack> blockStacks = new Array<>();
    // Block과 BlockBreaker 객체의 수
    int numBlocks = 4; // 예를 들어 5개의 블럭
    private Block block;
    private BlockBreaker breaker;
    private TextureRegion currentBlockImage;
    private TextureRegion currentBreakImage;
    private Texture border = new Texture(Gdx.files.internal("minecraft/border.png"));
    private Texture missionBorder = new Texture(Gdx.files.internal("images/mission_box.png"));
    private Texture block0 = new Texture(Gdx.files.internal("minecraft/block0.png"));
    private TextureRegionDrawable block0Drawable = new TextureRegionDrawable(block0);
    private TextureRegionDrawable borderDrawable = new TextureRegionDrawable(border);
    private Image block0Image;
    private Image borderImage;
    private Image blockImage;
    private Image breakImage;
    private Image missionImage;
    private Stack blockStack;
    private float initialX=0;
    private float initialY=0;
    private float x = initialX;
    private float y = initialY;
    private int index=0;
    private int mineralCount = 0;
    private boolean gameClear = false;
    private int blockLength = 50;
    private int boxLength = blockLength * numBlocks;
    private int gap = 20;
    private boolean borderOn = false;

    Random random = new Random();

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
                    MissionDialog2.this.hide(); // 팝업 창 닫기
                    return true; // 키 입력 처리됨을 알림
                }
                return super.keyDown(event, keycode);
            }
        });

        // 반복문 밖에서 리스트를 생성하고 섞음
        ArrayList<String> blockTypes = new ArrayList<>();
        blockTypes.add("diamond");
        blockTypes.add("emerald");
        blockTypes.add("gold");
        blockTypes.add("iron");
        blockTypes.add("bluegold");
        blockTypes.add("redstone");

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

//        missionImage = new Image(border);
//        contentTable.add(missionImage).width(boxLength+gap).height(boxLength+gap).expand().fill();

        //생성된 블럭 스택들을 table에 차례로 추가하기
        for(int i=0;i<numBlocks*numBlocks;i++){
            contentTable.add(blockStacks.get(i)).width(blockLength).height(blockLength).expand().fill().pad(10);
        }

        initialX = 10;
        initialY = 10;

        this.getCell(contentTable).width(boxLength+gap).height(boxLength+gap);
//        this.setBackground((Drawable) null);

        stage.addActor(this);
    }

    public void showMission(Stage stage){
        this.setSize(640, 360);  // 팝업창 크기를 640x360으로 설정

        // 레이아웃 업데이트
        this.invalidate();
        this.layout();

        // 팝업 창을 중앙에 배치
        this.setPosition(
            (stage.getWidth() - this.getWidth()) / 2,
            (stage.getHeight() - this.getHeight()) / 2
        );

//        for(int i=0;i<numBlocks*numBlocks;i++){
//            final int finalI = i;
//            // 호버 이벤트 추가
//            blockStacks.get(i).addListener(new InputListener() {
//                @Override
//                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                    // 호버 시 테두리 이미지 변경
//                    blockStacks.get(finalI).getChild(2).setVisible(true);
//                    borderOn = true;
//                    Gdx.app.log("", finalI + "번째 객체 호버상태");
//                }
//
//                @Override
//                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                    // 호버 시 테두리 이미지 변경
//                    blockStacks.get(finalI).getChild(2).setVisible(false);
//                    borderOn = false;
//                }
//            });
//        }

        //각 블럭 객체당 별도의 클릭 시 블럭 깨기 이벤트
        for (int i=0;i<numBlocks*numBlocks;i++){
            final int finalI = i;
            blockStacks.get(i).addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    //블럭 4단계를 넘어가면 클릭 이벤트 비활성화
                    if(blockObjects.get(finalI).getStage() >= 5){
                        return;
                    }

                    breakerObjects.get(finalI).onClick();

                    currentBlockImage = blockObjects.get(finalI).getCurrentBlockImage();
                    blockImage = new Image(currentBlockImage);
                    blockImage.setSize(blockLength,blockLength);

                    currentBreakImage = breakerObjects.get(finalI).getCurrentBreakImage();
                    breakImage = new Image(currentBreakImage);
                    breakImage.setSize(blockLength,blockLength);

//                    borderImage = new Image(border);
//                    borderImage.setSize(blockLength, blockLength);

                    blockStacks.get(finalI).add(blockImage);
                    blockStacks.get(finalI).add(breakImage);
//                    blockStacks.get(finalI).add(borderImage);

                    if(blockObjects.get(finalI).gotMineral()){
                        mineralCount++;
                        Gdx.app.log("","mineralCount : "+mineralCount);
                    }
                }
            });
        }

        this.show(stage);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

//        missionImage.setPosition(0,0);

        x = initialX;
        y = initialY;
        for(int i=0;i<numBlocks;i++){
            for(int j=0;j<numBlocks;j++){
                index = i * numBlocks + j; // 1차원 배열을 2차원 배열처럼 사용할 수 있도록 함
                blockStacks.get(index).setPosition(x,y);

                x += blockStacks.get(index).getWidth();
            }
            x = initialX;
            y += blockStacks.get(i).getHeight();
        }

        //광물 다 모으면 게임 클리어
        if(mineralCount==6 && !gameClear){
            Gdx.app.log("","game clear!");
            gameClear = true;
        }
    }
}
