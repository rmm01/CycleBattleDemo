package com.yckir.cyclebattledemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ToggleButton;
import java.util.ArrayList;

/**
 * a practice mode for the game.
 */
public class PracticeGameActivity extends AppCompatActivity {
    public static final String TAG="PRACTICE_GAME";
    private GameSurfaceView mGameSurfaceView;
    //private GestureDetector mGestureDetector;
    private MultiSwipeListener mSwipeListener;
    private int mPlayerNum;
    private long mStartTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_game);
        mGameSurfaceView = (GameSurfaceView)findViewById(R.id.practice_game_view);
        mPlayerNum=0;
        mStartTime=0;
        //mGestureDetector=new GestureDetector(this,new SwipeGestureListener());
        mSwipeListener = new MultiSwipeListener(5);
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
     * close the application.
     * @param view The GameSurfaceView
     */
    public void exitButton(View view) {
    }


    /**
     * restart the application.
     * @param view The GameSurfaceView
     */
    public void restartButton(View view) {
    }


    /**
     * start/stop the application.
     * @param view The GameSurfaceView
     */
    public void startToggle(View view) {
        ToggleButton t =(ToggleButton)view;
        Log.v(TAG,"isActivated = "+ t.isChecked());
        if(t.isChecked()){
            mStartTime=System.currentTimeMillis();
            mGameSurfaceView.start(mStartTime);
        }else{
            mGameSurfaceView.stop();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mSwipeListener.receiveTouchEvent(event);
        //mGestureDetector.onTouchEvent(event);
        return true;
    }


    /**
     * Detects fling Gesture and determines the direction of the fling
     */
    public class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener{
        public static final String TAG="SWIPE_GESTURE_LISTENER";

        @Override
        /**
         * Determines the direction of the fling and makes a request to change direction of the
         * currently selected cycle. The Request will be applied if the direction is perpendicular
         * to the cycles direction.
         *
         * @see Compass
         */
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float x1 = e1.getX();
            float x2 = e2.getX();
            float y1 = e1.getY();
            float y2 = e2.getY();
            long currentTime=System.currentTimeMillis();

            Compass flingDirection = Compass.getDirection(x1, y1, x2, y2);
            Log.v(TAG, "FlingDirection = " + flingDirection + " at time " + (currentTime - mStartTime));
            mGameSurfaceView.requestDirectionChange(mPlayerNum, flingDirection, currentTime - mStartTime);
            return true;
        }
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
            int action = event.getAction();
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
            if( mStartTime == 0 )
                return;
            int id = event.getPointerId(event.getActionIndex());

            Point p1 = mEvents.get(id);
            Point p2 = new Point( event.getX(), event.getY() );
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
            long flingTime = System.currentTimeMillis() - mStartTime;

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
