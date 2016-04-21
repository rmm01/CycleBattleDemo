package com.yckir.cyclebattledemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Window;
import android.view.WindowManager;

import com.yckir.cyclebattledemo.R;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        disableStatusBar();
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        String creditsKey = getResources().getString(R.string.pref_credits_key);

        Preference fooBarPref = findPreference(creditsKey);

        fooBarPref.setOnPreferenceClickListener(this);
    }

    /**
     * Disables the status bar
     */
    private void disableStatusBar(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        startActivity(new Intent(this,CreditsActivity.class));
        return true;
    }
}
