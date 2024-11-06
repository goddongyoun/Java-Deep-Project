package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class MissionDialog4 extends Dialog {
    Stage stage;
    Table contentTable = getContentTable();

    private int dialogSize = 620;

    //땅
    private Texture landTexture = new Texture(Gdx.files.internal("mission4/mission4Land.png"));
    private Image land = new Image(landTexture);

    //언덕
    private Texture hillTexture = new Texture(Gdx.files.internal("mission4/mission4Hill.png"));
    private Image hill = new Image(hillTexture);

    //하늘
    private Texture skyTexture = new Texture(Gdx.files.internal("mission4/mission4Sky.png"));
    private Image sky = new Image(skyTexture);

    //건슈터
    private TextureAtlas gunShooterAtlas = new TextureAtlas(Gdx.files.internal("mission4/gunShooter.atlas"));
    private Array<TextureRegion> gunShooterRegion = new Array<>();
    private Animation<TextureRegion> gunShooterAnime;
    private TextureRegionDrawable gunShooterDrawable;
    private Image gunShooter;
    private float gunShooterStateTime = 0f;
    private int gunShooterSize = 200;

    private boolean isShooting = false;

    public MissionDialog4(String title, Skin skin, Stage stage) {
        super(title, skin);
        this.stage = stage;

        // ESC 키를 눌렀을 때 닫기 버튼 동작을 실행
        this.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    MissionDialog4.this.hide(); // 팝업 창 닫기
                    return true; // 키 입력 처리됨을 알림
                }
                return super.keyDown(event, keycode);
            }
        });

        for (int i = 0; i<6;i++){
            gunShooterRegion.add(gunShooterAtlas.findRegion("ShooterMarine"+(i+1)));
        }
        gunShooterAnime = new Animation<TextureRegion>(0.08f,gunShooterRegion,Animation.PlayMode.NORMAL);
        gunShooterDrawable = new TextureRegionDrawable(gunShooterAnime.getKeyFrame(0));
        gunShooter = new Image(gunShooterDrawable);

        contentTable.add(sky).width(dialogSize).height(dialogSize).expand().fill();
        contentTable.add(land).width(dialogSize).height(dialogSize).expand().fill();
        contentTable.add(hill).width(dialogSize).height(dialogSize).expand().fill();
        contentTable.add(gunShooter).width(gunShooterSize).height(gunShooterSize).expand().fill();

        this.getCell(contentTable).width(dialogSize).height(dialogSize).expand().fill();
        // 미션 클래스 자체의 배경을 제거
        this.setBackground((Drawable) null);
        stage.addActor(this);
    }

    public void showMission(Stage stage){
        this.setSize(dialogSize, dialogSize);

        // 레이아웃 업데이트
        this.invalidate();
        this.layout();

        // 팝업 창을 중앙에 배치
        this.setPosition(
            (stage.getWidth() - this.getWidth()) / 2,
            (stage.getHeight() - this.getHeight()) / 2
        );

        stage.addListener(new ClickListener(Input.Buttons.LEFT) { // 오른쪽 버튼 클릭 리스너 추가
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("오른쪽 클릭 됨");
                gunShooterStateTime = 0f;  // 애니메이션 시작을 위해 상태 시간 초기화
                isShooting = true;        // 애니메이션이 시작되었음을 표시
            }
        });
    }

    public void act(float delta) {
        super.act(delta);

        sky.setPosition(
            (this.getWidth() - sky.getWidth()) / 2,
            (this.getHeight() - sky.getHeight()) / 2
        );
        land.setPosition(
            (this.getWidth() - land.getWidth()) / 2,
            (this.getHeight() - land.getHeight()) / 2
        );
        hill.setPosition(
            (this.getWidth() - hill.getWidth()) / 2,
            (this.getHeight() - hill.getHeight()) / 2
        );
        gunShooter.setPosition(440,60);

        if (isShooting) {
            //애니메이션 시간 업데이트
            gunShooterStateTime += delta;
            //현재 애니메이션 프레임 가져오기
            TextureRegion currentFrame = gunShooterAnime.getKeyFrame(gunShooterStateTime, false);
            ((TextureRegionDrawable) gunShooter.getDrawable()).setRegion(currentFrame);
        }
    }

    public void fire(float delta){

    }
}
