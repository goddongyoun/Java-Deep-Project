package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String title;
    private String code;
    private String password;
    private int maxPlayers;
    private List<Player> players;
    private Player host;

    public Room(String title, String code, String password, int maxPlayers, Player host) {
        this.title = title;
        this.code = code;
        this.password = password;
        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>();
        this.host = host;
        this.players.add(host);
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

    public Player getHost() {
        return host;
    }

    public void setHost(Player host) {
        this.host = host;
    }
}
