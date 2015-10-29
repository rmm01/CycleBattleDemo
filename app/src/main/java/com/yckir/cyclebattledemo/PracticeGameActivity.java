package com.yckir.cyclebattledemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

/**
 * a practice mode for the game.
 */
public class PracticeGameActivity extends AppCompatActivity {
    public static final String TAG="PRACTICE_GAME";
    private GameSurfaceView mGameSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_game);

        Log.v(TAG, "1.00");
        mGameSurfaceView = (GameSurfaceView)findViewById(R.id.practice_game_view);
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
}
