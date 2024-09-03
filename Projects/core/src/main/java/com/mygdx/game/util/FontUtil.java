package com.mygdx.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class FontUtil {

    public static void setupKoreanFont(Skin skin) {
        // TTF 폰트 파일 경로
        String fontPath = "ui/NanumGothic.ttf";  // 한글 지원 폰트 파일

        // FreeTypeFontGenerator를 사용하여 폰트 생성
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        // 폰트 크기 설정
        parameter.size = 32;

        // 필요한 모든 한글 글자와 숫자, 영어, 특수문자를 포함
        parameter.characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789" +
            "가나다라마바사아자차카타파하각낙닥락막박삿앋잦찧칵탁팍핛한글";

        // 폰트 생성
        BitmapFont koreanFont = generator.generateFont(parameter);

        // 생성기 메모리 해제
        generator.dispose();

        // Skin에 폰트 추가
        skin.add("koreanFont", koreanFont);
    }
}
