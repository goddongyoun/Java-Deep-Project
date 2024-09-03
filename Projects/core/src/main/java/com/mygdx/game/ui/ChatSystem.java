package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ChatSystem extends Table {
    private TextArea chatHistory;
    private TextField inputField;
    private ImageButton sendButton;
    private ScrollPane scrollPane;

    public ChatSystem(Skin skin) {
        super(skin);

        // 채팅 창 배경 설정
        Texture chatWindowTexture = new Texture(Gdx.files.internal("chat-window.png"));
        TextureRegionDrawable chatWindowBackground = new TextureRegionDrawable(chatWindowTexture);
        this.setBackground(chatWindowBackground);

        // 채팅 히스토리 영역 설정
        Label.LabelStyle labelStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
        labelStyle.fontColor = Color.WHITE;
        chatHistory = new TextArea("", skin);
        chatHistory.setDisabled(true);
        chatHistory.getStyle().fontColor = Color.WHITE;

        // 채팅창 백그라운드
        chatHistory.getStyle().background = null;

        scrollPane = new ScrollPane(chatHistory, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setForceScroll(false, true);

        // 채팅 입력 필드 설정
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle(skin.get(TextField.TextFieldStyle.class));
        textFieldStyle.fontColor = Color.WHITE;
        inputField = new TextField("", textFieldStyle);

        // 채팅 필드
        textFieldStyle.background = null;

        // 전송 버튼 설정
        Texture sendButtonTexture = new Texture(Gdx.files.internal("send-button.png"));
        sendButton = new ImageButton(new TextureRegionDrawable(sendButtonTexture));

        // 레이아웃 구성
        this.add(scrollPane).expand().fill().pad(10).row();
        Table inputTable = new Table();
        inputTable.add(inputField).expandX().fillX().padRight(5);
        inputTable.add(sendButton).size(40, 40);
        this.add(inputTable).expandX().fillX().pad(5, 10, 0, 10);

        // 전송 버튼 클릭 리스너
        sendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sendMessage();
            }
        });

        // Enter 키 입력 리스너
        inputField.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    sendMessage();
                    return true;
                }
                return false;
            }
        });
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isEmpty()) {
            chatHistory.appendText(message + "\n");
            inputField.setText("");
            scrollPane.scrollTo(0, 0, 0, 0);
        }
    }

    public void receiveMessage(String message) {
        chatHistory.appendText(message + "\n");
        scrollPane.scrollTo(0, 0, 0, 0);
    }
}
