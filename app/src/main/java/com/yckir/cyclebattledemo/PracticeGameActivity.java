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

/**
 * a practice mode for the game.
 */
public class PracticeGameActivity extends AppCompatActivity {
    public static final String TAG="PRACTICE_GAME";
    private GameSurfaceView mGameSurfaceView;
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_game);

        mGameSurfaceView = (GameSurfaceView)findViewById(R.id.practice_game_view);

        SwipeGestureListener swipeGestureListener= new SwipeGestureListener();

        mGestureDetector=new GestureDetector(this,swipeGestureListener);
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
            mGameSurfaceView.start(1000);
        }else{
            mGameSurfaceView.stop();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    /**
     * Detects fling Gesture and determines the direction of the fling
     */
    public class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener{
        public static final String TAG="SWIPE_GESTURE_LISTENER";

        @Override
        /**
         * determines the direction of the fling.
         */
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            float x1 = e1.getX();
            float x2 = e2.getX();
            float y1 = e1.getY();
            float y2 = e2.getY();
            Compass flingDirection = Compass.getDirection(x1,y1,x2,y2);
            Log.v(TAG, "FlingDirection = " + flingDirection);

            return true;
        }
    }
}
