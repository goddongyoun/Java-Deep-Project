package com.mygdx.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.Main;
import com.mygdx.game.Player;
import com.mygdx.game.Room;
import com.mygdx.game.screens.MainMenuScreen;
import com.mygdx.game.util.FontManager;

public class LobbyUI extends Table {
    private Main game;
    private Skin skin;
    private Label roomInfoLabel;
    private Table playerListTable;
    private TextArea chatArea;
    private TextField chatInputField;
    private TextButton startGameButton;
    private TextButton leaveRoomButton;

    public LobbyUI(final Main game, Skin skin) {
        this.game = game;
        this.skin = skin;

        setFillParent(true);

        Room currentRoom = game.getCurrentRoom();
        roomInfoLabel = new Label("방 제목: " + currentRoom.getTitle() + " (코드: " + currentRoom.getCode() + ")",
            new Label.LabelStyle(FontManager.getInstance().getFont(18), Color.WHITE));
        add(roomInfoLabel).colspan(2).pad(10).row();

        playerListTable = new Table(skin);
        updatePlayerList();
        add(playerListTable).colspan(2).pad(10).row();

        chatArea = new TextArea("", new TextArea.TextFieldStyle(FontManager.getInstance().getFont(16), Color.BLACK, null, null, null));
        chatArea.setDisabled(true);
        add(chatArea).colspan(2).width(400).height(200).pad(10).row();

        chatInputField = new TextField("", new TextField.TextFieldStyle(FontManager.getInstance().getFont(16), Color.BLACK, null, null, null));
        add(chatInputField).width(300).pad(10);

        TextButton sendButton = new TextButton("전송", new TextButton.TextButtonStyle(null, null, null, FontManager.getInstance().getFont(16)));
        sendButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sendChatMessage();
            }
        });
        add(sendButton).pad(10).row();

        startGameButton = new TextButton("게임 시작", new TextButton.TextButtonStyle(null, null, null, FontManager.getInstance().getFont(18)));
        startGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                startGame();
            }
        });
        add(startGameButton).colspan(2).pad(10).row();

        leaveRoomButton = new TextButton("방 나가기", new TextButton.TextButtonStyle(null, null, null, FontManager.getInstance().getFont(18)));
        leaveRoomButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                leaveRoom();
            }
        });
        add(leaveRoomButton).colspan(2).pad(10).row();

        updateUI();
    }

    private void updatePlayerList() {
        playerListTable.clear();
        for (Player player : game.getCurrentRoom().getPlayers()) {
            playerListTable.add(new Label(player.getNickname(), new Label.LabelStyle(FontManager.getInstance().getFont(16), Color.WHITE))).pad(5);
            playerListTable.add(new Label(player.isReady() ? "준비" : "대기중", new Label.LabelStyle(FontManager.getInstance().getFont(16), Color.WHITE))).pad(5).row();
        }
    }

    private void sendChatMessage() {
        String message = chatInputField.getText();
        if (!message.isEmpty()) {
            chatArea.appendText(game.getPlayerNickname() + ": " + message + "\n");
            chatInputField.setText("");
        }
    }

    private void startGame() {
        // TODO: 게임 시작 로직 구현
    }

    private void leaveRoom() {
        game.setCurrentRoom(null);
        game.setScreen(new MainMenuScreen(game));
    }

    private void updateUI() {
        Room currentRoom = game.getCurrentRoom();
        if (currentRoom != null) {
            startGameButton.setVisible(currentRoom.getHost().getNickname().equals(game.getPlayerNickname()));
        }
    }
}
