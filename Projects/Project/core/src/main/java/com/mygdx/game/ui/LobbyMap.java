package com.mygdx.game.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Main;
import com.mygdx.game.Player;

public class LobbyMap extends Actor {
    private Main game;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Player localPlayer;

    public LobbyMap(Main game) {
        this.game = game;
        map = new TmxMapLoader().load("maps/lobby_map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        camera = new OrthographicCamera();
        viewport = new FitViewport(400, 300, camera);

        localPlayer = new Player(game.getPlayerNickname(), 200, 150);
    }

    public void update(float delta) {
        localPlayer.update(delta);

        camera.position.set(localPlayer.getPosition(), 0);
        camera.update();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        viewport.apply();
        renderer.setView(camera);
        renderer.render();

        batch.setProjectionMatrix(camera.combined);
        localPlayer.render(batch);
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        viewport.update((int)width, (int)height);
    }

    public void dispose() {
        map.dispose();
        renderer.dispose();
        localPlayer.dispose();
    }
}
