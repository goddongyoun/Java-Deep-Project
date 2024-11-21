package com.mygdx.game;

import com.ImportedPackage._Imported_ClientBase;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.mygdx.game.screens.LoadingScreen;
import com.mygdx.game.util.FontManager;
import com.badlogic.gdx.graphics.Color;

public class Main extends Game {
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;

    private float masterVolume = 1f;
    private float bgmVolume = 1f;
    private float sfxVolume = 1f;
    private String playerNickname = "Player";
    private Room currentRoom;
    private Color nicknameColor = Color.BLACK;

    @Override
    public void create() {
        Gdx.graphics.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);

        FontManager.getInstance().preloadFonts(16, 18, 24, 32);

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
        _Imported_ClientBase.SHUTDOWN();
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

    public Color getNicknameColor() { return nicknameColor; }
    public void setNicknameColor(Color color) { this.nicknameColor = color; }
}
