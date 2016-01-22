package com.yckir.cyclebattledemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.yckir.cyclebattledemo.utility.ClassStateString;
import com.yckir.cyclebattledemo.utility.Compass;
import com.yckir.cyclebattledemo.utility.FourRegionSwipeDetector;

/**
 * A multiplayer Game mode.
 */
public class MultiplayerActivity extends AppCompatActivity implements GameSurfaceView.GameEventListener,
        FourRegionSwipeDetector.OnRegionSwipeListener {

    public  static final String     TAG                         =   "PRACTICE_GAME";
    public  static final String     NUM_PLAYERS_KEY             =   TAG + ":NUM_PLAYERS";
    private static final String     START_VISIBILITY_KEY        =   TAG + ":START_VISIBILITY";
    private static final String     RESUME_VISIBILITY_KEY       =   TAG + ":RESUME_VISIBILITY";
    private static final String     NEW_GAME_VISIBILITY_KEY     =   TAG + ":NEW_GAME_VISIBILITY";

    private AlertDialog mPauseDialog;
    private FourRegionSwipeDetector mSwipeListener;
    private GameSurfaceView mGameSurfaceView;
    private Button mStartButton;
    private Button mResumeButton;
    private Button mNewGameButton;


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
     * Pauses the game if a match is running.
     */
    private void pauseGame(){
        if(mGameSurfaceView.getState() != GameSurfaceView.RUNNING)
            return;
        mResumeButton.setVisibility(View.VISIBLE);
        mGameSurfaceView.pause(System.currentTimeMillis());
    }


    /**
     * Checks the bundle inside the intent that started this activity to see if it specifies the
     * number of players.
     */
    private void setNumPlayers(){
        Bundle b = getIntent().getExtras();
        int noPlayersSpecified=-1;
        if( b == null )
            return;

        int numPlayers = b.getInt( NUM_PLAYERS_KEY ,noPlayersSpecified);

        if(numPlayers == noPlayersSpecified)
            return;

        if(numPlayers < 2 || numPlayers > 4 ) {
            Log.e(TAG,"invalid number " + numPlayers + " for number of players was passed in " +
                    "intent bundle, using default value in GameSurfaceView");
            return;
        }

        mGameSurfaceView.setNumPlayers(numPlayers);
        mSwipeListener.setNumRegions(numPlayers);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, " onCreate ");
        disableStatusBar();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiplayer_game_activity);

        mGameSurfaceView = (GameSurfaceView)findViewById(R.id.multiplayer_game_view);
        mStartButton = (Button)findViewById(R.id.start_game_button);
        mResumeButton = (Button)findViewById(R.id.resume_game_button);
        mNewGameButton = (Button)findViewById(R.id.new_game_button);

        mGameSurfaceView.addGameEventListener(this);
        mSwipeListener = new FourRegionSwipeDetector(2, getResources().getDisplayMetrics(), this);
        setNumPlayers();
        createPauseDialog();
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
        outState.putBoolean(RESUME_VISIBILITY_KEY, mResumeButton.getVisibility() == View.VISIBLE);
        outState.putBoolean(NEW_GAME_VISIBILITY_KEY, mNewGameButton.getVisibility() == View.VISIBLE);

        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.v(TAG, " onRestoreInstanceState ");

        boolean b1 = savedInstanceState.getBoolean(START_VISIBILITY_KEY);
        boolean b3 = savedInstanceState.getBoolean(RESUME_VISIBILITY_KEY);
        boolean b4 = savedInstanceState.getBoolean(NEW_GAME_VISIBILITY_KEY);

        mStartButton.setVisibility(b1 ? View.VISIBLE : View.INVISIBLE);
        mResumeButton.setVisibility(b3 ? View.VISIBLE : View.INVISIBLE);
        mNewGameButton.setVisibility(b4 ? View.VISIBLE : View.INVISIBLE);

        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public void onBackPressed() {
        Log.v(TAG, "back pressed");
        pauseGame();
        mPauseDialog.show();
    }


    /**
     * Called when the start button is pressed.
     *
     * @param view The start Button
     */
    public void startButton(View view){
        mStartButton.setVisibility(View.INVISIBLE);
        mGameSurfaceView.start(System.currentTimeMillis());
    }


    /**
     * Called when the resume button is pressed.
     *
     * @param view The resume Button
     */
    public void resumeButton(View view){
        mResumeButton.setVisibility(View.INVISIBLE);
        mGameSurfaceView.resume(System.currentTimeMillis());
    }


    /**
     * Called when the NewGame button is pressed.
     *
     * @param view The NewGame Button
     */
    public void newGameButton(View view) {
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


    /**
     * Called when the replay button is pressed.
     * @param view view of the button that was pressed.
     */
    public void replayButton(View view){
        mGameSurfaceView.replay();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mSwipeListener.receiveTouchEvent(event);
        return true;
    }


    @Override
    public void gameEnded(int winner) {
        mNewGameButton.setVisibility(View.VISIBLE);
    }


    @Override
    public void onRegionSwipe(int playerNumber, Compass direction, long swipeTime) {
        if( mGameSurfaceView.getState() != GameSurfaceView.RUNNING )
            return;
        mGameSurfaceView.requestDirectionChange(playerNumber, direction, swipeTime);
    }


    @Override
    public String toString() {
        ClassStateString description = new ClassStateString(TAG);
        description.addMember("StartButtonVisible", mStartButton.getVisibility() == View.VISIBLE );
        description.addMember("ResumeButtonVisible", mResumeButton.getVisibility() == View.VISIBLE);
        description.addMember("NewGameButtonVisible", mNewGameButton.getVisibility() == View.VISIBLE);
        description.addClassMember("mGameSurfaceView", mGameSurfaceView);
        return description.getString();
    }
}
