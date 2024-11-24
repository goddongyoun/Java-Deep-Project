package com.mygdx.game;

import com.ImportedPackage._Imported_ClientBase;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.screens.LobbyScreen;
import com.mygdx.game.util.FontManager;

public class PlayerOfMulti {

    private String nickname;
    private Vector2 position;
    private Vector2 velocity;
    private float speedX = 160f;
    private float speedY = 120f;
    private Rectangle bounds;
    private float size;
    private Vector2 serverPosition = new Vector2();

    private TextureAtlas atlas;
    private Animation<TextureRegion> idleLeftAnimation;
    private Animation<TextureRegion> idleRightAnimation;
    private Animation<TextureRegion> runLeftAnimation;
    private Animation<TextureRegion> runRightAnimation;
    private TextureRegion defaultTexture;
    private float stateTime;
    private boolean facingLeft = false;
    private boolean isReady;
    private PlayerState currentState;

    private BitmapFont font;
    private Color nicknameColor;
    private Color outlineColor;
    private GlyphLayout glyphLayout;
    private int fontSize = 19; // 폰트 크기를 조절할 수 있는 변수

    private enum PlayerState {
        IDLE, RUNNING
    }

    public PlayerOfMulti(String nickname, float x, float y, float size) {
        this.nickname = nickname;
        this.position = new Vector2(x, y);
        this.velocity = new Vector2();
        this.size = size;
        this.bounds = new Rectangle(x, y, size, size);
        this.isReady = false;
        this.currentState = PlayerState.IDLE;
        this.nicknameColor = Color.WHITE;
        this.outlineColor = Color.BLACK;
        this.glyphLayout = new GlyphLayout();

        initializeFont();
        loadTextures();
    }

    private void initializeFont() {
        this.font = FontManager.getInstance().getFont(fontSize);
        this.font.getData().markupEnabled = true;
        this.font.getData().setScale(1);
    }

    private void loadTextures() {
        try {
            String atlasPath = "player/Frogs.atlas";
            FileHandle atlasFile = Gdx.files.internal(atlasPath);
            if (!atlasFile.exists()) {
                Gdx.app.error("Player", "Atlas file not found: " + atlasPath);
                createDefaultTexture();
                return;
            }

            Gdx.app.log("Player", "Attempting to load atlas: " + atlasPath);
            atlas = new TextureAtlas(atlasFile);

            Gdx.app.log("Player", "Atlas loaded successfully. Regions: " + atlas.getRegions().size);

            float frameDuration = 0.1f;

            idleLeftAnimation = createAnimation("FrogIdleL", frameDuration);
            idleRightAnimation = createAnimation("FrogIdleR", frameDuration);
            runLeftAnimation = createAnimation("FrogRunL", frameDuration);
            runRightAnimation = createAnimation("FrogRunR", frameDuration);

            defaultTexture = atlas.findRegion("FrogIdleR1");
            if (defaultTexture == null) {
                Gdx.app.error("Player", "Failed to load default texture from atlas");
                createDefaultTexture();
            } else {
                Gdx.app.log("Player", "Default texture set successfully");
            }

            Gdx.app.log("Player", "Animations loaded successfully");
        } catch (Exception e) {
            Gdx.app.error("Player", "Error loading textures", e);
            createDefaultTexture();
        }
    }

    private Animation<TextureRegion> createAnimation(String regionName, float frameDuration) {
        Array<TextureAtlas.AtlasRegion> frames = new Array<>();
        for (int i = 1; i <= 12; i++) {
            TextureAtlas.AtlasRegion region = atlas.findRegion(regionName + i);
            if (region != null) {
                frames.add(region);
            } else {
                break;
            }
        }
        Gdx.app.log("Player", regionName + " Frames: " + frames.size);
        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }

