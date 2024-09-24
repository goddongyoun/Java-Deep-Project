package com.mygdx.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Main;
import com.mygdx.game.Player;
import com.mygdx.game.Room;
import com.mygdx.game.screens.LobbyScreen;
import com.mygdx.game.util.FontManager;

public class JoinRoomDialog extends Dialog {
    private Main game;
    private TextField roomCodeField;
    private TextField passwordField;
    private Label messageLabel;

    public JoinRoomDialog(Skin skin, final Main game) {
        super("방 참여", skin, "dialog");
        this.game = game;

        getTitleLabel().setStyle(createLabelStyle(skin, 24));

        Table contentTable = getContentTable();
        contentTable.pad(10);

        Label.LabelStyle labelStyle = createLabelStyle(skin, 18);
        TextField.TextFieldStyle textFieldStyle = createTextFieldStyle(skin, 18);

        contentTable.add(new Label("방 코드:", labelStyle)).align(Align.left);
        roomCodeField = new TextField("", textFieldStyle);
        contentTable.add(roomCodeField).fillX().row();

        contentTable.add(new Label("비밀번호 (있는 경우):", labelStyle)).align(Align.left);
        passwordField = new TextField("", textFieldStyle);
        passwordField.setPasswordCharacter('*');
        passwordField.setPasswordMode(true);
        contentTable.add(passwordField).fillX().row();

        messageLabel = new Label("", labelStyle);
        contentTable.add(messageLabel).colspan(2).pad(10).row();

        button("참여", true, createTextButtonStyle(skin, 18));
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

    private static TextButton.TextButtonStyle createTextButtonStyle(Skin skin, int fontSize) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        style.font = FontManager.getInstance().getFont(fontSize);
        return style;
    }

    @Override
    protected void result(Object object) {
        if ((Boolean)object) {
            String roomCode = roomCodeField.getText().toUpperCase();
            String password = passwordField.getText();

            // TODO: 실제로는 서버에서 방 정보를 가져와야 함
            // 임시로 방을 생성하여 참여하는 것으로 구현
            if (!roomCode.isEmpty()) {
                Player player = new Player(game.getPlayerNickname());
                Room room = new Room("Joined Room", roomCode, password, 6, player);
                game.setCurrentRoom(room);
                game.setScreen(new LobbyScreen(game));
            } else {
                messageLabel.setText("올바른 방 코드를 입력해주세요.");
            }
        }
    }
}
