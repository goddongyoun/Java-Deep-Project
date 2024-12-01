package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Player;
import com.mygdx.game.util.FontManager;
import com.badlogic.gdx.utils.TimeUtils;

public class GameUI {
    private SpriteBatch batch;
    private BitmapFont font;
    private Player player;
    private OrthographicCamera camera;

    // 보스 관련 변수
    private boolean isBossPlayer;
    private boolean bossActivated;
    private float gameStartTimer;
    private static final float BOSS_ACTIVATION_TIME = 20f;
    private float bossSkillCooldown = 3f;
    private float lastBossSkillTime = 0;

    public GameUI(SpriteBatch batch, Player player, OrthographicCamera camera) {
        this.batch = batch;
        this.player = player;
        this.camera = camera;
        this.font = FontManager.getInstance().getFont(20);
        this.gameStartTimer = 0;
    }

    public void update(float delta) {
        gameStartTimer += delta;
    }

    public void render() {
        // 화면 중앙 좌표 계산 (카메라 기준)
        float centerX = camera.position.x - (camera.viewportWidth / 2);
        float centerY = camera.position.y + (camera.viewportHeight / 2);

        batch.setProjectionMatrix(camera.combined); // 카메라 기준으로 렌더링
        batch.begin();

        // 보스 변신 타이머
        if (!bossActivated) {
            font.setColor(Color.WHITE);
            String timeText = String.format("보스 등장까지: %.1f초",
                Math.max(0, BOSS_ACTIVATION_TIME - gameStartTimer));
            font.draw(batch, timeText, centerX + camera.viewportWidth / 2 - font.getRegion().getRegionWidth() / 2,
                centerY - 50);
        }

        // 보스 스킬 쿨타임
        if (bossActivated && isBossPlayer) {
            float skillCooldown = bossSkillCooldown - (gameStartTimer - lastBossSkillTime);
            String skillText;
            if (skillCooldown > 0) {
                font.setColor(Color.RED);
                skillText = String.format("스킬 쿨타임: %.1f초", skillCooldown);
            } else {
                font.setColor(Color.GREEN);
                skillText = "스킬 사용 가능! (A키)";
            }
            font.draw(batch, skillText, centerX + camera.viewportWidth / 2 - font.getRegion().getRegionWidth() / 2,
                centerY - 70);
        }

        // 플레이어 구르기 쿨타임
        if (player.isInGame() && !player.isPetrified() && !player.isBoss()) {
            float rollCooldown = player.getRollingCooldown() -
                (TimeUtils.millis() - player.getLastRollingTime()) / 1000f;
            String rollText;
            if (rollCooldown > 0) {
                font.setColor(Color.YELLOW);
                rollText = String.format("구르기 쿨타임: %.1f초", rollCooldown);
            } else {
                font.setColor(Color.GREEN);
                rollText = "구르기 가능! (SPACE)";
            }
            font.draw(batch, rollText, centerX + camera.viewportWidth / 2 - font.getRegion().getRegionWidth() / 2,
                centerY - 90);
        }

        batch.end();
    }

    // Setters
    public void setBossActivated(boolean activated) {
        this.bossActivated = activated;
    }

    public void setIsBossPlayer(boolean isBoss) {
        this.isBossPlayer = isBoss;
    }

    public void setLastBossSkillTime(float time) {
        this.lastBossSkillTime = time;
    }

    public void dispose() {
        // font은 FontManager에서 관리되므로 여기서 dispose하지 않습니다
    }
}
