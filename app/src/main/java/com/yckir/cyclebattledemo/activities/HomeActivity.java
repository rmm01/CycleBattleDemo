package com.yckir.cyclebattledemo.activities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yckir.cyclebattledemo.R;
import com.yckir.cyclebattledemo.utility.SoundManager;

public class HomeActivity extends AppCompatActivity {

    private AlertDialog mHowToPlayDialog;
    private AlertDialog mRulesDialog;
    private AlertDialog mPlayDialog;
    private SoundManager mSoundManager = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        disableStatusBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initHowToPlayDialog();
        initRulesDialog();
        initPlayDialog();

        mSoundManager = new SoundManager(this, 0, SoundManager.HOME_MUSIC_ID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mSoundManager == null)
            mSoundManager = new SoundManager(this, 0, SoundManager.HOME_MUSIC_ID);
        mSoundManager.playBackground();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSoundManager.pauseBackground();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSoundManager.release();
        mSoundManager = null;
    }

    /**
     * Disables the status bar
     */
    private void disableStatusBar(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    private void initHowToPlayDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.home_activity_how_to_play_dialog_description);
        builder.setTitle(R.string.home_activity_how_to_play);
        mHowToPlayDialog = builder.create();
    }


    private void initRulesDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.home_activity_rules_dialog_description);
        builder.setTitle(R.string.home_activity_rules);
        mRulesDialog = builder.create();
    }


    private void initPlayDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] options = getResources().getStringArray(R.array.home_activity_play_dialog_num_players_array);
        final Context context = this;
        builder.setItems( options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, MultiplayerActivity.class);
                Bundle b = new Bundle();
                b.putInt(MultiplayerActivity.NUM_PLAYERS_BUNDLE_KEY, Integer.parseInt(options[which]));
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        builder.setTitle(R.string.home_activity_play_dialog_title);
        builder.setNegativeButton(R.string.home_activity_play_dialog_negative_button, null);
        mPlayDialog=builder.create();
    }


    public void play_button_clicked(View view) {
        mPlayDialog.show();
    }


    public void how_to_play_button_clicked(View view) {
        mHowToPlayDialog.show();
    }


    public void rules_button_clicked(View view) {
        mRulesDialog.show();
    }
}
