package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Main;
import com.mygdx.game.util.FontManager;

public class LoadingScreen implements Screen {
    private Main game;
    private SpriteBatch batch;
    private BitmapFont font;

    public LoadingScreen(Main game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont(); // 기본 폰트 사용 (로딩 중에는 사용자 정의 폰트를 사용할 수 없음)
    }

    @Override
    public void show() {
        // 리소스 로딩 시작
        FontManager.getInstance().preloadFonts(16, 18, 24, 32);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.draw(batch, "Loading...", Gdx.graphics.getWidth() / 2f - 50, Gdx.graphics.getHeight() / 2f);
        batch.end();

        // 로딩이 완료되면 메인 메뉴로 전환
        if (FontManager.getInstance().isLoadingComplete()) {
            game.setScreen(new MainMenuScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
