package com.mygdx.game.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class MissionDialog2 extends Dialog {
    // 블럭 클래스
    public class Block {
        private int stage; // 1 ~ 5 단계
        private String mineral; // 다이아몬드, 에메랄드, 금, 철, 청금석, 레드스톤, 심층암 중 하나
        private TextureAtlas blocks = new TextureAtlas(Gdx.files.internal("mission2/blocks.atlas"));; // 블록 이미지가 저장된 TextureAtlas
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
                    case "blueGold":
                        blocksArray.add(blocks.findRegion("Block4_Lapis"));
                        break;
                    case "redStone":
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
    }

    // 블럭 깨기 클래스
    public class BlockBreaker {
        private int breakStage = 0; // 블럭 깨기 단계 (1 ~ 10)
        private Block block; // 연결된 Block 객체
        private TextureAtlas breaks = new TextureAtlas(Gdx.files.internal("mission2/breaks.atlas"));
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
            Gdx.app.log("onclick","breakStage"+breakStage);
        }

        // 현재 블럭 깨기 이미지를 반환
        public TextureRegion getCurrentBreakImage() {
            return breaksArray.get(breakStage); // 배열 인덱스는 0부터 시작
        }
    }

    private Stage stage;
    private Table contentTable;
    private TextureAtlas breaks = new TextureAtlas(Gdx.files.internal("mission2/breaks.atlas"));

    // Block과 BlockBreaker 배열
    Array<Block> blockObjects = new Array<>();
    Array<BlockBreaker> breakers = new Array<>();

    // 이미지 저장용 리스트 선언
    Array<Image> blockImages = new Array<>();
    Array<Image> breakImages = new Array<>();

    // 블럭 + 블럭깨기 합치기 위한 스택 배열 선언
    Array<Stack> blockStacks = new Array<>();

    // Block과 BlockBreaker 객체의 수
    int numBlocks = 1; // 예를 들어 5개의 블럭

    private Block block;
    private Block block2;
    private BlockBreaker breaker;
    private BlockBreaker breaker2;

    private TextureRegion currentBlockImage;
    private TextureRegion currentBlockImage2;
    private TextureRegion currentBreakImage;
    private TextureRegion currentBreakImage2;
    private Image blockImage;
    private Image breakImage;
    private Image blockImage2;
    private Image breakImage2;
    private Stack blockStack;
    private Stack blockStack2;

    public MissionDialog2(String title, Skin skin, Stage stage) {
        super(title, skin);
        this.stage = stage;
        contentTable = getContentTable();

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

        block = new Block(1,"emerald");
        block2 = new Block(1,"diamond");
        breaker = new BlockBreaker(block);
        breaker2 = new BlockBreaker(block2);

        currentBlockImage = block.getCurrentBlockImage();
        currentBlockImage2 = block2.getCurrentBlockImage();
        currentBreakImage = breaker.getCurrentBreakImage();
        currentBreakImage2 = breaker2.getCurrentBreakImage();

        blockImage = new Image(currentBlockImage);
        blockImage2 = new Image(currentBlockImage2);
        breakImage = new Image(currentBreakImage);
        breakImage2 = new Image(currentBreakImage2);

        blockImage.setSize(100, 100);
        blockImage2.setSize(100, 100);
        breakImage.setSize(100, 100);
        breakImage2.setSize(100, 100);

        blockStack = new Stack();
        blockStack.add(blockImage);
        blockStack.add(breakImage);

        blockStack2 = new Stack();
        blockStack2.add(blockImage2);
        blockStack2.add(breakImage2);

        contentTable.add(blockStack).width(100).height(100).expand().fill();
        contentTable.add(blockStack2).width(100).height(100).expand().fill();

//        for (int i = 0; i < numBlocks; i++) {
//            block = new Block(1, "diamond");
//            breaker = new BlockBreaker(block);
//            blockObjects.add(block);
//            breakers.add(breaker);
//            currentBlockImage = blockObjects.get(i).getCurrentBlockImage();
//            currentBreakImage = breakers.get(i).getCurrentBreakImage();
//            blockImage = new Image(currentBlockImage);
//            breakImage = new Image(currentBreakImage);
//            blockStack = new Stack();
//            blockStack.add(blockImage);
//            blockStack.add(breakImage);
//            contentTable.add(blockStack).width(70).height(70).expandX().fill();
//            blockStacks.add(blockStack);
//            Gdx.app.log("block","getblock : "+blockObjects.get(i).getStage());
//            Gdx.app.log("break","getbreak : "+breakers.get(i).getCurrentBreakImage());
//            Gdx.app.log("","get blockstacks : "+blockStacks.get(i));
//        }

//        // 블럭과 블럭 깨기 객체 생성 (이 부분은 상황에 따라 다를 수 있습니다)
//        for (int i = 0; i < numBlocks; i++) {
//            Block block = new Block(1, "다이아몬드"); // 블록 객체 생성, 초기 값 설정
//            BlockBreaker breaker = new BlockBreaker(block); // BlockBreaker 객체 생성
//            blocks.add(block); // Block 객체 배열에 추가
//            breakers.add(breaker); // BlockBreaker 객체 배열에 추가
//        }
//
//        // Table에 블록 이미지와 깨기 이미지를 추가하는 반복문
//        for (int i = 0; i < numBlocks; i++) {
//            // Block 이미지 생성
//            TextureRegion currentBlockImage = blocks.get(i).getCurrentBlockImage();
//            Image blockImage = new Image(currentBlockImage);
//            blockImage.setSize(50, 50);
//            blockImages.add(blockImage);  // blockImages 리스트에 추가
//
//            // BlockBreaker 이미지 생성
//            TextureRegion currentBreakImage = breakers.get(i).getCurrentBreakImage();
//            Image breakImage = new Image(currentBreakImage);
//            breakImage.setSize(50, 50);
//            breakImages.add(breakImage);  // breakImages 리스트에 추가
//
//            // Table에 이미지 추가
//            contentTable.add(blockImage).width(50).height(50).expand().fill();
//            contentTable.add(breakImage).width(50).height(50).expand().fill();
//        }

        this.getCell(contentTable).width(640).height(360);

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

        this.show(stage);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        blockStack.setPosition(100,100);
        blockStack2.setPosition(200,100);

        if(block.getStage() <= 4) {
            blockStack.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    contentTable.removeActor(blockStack);
                    breaker.onClick();
                    currentBlockImage = block.getCurrentBlockImage();
                    currentBreakImage = breaker.getCurrentBreakImage();
                    blockImage = new Image(currentBlockImage);
                    breakImage = new Image(currentBreakImage);
                    blockImage.setSize(100, 100);
                    breakImage.setSize(100, 100);
                    blockStack.add(blockImage);
                    blockStack.add(breakImage);
                    contentTable.add(blockStack).width(100).height(100).expand().fill();
                }
            });
        }

        if(block2.getStage() <= 4) {
            blockStack2.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    contentTable.removeActor(blockStack2);
                    breaker2.onClick();
                    currentBlockImage2 = block2.getCurrentBlockImage();
                    currentBreakImage2 = breaker2.getCurrentBreakImage();
                    blockImage2 = new Image(currentBlockImage2);
                    breakImage2 = new Image(currentBreakImage2);
                    blockImage2.setSize(100, 100);
                    breakImage2.setSize(100, 100);
                    blockStack2.add(blockImage2);
                    blockStack2.add(breakImage2);
                    contentTable.add(blockStack2).width(100).height(100).expand().fill();
                }
            });
        }
    }
}
