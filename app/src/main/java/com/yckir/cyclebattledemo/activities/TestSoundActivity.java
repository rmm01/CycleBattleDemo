package com.yckir.cyclebattledemo.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.yckir.cyclebattledemo.R;
import com.yckir.cyclebattledemo.utility.SoundManager;

/**
 * Class for testing the sound effects.
 */
public class TestSoundActivity extends AppCompatActivity {
    public static final String TAG = "TEST_SOUND_ACTIVITY";

    private SoundManager mSoundManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sound);

        mSoundManager = new SoundManager(this, 0);

    }

    public void buttonPressed(View v) {
        switch (v.getId()) {

            case R.id.turn:
                mSoundManager.playSoundEffect(SoundManager.TURN_SOUND_ID);
                break;

            case R.id.crash:
                mSoundManager.playSoundEffect(SoundManager.CRASH_SOUND_ID);
                break;

            case R.id.finished:
                mSoundManager.playSoundEffect(SoundManager.FINISHED_SOUND_ID);
                break;

            case R.id.countdown:
                mSoundManager.playSoundEffect(SoundManager.COUNTDOWN_SOUND_ID);
                break;

            case R.id.paused:
                mSoundManager.playSoundEffect(SoundManager.PAUSE_SOUND_ID);
                break;

            case R.id.prompt:
                mSoundManager.playSoundEffect(SoundManager.PROMPT_SOUND_ID);
                break;

            case R.id.stop_all:
                mSoundManager.stopSounds();
                break;


            case R.id.start_background:
                mSoundManager.playBackground();
                break;

            case R.id.stop_background:
                mSoundManager.stopBackground();
                break;

            case R.id.pause_background:
                mSoundManager.pauseBackground();
                break;

            case R.id.restart_background:
                mSoundManager.seekToBackground(0);
                break;

            default:
                Log.e(TAG, "unknown id");

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSoundManager.stopBackground();
    }
}
