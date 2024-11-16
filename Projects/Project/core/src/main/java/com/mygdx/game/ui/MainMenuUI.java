package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.AssetManager;
import com.mygdx.game.Main;
import com.mygdx.game.screens.SinglePlayerGameScreen;

public class MainMenuUI extends Group implements Disposable {
    private Main game;
    private AssetManager assetManager;
    private Skin skin;
    private Table buttonTable;
    private Image logoImage;
    private TextureAtlas buttonAtlas;

    private float logoPositionPercentX = 0.51f;
    private float logoPositionPercentY = 0.7f;
    private float buttonsPositionPercentX = 0.478f;
    private float buttonsPositionPercentY = 0.27f;
    private float logoSizePercent = 0.4f;
    private float buttonSizePercent = 0.13f;
    private float buttonSpacing = 15f;

    public MainMenuUI(final Main game) {
        this.game = game;
        this.assetManager = AssetManager.getInstance();
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        this.buttonAtlas = new TextureAtlas(Gdx.files.internal("ui/button.atlas"));

        createLogo();
        createButtons();
    }

    private void createLogo() {
        Texture logoTexture = assetManager.getLogoTexture();
        logoImage = new Image(logoTexture);
        addActor(logoImage);
    }

    private void createButtons() {
        buttonTable = new Table();
        addActor(buttonTable);

        createButton("createLobbyBtn", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CreateRoomDialog dialog = new CreateRoomDialog(skin, game);
                dialog.show(getStage());
            }
        });
        createButton("joinLobbyBtn", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                JoinRoomDialog dialog = new JoinRoomDialog(skin, game);
                dialog.show(getStage());
            }
        });
        createButton("settingBtn", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SinglePlayerGameScreen(game));
            }
        });
        createButton("closeGameBtn", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    private void createButton(String name, ClickListener listener) {
        TextureRegionDrawable upDrawable = new TextureRegionDrawable(buttonAtlas.findRegion(name));
        TextureRegionDrawable hoverDrawable = new TextureRegionDrawable(buttonAtlas.findRegion(name + "Hover"));

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = upDrawable;
        style.over = hoverDrawable;
        style.down = hoverDrawable;

        ImageButton button = new ImageButton(style);
        button.addListener(listener);
        buttonTable.add(button).pad(buttonSpacing).row();
    }

    public void updateLayout() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // 로고 크기 및 위치 설정
        float logoWidth = screenWidth * logoSizePercent;
        float logoHeight = logoWidth * logoImage.getHeight() / logoImage.getWidth();
        logoImage.setSize(logoWidth, logoHeight);
        logoImage.setPosition(
            screenWidth * logoPositionPercentX - logoWidth / 2,
            screenHeight * logoPositionPercentY - logoHeight / 2
        );

        // 버튼 크기 및 위치 설정
        float buttonWidth = screenWidth * buttonSizePercent;
        float buttonHeight = buttonWidth * 32f / 114f;
        for (Actor actor : buttonTable.getChildren()) {
            if (actor instanceof ImageButton) {
                ImageButton button = (ImageButton) actor;
                button.setSize(buttonWidth, buttonHeight);
            }
        }
        buttonTable.pack();
        buttonTable.setPosition(
            screenWidth * buttonsPositionPercentX - buttonTable.getWidth() / 2,
            screenHeight * buttonsPositionPercentY - buttonTable.getHeight() / 2
        );
    }

    public void setLogoPositionPercentX(float percent) {
        this.logoPositionPercentX = percent;
        updateLayout();
    }

    public void setLogoPositionPercentY(float percent) {
        this.logoPositionPercentY = percent;
        updateLayout();
    }

    public void setButtonsPositionPercentX(float percent) {
        this.buttonsPositionPercentX = percent;
        updateLayout();
    }

    public void setButtonsPositionPercentY(float percent) {
        this.buttonsPositionPercentY = percent;
        updateLayout();
    }

    public void setLogoSizePercent(float percent) {
        this.logoSizePercent = percent;
        updateLayout();
    }

    public void setButtonSizePercent(float percent) {
        this.buttonSizePercent = percent;
        updateLayout();
    }

    public void setButtonSpacing(float spacing) {
        this.buttonSpacing = spacing;
        updateLayout();
    }

    @Override
    public void dispose() {
        skin.dispose();
        buttonAtlas.dispose();
    }
}
