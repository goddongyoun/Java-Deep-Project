package com.mygdx.game.ui;

import com.ImportedPackage._Imported_ClientBase;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Main;
import com.mygdx.game.Player;
import com.mygdx.game.PlayerOfMulti;
import com.mygdx.game.Room;

public class LobbyMap extends Actor {
    private Main game;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Player localPlayer;
    private Array<TiledMapTileLayer> collisionLayers;

    private static float WORLD_WIDTH = 1280;
    private static float WORLD_HEIGHT = 720;

    public LobbyMap(final Main game) {
        this.game = game;
        WORLD_WIDTH = Main.WINDOW_WIDTH;
        WORLD_HEIGHT = Main.WINDOW_HEIGHT;
        map = new TmxMapLoader().load("maps/lobby_map_32x18.tmx");

        Gdx.app.log("LobbyMap", "Map loaded: " + (map != null));

        int mapWidth = map.getProperties().get("width", Integer.class);
        int mapHeight = map.getProperties().get("height", Integer.class);
        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);

        Gdx.app.log("LobbyMap", "Map dimensions: " + mapWidth + "x" + mapHeight + ", Tile size: " + tileWidth + "x" + tileHeight);

        float unitScale = Math.min(WORLD_WIDTH / (mapWidth * tileWidth), WORLD_HEIGHT / (mapHeight * tileHeight));

        renderer = new OrthogonalTiledMapRenderer(map, unitScale);
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply(true);

        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();

        float playerSize = tileWidth * unitScale * 1.6f;
        /*float playerX = WORLD_WIDTH / 2;
        float playerY = WORLD_HEIGHT / 2;*/
        localPlayer = this.game.getCurrentRoom().getme();
        localPlayer.size = playerSize;

        findCollisionLayers();
    }

    private void findCollisionLayers() {
        collisionLayers = new Array<>();
        for (MapLayer layer : map.getLayers()) {
            String layerName = layer.getName().toLowerCase();
            if (layerName.contains("block") || layerName.contains("wall")) {
                if (layer instanceof TiledMapTileLayer) {
                    collisionLayers.add((TiledMapTileLayer) layer);
                    Gdx.app.log("LobbyMap", "Collision layer found: " + layer.getName());
                }
            }
        }
        if (collisionLayers.size == 0) {
            Gdx.app.error("LobbyMap", "No collision layers found in the map!");
        }
    }

    public void update(float delta) {
        Vector2 oldPosition = localPlayer.getPosition().cpy();
        localPlayer.update(delta);
        Room room = game.getCurrentRoom();
    	_Imported_ClientBase.updateLoc(Math.round(room.getme().getPosition().x), Math.round(room.getme().getPosition().y));
    	int isErr = _Imported_ClientBase.getLocation();
    	if(isErr == 0) {
    		room.pCount = _Imported_ClientBase.playerCount-1;
        	int temp = 0;
        	for(int i = 0; i<5; i++) {
        		if(_Imported_ClientBase.players[i] != null) {
        			if(!(_Imported_ClientBase.players[i].name.equals(room.getme().getNickname()))) { // 그냥 언어포맷 무시하고 한글로 씀. 이름이 다르다면
        				if(room.m_players[temp] == null) {
        					room.m_players[temp] = new PlayerOfMulti(_Imported_ClientBase.players[i].name, _Imported_ClientBase.players[i].x,_Imported_ClientBase.players[i].y, room.getme().size);
        				}
        				//System.out.println("CAME!!!!!!!!!!!");
        				room.m_players[temp].setNickname(_Imported_ClientBase.players[i].name);
        				room.m_players[temp].update(_Imported_ClientBase.players[i].x,_Imported_ClientBase.players[i].y, delta);
        				temp++;
        			}
        		}
        	}
    	}
    	
        handleCollisions(oldPosition);

        float cameraX = localPlayer.getPosition().x;
        float cameraY = localPlayer.getPosition().y;
        float viewportHalfWidth = viewport.getWorldWidth() / 2;
        float viewportHalfHeight = viewport.getWorldHeight() / 2;

        cameraX = Math.max(viewportHalfWidth, Math.min(map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class) * renderer.getUnitScale() - viewportHalfWidth, cameraX));
        cameraY = Math.max(viewportHalfHeight, Math.min(map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class) * renderer.getUnitScale() - viewportHalfHeight, cameraY));

        camera.position.set(cameraX, cameraY, 0);
        camera.update();
    }

    private void handleCollisions(Vector2 oldPosition) {
        Rectangle playerBounds = localPlayer.getBounds();
        Vector2 playerPosition = localPlayer.getPosition();

        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);
        float unitScale = renderer.getUnitScale();

        int startX = (int) (playerBounds.x / (tileWidth * unitScale));
        int startY = (int) (playerBounds.y / (tileHeight * unitScale));
        int endX = (int) ((playerBounds.x + playerBounds.width - 1) / (tileWidth * unitScale));
        int endY = (int) ((playerBounds.y + playerBounds.height - 1) / (tileHeight * unitScale));

        boolean collisionX = false;
        boolean collisionY = false;

        for (TiledMapTileLayer collisionLayer : collisionLayers) {
            for (int y = startY; y <= endY; y++) {
                for (int x = startX; x <= endX; x++) {
                    TiledMapTileLayer.Cell cell = collisionLayer.getCell(x, y);
                    if (cell != null && cell.getTile() != null) {
                        if (!collisionX && (playerPosition.x > oldPosition.x || playerPosition.x < oldPosition.x)) {
                            playerPosition.x = oldPosition.x;
                            collisionX = true;
                        }
                        if (!collisionY && (playerPosition.y > oldPosition.y || playerPosition.y < oldPosition.y)) {
                            playerPosition.y = oldPosition.y;
                            collisionY = true;
                        }
                        if (collisionX && collisionY) break;
                    }
                }
                if (collisionX && collisionY) break;
            }
            if (collisionX && collisionY) break;
        }

        localPlayer.setPosition(playerPosition);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        viewport.apply();
        renderer.setView(camera);
        renderer.render();

        batch.setProjectionMatrix(camera.combined);
        localPlayer.render(batch);
        for(int i = 0; i<game.getCurrentRoom().pCount; i++) {
            if(game.getCurrentRoom().m_players[i] != null) {
                game.getCurrentRoom().m_players[i].render(batch);
            }

        }
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        viewport.update((int)width, (int)height, true);
    }

    public void dispose() {
        map.dispose();
        renderer.dispose();
        localPlayer.dispose();
    }
}
