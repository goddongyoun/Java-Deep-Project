package com.mygdx.game.ui;

import com.ImportedPackage._Imported_ClientBase;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Main;
import com.mygdx.game.Player;
import com.mygdx.game.Room;
import com.mygdx.game.screens.LobbyScreen;
import com.mygdx.game.util.FontManager;

public class CreateRoomDialog extends Dialog {
    private Main game;
    private TextField roomNameField;
    private TextField playerNameField;
    private SelectBox<Integer> playerCountSelect;
    private TextField passwordField;

    public CreateRoomDialog(Skin skin, final Main game) {
        super("방 만들기", skin, "dialog");
        this.game = game;

        getTitleLabel().setStyle(createLabelStyle(skin, 24));

        Table contentTable = getContentTable();
        contentTable.pad(10);

        Label.LabelStyle labelStyle = createLabelStyle(skin, 15);
        TextField.TextFieldStyle textFieldStyle = createTextFieldStyle(skin, 15);
        SelectBox.SelectBoxStyle selectBoxStyle = createSelectBoxStyle(skin, 13);

        contentTable.add(new Label("방 제목:", labelStyle)).align(Align.left);
        roomNameField = new TextField("Default Room", textFieldStyle);
        contentTable.add(roomNameField).fillX().row();

        contentTable.add(new Label("플레이어 이름:", labelStyle)).align(Align.left);
        playerNameField = new TextField(game.getPlayerNickname(), textFieldStyle);
        contentTable.add(playerNameField).fillX().row();

        contentTable.add(new Label("플레이어 수:", labelStyle)).align(Align.left);
        playerCountSelect = new SelectBox<>(selectBoxStyle);
        playerCountSelect.setItems(2, 3, 4, 5, 6);
        playerCountSelect.setSelected(5);
        contentTable.add(playerCountSelect).fillX().row();

        contentTable.add(new Label("비밀번호 (선택):", labelStyle)).align(Align.left);
        passwordField = new TextField("", textFieldStyle);
        contentTable.add(passwordField).fillX().row();

        button("만들기", true, createTextButtonStyle(skin, 18));
        button("취소", false, createTextButtonStyle(skin, 18));
    }

    private static Label.LabelStyle createLabelStyle(Skin skin, int fontSize) {
        Label.LabelStyle style = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
        style.font = FontManager.getInstance().getFont(fontSize);
        return style;
    }

    private static TextField.TextFieldStyle createTextFieldStyle(Skin skin, int fontSize) {
        TextField.TextFieldStyle style = new TextField.TextFieldStyle(skin.get(TextField.TextFieldStyle.class));
        style.font = FontManager.getInstance().getFont(fontSize);
        return style;
    }

    private static SelectBox.SelectBoxStyle createSelectBoxStyle(Skin skin, int fontSize) {
        SelectBox.SelectBoxStyle style = new SelectBox.SelectBoxStyle(skin.get(SelectBox.SelectBoxStyle.class));
        style.font = FontManager.getInstance().getFont(fontSize);
        style.listStyle = new List.ListStyle(style.listStyle);
        style.listStyle.font = FontManager.getInstance().getFont(fontSize);
        return style;
    }

    private static TextButton.TextButtonStyle createTextButtonStyle(Skin skin, int fontSize) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        style.font = FontManager.getInstance().getFont(fontSize);
        return style;
    }

    @Override
    protected void result(Object object) {
        if ((Boolean)object) {
            String roomName = roomNameField.getText();
            String playerName = playerNameField.getText();
            //int playerCount = playerCountSelect.getSelected();
            int playerCount = 5; // Man playerCount is 5 in Server setting
            String password = passwordField.getText();
            int roomCode = 0; // 임시로 0으로 설정
            
            //Calling Server
            try {
				_Imported_ClientBase.run(playerName); // Calling the server for the first time so that the next request can be sent
			} catch (Exception e) {
				// Where the Server Failure window should be called !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				// Here
				e.printStackTrace();
			}
            _Imported_ClientBase.MakeGame_TCP(roomName); // Calling the server to make a room as TCP
            
            game.setPlayerNickname(playerName);
            Player host = new Player(playerName, 0, 0);
            Room newRoom = new Room(roomName, String.valueOf(roomCode), password, playerCount, host);

            game.setCurrentRoom(newRoom);
            game.setScreen(new LobbyScreen(game));
        }
    }
}
