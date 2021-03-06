package com.yckir.cyclebattledemo.utility;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.util.Log;

import com.yckir.cyclebattledemo.R;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

/**
 * Handles playing audio used in a game. This class handles two types of audio, sound effects, and
 * background music. Only once background audio exists.
 * The Background music can be played, paused, stopped, and restarted. All methods can be called
 * regardless of the state of the class.
 * <p>
 * Sound effects are identified by their id's. The 6 ids are TURN_SOUND_ID, CRASH_SOUND_ID,
 * FINISHED_SOUND_ID, COUNTDOWN_SOUND_ID, PAUSE_SOUND_ID, and PROMPT_SOUND_ID.
 * <p>
 * Call release when you no longer need this object to release its resources.
 * <p>
 * Note: this class checks the preference for pref_background_music_key and pref_sound_effects_key.
 * Methods related to either will do nothing if the preference is set to true.
 *
 */
public class SoundManager {

    public static final String TAG = "SOUND_MANAGER";
    public static final String PREFIX = "android.resource://com.yckir.cyclebattledemo/";

    public static final int SOUND_POOL_MAX_STREAMS  = 5;

    @IntDef({TURN_SOUND_ID, CRASH_SOUND_ID, FINISHED_SOUND_ID, COUNTDOWN_SOUND_ID, PAUSE_SOUND_ID,
            PROMPT_SOUND_ID, GO_SOUND_ID })
    @Retention(RetentionPolicy.SOURCE)
    public @interface SOUND_EFFECTS{}

    public static final int TURN_SOUND_ID           = 0;
    public static final int CRASH_SOUND_ID          = 1;
    public static final int FINISHED_SOUND_ID       = 2;
    public static final int COUNTDOWN_SOUND_ID      = 3;
    public static final int PAUSE_SOUND_ID          = 4;
    public static final int PROMPT_SOUND_ID         = 5;
    public static final int GO_SOUND_ID             = 6;

    @IntDef({MATCH_MUSIC_ID, HOME_MUSIC_ID})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BACKGROUND_MUSIC{}

    public static final int MATCH_MUSIC_ID          = 0;
    public static final int HOME_MUSIC_ID           = 1;

    private final int mBackgroundId;
    private boolean mPrepared;
    private boolean mPlayWhenReady;

    private Context mContext;
    private SoundPool mSoundPool;
    private HashMap<Integer, Integer> mSoundPoolMap;
    private MediaPlayer mMediaPlayer;
    private boolean mSoundEffectsDisabled;
    private boolean mBackgroundMusicDisabled;


    /**
     * creates an instance of the SoundManager.
     *
     * @param context activity context.
     * @param startTime the start time for the background music in milliseconds.
     */
    public SoundManager(Context context, int startTime, @BACKGROUND_MUSIC int backgroundId){
        mContext = context;
        mPlayWhenReady = false;
        mPrepared = false;
        mBackgroundId = backgroundId;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        mBackgroundMusicDisabled = pref.getBoolean(
                context.getResources().getString(R.string.pref_background_music_key), false );

        mSoundEffectsDisabled = pref.getBoolean(
                context.getResources().getString(R.string.pref_sound_effects_key), false );


        if( !mBackgroundMusicDisabled )
            prepareMediaPlayer(startTime);
        else
            Log.v(TAG, "background music is disabled because of preference");

        if( !mSoundEffectsDisabled) {
            createSoundPool();

            mSoundPoolMap = new HashMap<>(10);

            mSoundPoolMap.put(TURN_SOUND_ID, mSoundPool.load(context, R.raw.turn_30ms, 1));
            mSoundPoolMap.put(CRASH_SOUND_ID, mSoundPool.load(context, R.raw.explosion_02, 1));
            mSoundPoolMap.put(FINISHED_SOUND_ID, mSoundPool.load(context, R.raw.chipquest, 2));
            mSoundPoolMap.put(COUNTDOWN_SOUND_ID, mSoundPool.load(context, R.raw.countdown, 1));
            mSoundPoolMap.put(PAUSE_SOUND_ID, mSoundPool.load(context, R.raw.pickup_01, 2));
            mSoundPoolMap.put(PROMPT_SOUND_ID, mSoundPool.load(context, R.raw.collect_point_00, 1));
            mSoundPoolMap.put(GO_SOUND_ID, mSoundPool.load(context, R.raw.go, 1));
        }else{
            Log.v(TAG, "sound effects are disabled because of preference");
        }
    }


