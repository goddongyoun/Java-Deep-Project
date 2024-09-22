package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Main;
import com.mygdx.game.util.FontManager;

public class SettingsDialog extends Dialog {
    private Main game;
    private CheckBox fullscreenCheckbox;
    private Slider masterVolumeSlider;
    private Slider bgmVolumeSlider;
    private Slider sfxVolumeSlider;
    private TextField nicknameField;

    public SettingsDialog(Skin skin, final Main game) {
        super("설정", skin, "dialog");
        this.game = game;

        getTitleLabel().setStyle(createLabelStyle(skin, 24));

        Table contentTable = getContentTable();
        contentTable.pad(10);

        Label.LabelStyle labelStyle = createLabelStyle(skin, 18);
        TextField.TextFieldStyle textFieldStyle = createTextFieldStyle(skin, 18);
        Slider.SliderStyle sliderStyle = createSliderStyle(skin);
        CheckBox.CheckBoxStyle checkBoxStyle = createCheckBoxStyle(skin, 18);

        fullscreenCheckbox = new CheckBox("전체화면", checkBoxStyle);
        fullscreenCheckbox.setChecked(Gdx.graphics.isFullscreen());
        contentTable.add(fullscreenCheckbox).colspan(2).align(Align.left).row();

        contentTable.add(new Label("전체 음량:", labelStyle)).align(Align.left);
        masterVolumeSlider = new Slider(0, 1, 0.01f, false, sliderStyle);
        masterVolumeSlider.setValue(game.getMasterVolume());
        contentTable.add(masterVolumeSlider).fillX().row();

        contentTable.add(new Label("배경음악 음량:", labelStyle)).align(Align.left);
        bgmVolumeSlider = new Slider(0, 1, 0.01f, false, sliderStyle);
        bgmVolumeSlider.setValue(game.getBgmVolume());
        contentTable.add(bgmVolumeSlider).fillX().row();

        contentTable.add(new Label("효과음 음량:", labelStyle)).align(Align.left);
        sfxVolumeSlider = new Slider(0, 1, 0.01f, false, sliderStyle);
        sfxVolumeSlider.setValue(game.getSfxVolume());
        contentTable.add(sfxVolumeSlider).fillX().row();

        contentTable.add(new Label("닉네임:", labelStyle)).align(Align.left);
        nicknameField = new TextField(game.getPlayerNickname(), textFieldStyle);
        contentTable.add(nicknameField).fillX().row();

        button("저장", true, createTextButtonStyle(skin, 18));
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

    private static Slider.SliderStyle createSliderStyle(Skin skin) {
        return new Slider.SliderStyle(skin.get(Slider.SliderStyle.class));
    }

    private static CheckBox.CheckBoxStyle createCheckBoxStyle(Skin skin, int fontSize) {
        CheckBox.CheckBoxStyle style = new CheckBox.CheckBoxStyle(skin.get(CheckBox.CheckBoxStyle.class));
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
            game.setMasterVolume(masterVolumeSlider.getValue());
            game.setBgmVolume(bgmVolumeSlider.getValue());
            game.setSfxVolume(sfxVolumeSlider.getValue());
            game.setPlayerNickname(nicknameField.getText());

            if (fullscreenCheckbox.isChecked() != Gdx.graphics.isFullscreen()) {
                if (fullscreenCheckbox.isChecked()) {
                    DisplayMode displayMode = Gdx.graphics.getDisplayMode();
                    Gdx.graphics.setFullscreenMode(displayMode);
                } else {
                    Gdx.graphics.setWindowedMode(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
                }
            }

            // TODO: 설정을 파일이나 데이터베이스에 저장하는 로직 추가
        }
    }
}
