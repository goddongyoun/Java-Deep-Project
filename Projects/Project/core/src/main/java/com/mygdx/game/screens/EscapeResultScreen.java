package com.mygdx.game.screens;

import com.ImportedPackage._Imported_ClientBase;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Main;
import com.mygdx.game.Player;
import com.mygdx.game.Room;

public class EscapeResultScreen implements Screen {
    private Main game;
    private Stage stage;
    private SpriteBatch batch;
    private Texture resultTexture;
    private Room currentRoom;
    private Player player;
    private boolean isWin;

    private float gap = 80;
    private Image bossImage = new Image(new Texture(Gdx.files.internal("boss/BossIdleL1.png")));
    private TextureAtlas playerAtlas = new TextureAtlas(Gdx.files.internal("player/Frogs.atlas"));
    private TextureAtlas deadAtlas = new TextureAtlas(Gdx.files.internal("player/FrogDead.atlas"));
    private Array<Image> playerLImage = new Array<Image>();
    private Array<Image> playerRImage = new Array<Image>();
    private int playerSize=70;
    private int bossSize=90;
    private Image resultImage;
    private int totalPlayer;
    private int deadPlayer;

    private float screenDuration = 5.0f;
    private float screenTimer = 0f;

    boolean toggle = true; // 위/아래 번갈아 실행하기 위한 토글 변수

    public EscapeResultScreen(Main game , boolean winOrDead, int deadPlayer,int totalPlayer) {
        this.stage = new Stage(new FitViewport(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT));
        this.batch = new SpriteBatch();
        this.game = game;
        this.currentRoom = game.getCurrentRoom();
        this.player = currentRoom.getme();
        this.totalPlayer = totalPlayer;
        this.deadPlayer = deadPlayer;
        isWin = winOrDead;

        if(!player.isBoss()) {
            _Imported_ClientBase.setEnd();
        }

        if(isWin) {
            resultTexture = new Texture(Gdx.files.internal("ui/win.png"));
        }
        else {
            resultTexture = new Texture(Gdx.files.internal("ui/defeat.png"));
        }

        // UI 구성
        setupUI();
    }

    private void setupUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        resultImage = new Image(resultTexture);
        mainTable.add(resultImage).width(Main.WINDOW_WIDTH/2).height((Main.WINDOW_WIDTH*11/13)/2).expand().center();

        stage.addActor(mainTable);

        if (!isWin) {
            bossImage.setSize(bossSize, bossSize);
            stage.addActor(bossImage);

            int l=0,r=0;

            for (int i=0;i<totalPlayer;i++){
                if (toggle){
                    playerRImage.add(new Image(deadAtlas.findRegion("FrogDeadR8")));
                    playerRImage.get(r).setSize(playerSize,playerSize);
                    stage.addActor(playerRImage.get(r));
                    r++;
                }else{
                    playerLImage.add(new Image(deadAtlas.findRegion("FrogDeadL8")));
                    playerLImage.get(l).setSize(playerSize,playerSize);
                    stage.addActor(playerLImage.get(l));
                    l++;
                }
                toggle = !toggle; // 토글 값 반전
            }
        }else{

            int l=0,r=0;

            for (int i=0;i<totalPlayer;i++){
                if (toggle){
                    System.out.println(1);
                    playerRImage.add(new Image(playerAtlas.findRegion("FrogIdleR1")));
                    playerRImage.get(r).setSize(playerSize,playerSize);
                    stage.addActor(playerRImage.get(r));
                    r++;
                }else{
                    System.out.println(2);
                    playerLImage.add(new Image(playerAtlas.findRegion("FrogIdleL1")));
                    playerLImage.get(l).setSize(playerSize,playerSize);
                    stage.addActor(playerLImage.get(l));
                    l++;
                }
                toggle = !toggle; // 토글 값 반전
            }

            for (int i=0;i<playerRImage.size;i++){
                System.out.println(playerRImage.get(i));
            }

            for (int i=0;i<playerLImage.size;i++){
                System.out.println(playerLImage.get(i));
            }
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // 배경색 설정 (#442434)
        Gdx.gl.glClearColor(0.267f, 0.141f, 0.204f, 1); // RGBA
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        resultImage.setPosition(
            (Main.WINDOW_WIDTH - resultImage.getWidth()) / 2,
            (Main.WINDOW_HEIGHT - resultImage.getHeight()) / 2 + 50
        );

        if (!isWin) {
            bossImage.setPosition(
                (Main.WINDOW_WIDTH - bossImage.getWidth()) / 2,
                (Main.WINDOW_HEIGHT - bossImage.getHeight()) /2
            );
        }

        toggle = true;
        int l=0,r=0;

        for (int i=0;i<totalPlayer;i++){
            if (toggle){
                if (r==0) {
                    playerRImage.get(r).setPosition(
                        ((this.stage.getWidth() - playerSize) / 2) - gap,
                        (this.stage.getHeight() - playerSize) / 2 - playerSize / 3
                    );
                }else{
                    playerRImage.get(r).setPosition(
                        ((this.stage.getWidth() - playerSize) / 2) - gap*2,
                        (this.stage.getHeight() - playerSize) / 2 + playerSize / 3
                    );
                }
                r++;
            }else{
                if (l==0) {
                    playerLImage.get(l).setPosition(
                        ((this.stage.getWidth() - playerSize) / 2) + gap,
                        (this.stage.getHeight() - playerSize) / 2 - playerSize / 3
                    );
                }else{
                    playerLImage.get(l).setPosition(
                        ((this.stage.getWidth() - playerSize) / 2) + gap*2,
                        (this.stage.getHeight() - playerSize) / 2 + playerSize / 3
                    );
                }
                l++;
            }
            toggle = !toggle;
        }

        // 타이머 업데이트
        screenTimer += delta;

        // 5초가 지나면 로비로 전환
        if (screenTimer >= screenDuration) {
            player.setPosition(Main.WINDOW_WIDTH/2, Main.WINDOW_HEIGHT/2);
            player.size = 32;
            player.transformToFlog();
            player.nicknameColor = Color.WHITE;
            for(int i = 0; i<currentRoom.pCount; i++) {
                if(currentRoom.m_players[i] != null) {
                    currentRoom.m_players[i].size = 64;
                    currentRoom.m_players[i].transformToFlog();
                    currentRoom.m_players[i].nicknameColor = Color.WHITE;
                }
            }
            game.setScreen(new LobbyScreen(game, LobbyScreen.isJoined));
            //dispose();
            return;
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        resultTexture.dispose();
    }
}
