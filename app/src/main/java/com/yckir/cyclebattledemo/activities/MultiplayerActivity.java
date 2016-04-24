package com.yckir.cyclebattledemo.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.yckir.cyclebattledemo.fragments.ResultsDialogFragment;
import com.yckir.cyclebattledemo.utility.AlarmHandler;
import com.yckir.cyclebattledemo.utility.FileUtility;
import com.yckir.cyclebattledemo.utility.GameResultsData;
import com.yckir.cyclebattledemo.views.gameSurfaceView.GameSurfaceView;
import com.yckir.cyclebattledemo.R;
import com.yckir.cyclebattledemo.utility.ClassStateString;
import com.yckir.cyclebattledemo.utility.SoundManager;

import java.io.File;
import java.util.HashMap;

/**
 * A multiplayer Game mode. Checks the bundle received via intent for the NUM_PLAYERS_BUNDLE_KEY argument.
 * This can be used to set the number of players. If it is not included, then the GameSurfaceView in xml
 * will determine the number of players. A call to onPause will pause a game if it is running.
 * An ImageView is used to display a static background. A GameSurfaceView is used to draw the animation.
 * The static background saved to a file by the GameSurfaceView and the ImageView sets it as its
 * main content once its ready.
 */
public class MultiplayerActivity extends AppCompatActivity implements GameSurfaceView.GameEventListener, AlarmHandler.AlarmListener {

    public  static final String     TAG                         =   "PRACTICE_GAME";
    public  static final String     NUM_PLAYERS_BUNDLE_KEY      =   TAG + ":NUM_PLAYERS";
    private static final String     TOUCH_MODE_KEY              =   TAG + ":TOUCH_MODE";
    private static final String     BACKGROUND_TIME_KEY         =   TAG + ":BACKGROUND_TIME";
    private static final String     WINS_KEY                    =   TAG + ":WINS";

    private static final int START_TOUCH_MODE = 0;
    private static final int RESUME_TOUCH_MODE = 1;
    private static final int NEW_GAME_TOUCH_MODE = 2;
    private static final int NO_TOUCH_MODE = 3;

    private int mTouchMode;

    private String mStartText;
    private String mNewGameText;
    private String mResumeText;
    private String mReadyText;
    private String mSetText;

    private int mStartAlarmId           =   10000;
    private int mResumeAlarmId          =   20000;
    private int mResultsAlarmId         =   30000;
    private int mReadyCountdownAlarmId  =   40000;
    private int mSetCountdownAlarmId    =   50000;

    private int mStartBackgroundTime    =   0;

    private AlarmHandler mAlarm;
    private HashMap<String, Integer> mWins;
    private SoundManager mSoundManager;

    private AlertDialog mPauseDialog;
    private GameSurfaceView mGameSurfaceView;
    private ImageView mBackgroundView;

