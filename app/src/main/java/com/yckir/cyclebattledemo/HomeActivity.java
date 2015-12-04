package com.yckir.cyclebattledemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    public void player_button_clicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] options = {"2","3","4"};
        final Context context = this;
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, PracticeGameActivity.class);
                Bundle b = new Bundle();
                b.putInt(PracticeGameActivity.NUM_PLAYERS_KEY, Integer.parseInt(options[which]));
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        builder.setTitle("How many players");
        builder.setNegativeButton("cancel", null);
        builder.create().show();
    }


    public void how_to_play_button_clicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("1) Place the device on a flat surface of hold it so that all player can see it.\n\n" +
            "2) Player 1 is red, 2 is green, 3 is white, and 4 is Magenta.\n\n" +
            "3) Place yourself near the edge of the screen where your color appears.  \n\n" +
            "4) Once a game begins, each player swipes a finger on the touch screen in the direction " +
                        "they want to move.\n\n" +
            "5) Make sure to swipe as close to your edge of the screen as possible.\n\n" +

                "Tablet recommended for three and four player modes."
        );
        builder.setTitle("How To Play");

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void rules_button_clicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                        "1) Cycles cant stop moving once they've started.\n\n" +
                        "2) Cycles can't move backwards.\n\n" +
                        "3) Cycles can only travel North, South, East or West.\n\n" +
                        "4) As each cycle moves, they leave behind a path in its color.\n\n" +
                        "5) If a player collides with a path, cycle, or goes outside the grid, they lose.\n\n" +
                        "6) The last player remaining wins.\n\n" +
                        "7) Make sure to swipe as close to your edge of the screen as possible"

        );
        builder.setTitle("Game Rules");

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
