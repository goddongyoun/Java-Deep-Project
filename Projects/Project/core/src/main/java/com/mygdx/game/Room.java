package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String title;
    private String code;
    private String password;
    private int maxPlayers;
    private List<Player> players;
    private Player me;
    public PlayerOfMulti[] m_players = new PlayerOfMulti[4];
    public int pCount = 0;

    public Room(String title, String code, String password, int maxPlayers, Player me) {
        this.title = title;
        this.code = code;
        this.password = password;
        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>();
        this.me = me;
        this.players.add(me);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public String getPassword() {
        return password;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        if (players.size() < maxPlayers) {
            players.add(player);
        }
    }

    public void addMulPlayer(PlayerOfMulti p) {

    }

    public Player getme() {
        return me;
    }

    public void setme(Player me) {
        this.me = me;
    }
}
