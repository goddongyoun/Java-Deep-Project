package com.mygdx.game.ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.mygdx.game.Main;

public class MainMenuUI {

    private final Main game;
    private final Stage stage;
    private final Skin skin;

    public MainMenuUI(Main game, Stage stage, Skin skin) {
        this.game = game;
        this.stage = stage;
        this.skin = skin;

        // UI 생성 및 설정
        initializeUI();
    }

    private void initializeUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();  // 버튼들을 화면 가운데로 정렬

        // 이미지 버튼 생성
        ImageButton createRoomButton = createImageButton("create_room.png");
        ImageButton joinRoomButton = createImageButton("join_room.png");
        ImageButton settingsButton = createImageButton("settings.png");
        ImageButton tutorialButton = createImageButton("tutorial.png");
        ImageButton exitButton = createImageButton("exit.png");

        // 버튼 1열로 구성
        table.add(createRoomButton).padBottom(5).padTop(300).row();
        table.add(joinRoomButton).padBottom(5).row();
        table.add(settingsButton).padBottom(5).row();
        table.add(tutorialButton).padBottom(5).row();
        table.add(exitButton).padBottom(5).row();

        stage.addActor(table);

        // 방 만들기 버튼 클릭 리스너 설정
        createRoomButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // CreateRoomDialog 호출 시 올바른 순서로 인자를 전달
                new CreateRoomDialog(skin, stage, game).show(stage);
            }
        });

        // 나머지 버튼 리스너 설정
        // 예: Join Room, Settings, Tutorial, Exit 버튼에 대한 동작 추가 가능
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();  // 애플리케이션 종료
            }
        });
    }

    private ImageButton createImageButton(String imagePath) {
        Texture texture = new Texture(Gdx.files.internal(imagePath));
        TextureRegionDrawable drawable = new TextureRegionDrawable(texture);
        return new ImageButton(drawable);
    }

    public void dispose() {
        stage.dispose();
    }
}
