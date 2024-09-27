package com.mygdx.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.ObjectMap;

public class FontManager {
    private static FontManager instance;
    private ObjectMap<Integer, BitmapFont> fonts;
    private FreeTypeFontGenerator generator;
    private boolean loadingComplete;

    private FontManager() {
        fonts = new ObjectMap<>();
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/NanumGothic.ttf"));
        loadingComplete = false;
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
        parameter.characters = generateCharacters();
        parameter.minFilter = TextureFilter.Linear;
        parameter.magFilter = TextureFilter.Linear;
        parameter.borderWidth = 0;
        parameter.borderColor = null;
        parameter.shadowOffsetX = 0;
        parameter.shadowOffsetY = 0;
        parameter.shadowColor = null;
        parameter.spaceX = 0;
        parameter.spaceY = 0;
        parameter.kerning = true;
        parameter.flip = false;
        parameter.genMipMaps = true;
        parameter.incremental = true;

        BitmapFont font = generator.generateFont(parameter);
        font.setUseIntegerPositions(false);
        font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        return font;
    }

    private String generateCharacters() {
        StringBuilder sb = new StringBuilder();

        // 기본 ASCII 문자 (공백부터 ~까지)
        for (char c = ' '; c <= '~'; c++) {
            sb.append(c);
        }

        // 한글 완성형 문자 (가 ~ 힣)
        for (char c = '가'; c <= '힣'; c++) {
            sb.append(c);
        }

        // 한글 자음과 모음
        sb.append("ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ");

        // 추가 ASCII 문자 및 특수 문자
        for (char c = 0; c < 256; c++) {
            sb.append(c);
        }

        return sb.toString();
    }

    public void preloadFonts(int... sizes) {
        for (int size : sizes) {
            getFont(size);
        }
        loadingComplete = true;
    }

    public boolean isLoadingComplete() {
        return loadingComplete;
    }

    public void dispose() {
        for (BitmapFont font : fonts.values()) {
            font.dispose();
        }
        generator.dispose();
    }
}
