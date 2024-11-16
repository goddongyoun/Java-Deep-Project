package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Main;
import com.mygdx.game.Player;
import com.mygdx.game.Room;
import com.mygdx.game.screens.LobbyScreen;
import com.mygdx.game.screens.MainMenuScreen;
import com.mygdx.game.util.FontManager;
import com.ImportedPackage.*;

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
            int playerCount = playerCountSelect.getSelected();
            String password = passwordField.getText();
            String roomCode = null; // 임시로 0으로 설정

            try {
                // Trying To Connect
                _Imported_ClientBase.run(playerName);
            } catch (Exception e) {
                e.printStackTrace();

                Dialog errorDialog = new Dialog("", getSkin()) {
                    @Override
                    protected void result(Object obj) {
                        hide();
                        game.setScreen(new MainMenuScreen(game));
                    }
                };
                Label.LabelStyle labelStyle = new Label.LabelStyle(getSkin().get(Label.LabelStyle.class));
                labelStyle.font = FontManager.getInstance().getFont(24);

                TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(getSkin().get(TextButton.TextButtonStyle.class));
                buttonStyle.font = FontManager.getInstance().getFont(20);

                //errorDialog.text(new Label("서버와 연결할 수 없습니다.\n메인 메뉴로 돌아갑니다.", labelStyle));
                errorDialog.text(new Label("서버와 연결할 수 없습니다.\n테스트 모드로 들어갑니다.", labelStyle));
                errorDialog.button(new TextButton("확인", buttonStyle), true);
                errorDialog.getContentTable().pad(20);

                errorDialog.show(getStage());

                this.hide();

                //!!!!!!!!! should be killed when publish
                game.setPlayerNickname(playerName);
                Player host = new Player(playerName, 0, 0, 32);
                Room newRoom = new Room(roomName, String.valueOf(roomCode), password, playerCount, host);

                game.setCurrentRoom(newRoom);
                game.setScreen(new LobbyScreen(game, false));
                //!!!!!!!!! ends

                return;
            }
            String saver = _Imported_ClientBase.MakeGame_TCP(roomName); //The returning string looks like 'Success makeGame/ABC123', so the string has to be split by '/'.
            String[] parts = saver.split("/");
            if(parts[0].equals("Success makeGame")) {
                if(parts.length > 1) {
                    roomCode = parts[1];
                    game.setPlayerNickname(playerName);
                    Player host = new Player(playerName, Main.WINDOW_WIDTH/2, Main.WINDOW_HEIGHT/2, 32);
                    Room newRoom = new Room(roomName, roomCode, password, playerCount, host);

                    game.setCurrentRoom(newRoom);
                    game.setScreen(new LobbyScreen(game, false));
                }
                else { // just in case
                    System.out.println("ROOM CODE INVALID?");
                }
            }
        }
    }
}