    /**
     * instantiates a new MediaPlayer and prepares it.
     *
     * @param startTime the start time for the background music in milliseconds.
     */
    private void prepareMediaPlayer(final int startTime){
        if(mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        mPrepared = false;
        mMediaPlayer = new MediaPlayer();
        setBackgroundMusic();
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mPrepared = true;
                mp.seekTo(startTime);

                mp.setVolume(0.5f,0.5f);

                if(mPlayWhenReady)
                    mp.start();
            }
        });
        mMediaPlayer.prepareAsync();
    }


    /**
     * creates the SoundPool. How its created depends on api
     */
    private void createSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool();
        } else {
            createOldSoundPool();
        }
    }


    /**
     * creates the SoundPool for api below lollipop
     */
    @SuppressWarnings("deprecation")
    private void createOldSoundPool(){
        mSoundPool = new SoundPool(SOUND_POOL_MAX_STREAMS, AudioManager.STREAM_MUSIC,0);
    }


    /**
     * creates the SoundPool for Lollipop and above
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createNewSoundPool(){
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(SOUND_POOL_MAX_STREAMS)
                .build();
    }


    /**
     * set the background music for the MediaPlayer.
     */
    private void setBackgroundMusic() {
        AssetFileDescriptor afd = mContext.getResources().openRawResourceFd(getBackgroundResource());

        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
            afd.close();
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Unable to play audio queue do to ARG exception: " + e.getMessage(), e);
        } catch (IllegalStateException e) {
            Log.e(TAG, "Unable to play audio queue do to STATE exception: " + e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, "Unable to play audio queue do to IO exception: " + e.getMessage(), e);
        }
    }


    /**
     * set the background music for the MediaPlayer using an alternative implementation.
     */
    private void setBackgroundMusicAlt() {
        Uri uri = Uri.parse(PREFIX + getBackgroundResource());

        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mContext, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Get the resource for the background music. bgm_action_3 is the default.
     *
     * @return the resource for the background music.
     */
    private int getBackgroundResource(){
        switch (mBackgroundId){
            case HOME_MUSIC_ID:
                return R.raw.battle_the_outsiders;

            case MATCH_MUSIC_ID:
                return R.raw.bgm_action_3;

            default:
                Log.e(TAG, "unknown background id received " + mBackgroundId);
                return R.raw.bgm_action_3;
        }
    }


    /**
     * Play a sound effect.
     *
     * @param id the id for a sound effect.
     */
    public void playSoundEffect(@SOUND_EFFECTS int id){
        if( mSoundEffectsDisabled )
            return;
        mSoundPool.play(mSoundPoolMap.get(id),0.4f,0.4f,1,0,1);
    }


    /**
     * stop all sound effects.
     */
    public void stopSounds(){
        if( mSoundEffectsDisabled )
            return;
        mSoundPool.autoPause();
    }


    /**
     * Plays the background music. Does nothing if already playing. If the media player is not
     * prepared to play, it will play asynchronously when it is prepared, unless a pause or stop is
     * called before then.
     */
    public void playBackground(){
        if( mBackgroundMusicDisabled )
            return;

        //play the audio when onPrepared is called
        mPlayWhenReady = true;

        //create and prepare the media player if instance is null
        if( mMediaPlayer == null ) {
            prepareMediaPlayer(0);
            return;
        }

        //if the player is not yet prepared, mPlayWhenReady will make sure it gets played
        if( !mPrepared)
            return;

        //if prepared and already playing, do nothing
         if( mMediaPlayer.isPlaying() )
            return;

        //if prepared and not playing
        mMediaPlayer.start();
    }


    /**
     * Pauses the background music if it is playing.
     */
    public void pauseBackground(){
        if( mBackgroundMusicDisabled )
            return;

        //set flag to make sure that nothing should play in the future.
        mPlayWhenReady = false;

        //do nothing if media player is not initialized
        if(mMediaPlayer == null)
            return;

        //if the player is not yet prepared, mPlayWhenReady will make sure nothing starts playing
        if( !mPrepared)
            return;

        //if prepared  and is playing, pause it
        if( mMediaPlayer.isPlaying() )
            mMediaPlayer.pause();

        //if prepared and not playing, do nothing
    }


    /**
     * Stops the background music. The MediaPlayer will need to be recreated if it wants to be
     * reused again.
     */
    public void stopBackground(){
        if( mBackgroundMusicDisabled )
            return;

        mPrepared = false;
        mPlayWhenReady = false;

        if(mMediaPlayer == null)
            return;

        mMediaPlayer.release();
        mMediaPlayer = null;
    }


    /**
     * Restart the background music to the selected position. If the music is already playing,
     * the music jumps to the selected, if it is paused, calling playBackground will play from
     * the selected position. If the given time is invalid, the position is moved to the beginning.
     *
     * @param time the position of the song in milliseconds
     */
    public void seekToBackground(int time){
        if( mBackgroundMusicDisabled )
            return;

        //no flags need to be changed since seekTo is safe to call if playing or paused.

        //if media player doesn't exist or isn't prepared, it will start from beginning by default.
        if(mMediaPlayer == null || !mPrepared)
            return;

        if(time < 0 || mMediaPlayer.getDuration() < time)
            mMediaPlayer.seekTo(0);
        else
            mMediaPlayer.seekTo(time);
    }


    /**
     * @return current time in milliseconds of the current position of background music, -1 if
     * background music is disabled
     */
    public int getCurrentBackgroundTime(){
        if( mBackgroundMusicDisabled )
            return -1;

        return mMediaPlayer.getCurrentPosition();
    }


    /**
     * releases this objects resources. Any instances holding this object should be set to null.
     */
    public void release(){
        if( !mBackgroundMusicDisabled )
            stopBackground();
        if( !mSoundEffectsDisabled )
            mSoundPool.release();
    }
}
