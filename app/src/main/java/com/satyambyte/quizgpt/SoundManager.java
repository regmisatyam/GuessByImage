package com.satyambyte.quizgpt;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class SoundManager {
    private static SoundManager instance;
    private SoundPool soundPool;
    private int buttonClickSoundId;
    private int coinEffectSoundId;
    private int bombExplosionSoundId;

    private SoundManager(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        }

        // Load your sound effects here
        buttonClickSoundId = soundPool.load(context, R.raw.button_click, 1);
        coinEffectSoundId = soundPool.load(context, R.raw.coin_effect, 1);
        bombExplosionSoundId = soundPool.load(context, R.raw.bomb_explosion, 1);
    }

    public static SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }
        return instance;
    }

    public void playButtonClickSound() {
        soundPool.play(buttonClickSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playCoinEffectSound() {
        soundPool.play(coinEffectSoundId, 0.8f, 0.8f, 1, 0, 1.0f);
    }

    public void playBombExplosionSound() {
        soundPool.play(bombExplosionSoundId, 1.0f, 1.0f, 1, 0, 0.9f);
    }

    public void release() {
        soundPool.release();
        instance = null;
    }
}
