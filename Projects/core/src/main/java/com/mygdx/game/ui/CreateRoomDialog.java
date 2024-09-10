package com.mygdx.game.ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.mygdx.game.Main;
import com.mygdx.game.screens.LobbyScreen;

public class CreateRoomDialog extends Dialog {

    private TextField roomNameField;
    private Slider playerCountSlider;
    private Label playerCountLabel;
    private String roomCode;
    private Main game;
    private Skin skin;

    public CreateRoomDialog(Skin skin, Stage stage, Main game) {
        super("", skin);
        this.game = game;
        this.skin = skin;

        // 배경 이미지 설정
        Texture backgroundTexture = new Texture(Gdx.files.internal("dialog-background.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        this.setBackground(backgroundDrawable);

        // 기본 폰트 설정
        BitmapFont font = new BitmapFont();  // 기본 폰트 사용
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, skin.getColor("white"));

        // contentTable과 buttonTable 가져오기
        Table contentTable = getContentTable();
        Table buttonTable = getButtonTable();

        // 기본 방 이름과 플레이어 수 설정
        roomNameField = new TextField("Default Game", skin);
        playerCountSlider = new Slider(2, 6, 1, false, skin);
        playerCountLabel = new Label("2", labelStyle);

        // 참여 코드 생성
        roomCode = generateRoomCode();

        // Room Name 라벨과 필드 추가
        contentTable.add(new Label("Room Name:", labelStyle)).left().pad(10);
        contentTable.add(roomNameField).width(200).pad(10).row();

        // Player Count 라벨, 슬라이더, 플레이어 수 라벨 추가
        contentTable.add(new Label("Player Count:", labelStyle)).left().pad(10);
        contentTable.add(playerCountSlider).width(200).pad(10);
        contentTable.add(playerCountLabel).padLeft(10).row();

        // Map 선택 라벨과 기본 선택 맵 추가
        contentTable.add(new Label("Map:", labelStyle)).left().pad(10);
        contentTable.add(new Label("Map1", labelStyle)).width(200).pad(10).row();

        // 슬라이더 값 변경 시 라벨 업데이트
        playerCountSlider.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playerCountLabel.setText(String.valueOf((int) playerCountSlider.getValue()));
            }
        });

        // 확인 및 취소 버튼 이미지 설정
        Texture confirmTexture = new Texture("button-confirm.png");
        TextureRegionDrawable confirmDrawable = new TextureRegionDrawable(new TextureRegion(confirmTexture));

        Texture cancelTexture = new Texture("button-cancel.png");
        TextureRegionDrawable cancelDrawable = new TextureRegionDrawable(new TextureRegion(cancelTexture));

        // TextButton 스타일에 이미지 설정
        TextButton confirmButton = new TextButton("", new TextButton.TextButtonStyle(confirmDrawable, confirmDrawable, confirmDrawable, font));
        TextButton cancelButton = new TextButton("", new TextButton.TextButtonStyle(cancelDrawable, cancelDrawable, cancelDrawable, font));

        // 버튼을 다이얼로그에 추가
        buttonTable.add(confirmButton).pad(10).width(100).height(50);
        buttonTable.add(cancelButton).pad(10).width(100).height(50);

        // 버튼 리스너 추가
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String roomName = roomNameField.getText();
                int maxPlayers = (int) playerCountSlider.getValue();
                game.setScreen(new LobbyScreen(game, skin, roomName, maxPlayers, roomCode));
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        // 기본적인 다이얼로그 설정
        setModal(true);
        setMovable(false);
        setResizable(false);
        show(stage);
    }

    private String generateRoomCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            code.append(characters.charAt((int) (Math.random() * characters.length())));
        }
        return code.toString();
    }
}
