package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.mygdx.game.screens.LoadingScreen;
import com.mygdx.game.util.FontManager;

public class Main extends Game {
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;

    private float masterVolume = 1f;
    private float bgmVolume = 1f;
    private float sfxVolume = 1f;
    private String playerNickname = "Player";
    private Room currentRoom;

    @Override
    public void create() {
        Gdx.graphics.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);

        // 폰트 미리 로드
        FontManager.getInstance().preloadFonts(16, 18, 24, 32); // 필요한 폰트 크기들

        setScreen(new LoadingScreen(this));
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        getScreen().dispose();
        FontManager.getInstance().dispose();
    }

    // Getter and setter methods
    public float getMasterVolume() { return masterVolume; }
    public void setMasterVolume(float volume) { this.masterVolume = volume; }

    public float getBgmVolume() { return bgmVolume; }
    public void setBgmVolume(float volume) { this.bgmVolume = volume; }

    public float getSfxVolume() { return sfxVolume; }
    public void setSfxVolume(float volume) { this.sfxVolume = volume; }

    public String getPlayerNickname() { return playerNickname; }
    public void setPlayerNickname(String nickname) { this.playerNickname = nickname; }

    public Room getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(Room room) { this.currentRoom = room; }
}
