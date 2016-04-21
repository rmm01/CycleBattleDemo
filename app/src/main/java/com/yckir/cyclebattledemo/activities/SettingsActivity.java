package com.yckir.cyclebattledemo.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.yckir.cyclebattledemo.R;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
