package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private String nickname;
    private Vector2 position;
    private Vector2 velocity;
    private float speed = 200f; // 픽셀/초
    private Rectangle bounds;

    private TextureAtlas atlas;
    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private TextureRegion standingFrame;
    private float stateTime;
    private boolean facingLeft = false;
    private boolean isReady;
    private PlayerState currentState;

    private enum PlayerState {
        STANDING, WALKING_LEFT, WALKING_RIGHT
    }

    public Player(String nickname, float x, float y) {
        this.nickname = nickname;
        this.position = new Vector2(x, y);
        this.velocity = new Vector2();
        this.bounds = new Rectangle(x, y, 32, 32); // 캐릭터 크기에 맞게 조정
        this.isReady = false;
        this.currentState = PlayerState.STANDING;

        atlas = new TextureAtlas(Gdx.files.internal("player/Player.atlas"));
        walkLeftAnimation = new Animation<>(0.1f, atlas.findRegions("PlayerLeft"), Animation.PlayMode.LOOP);
        walkRightAnimation = new Animation<>(0.1f, atlas.findRegions("PlayerRight"), Animation.PlayMode.LOOP);
        standingFrame = atlas.findRegion("PlayerRight1"); // 기본 서있는 모습은 오른쪽을 향하도록 설정
    }

    public void update(float delta) {
        stateTime += delta;

        // 입력 처리
        velocity.setZero();
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocity.x -= 1;
            currentState = PlayerState.WALKING_LEFT;
            facingLeft = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocity.x += 1;
            currentState = PlayerState.WALKING_RIGHT;
            facingLeft = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) velocity.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) velocity.y -= 1;

        // 대각선 이동 시 속도 정규화
        if (!velocity.isZero()) {
            velocity.nor();
        } else {
            currentState = PlayerState.STANDING;
        }

        // 위치 업데이트
        position.mulAdd(velocity, speed * delta);

        // 충돌 영역 업데이트
        bounds.setPosition(position);
    }

    public void render(Batch batch) {
        TextureRegion currentFrame;

        switch (currentState) {
            case WALKING_LEFT:
                currentFrame = walkLeftAnimation.getKeyFrame(stateTime, true);
                break;
            case WALKING_RIGHT:
                currentFrame = walkRightAnimation.getKeyFrame(stateTime, true);
                break;
            case STANDING:
            default:
                currentFrame = facingLeft ? walkLeftAnimation.getKeyFrame(0) : walkRightAnimation.getKeyFrame(0);
                break;
        }

        batch.draw(currentFrame, position.x, position.y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        atlas.dispose();
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