    /**
     * initialize the pause dialog.
     */
    private void createPauseDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.multiplayer_activity_pause_dialog_title);
        builder.setMessage(R.string.multiplayer_activity_pause_dialog_message);
        builder.setNegativeButton(R.string.multiplayer_activity_pause_dialog_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setPositiveButton(R.string.multiplayer_activity_pause_dialog_positive_button, null);
        mPauseDialog = builder.create();
    }


    /**
     * Disables the status bar
     */
    private void disableStatusBar(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    /**
     * Pauses the game if a match is running. Cancel any pending alarms
     * since the game is being paused.
     */
    private void pauseGame(){
        final int state = mGameSurfaceView.getState();

        switch (state){
            case GameSurfaceView.WAITING:
                mStartAlarmId ++;
                mReadyCountdownAlarmId ++;
                mSetCountdownAlarmId ++;
                mTouchMode = START_TOUCH_MODE;
                mGameSurfaceView.setText(mStartText, true);
                break;
            case GameSurfaceView.RUNNING:
                mGameSurfaceView.pause(System.currentTimeMillis());
                mSoundManager.pauseBackground();
                mSoundManager.playSoundEffect(SoundManager.PAUSE_SOUND_ID);
                mTouchMode = RESUME_TOUCH_MODE;
                mGameSurfaceView.setText(mResumeText, true);
                break;
            case GameSurfaceView.PAUSED:
                mResumeAlarmId ++;
                mReadyCountdownAlarmId ++;
                mSetCountdownAlarmId ++;
                mTouchMode = RESUME_TOUCH_MODE;
                mGameSurfaceView.setText(mResumeText, true);
                break;
            case GameSurfaceView.FINISHED:
                break;
        }
    }


    /**
     * Checks the bundle inside the intent that started this activity to see if it specifies the
     * number of players.
     */
    private void parseIntentBundle(){
        Bundle b = getIntent().getExtras();
        int noPlayersSpecified=-1;
        if( b == null )
            return;

        int numPlayers = b.getInt(NUM_PLAYERS_BUNDLE_KEY,noPlayersSpecified);

        if(numPlayers == noPlayersSpecified)
            return;

        if(numPlayers < 2 || numPlayers > 4 ) {
            Log.e(TAG,"invalid number " + numPlayers + " for number of players was passed in " +
                    "intent bundle, using default value in GameSurfaceView");
            return;
        }

        mGameSurfaceView.setNumPlayers(numPlayers);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, " onCreate ");
        disableStatusBar();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiplayer_game_activity);

        mTouchMode = START_TOUCH_MODE;

        mGameSurfaceView = (GameSurfaceView)findViewById(R.id.multiplayer_game_view);
        mBackgroundView = (ImageView)findViewById(R.id.background_image_view);

        Resources res = getResources();
        mStartText = res.getString(R.string.start_prompt);
        mNewGameText = res.getString(R.string.new_game_prompt);
        mResumeText = res.getString(R.string.resume_prompt);
        mReadyText = res.getString(R.string.countdown_ready);
        mSetText = res.getString(R.string.countdown_set);

        mGameSurfaceView.setGameEventListener(this);
        mGameSurfaceView.setZOrderOnTop(true);
        mGameSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);

        mAlarm = new AlarmHandler(this);
        mWins = new HashMap<>(4);

        parseIntentBundle();
        createPauseDialog();
        FileUtility.createDirectories(this);
        mGameSurfaceView.setText(mStartText, false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(mSoundManager == null)
            mSoundManager = new SoundManager(this, mStartBackgroundTime, SoundManager.MATCH_MUSIC_ID);
    }


    @Override
    protected void onPause() {
        Log.v(TAG, " onPause ");
        pauseGame();
        super.onPause();
    }


    @Override
    protected void onStop() {
        Log.v(TAG, " onStop ");
        super.onStop();
        mSoundManager.release();
        mSoundManager = null;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(TOUCH_MODE_KEY, mTouchMode);
        outState.putInt(BACKGROUND_TIME_KEY, mSoundManager.getCurrentBackgroundTime());
        outState.putSerializable(WINS_KEY, mWins);

        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mTouchMode = savedInstanceState.getInt(TOUCH_MODE_KEY);
        mStartBackgroundTime = savedInstanceState.getInt(BACKGROUND_TIME_KEY);

        mWins = (HashMap<String, Integer>) savedInstanceState.getSerializable(WINS_KEY);

        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public void onBackPressed() {
        Log.v(TAG, "back pressed");
        pauseGame();
        mPauseDialog.show();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(MotionEventCompat.getActionMasked(event) != MotionEvent.ACTION_UP) {
            return super.onTouchEvent(event);
        }

        switch (mTouchMode){
            case START_TOUCH_MODE:
                mTouchMode = NO_TOUCH_MODE;

                //play ready sound
                alarm( mReadyCountdownAlarmId );
                //play set sound in 1 second
                mAlarm.setAlarm(1000, mSetCountdownAlarmId);
                //play go sound and start the game in 2 seconds
                mAlarm.setAlarm(2000, mStartAlarmId);
                return true;

            case RESUME_TOUCH_MODE:
                mTouchMode = NO_TOUCH_MODE;

                //play ready sound
                alarm( mReadyCountdownAlarmId );
                //play set sound in 1 second
                mAlarm.setAlarm(1000, mSetCountdownAlarmId);
                //play go sound and start the game in 2 seconds
                mAlarm.setAlarm(2000, mResumeAlarmId);
                return true;

            case NEW_GAME_TOUCH_MODE:
                mTouchMode = START_TOUCH_MODE;
                mGameSurfaceView.setText(mStartText, false);
                mGameSurfaceView.newGame();
                mSoundManager.stopSounds();
                mSoundManager.playSoundEffect(SoundManager.PROMPT_SOUND_ID);
                return true;

            case NO_TOUCH_MODE:
                return super.onTouchEvent(event);
            default:
                Log.e(TAG, "unknown touch mode = " + mTouchMode);
                return super.onTouchEvent(event);

        }
    }


    @Override
    public void gameEnded(GameResultsData gameResultsData) {

        mAlarm.setAlarm(1000, mResultsAlarmId);

        if(mWins.isEmpty())
            gameResultsData.initWins(mWins);

        gameResultsData.updateWins(mWins);

        FragmentManager fragmentManager = getSupportFragmentManager();
        ResultsDialogFragment fragment = ResultsDialogFragment.newInstance(gameResultsData);
        fragment.show(fragmentManager,"dialog");

        mSoundManager.pauseBackground();
        mSoundManager.seekToBackground(0);
        mSoundManager.playSoundEffect(SoundManager.FINISHED_SOUND_ID);
    }


    @Override
    public void backgroundReady(File file) {

        Bitmap myBitmap = BitmapFactory.decodeFile(file.getPath());
        BitmapDrawable drawable = new BitmapDrawable(getResources(),myBitmap);

        mBackgroundView.setImageDrawable(drawable);
    }


    @Override
    public void directionChange() {
        mSoundManager.playSoundEffect(SoundManager.TURN_SOUND_ID);
    }


    @Override
    public void crash() {
        mSoundManager.playSoundEffect(SoundManager.CRASH_SOUND_ID);
    }


    @Override
    public void alarm(int id) {
        if( id == mReadyCountdownAlarmId ){
            mGameSurfaceView.setText(mReadyText, true);
            mSoundManager.playSoundEffect(SoundManager.COUNTDOWN_SOUND_ID);
        }

        if( id == mSetCountdownAlarmId ){
            mGameSurfaceView.setText(mSetText, true);
            mSoundManager.playSoundEffect(SoundManager.COUNTDOWN_SOUND_ID);
        }

        if( id == mStartAlarmId ){
            mGameSurfaceView.start(System.currentTimeMillis());
            mSoundManager.playBackground();
            mSoundManager.playSoundEffect(SoundManager.GO_SOUND_ID);
            return;
        }

        if( id == mResumeAlarmId ){
            mGameSurfaceView.resume(System.currentTimeMillis());
            mSoundManager.playBackground();
            mSoundManager.playSoundEffect(SoundManager.GO_SOUND_ID);
        }

        if( id == mResultsAlarmId ){
            mTouchMode = NEW_GAME_TOUCH_MODE;
            mGameSurfaceView.setText(mNewGameText, true);
        }
    }


    @Override
    public String toString() {
        ClassStateString description = new ClassStateString(TAG);
        description.addMember("mTouchMode", mTouchMode);
        description.addClassMember("mGameSurfaceView", mGameSurfaceView);
        return description.getString();
    }
}