package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.ImportedPackage.*;

public class CreateRoomDialog extends Dialog {
    private Main game;
    private TextField roomNameField;
    private TextField playerNameField;
    private SelectBox<Integer> playerCountSelect;
    private TextField passwordField;

    private TextureRegionDrawable makeRoom = new TextureRegionDrawable(new Texture(Gdx.files.internal("makeJoinRoom/makeRoom.png")));
    private TextureRegionDrawable makeRoomPivot = new TextureRegionDrawable(new Texture(Gdx.files.internal("makeJoinRoom/makeRoomPivot.png")));
    private static TextureRegionDrawable dropDown1 = new TextureRegionDrawable(new Texture(Gdx.files.internal("makeJoinRoom/dropDown1.png")));
    private static TextureRegionDrawable dropDown2 = new TextureRegionDrawable(new Texture(Gdx.files.internal("makeJoinRoom/dropDown2.png")));
    private static TextureRegionDrawable dropDown3 = new TextureRegionDrawable(new Texture(Gdx.files.internal("makeJoinRoom/dropDown3.png")));
    private static TextureRegionDrawable no = new TextureRegionDrawable(new Texture(Gdx.files.internal("makeJoinRoom/No1.png")));
    private static TextureRegionDrawable noHover = new TextureRegionDrawable(new Texture(Gdx.files.internal("makeJoinRoom/No2.png")));
    private static TextureRegionDrawable okay = new TextureRegionDrawable(new Texture(Gdx.files.internal("makeJoinRoom/Okay1.png")));
    private static TextureRegionDrawable okayHover = new TextureRegionDrawable(new Texture(Gdx.files.internal("makeJoinRoom/Okay2.png")));

    private Button okayButton;
    private Button noButton;

    public CreateRoomDialog(Skin skin, final Main game) {
        super("", skin, "dialog");
        this.game = game;

        getTitleLabel().setStyle(createLabelStyle(skin, 24));

        Table contentTable = getContentTable();
        contentTable.pad(10);

        Label.LabelStyle labelStyle = createLabelStyle(skin, 15);
        TextField.TextFieldStyle textFieldStyle = createTextFieldStyle(skin, 15);
        SelectBox.SelectBoxStyle selectBoxStyle = createSelectBoxStyle(skin, 13);

        contentTable.setBackground(makeRoom);

        roomNameField = new TextField("Default Room", textFieldStyle);
        contentTable.add(roomNameField).width(140).height(22).expand().fillX().row();

        playerNameField = new TextField(game.getPlayerNickname(), textFieldStyle);
        contentTable.add(playerNameField).width(140).height(22).expand().fillX().row();

        playerCountSelect = new SelectBox<>(selectBoxStyle);
        playerCountSelect.setItems(2, 3, 4, 5, 6);
        playerCountSelect.setSelected(5);
        contentTable.add(playerCountSelect).width(140).height(22).expand().fillX().row();

        passwordField = new TextField("", textFieldStyle);
        contentTable.add(passwordField).width(140).height(22).expand().fillX().row();

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

    private static SelectBox.SelectBoxStyle createSelectBoxStyle(Skin skin, int fontSize) {
        // Skin에서 기본 SelectBox 스타일 가져오기
        SelectBox.SelectBoxStyle style = new SelectBox.SelectBoxStyle(skin.get(SelectBox.SelectBoxStyle.class));

        // 기본 배경 제거 또는 설정
        style.background = dropDown1;

        // 포커스(호버) 배경 설정
        style.backgroundOver = dropDown1; // 호버 배경 제거 (필요하면 설정)

        // 드롭다운 메뉴가 열릴 때 배경 설정
        style.backgroundOpen = dropDown1; // 열릴 때 배경 제거

        // 드롭다운 메뉴의 스크롤 스타일
        if (style.scrollStyle != null) {
            style.scrollStyle.background = null; // 스크롤 배경 제거
        }

        // 리스트 스타일 설정
        style.listStyle = new List.ListStyle(style.listStyle);

        // 드롭다운 리스트 배경 설정
        TextureRegionDrawable listBackground = dropDown2;
        listBackground.setMinWidth(140);
        listBackground.setMinHeight(22);
        style.listStyle.background = listBackground; // 드롭다운 배경 설정

        // 텍스트 위치 조정
        style.listStyle.selection.setLeftWidth(6);
        style.background.setLeftWidth(6); // 텍스트 왼쪽 여백

        // 폰트 설정
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

        passwordField.setPosition(this.getWidth()/2-6, 60);
        playerCountSelect.setPosition(this.getWidth()/2-8, 60+38);
        playerNameField.setPosition(this.getWidth()/2-6, 60+39*2);
        roomNameField.setPosition(this.getWidth()/2-6, 60+39*3);

        okayButton.setPosition(116,11);
        noButton.setPosition(184,11);
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
                Player host = new Player(playerName, 0, 0, 32, true);
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
                    Player host = new Player(playerName, Main.WINDOW_WIDTH/2, Main.WINDOW_HEIGHT/2, 32, true);
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
