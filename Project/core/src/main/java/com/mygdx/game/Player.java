package com.mygdx.game;

public class Player {
    private String nickname;
    private boolean isReady;

    public Player(String nickname) {
        this.nickname = nickname;
        this.isReady = false;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }
}
