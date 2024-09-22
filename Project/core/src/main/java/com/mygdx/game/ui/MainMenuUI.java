package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.AssetManager;
import com.mygdx.game.Main;
import com.mygdx.game.util.FontManager;

public class MainMenuUI extends Group implements Disposable {
    private Main game;
    private AssetManager assetManager;
    private FontManager fontManager;
    private Skin skin;
    private Table buttonTable;
    private float yPositionPercent = 0.4f;

    public MainMenuUI(final Main game) {
        this.game = game;
        this.assetManager = AssetManager.getInstance();
        this.fontManager = FontManager.getInstance();
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        buttonTable = new Table();
        addActor(buttonTable);

        Texture buttonTexture = assetManager.getButtonTexture();

        createButton("", buttonTexture, 0, 0, 114, 32, 1.6f, 1.6f, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CreateRoomDialog dialog = new CreateRoomDialog(skin, game);
                dialog.show(getStage());
            }
        });
        createButton("", buttonTexture, 0, 38, 114, 32, 1.6f, 1.6f, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                JoinRoomDialog dialog = new JoinRoomDialog(skin, game);
                dialog.show(getStage());
            }
        });
        createButton("", buttonTexture, 0, 74, 114, 32, 1.6f, 1.6f, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SettingsDialog dialog = new SettingsDialog(skin, game);
                dialog.show(getStage());
            }
        });
        createButton("", buttonTexture, 0, 111, 114, 32, 1.6f, 1.6f, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        updatePosition();
    }

    private void createButton(String text, Texture buttonTexture, int x, int y, float width, float height, float scaleX, float scaleY, ClickListener listener) {
        AnimatedImageButton button = new AnimatedImageButton(buttonTexture, x, y, (int)width, (int)height);
        button.setSize(width * scaleX, height * scaleY);
        button.addListener(listener);

        Label label = new Label(text, new Label.LabelStyle(fontManager.getFont(24), null));
        label.setPosition(width / 2 - label.getWidth() / 2, height / 2 - label.getHeight() / 2);

        Table cellTable = new Table();
        cellTable.add(button).size(width * scaleX, height * scaleY);
        cellTable.add(label).expand().fill().center();

        buttonTable.add(cellTable).pad(10).row();
    }

    public void updatePosition() {
        float yPosition = Gdx.graphics.getHeight() * yPositionPercent;
        setPosition(
            (Gdx.graphics.getWidth() - buttonTable.getWidth()) / 2,
            yPosition - buttonTable.getHeight() / 2
        );
        buttonTable.setPosition(0, 0);
    }

    public void setYPositionPercent(float percent) {
        this.yPositionPercent = percent;
        updatePosition();
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        skin.dispose();
    }
}