    private void createDefaultTexture() {
        Gdx.app.log("Player", "Creating default texture");
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.MAGENTA);
        pixmap.fill();
        defaultTexture = new TextureRegion(new Texture(pixmap));
        pixmap.dispose();
    }

	public void update(float x, float y, float delta) {
		stateTime += delta;

		// 서버 위치 저장
		serverPosition.set(x, y);

		// 현재 위치와 서버 위치의 차이 계산
		float dx = serverPosition.x - position.x;
		float dy = serverPosition.y - position.y;

		// 텔레포트가 필요한 거리 체크 (예: 100 이상 차이나면)
		float teleportThreshold = 100f;
		if (Math.abs(dx) > teleportThreshold || Math.abs(dy) > teleportThreshold) {
			// 직접 서버 위치로 텔레포트
			position.x = serverPosition.x;
			position.y = serverPosition.y;
			velocity.setZero();
			currentState = PlayerState.IDLE;
		} else {
			// 일반적인 이동 처리
			velocity.setZero();
			float moveThreshold = 1f;

			if (Math.abs(dx) > moveThreshold || Math.abs(dy) > moveThreshold) {
				velocity.x = dx;
				velocity.y = dy;
				velocity.nor(); // 방향 정규화

				// 이동 상태 설정
				currentState = PlayerState.RUNNING; // 어떤 방향이든 이동중이면 RUNNING

				// x 방향 이동시 방향 설정
				if (Math.abs(dx) > moveThreshold) {
					if (velocity.x < 0) {
						facingLeft = true;
					} else if (velocity.x > 0) {
						facingLeft = false;
					}
				}
			} else {
				currentState = PlayerState.IDLE;
			}

			// velocity를 사용한 부드러운 이동
			position.x += velocity.x * speedX * delta;
			position.y += velocity.y * speedY * delta;
		}

		bounds.setPosition(position);
	}

    public void render(Batch batch) {
        TextureRegion currentFrame = null;

        try {
            switch (currentState) {
                case RUNNING:
                    currentFrame = facingLeft ? runLeftAnimation.getKeyFrame(stateTime, true) : runRightAnimation.getKeyFrame(stateTime, true);
                    break;
                case IDLE:
                default:
                    currentFrame = facingLeft ? idleLeftAnimation.getKeyFrame(stateTime, true) : idleRightAnimation.getKeyFrame(stateTime, true);
                    break;
            }
        } catch (Exception e) {
            Gdx.app.error("Player", "Error getting animation frame", e);
        }

        if (currentFrame != null && currentFrame.getTexture() != null) {
            batch.draw(currentFrame, position.x, position.y, size, size);
        } else if (defaultTexture != null && defaultTexture.getTexture() != null) {
            Gdx.app.log("Player", "Using default texture");
            batch.draw(defaultTexture, position.x, position.y, size, size);
        } else {
            Gdx.app.error("Player", "No valid texture to render");
        }
        
        String tempBName = null;
        // 닉네임 그리기 (윤곽선 포함)
        if(LobbyScreen.shouldStart == true) {
        	if(nickname.equals(_Imported_ClientBase.getBossName())) {
            	if(nicknameColor != Color.RED) {
            		nicknameColor = Color.RED;
            	}
            	tempBName = "*BOSS* "+nickname;
                glyphLayout.setText(font, tempBName);
        	}
        }
        else {
        	glyphLayout.setText(font, nickname);
        }
        float nicknameX = position.x + size / 2 - glyphLayout.width / 2;
        float nicknameY = position.y + size + glyphLayout.height + 5;

        // 윤곽선 그리기
        font.setColor(outlineColor);
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                	if(tempBName == null) {
                		font.draw(batch, nickname, nicknameX + i, nicknameY + j);
                	}
                	else {
                		font.draw(batch, tempBName, nicknameX + i, nicknameY + j);
                	}
                }
            }
        }
        
        // 닉네임 그리기
        font.setColor(nicknameColor);
        if(tempBName == null) {
    		font.draw(batch, nickname, nicknameX, nicknameY);
    	}
    	else {
    		font.draw(batch, tempBName, nicknameX, nicknameY);
    	}
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getSize() {
        return this.size;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
        this.bounds.setPosition(position);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getSpeedX() {
        return speedX;
    }

    public void setSpeedX(float speedX) {
        this.speedX = speedX;
    }

    public float getSpeedY() {
        return speedY;
    }

    public void setSpeedY(float speedY) {
        this.speedY = speedY;
    }

    public void dispose() {
        if (atlas != null) {
            atlas.dispose();
        }
        if (defaultTexture != null && defaultTexture.getTexture() != null) {
            defaultTexture.getTexture().dispose();
        }
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

    // 폰트 크기를 조절하는 메서드
    public void setFontSize(int size) {
        this.fontSize = size;
        initializeFont();
    }

    // 닉네임 색상을 설정하는 메서드
    public void setNicknameColor(Color color) {
        this.nicknameColor = color;
    }

    // 윤곽선 색상을 설정하는 메서드
    public void setOutlineColor(Color color) {
        this.outlineColor = color;
    }
}