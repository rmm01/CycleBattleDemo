package com.yckir.cyclebattledemo.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yckir.cyclebattledemo.R;


public class CreditsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        disableStatusBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credits);

        TextView textView;

        textView = (TextView) findViewById(R.id.link1);
        if (textView != null) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        textView = (TextView) findViewById(R.id.link2);
        if (textView != null) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        textView = (TextView) findViewById(R.id.link3);
        if (textView != null) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        textView = (TextView) findViewById(R.id.link4);
        if (textView != null) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        textView = (TextView) findViewById(R.id.link5);
        if (textView != null) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        textView = (TextView) findViewById(R.id.link6);
        if (textView != null) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        textView = (TextView) findViewById(R.id.link7);
        if (textView != null) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }


    /**
     * Disables the status bar
     */
    private void disableStatusBar(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
