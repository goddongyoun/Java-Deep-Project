package com.mygdx.game.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;

public class AnimatedImageButton extends ImageButton {
    private TextureRegionDrawable normalDrawable;
    private TextureRegionDrawable hoverDrawable;

    public AnimatedImageButton(Texture texture, int x, int y, int width, int height) {
        super(new ImageButtonStyle());

        TextureRegion normalRegion = new TextureRegion(texture, x, y, width, height);
        TextureRegion hoverRegion = new TextureRegion(texture, x + width, y, width, height);

        normalDrawable = new TextureRegionDrawable(normalRegion);
        hoverDrawable = new TextureRegionDrawable(hoverRegion);

        ImageButtonStyle style = getStyle();
        style.up = normalDrawable;
        style.over = hoverDrawable;

        setSize(width, height);

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (isOver()) {
                    fire(new ChangeEvent());
                }
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                getStyle().up = hoverDrawable;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                getStyle().up = normalDrawable;
            }
        });
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        updateHitBox();
    }

    private void updateHitBox() {
        float width = getWidth();
        float height = getHeight();
        float x = getX();
        float y = getY();
        setBounds(x, y, width, height);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        updateHitBox();
    }
}
