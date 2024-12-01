package com.mygdx.game.ui;

import com.ImportedPackage._Imported_ClientBase;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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

    private static TextureRegionDrawable joinRoom = new TextureRegionDrawable(new Texture(Gdx.files.internal("makeJoinRoom/joinRoom.png")));
    private static TextureRegionDrawable no = new TextureRegionDrawable(new Texture(Gdx.files.internal("makeJoinRoom/No1.png")));
    private static TextureRegionDrawable noHover = new TextureRegionDrawable(new Texture(Gdx.files.internal("makeJoinRoom/No2.png")));
    private static TextureRegionDrawable okay = new TextureRegionDrawable(new Texture(Gdx.files.internal("makeJoinRoom/Okay1.png")));
    private static TextureRegionDrawable okayHover = new TextureRegionDrawable(new Texture(Gdx.files.internal("makeJoinRoom/Okay2.png")));

    private Button okayButton;
    private Button noButton;

    public JoinRoomDialog(Skin skin, final Main game) {
        super("", skin, "dialog");
        this.game = game;

        getTitleLabel().setStyle(createLabelStyle(skin, 24));

        Table contentTable = getContentTable();
        contentTable.pad(10);

        Label.LabelStyle labelStyle = createLabelStyle(skin, 18);
        TextField.TextFieldStyle textFieldStyle = createTextFieldStyle(skin, 18);

        contentTable.setBackground(joinRoom);

        roomCodeField = new TextField("", textFieldStyle);
        contentTable.add(roomCodeField).width(140).height(22).expand().fillX().row();

        passwordField = new TextField("", textFieldStyle);
        passwordField.setPasswordCharacter('*');
        passwordField.setPasswordMode(true);
        contentTable.add(passwordField).width(140).height(22).expand().fillX().row();

        playerNameField = new TextField(game.getPlayerNickname(), textFieldStyle);
        contentTable.add(playerNameField).width(140).height(22).expand().fillX().row();

        messageLabel = new Label("", labelStyle);
        contentTable.add(messageLabel).colspan(2).pad(10).row();

        Button.ButtonStyle noButtonStyle = new Button.ButtonStyle();
        noButtonStyle.up = no; // 기본 상태
        noButtonStyle.down = noHover; // 눌렸을 때
        noButtonStyle.over = noHover; // 호버 상태 (옵션)
        noButton = new Button(noButtonStyle);

        Button.ButtonStyle okayButtonStyle = new Button.ButtonStyle();
        okayButtonStyle.up = okay; // 기본 상태
        okayButtonStyle.down = okayHover; // 눌렸을 때
        okayButtonStyle.over = okayHover; // 호버 상태 (옵션)
        okayButton = new Button(okayButtonStyle);

        contentTable.add(okayButton).width(50).height(40).expand().fill();
        contentTable.add(noButton).width(50).height(40).expand().fill();

        buttonEventListener();

        // 크기를 명시적으로 설정하여 다이얼로그가 팝업 크기를 따르도록 함
        this.getCell(contentTable).width(350).height(350*24/31);
        // 배경을 제거
        this.setBackground((Drawable) null);
    }

    private static Label.LabelStyle createLabelStyle(Skin skin, int fontSize) {
        Label.LabelStyle style = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
        style.font = FontManager.getInstance().getFont(fontSize);
        return style;
    }

    private static TextField.TextFieldStyle createTextFieldStyle(Skin skin, int fontSize) {
        TextField.TextFieldStyle style = new TextField.TextFieldStyle(skin.get(TextField.TextFieldStyle.class));
        style.background = null; // 배경 제거
        style.focusedBackground = null; // 포커스 배경 제거
        style.disabledBackground = null; // 비활성 배경 제거
        style.font = FontManager.getInstance().getFont(fontSize);
        return style;
    }

    private void buttonEventListener(){
        // 버튼 클릭 시 true 값을 전달
        okayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                result(true);
            }
        });

        // 버튼 클릭 시 true 값을 전달
        noButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                result(false);
                hide();
            }
        });
    }

    @Override
    public void act(float delta){
        super.act(delta);

        messageLabel.setPosition((this.getWidth()-messageLabel.getWidth())/2, 60);
        playerNameField.setPosition(this.getWidth()/2-6, 60+38);
        passwordField.setPosition(this.getWidth()/2-6, 60+39*2);
        roomCodeField.setPosition(this.getWidth()/2-6, 60+39*3);

        okayButton.setPosition(116,11);
        noButton.setPosition(184,11);
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
