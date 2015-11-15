package com.yckir.cyclebattledemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;
import java.util.ArrayList;

/**
 * a practice mode for the game.
 */
public class PracticeGameActivity extends AppCompatActivity implements GameSurfaceView.GameEventListener {
    public  static final String     TAG                         =   "PRACTICE_GAME";
    private static final String     START_VISIBILITY_KEY        =   TAG + ":START_VISIBILITY";
    private static final String     PAUSE_VISIBILITY_KEY        =   TAG + ":PAUSE_VISIBILITY";
    private static final String     RESUME_VISIBILITY_KEY       =   TAG + ":RESUME_VISIBILITY";
    private static final String     NEW_GAME_VISIBILITY_KEY     =   TAG + ":NEW_GAME_VISIBILITY";
    private Button mStartButton;
    private Button mPauseButton;
    private Button mResumeButton;
    private Button mNewGameButton;
    private GameSurfaceView mGameSurfaceView;
    private MultiSwipeListener mSwipeListener;
    private boolean isRunning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, " onCreate ");
        super.onCreate(savedInstanceState);

        isRunning=false;

        setContentView(R.layout.multiplayer_game_activity);
        mGameSurfaceView = (GameSurfaceView)findViewById(R.id.multiplayer_game_view);
        mStartButton = (Button)findViewById(R.id.start_game_button);
        mPauseButton = (Button)findViewById(R.id.pause_game_button);
        mResumeButton = (Button)findViewById(R.id.resume_game_button);
        mNewGameButton = (Button)findViewById(R.id.new_game_button);

        mGameSurfaceView.addGameEventListener(this);
        mSwipeListener = new MultiSwipeListener(5);
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
        outState.putBoolean(PAUSE_VISIBILITY_KEY, mPauseButton.getVisibility()== View.VISIBLE);
        outState.putBoolean(RESUME_VISIBILITY_KEY, mResumeButton.getVisibility()== View.VISIBLE);
        outState.putBoolean(NEW_GAME_VISIBILITY_KEY, mNewGameButton.getVisibility() == View.VISIBLE);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.v(TAG, " onRestoreInstanceState ");

        boolean b1 = savedInstanceState.getBoolean(START_VISIBILITY_KEY);
        boolean b2 = savedInstanceState.getBoolean(PAUSE_VISIBILITY_KEY);
        boolean b3 = savedInstanceState.getBoolean(RESUME_VISIBILITY_KEY);
        boolean b4 = savedInstanceState.getBoolean(NEW_GAME_VISIBILITY_KEY);

        mStartButton.setVisibility(b1 ? View.VISIBLE : View.INVISIBLE);
        mPauseButton.setVisibility(b2 ? View.VISIBLE : View.INVISIBLE);
        mResumeButton.setVisibility(b3 ? View.VISIBLE : View.INVISIBLE);
        mNewGameButton.setVisibility(b4 ? View.VISIBLE : View.INVISIBLE);

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_practice_game, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Called when the start button is pressed.
     *
     * @param view The start Button
     */
    public void startButton(View view){
        isRunning=true;
        view.setVisibility(View.INVISIBLE);
        mPauseButton.setVisibility(View.VISIBLE);
        mGameSurfaceView.start(System.currentTimeMillis());
    }


    /**
     * Called when the pause button is pressed.
     *
     * @param view The pause Button
     */
    public void pauseButton(View view){
        isRunning=false;
        view.setVisibility(View.INVISIBLE);
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
        view.setVisibility(View.INVISIBLE);
        mPauseButton.setVisibility(View.VISIBLE);
        mGameSurfaceView.resume(System.currentTimeMillis());
    }


    /**
     * Called when the NewGame button is pressed.
     *
     * @param view The NewGame Button
     */
    public void newGameButton(View view) {
        isRunning = false;
        view.setVisibility(View.INVISIBLE);
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



        for( String line : state.split("\n") ) {
            Log.v( "STATE", line );
        }


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
        mPauseButton.setVisibility(View.INVISIBLE);
        mNewGameButton.setVisibility(View.VISIBLE);
    }


    @Override
    public String toString() {
        ClassStateString description = new ClassStateString(TAG);
        description.addMember("isRunning",isRunning);
        description.addMember("StartButtonVisible", mStartButton.getVisibility() == View.VISIBLE );
        description.addMember("PauseButtonVisible", mPauseButton.getVisibility() == View.VISIBLE );
        description.addMember("ResumeButtonVisible", mResumeButton.getVisibility() == View.VISIBLE);
        description.addMember("NewGameButtonVisible",  mNewGameButton.getVisibility() == View.VISIBLE);
        description.addClassMember("mGameSurfaceView", mGameSurfaceView);
        return description.getString();
    }


    /**
     * Detects finger Swipe Gesture and determines the direction of the fling. Is capable of
     * detecting multiple swipes occurring at once.
     */
    public class MultiSwipeListener{

        private ArrayList<Point> mEvents;
        private final double MIN_FLING_DISTANCE = 10;


        public MultiSwipeListener(int size){
            mEvents = new ArrayList<>(size);
        }


        /**
         * Receive the following touch event and determine what to do to take depending on events
         * action.
         *
         * @param event a motion event
         */
        public void receiveTouchEvent(MotionEvent event){
            int action = event.getActionMasked();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    capture(event);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    isFling(event);
                    break;
            }
        }


        /**
         * Store the event.
         *
         * @param event to be stored
         */
        private void capture(MotionEvent event) {
            int index = event.getActionIndex();
            int id = event.getPointerId(index);
            //int x = (int)event.getX(index);
            //int y = (int)event.getY(index);
            //Log.v(TAG, "finger pressed: index = " + index + ", id = " + id+ " at " + x + ", " + y);
            mEvents.add(id,new Point(event.getX(index), event.getY(index) ));
        }


        /**
         * called when an DOWN has been received for an event. Determines if the event was a swipe
         * gesture.
         * @param event event with an action DOWN
         */
        private void isFling(MotionEvent event) {
            //don't accept any gestures until the game starts.
            if(! isRunning )
                return;
            int index = event.getActionIndex();
            int id = event.getPointerId(index);
            //Log.v( TAG, "finger released: index " + index + " and id " + id );

            Point p1 = mEvents.get(id);
            Point p2 = new Point( event.getX(index), event.getY(index) );
            //Point.logPoints(p1, p2);

            if(Point.delta(p1,p2) < MIN_FLING_DISTANCE)
                return;

            onFling(p1,p2);
        }


        /**
         * A fling has happened at the two points, have the SurfaceView make a request to change
         * the players direction.
         *
         * @param p1 point 1
         * @param p2 point 2
         */
        private void onFling(Point p1, Point p2) {
            Compass flingDirection = Compass.getDirection( p1, p2 );
            int player = determinePlayerNumber(p1, p2, mGameSurfaceView.getHeight());
            long flingTime = System.currentTimeMillis();

            Log.v(TAG, " swipe by player " + player + ", FlingDirection = "
                    + flingDirection + " at time " + flingTime);

            mGameSurfaceView.requestDirectionChange( player, flingDirection, flingTime );
        }


        /**
         * Determine which player swiped a finger on the screen. player zero is the top half of the
         * screen. player 2 is the bottom half of the screen.
         *
         * @param p1 point 1
         * @param p2 point 2
         * @param height the height of the screen
         * @return the player that swiped the screen
         */
        private int determinePlayerNumber(Point p1, Point p2, int height){
            double y1 = p1.getPositionY();
            double y2 = p2.getPositionY();
            int swipeCenter = (int)( Math.abs( y2 - y1 ) / 2 + Math.min( y2, y1 ) );
            int player = swipeCenter/(height/2);
            if(player>1) {
                Log.v(TAG,"The swipe was outside the height, assuming it was player 1");
                player = 1;
            }
            return  player;
        }
    }
}
