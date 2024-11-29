package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Player;
import com.mygdx.game.util.FontManager;
import com.badlogic.gdx.utils.TimeUtils;

public class GameUI {
    private SpriteBatch batch;
    private BitmapFont font;
    private Player player;

    // 보스 관련 변수
    private boolean isBossPlayer;
    private boolean bossActivated;
    private float gameStartTimer;
    private static final float BOSS_ACTIVATION_TIME = 20f;
    private float bossSkillCooldown = 5f;
    private float lastBossSkillTime = 0;

    public GameUI(SpriteBatch batch, Player player) {
        this.batch = batch;
        this.player = player;
        this.font = FontManager.getInstance().getFont(20);
        this.gameStartTimer = 0;
    }

    public void update(float delta) {
        gameStartTimer += delta;
    }

    public void render() {
        batch.begin();

        // 보스 변신 타이머
        if (!bossActivated) {
            font.setColor(Color.WHITE);
            String timeText = String.format("보스 등장까지: %.1f초",
                Math.max(0, BOSS_ACTIVATION_TIME - gameStartTimer));
            font.draw(batch, timeText, 10, Gdx.graphics.getHeight() - 10);
        }

        // 보스 스킬 쿨타임
        //TODO 글자 위치 조정
        if (bossActivated && isBossPlayer) {
            float skillCooldown = bossSkillCooldown - (gameStartTimer - lastBossSkillTime);
            if (skillCooldown > 0) {
                font.setColor(Color.RED);
                font.draw(batch, String.format("스킬 쿨타임: %.1f초", skillCooldown),
                    10, Gdx.graphics.getHeight() - 10);
            } else {
                font.setColor(Color.GREEN);
                font.draw(batch, "스킬 사용 가능! (A키)",
                    10, Gdx.graphics.getHeight() - 10);
            }
        }

        // 구르기 쿨타임
        if (player.isInGame() && !player.isPetrified()) {
            float rollCooldown = player.getRollingCooldown() -
                (TimeUtils.millis() - player.getLastRollingTime()) / 1000f;
            if (rollCooldown > 0) {
                font.setColor(Color.YELLOW);
                font.draw(batch, String.format("구르기 쿨타임: %.1f초", rollCooldown),
                    10, Gdx.graphics.getHeight() - 30);
            } else {
                font.setColor(Color.GREEN);
                font.draw(batch, "구르기 가능! (SPACE)",
                    10, Gdx.graphics.getHeight() - 30);
            }
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
