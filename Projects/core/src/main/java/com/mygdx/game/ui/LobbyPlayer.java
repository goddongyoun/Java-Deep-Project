package com.mygdx.game.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class LobbyPlayer extends Table {

    private Image playerImage;
    private float speed = 400f; // 속도 (픽셀/초)
    private Array<Integer> keysPressed; // 눌린 키를 저장할 배열
    private Texture playerTexture;

    public LobbyPlayer(Skin skin, String playerName) {
        playerTexture = new Texture("player_default.png");
        playerImage = new Image(new TextureRegionDrawable(playerTexture));

        Label playerLabel = new Label(playerName, skin);

        this.add(playerImage).size(64, 64).padRight(10);
        this.add(playerLabel).left();

        keysPressed = new Array<>();

        // 키 입력을 감지하여 키를 눌렀을 때와 뗐을 때를 처리
        this.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (!keysPressed.contains(keycode, true)) {
                    keysPressed.add(keycode);
                }
                return true;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                keysPressed.removeValue(keycode, true);
                return true;
            }
        });
    }

    public void update(float delta) {
        // 키 입력에 따른 이동 처리
        for (int keycode : keysPressed) {
            switch (keycode) {
                case com.badlogic.gdx.Input.Keys.LEFT:
                    this.moveBy(-speed * delta, 0);
                    break;
                case com.badlogic.gdx.Input.Keys.RIGHT:
                    this.moveBy(speed * delta, 0);
                    break;
                case com.badlogic.gdx.Input.Keys.UP:
                    this.moveBy(0, speed * delta);
                    break;
                case com.badlogic.gdx.Input.Keys.DOWN:
                    this.moveBy(0, -speed * delta);
                    break;
            }
        }
    }

    @Override
    public boolean remove() {
        dispose();  // 제거 시 리소스 해제
        return super.remove();
    }

    public void dispose() {
        playerTexture.dispose();  // 텍스처 해제
    }
}
