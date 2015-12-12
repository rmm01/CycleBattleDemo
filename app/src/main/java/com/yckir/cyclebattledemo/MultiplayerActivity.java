package com.yckir.cyclebattledemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ToggleButton;

/**
 * a practice mode for the game.
 */
public class MultiplayerActivity extends AppCompatActivity implements GameSurfaceView.GameEventListener,
        FourRegionSwipeDetector.OnRegionSwipeListener {
    public  static final String     TAG                         =   "PRACTICE_GAME";
    public  static final String     NUM_PLAYERS_KEY             =   TAG + ":NUM_CYCLES";
    private static final String     START_VISIBILITY_KEY        =   TAG + ":START_VISIBILITY";
    //private static final String     PAUSE_VISIBILITY_KEY        =   TAG + ":PAUSE_VISIBILITY";
    private static final String     RESUME_VISIBILITY_KEY       =   TAG + ":RESUME_VISIBILITY";
    private static final String     NEW_GAME_VISIBILITY_KEY     =   TAG + ":NEW_GAME_VISIBILITY";
    private Button mStartButton;
    //private Button mPauseButton;
    private Button mResumeButton;
    private Button mNewGameButton;
    private GameSurfaceView mGameSurfaceView;
    private FourRegionSwipeDetector mSwipeListener;
    private boolean isRunning;
    private boolean mCyclesCreated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, " onCreate ");
        disableStatusBar();
        super.onCreate(savedInstanceState);

        isRunning=false;
        mCyclesCreated =false;

        setContentView(R.layout.multiplayer_game_activity);
        mGameSurfaceView = (GameSurfaceView)findViewById(R.id.multiplayer_game_view);
        mStartButton = (Button)findViewById(R.id.start_game_button);
        //mPauseButton = (Button)findViewById(R.id.pause_game_button);
        mResumeButton = (Button)findViewById(R.id.resume_game_button);
        mNewGameButton = (Button)findViewById(R.id.new_game_button);

        mGameSurfaceView.addGameEventListener(this);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mSwipeListener = new FourRegionSwipeDetector(2,metrics,this);
    }


    private void disableStatusBar(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    /**
     * Gets the height of the status bar.
     *
     * @return the height of the status bar in pixels
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    @Override
    protected void onStart() {
        Log.v(TAG, " onStart ");
        super.onStart();
    }


    @Override
    protected void onResume() {
        Log.v(TAG, " onResume ");
        super.onResume();
        if(mCyclesCreated)
            return;
        Bundle b = getIntent().getExtras();
        if(b!=null){
            int numPlayers = b.getInt(NUM_PLAYERS_KEY);
            if(numPlayers!=0) {
                mGameSurfaceView.updateNumPlayers(numPlayers);
                mSwipeListener.setNumRegions(numPlayers);
                mCyclesCreated=true;
            }
        }

    }


    @Override
    protected void onPause() {
        Log.v(TAG, " onPause ");

        switch (mGameSurfaceView.getState()) {
            case GameSurfaceView.WAITING:
            case GameSurfaceView.FINISHED:
            case GameSurfaceView.PAUSED:
                break;

            case GameSurfaceView.RUNNING:
                pauseButton(null);
                break;
        }
        super.onPause();
    }


    @Override
    protected void onStop() {
        Log.v(TAG, " onStop ");
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        Log.v(TAG, " onDestroy ");
        super.onDestroy();
    }


    @Override
    protected void onRestart() {
        Log.v(TAG, " onRestart ");
        super.onRestart();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.v(TAG, " onSaveInstanceState ");

        outState.putBoolean(START_VISIBILITY_KEY, mStartButton.getVisibility() == View.VISIBLE);
        //outState.putBoolean(PAUSE_VISIBILITY_KEY, mPauseButton.getVisibility() == View.VISIBLE);
        outState.putBoolean(RESUME_VISIBILITY_KEY, mResumeButton.getVisibility() == View.VISIBLE);
        outState.putBoolean(NEW_GAME_VISIBILITY_KEY, mNewGameButton.getVisibility() == View.VISIBLE);

        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.v(TAG, " onRestoreInstanceState ");

        mCyclesCreated=true;

        boolean b1 = savedInstanceState.getBoolean(START_VISIBILITY_KEY);
        //boolean b2 = savedInstanceState.getBoolean(PAUSE_VISIBILITY_KEY);
        boolean b3 = savedInstanceState.getBoolean(RESUME_VISIBILITY_KEY);
        boolean b4 = savedInstanceState.getBoolean(NEW_GAME_VISIBILITY_KEY);

        mStartButton.setVisibility(b1 ? View.VISIBLE : View.INVISIBLE);
        //mPauseButton.setVisibility(b2 ? View.VISIBLE : View.INVISIBLE);
        mResumeButton.setVisibility(b3 ? View.VISIBLE : View.INVISIBLE);
        mNewGameButton.setVisibility(b4 ? View.VISIBLE : View.INVISIBLE);

        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public void onBackPressed() {
        Log.v(TAG, "back pressed");

        if(mGameSurfaceView.getState() == GameSurfaceView.RUNNING)
            pauseButton(null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Game");
        builder.setMessage("Are you sure you want to leave the game?");
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setPositiveButton("No", null);
        builder.create().show();
    }


    /**
     * Called when the start button is pressed.
     *
     * @param view The start Button
     */
    public void startButton(View view){
        isRunning=true;
        mStartButton.setVisibility(View.INVISIBLE);
        //mPauseButton.setVisibility(View.VISIBLE);
        mGameSurfaceView.start(System.currentTimeMillis());
    }


    /**
     * Called when the pause button is pressed.
     *
     * @param view The pause Button
     */
    public void pauseButton(View view){
        isRunning=false;
        //mPauseButton.setVisibility(View.INVISIBLE);
        mResumeButton.setVisibility(View.VISIBLE);
        mGameSurfaceView.pause(System.currentTimeMillis());
    }


    /**
     * Called when the resume button is pressed.
     *
     * @param view The resume Button
     */
    public void resumeButton(View view){
        isRunning=true;
        mResumeButton.setVisibility(View.INVISIBLE);
        //mPauseButton.setVisibility(View.VISIBLE);
        mGameSurfaceView.resume(System.currentTimeMillis());
    }


    /**
     * Called when the NewGame button is pressed.
     *
     * @param view The NewGame Button
     */
    public void newGameButton(View view) {
        isRunning = false;
        mNewGameButton.setVisibility(View.INVISIBLE);
        mStartButton.setVisibility(View.VISIBLE);
        mGameSurfaceView.newGame();
    }


    /**
     * Logs the current state of the activity and its member variables.
     *
     * @param view the view of the Button pressed
     */
    public void logInfoButton(View view) {
        String state = this.toString();
        Log.v("STATE", state);
    }


    public void replayButton(View view){
        mGameSurfaceView.replay();
    }


    /**
     * start,pause,resume the application.
     * @param view The GameSurfaceView
     */
    public void startToggle(View view) {
        long currentTime = System.currentTimeMillis();
        ToggleButton t =(ToggleButton)view;
        Log.v(TAG, "isActivated = " + t.isChecked());
        if(t.isChecked()){
            isRunning=false;
            mGameSurfaceView.pause(currentTime);
        }else{
            isRunning=true;
            mGameSurfaceView.resume(currentTime);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mSwipeListener.receiveTouchEvent(event);
        return true;
    }


    @Override
    public void gameEnded(int winner) {
        isRunning=false;
        //mPauseButton.setVisibility(View.INVISIBLE);
        mNewGameButton.setVisibility(View.VISIBLE);
    }


    @Override
    public void onRegionSwipe(int regionNumber, Compass direction, long swipeTime) {
        if(! isRunning )
            return;
        mGameSurfaceView.requestDirectionChange(regionNumber, direction, swipeTime);

    }


    @Override
    public String toString() {
        ClassStateString description = new ClassStateString(TAG);
        description.addMember("isRunning",isRunning);
        description.addMember("StartButtonVisible", mStartButton.getVisibility() == View.VISIBLE );
        //description.addMember("PauseButtonVisible", mPauseButton.getVisibility() == View.VISIBLE );
        description.addMember("ResumeButtonVisible", mResumeButton.getVisibility() == View.VISIBLE);
        description.addMember("NewGameButtonVisible", mNewGameButton.getVisibility() == View.VISIBLE);
        description.addClassMember("mGameSurfaceView", mGameSurfaceView);
        return description.getString();
    }
}
