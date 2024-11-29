package com.mygdx.game.ui;

import com.ImportedPackage._Imported_ClientBase;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Main;
import com.mygdx.game.Player;
import com.mygdx.game.Room;
import com.mygdx.game.screens.LobbyScreen;
import com.mygdx.game.screens.MainMenuScreen;
import com.mygdx.game.util.FontManager;

public class JoinRoomDialog extends Dialog {
    private Main game;
    private TextField roomCodeField;
    private TextField passwordField;
    private TextField playerNameField;
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

        contentTable.add(new Label("플레이어 이름:", labelStyle)).align(Align.left);
        playerNameField = new TextField(game.getPlayerNickname(), textFieldStyle);
        contentTable.add(playerNameField).fillX().row();

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
            String playerName = playerNameField.getText();

            // TODO: 실제로는 서버에서 방 정보를 가져와야 함
            // 임시로 방을 생성하여 참여하는 것으로 구현
            if (!roomCode.isEmpty()) {
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

                    errorDialog.text(new Label("서버와 연결할 수 없습니다.\n메인 메뉴로 돌아갑니다.", labelStyle));
                    errorDialog.button(new TextButton("확인", buttonStyle), true);
                    errorDialog.getContentTable().pad(20);

                    errorDialog.show(getStage());

                    this.hide();
                    return;
                }
                if(_Imported_ClientBase.joinGame(roomCode, playerName).equals("SuccessfullyJoind")) {
                    game.setPlayerNickname(playerName);
                    // Player 생성자에 적절한 크기 값을 전달합니다. 여기서는 임시로 32를 사용합니다.
                    Player player = new Player(playerName, Main.WINDOW_WIDTH/2, Main.WINDOW_HEIGHT/2, 32, true);
                    Room room = new Room("Joined Room", roomCode, password, 5, player);

                    game.setCurrentRoom(room);
                    game.setScreen(new LobbyScreen(game, true));
                }

            } else {
                messageLabel.setText("올바른 방 코드를 입력해주세요.");
            }
        }
    }
}
