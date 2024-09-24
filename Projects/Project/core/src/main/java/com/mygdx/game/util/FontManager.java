package com.mygdx.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.ObjectMap;

public class FontManager {
    private static FontManager instance;
    private ObjectMap<Integer, BitmapFont> fonts;
    private FreeTypeFontGenerator generator;

    private FontManager() {
        fonts = new ObjectMap<>();
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/NanumGothic.ttf"));
    }

    public static FontManager getInstance() {
        if (instance == null) {
            instance = new FontManager();
        }
        return instance;
    }

    public BitmapFont getFont(int size) {
        if (!fonts.containsKey(size)) {
            fonts.put(size, generateFont(size));
        }
        return fonts.get(size);
    }

    private BitmapFont generateFont(int size) {
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = size;
        parameter.characters = generateCharacters(size);
        BitmapFont font = generator.generateFont(parameter);
        font.setUseIntegerPositions(false);
        return font;
    }

    private String generateCharacters(int size) {
        StringBuilder sb = new StringBuilder();

        // 기본 ASCII 문자 (공백부터 ~까지)
        for (char c = ' '; c <= '~'; c++) {
            sb.append(c);
        }

        // 한글 완성형 문자 (가 ~ 힣)
        for (char c = '가'; c <= '힣'; c++) {
            sb.append(c);
        }

        // 한글 자음
        sb.append("ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ");

        // 한글 모음
        sb.append("ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ");

        // 크기가 작은 폰트에 대해 추가 문자 생성
        if (size <= 18) {
            // 추가 ASCII 문자 및 특수 문자
            for (char c = 0; c < 256; c++) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public void dispose() {
        for (BitmapFont font : fonts.values()) {
            font.dispose();
        }
        generator.dispose();
    }
}
